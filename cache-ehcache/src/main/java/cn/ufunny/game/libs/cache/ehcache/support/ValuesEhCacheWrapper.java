package cn.ufunny.game.libs.cache.ehcache.support;

import cn.ufunny.game.libs.cache.ehcache.model.ElementCacheObject;
import cn.ufunny.game.libs.cache.ehcache.model.ElementCacheObjects;
import cn.ufunny.game.libs.cache.exception.CacheKeyIsNullException;
import cn.ufunny.game.libs.cache.iface.ICacheObject;
import cn.ufunny.game.libs.cache.model.CacheObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ryan on 2015/12/31.
 */
public class ValuesEhCacheWrapper<K,T,V> {

    private Class<K> kClazz;

    private Class<T> tClazz;

    private Class<V> vClazz;

    private Cache cache;

    private int expectedSize = 4;

    private long expireTime;
    private boolean updateExpire;


    public ValuesEhCacheWrapper(Cache cache,Class<K> kClazz,Class<T> tClass,Class<V> vClazz,
                                int expectedSize,long expireTime,boolean updateExpire){
        this.kClazz = kClazz;
        this.tClazz = tClass;
        this.vClazz = vClazz;
        this.cache = cache;
        if(expectedSize > 0){
            this.expectedSize = expectedSize;
        }
        if(expireTime > 0){
            this.expireTime = expireTime;
        }
        this.updateExpire = updateExpire;
    }


    private List<V> findValues(ElementCacheObjects<T, V> objects){
        Map<T, CacheObject<V>> map = objects.findObjects();
        if(map == null){
            return null;
        }
        List<V> values = Lists.newArrayList();
        for(Map.Entry<T, CacheObject<V>> entry : map.entrySet()){
            values.add(entry.getValue().getData());
        }
        return values;
    }

