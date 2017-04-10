package cn.ufunny.game.libs.cache.support.annotation;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import cn.ufunny.game.libs.cache.exception.NoCacheableException;
import cn.ufunny.game.libs.cache.support.BaseCacheSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.ufunny.game.libs.cache.annotation.CacheField;
import cn.ufunny.game.libs.cache.annotation.DefaultCacheKey;


public abstract class AbstractCacheSupport<K, V> extends BaseCacheSupport<K,V> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractCacheSupport.class);
	
	private static final int MAX_DEGREE = 100;
	
	private static final String OBJECT_CLASSNAME = new Object().getClass().getSimpleName();
	

	private ClassField defaultKeyField;
	
	private List<KeyField> keyFields;
	

	
	protected <E extends Annotation> boolean hasFiledAnnotation(Field field,Class<E> clazz){
		E annotation = field.getDeclaredAnnotation(clazz);
		if(annotation != null){
			return true;
		}
		return false;
	}
	
	protected ClassField findClassFieldKey(Class<?> clazz,Class<? extends Annotation> annoClass){
		ClassField keyField = null;
		boolean found = false;
		Class<?> superClazz = clazz;
		List<Method> methods = Lists.newArrayList();
		for(int i=0;i<MAX_DEGREE;i++){
			if(found || superClazz.getSimpleName().equals(OBJECT_CLASSNAME)){
				break;
			}
			Field[] fields = superClazz.getDeclaredFields();
			for(Field field : fields){
				if(hasFiledAnnotation(field, annoClass)){
					methods.add(findMethod(field, superClazz));
					keyField = new ClassField(field, clazz,methods);
					found = true;
					break;
				}
			}
			superClazz = superClazz.getSuperclass();
		}
		return keyField;
	}
	
	@SuppressWarnings("unchecked")
	protected K findDefaultKey(V value){
		if(value == null){
			return null;
		}
		if(this.defaultKeyField == null){
			ClassField keyField = findClassFieldKey(this.vClazz, DefaultCacheKey.class);
			this.defaultKeyField = keyField;
			if(this.defaultKeyField == null){
				logger.error("the cache value does not have a default cache key");
				throw new NoCacheableException(this.vClazz);
			}
		}
		K k;
		try {
			Object object = this.defaultKeyField.findValue(value);
			if(object == null){
				logger.error("the cache value default cache key is null");
				throw new NoCacheableException(this.vClazz);
			}
			k = (K)object;
		} catch (IllegalArgumentException e) {
			logger.error("the cache value does not have a default cache key, exception : {} ", e);
			throw new NoCacheableException(this.vClazz);
		} 
		return k;
	}
	
	private boolean hasCacheKeyAnnotation(Field field){
		return hasFiledAnnotation(field, CacheField.class) && !hasFiledAnnotation(field, DefaultCacheKey.class);
	}
	
	private <T> Map<Integer,Object> findClassFields(List<ClassField> fields,Class<?> clazz){
		Map<Integer, Object> map = Maps.newHashMap();
		List<KeyField> keyFields = Lists.newArrayList();
		List<ClassField> classFields = Lists.newArrayList();
		for(ClassField classField : fields){
			Field field = classField.getField();
			if(hasCacheKeyAnnotation(field)){
				CacheField cacheField = field.getDeclaredAnnotation(CacheField.class);
				if(cacheField.classCache()){
					field.getType();
					Class<?> innerClazz = field.getType();
					map.put(2, innerClazz);
					Method method = findMethod(field, clazz);
					Field[] innerFields = innerClazz.getDeclaredFields();
					for(Field innerField : innerFields){
						List<Method> fieldMethods = Lists.newArrayList(classField.getMethods());
						fieldMethods.add(method);
						ClassField innerClassField = new ClassField(innerField, clazz, fieldMethods);
						classFields.add(innerClassField);
					}
				}
				else {
					List<Method> fieldMethods = Lists.newArrayList(classField.getMethods());
					fieldMethods.add(findMethod(field, clazz));
					KeyField keyField = new KeyField(new ClassField(field, clazz,fieldMethods));
					keyFields.add(keyField);
				}
			}
		}
		map.put(0, keyFields);
		map.put(1, classFields);
		return map;
	}
	
	@SuppressWarnings("unchecked")
	private <T> List<KeyField> findKeyField(List<Field> fields, Class<?> clazz) {
		List<KeyField> keyFields = Lists.newArrayList();
		List<ClassField> list = Lists.newArrayList();
		for(Field field : fields){
			List<Method> methods = Lists.newArrayList();
			ClassField classField = new ClassField(field, clazz, methods);
			list.add(classField);
		}
		Class<?> filedClazz = clazz;
		for(int i=0;i < MAX_DEGREE;i++){
			Map<Integer, Object> map = findClassFields(list, filedClazz);
			List<ClassField> listClassField = (List<ClassField>)map.get(1);
			List<KeyField> listKeyField = (List<KeyField>)map.get(0);
			if(listKeyField.size() > 0){
				keyFields.addAll(listKeyField);
			}
			if(listClassField.size() == 0){
				break;
			}
			list = listClassField;
			Class<?> innerClazz = (Class<?>)map.get(2);
			filedClazz = innerClazz;
		}
		return keyFields;
	}
	
	protected List<String> findCacheKeys(V value){
		if(this.keyFields == null){
			Class<? super V> clazz = this.vClazz;
			List<KeyField> keyFields = Lists.newArrayList();
			for(int i=0;i<MAX_DEGREE;i++){
				if(clazz.getSimpleName().equals(OBJECT_CLASSNAME)){
					break;
				}
				Field[] fields = clazz.getDeclaredFields();
				List<Field> listField = Lists.newArrayList(fields);
				List<KeyField> listKeyField = findKeyField(listField,clazz);
				if(listKeyField.size() > 0){
					keyFields.addAll(listKeyField);
				}
				clazz = clazz.getSuperclass();
			}
			this.keyFields = keyFields;
		}
		List<String> cacheKeys = Lists.newArrayListWithCapacity(this.keyFields.size());
		for(KeyField keyField : keyFields){
			String key = buildKey(keyField, value);
			if(key != null){
				cacheKeys.add(key);
			}
		}
		return cacheKeys;
	}
	
	protected<T> String buildKey(String key,T t){
		StringBuilder builder = new StringBuilder();
		builder.append(key).append(":");
		builder.append(t.toString());
		return builder.toString();
	}
	
	private<T> String buildKey(KeyField keyField,T t){
		StringBuilder builder = new StringBuilder();
		String key = keyField.findKeyFieldName(t);
		if(key == null){
			return  null;
		}
		builder.append(key);
		return builder.toString();
	}
	
	
	private <T> Method findMethod(Field field,Class<T> clazz){
		return findPropertyDescriptor(field, clazz).getReadMethod();
	}
	
	private <T> PropertyDescriptor findPropertyDescriptor(Field field,Class<T> clazz){
		PropertyDescriptor descriptor;
		try {
			descriptor = new PropertyDescriptor(field.getName(), clazz);
		} catch (IntrospectionException e) {
			logger.error("the cache value does not have a default get or set for cache key, exception : {} ", e);
			throw new NoCacheableException(clazz);
		}
		return descriptor;
	}
	
