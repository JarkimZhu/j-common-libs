package cn.ufunny.game.libs.cache.ehcache.support.inhert;

import cn.ufunny.game.libs.cache.ehcache.support.EhCacheManager;
import cn.ufunny.game.libs.cache.ehcache.support.Ehcacheable;
import cn.ufunny.game.libs.cache.ehcache.support.ValueEhCacheWrapper;
import cn.ufunny.game.libs.cache.ehcache.support.expire.BaseExpirableCacheSupport;
import cn.ufunny.game.libs.cache.ehcache.support.expire.CacheExpire;
import cn.ufunny.game.libs.cache.iface.ICacheObject;
import cn.ufunny.game.libs.cache.inherit.Identifiable;
import cn.ufunny.game.libs.cache.support.inhert.AbstractAlterableValueCacheSupport;
import net.sf.ehcache.Cache;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author ryan
 *
 * version 1.0
 *
 * @param <K>
 * @param <V>
 */
public abstract class EhAlterableValueCacheSupport<K, V extends Identifiable<K>>
		extends AbstractAlterableValueCacheSupport<K, V> implements Ehcacheable,BaseExpirableCacheSupport {
	
	@Resource
	protected EhCacheManager ehCacheManager;

	private ValueEhCacheWrapper<K,V> wrapper;


	@Override
	public void initCache() {
		String cacheName = findCacheName();
		if(cacheName == null){
			cacheName = vClazz.getSimpleName();
		}
		Cache cache = ehCacheManager.getCacheManager().getCache(cacheName);
		if(cache == null){
			ehCacheManager.getCacheManager().addCache(cacheName);
			cache = ehCacheManager.getCacheManager().getCache(cacheName);
		}
		long expireTime = 0;
		boolean updateExpire = true;
		CacheExpire cacheExpire = this.getClass().getDeclaredAnnotation(CacheExpire.class);
		if(cacheExpire != null){
			expireTime = cacheExpire.expireTime();
			updateExpire = cacheExpire.updateExpire();
		}
		wrapper = new ValueEhCacheWrapper<>(cache,kClazz,vClazz,expireTime,updateExpire);
	}


	@Override
	public V find(K key) {
		return wrapper.findByKey(key);
	}

	protected V findByNameKey(String key){
		return wrapper.findByKey(key);
	}

	protected boolean removeNameKey(String key){
		return wrapper.removeByKey(key);
	}

	@Override
	public List<V> findAll() {
		return wrapper.findAll();
	}

	@Override
	public List<V> findByKeys(Collection<K> keys) {
		return wrapper.findByKeys(keys);
	}
	
	/**
	 * 
	 * if V has change, put must be called
	 */
	@Override
	public boolean put(V value) {
		if(value == null){
			return false;
		}
		K k = value.findIdentify();
		boolean success = wrapper.putByKey(k,value);
		List<String> keys = value.findFieldIdentifies();
		if(keys != null){
			for(String key : keys){
				wrapper.putByKey(key,value);
			}
		}
		return success;
	}

	@Override
	public boolean putIfAbsent(V value) {
		if(value == null){
			return false;
		}
		K k = value.findIdentify();
		boolean success = wrapper.putIfAbsentByKey(k,value);
		List<String> keys = value.findFieldIdentifies();
		if(keys != null){
			for(String key : keys){
				wrapper.putIfAbsentByKey(key,value);
			}
		}
		return success;
	}
	
	@Override
	public <E> boolean putByKey(E key, V value) {
		return wrapper.putByKey(key,value);
	}

	@Override
	public <E> boolean putIfAbsentByKey(E key, V value) {
		return wrapper.putIfAbsentByKey(key,value);
	}

	@Override
	public boolean putAll(Collection<V> values) {
		if(values == null){
			return false;
		}
		boolean success = true;
		for(V value : values){
			success = put(value) && success;
		}
		return success;
	}

	@Override
	public boolean putAllIfAbsent(Collection<V> values) {
		if(values == null){
			return false;
		}
		boolean success = true;
		for(V value : values){
			success = putIfAbsent(value) && success;
		}
		return success;
	}
	
	@Override
	public boolean remove(V value) {
		if(value == null){
			return false;
		}
		K k = value.findIdentify();
		boolean success = wrapper.removeByKey(k);
		List<String> keys = value.findFieldIdentifies();
		if(keys != null){
			for(String key : keys){
				success = wrapper.removeByKey(key);
			}
		}
		return success;
	}

	@Override
	public boolean removeByKey(K key) {
		return wrapper.removeByKey(key);
	}

	public boolean removeKeyValue(K key) {
		if(key == null){
			return false;
		}
		V value = find(key);
		if(value == null){
			return removeByKey(key);
		}
		else {
			return remove(value);
		}
	}

	@Override
	public boolean removeByKeys(Collection<K> keys) {
		return wrapper.removeByKeys(keys);
	}

	@Override
	public boolean removeAll(Collection<V> values) {
		if(values == null){
			return false;
		}
		boolean success = true;
		for(V value : values){
			success = remove(value) && success;
		}
		return success;
	}

	public boolean removeKeyValues(Collection<K> keys) {
		if(keys == null){
			return false;
		}
		boolean success = false;
		for(K key : keys){
			success = removeKeyValue(key) && success;
		}
		return success;
	}
	
	@Override
	public boolean clean() {
		return wrapper.clean();
	}
	
	@Override
	public boolean hasKey(K key) {
		return wrapper.hasKey(key);
	}

	@Override
	public boolean hasFieldKey(String key) {
		return wrapper.hasKey(key);
	}

	@Override
	public Collection<ICacheObject<V>> values() {
		return wrapper.values();
	}

	@Override
	public int size() {
		return wrapper.size();
	}

	public List<K> findKeys(){
		return wrapper.findKeys();
	}

	public <E> boolean hasKeyValueChange(E key){
		return wrapper.hasKeyValueChange(key);
	}

	public void markKey(K key){
		this.wrapper.markKey(key);
	}

	public boolean hasMarkKey(K key){
		return this.wrapper.hasMarkKey(key);
	}

	public void setEhCacheManager(EhCacheManager ehCacheManager) {
		this.ehCacheManager = ehCacheManager;
	}

	@Override
	public void removeUnChangeExpireData() {
		wrapper.removeUnChangeExpireData();
	}

}