    public <E> List<V> findByKey(E key) {
        Element element = cache.get(key);
        if(element == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        ElementCacheObjects<T, V> objects = (ElementCacheObjects<T, V>) element;
        return findValues(objects);
    }

    public <E> V findByValueKey(E key, T valueKey) {
        Element element = cache.get(key);
        if(element == null){
            return null;
        }
        @SuppressWarnings("unchecked")
        ElementCacheObjects<T, V> objects = (ElementCacheObjects<T, V>)element;
        Map<T, CacheObject<V>> map = objects.findObjects();
        if(map == null){
            return null;
        }
        CacheObject<V> cacheObject = map.get(valueKey);
        if(cacheObject == null){
            return null;
        }
        return cacheObject.getData();

    }

    public <E> List<V> findByValueKeys(E key, Collection<T> valueKeys) {
        Element element = cache.get(key);
        if(element == null){
            return null;
        }
        @SuppressWarnings("unchecked")
        ElementCacheObjects<T, V> objects = (ElementCacheObjects<T, V>)element;
        Map<T, CacheObject<V>> map = objects.findObjects();
        if(map == null){
            return null;
        }
        List<V> vs = Lists.newArrayListWithCapacity(valueKeys.size());
        for(T valueKey : valueKeys){
            CacheObject<V> cacheObject = map.get(valueKey);
            if(cacheObject != null){
                vs.add(cacheObject.getData());
            }
        }
        return vs;
    }

    public List<V> findAll() {
        return findByKeys(findKeys());
    }

    public <E> List<V> findByKeys(Collection<E> keys) {
        Map<Object, Element> all = cache.getAll(keys);
        List<V> result = Lists.newArrayList();
        for (Element element : all.values()) {
            if(element != null){
                @SuppressWarnings("unchecked")
                ElementCacheObjects<T, V> objects = (ElementCacheObjects<T, V>)element;
                List<V> vs = findValues(objects);
                if(vs  != null){
                    result.addAll(vs);
                }
            }
        }
        return result;
    }

    /**
     * lock k if k is not in cache when put value
     */
    private ConcurrentHashMap<Object,Object> inPuts = new ConcurrentHashMap<>();

    private <E> void putEhcacheKey(E key){
        if(key == null){
            throw new CacheKeyIsNullException();
        }
        Object lock = inPuts.get(key);
        if(lock == null){
            lock = key;
            inPuts.put(key,lock);
        }
        synchronized (lock){
            Element element = cache.get(key);
            if(element == null || element.getObjectValue() == null){
                Map<T, CacheObject<V>> map;
                if(cache.getCacheConfiguration().getMaxEntriesLocalHeap() == 0){
                    map = Maps.newHashMapWithExpectedSize(expectedSize);
                }
                else {
                    map = new ConcurrentHashMap<>();
                }
                ElementCacheObjects<T, V> objects = new ElementCacheObjects<>(key, map);
                cache.put(objects);
            }
            inPuts.remove(key);
        }
    }

    private boolean putElementValue(Element element,T valueKey,V value){
        if(valueKey == null){
            return false;
        }
        @SuppressWarnings("unchecked")
        ElementCacheObjects<T, V> objects = (ElementCacheObjects<T, V>)element;
        Map<T, CacheObject<V>> map = objects.findObjects();
        CacheObject<V> object = new CacheObject<>(value);
        object.change();
        map.put(valueKey, object);
        cache.put(objects);
        return true;
    }

    public  <E> boolean putEhcacheValue(E key,T valueKey,V value){
        Element element = cache.get(key);
        if(element == null || element.getObjectValue() == null){
            putEhcacheKey(key);
            element = cache.get(key);
        }
        return putElementValue(element,valueKey,value);
    }

    private boolean putIfAbsentElementValue(Element element,T valueKey,V value){
        if(valueKey == null) {
            return false;
        }
        @SuppressWarnings("unchecked")
        ElementCacheObjects<T, V> objects = (ElementCacheObjects<T, V>)element;
        Map<T, CacheObject<V>> map = objects.findObjects();
        if(!map.containsKey(valueKey)){
            CacheObject<V> object = new CacheObject<>(value);
            object.change();
            map.put(valueKey, object);
            cache.put(objects);
            return true;
        }
        return false;
    }

    public <E> boolean putIfAbsentEhcacheValue(E key,T valueKey,V value){
        Element element = cache.get(key);
        if(element == null || element.getObjectValue() == null){
            putEhcacheKey(key);
            element = cache.get(key);
        }
        return putIfAbsentElementValue(element,valueKey,value);
    }

    public <E> boolean removeByKey(E key) {
        return cache.remove(key);
    }

    public  <E> boolean removeByValueKey(E key,T valueKey){
        if(valueKey == null){
            return false;
        }
        Element element = cache.get(key);
        if(element == null) {
            return false;
        }
        @SuppressWarnings("unchecked")
        ElementCacheObjects<T, V> objects = (ElementCacheObjects<T, V>)element;
        Map<T, CacheObject<V>> map = objects.findObjects();
        if(map == null){
            return false;
        }
        CacheObject<V> old = map.remove(valueKey);
        return old == null;
    }

    public <E> boolean removeByKeys(Collection<E> keys) {
        if(keys == null){
            return false;
        }
        boolean success = true;
        for(E key : keys){
            success = removeByKey(key) && success;
        }
        return success;
    }


    public boolean clean() {
        cache.removeAll();
        return true;
    }

    public <E> boolean hasKey(E key) {
        return cache.isKeyInCache(key);
    }

    public <E> boolean hasValueKey(E key, T valueKey) {
        Element element = cache.get(key);
        if(element == null) {
            return false;
        }
        @SuppressWarnings("unchecked")
        ElementCacheObjects<T, V> objects = (ElementCacheObjects<T, V>)element;
        Map<T, CacheObject<V>> map = objects.findObjects();
        return map.containsKey(valueKey);
    }

    private <E> boolean hasKeyType(E key){
        return key.getClass().getSimpleName().equals(this.kClazz.getSimpleName());
    }

    private <E> boolean hasValueKeyType(E valueKey){
        return valueKey.getClass().getSimpleName().equals(this.tClazz.getSimpleName());
    }

    private <E> boolean hasValueType(E value){
        return value.getClass().getSimpleName().equals(this.vClazz.getSimpleName());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Collection<ICacheObject<V>> values() {
        Map<Object, Element> all = cache.getAll(findKeys());
        if(all == null){
            return null;
        }
        List<ICacheObject<V>> result = Lists.newArrayList();
        for (Element element : all.values()) {
            ElementCacheObjects objects = (ElementCacheObjects)element;
            Map<T, CacheObject<V>> map = objects.findObjects();
            if(map != null){
                Collection<CacheObject<V>> vs = map.values();
                result.addAll(vs);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<K> findKeys(){
        List<K> keys = Lists.newArrayList();
        List<Object> ks = cache.getKeys();
        for(Object k : ks){
            if(hasKeyType(k)){
                keys.add((K)k);
            }
        }
        return keys;
    }

    public int size(){
        return cache.getSize();
    }


    public <E> boolean hasKeyValueChange(E key){
        Element element = cache.get(key);
        if(element == null){
            return false;
        }
        @SuppressWarnings("unchecked")
        ElementCacheObjects<T, V> objects = (ElementCacheObjects<T, V>) element;
        Map<T, CacheObject<V>> map = objects.findObjects();
        if(map == null){
            return false;
        }
        boolean change = false;
        for( CacheObject<V> object : map.values()){
            if(object.hasChange()){
                change = true;
                break;
            }
        }
        return change;
    }

    public void markKey(K key){
        ElementCacheObjects element = (ElementCacheObjects) cache.get(key);
        if(element == null){
            element = new ElementCacheObjects<>(key, (Map)null);
            element.mark();
            ElementCacheObjects old = (ElementCacheObjects)cache.putIfAbsent(element);
            if(old != null){
                old.mark();
            }
        }
        else {
            element.mark();
        }
    }

    public boolean hasMarkKey(K key){
        ElementCacheObjects element = (ElementCacheObjects) cache.get(key);
        if(element == null){
            return false;
        }
        return element.hasMark();
    }

    public void removeUnChangeExpireData(){
        if(this.expireTime == 0){
            return;
        }
        List<Object> ks = cache.getKeys();
        Map<Object, Element> all = cache.getAll(ks);
        if(all != null){
            for(Element element : all.values()){
                ElementCacheObjects cacheObject = (ElementCacheObjects)element;
                if(cacheObject.hasExpired(this.expireTime,this.updateExpire)){
                    cache.remove(cacheObject.getObjectKey());
                }
            }
        }
    }

}