//	private Map<Field, PropertyDescriptor> descriptors = Maps.newHashMap();
	

	class KeyField{
		
		private ClassField classField;
		
		private KeyField keyField;
		
		public KeyField(ClassField classField) {
			this.classField = classField;
		}

		public boolean hasNextKeyField(){
			return this.keyField != null;
		}
		
		private String findCacheKey(Field field){
			CacheField cacheField = field.getDeclaredAnnotation(CacheField.class);
			if ("".equals(cacheField.cacheKey())) {
				return field.getName();
			}
			return cacheField.cacheKey();
		}
		
		public <T> String findKeyFieldName(T value) throws NoCacheableException{
			StringBuilder builder = new StringBuilder();
			KeyField keyField = this;
			Object object = value;
			for(int i=0;i<MAX_DEGREE;i++){
				if(keyField.hasNextKeyField()){
					keyField = keyField.getKeyField();
					object = keyField.getClassField().findValue(object);
					if(object == null){
						return null;
					}
				}
				else {
					builder.append(findCacheKey(keyField.getClassField().getField())).append(":");
					Object v = keyField.getClassField().findValue(object);
					if(v == null){
						return null;
					}
					builder.append(v.toString());
					break;
				}
			}
			return builder.toString();
		}
		
		public ClassField getClassField() {
			return classField;
		}

		public void setClassField(ClassField classField) {
			this.classField = classField;
		}

		public KeyField getKeyField() {
			return keyField;
		}
		public void setKeyField(KeyField keyField) {
			this.keyField = keyField;
		}
	}
	
	
	class ClassField{
		
		private Field field;
		
		private Class<?> clazz;
		
		/**
		 * find value instance by class methods
		 */
		private List<Method> methods;

		public ClassField(Field field, Class<?> clazz, List<Method> methods) {
			this.field = field;
			this.clazz = clazz;
			this.methods = methods;
		}
		
		public <T> Object findValue(T value){
			Object object = value;
			for(Method method : this.methods){
				try {
					if (object == null){
						return null;
					}
					object = method.invoke(object);
				} catch (IllegalAccessException e) {
					logger.error("the cache value does not have a default cache key, exception : {} ", e);
					throw new NoCacheableException(object.getClass());
				} catch (IllegalArgumentException e) {
					logger.error("the cache value does not have a default cache key, exception : {} ", e);
					throw new NoCacheableException(object.getClass());
				} catch (InvocationTargetException e) {
					logger.error("the cache value does not have a default get or set for cache key, exception : {} ", e);
					throw new NoCacheableException(object.getClass());
				}
			}
			return object;
		}
		

		public Field getField() {
			return field;
		}

		public void setField(Field field) {
			this.field = field;
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public void setClazz(Class<?> clazz) {
			this.clazz = clazz;
		}

		public List<Method> getMethods() {
			return methods;
		}

		public void setMethods(List<Method> methods) {
			this.methods = methods;
		}
		
	}
	
}
