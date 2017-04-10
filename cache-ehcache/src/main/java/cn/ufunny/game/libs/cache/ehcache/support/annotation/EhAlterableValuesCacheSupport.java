package cn.ufunny.game.libs.cache.ehcache.support.annotation;


import cn.ufunny.game.libs.cache.ehcache.support.EhCacheManager;
import cn.ufunny.game.libs.cache.ehcache.support.Ehcacheable;
import cn.ufunny.game.libs.cache.ehcache.support.ValuesEhCacheWrapper;
import cn.ufunny.game.libs.cache.ehcache.support.expire.BaseExpirableCacheSupport;
import cn.ufunny.game.libs.cache.ehcache.support.expire.CacheExpire;
import cn.ufunny.game.libs.cache.iface.ICacheObject;
import cn.ufunny.game.libs.cache.support.annotation.AbstractAlterableValuesCacheSupport;
import net.sf.ehcache.Cache;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author ryan
 *
 * @param <K>
 * @param <T>
 * @param <V>
 */
public abstract class EhAlterableValuesCacheSupport<K, T, V > extends AbstractAlterableValuesCacheSupport<K,T, V>
		implements Ehcacheable,BaseExpirableCacheSupport {

	@Resource
	private EhCacheManager ehCacheManager;

	private ValuesEhCacheWrapper<K,T,V> wrapper;

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
		wrapper = new ValuesEhCacheWrapper<>(cache,kClazz,tClazz,vClazz,findExpectedSize(),expireTime,updateExpire);
	}


	protected int findExpectedSize(){
		return 6;
	}

	@Override
	public List<V> find(K key) {
		return wrapper.findByKey(key);
	}

	protected <E> List<V> findByNameKey(String name,E t){
		String key = buildKey(name, t);
		return wrapper.findByKey(key);
	}
	
	@Override
	public V findByValueKey(K key, T valueKey) {
		return wrapper.findByValueKey(key,valueKey);
	}

	@Override
	public List<V> findByValueKeys(K key, Collection<T> valueKeys) {
		return wrapper.findByValueKeys(key, valueKeys);
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
	 * if V has change, put must be called 
	 */
	@Override
	public boolean put(V value) {
		K key = findDefaultKey(value);
		T valueKey = findValueKey(value);
		return wrapper.putEhcacheValue(key,valueKey,value);
	}

	@Override
	public boolean putIfAbsent(V value) {
		K key = findDefaultKey(value);
		T valueKey = findValueKey(value);
		return wrapper.putIfAbsentEhcacheValue(key,valueKey,value);
	}

	@Override
	public <E> boolean putByKey(E key, V value) {
		T valueKey = findValueKey(value);
		return wrapper.putIfAbsentEhcacheValue(key,valueKey,value);
	}

	@Override
	public <E> boolean putIfAbsentByKey(E key, V value) {
		T valueKey = findValueKey(value);
		return wrapper.putIfAbsentEhcacheValue(key,valueKey,value);
	}

	@Override
	public boolean putByValueKey(K key, T valueKey,V value) {
		return wrapper.putEhcacheValue(key,valueKey,value);
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
	public boolean removeByKey(K key) {
		return wrapper.removeByKey(key);
	}

	@Override
	public boolean remove(V value) {
		K key = findDefaultKey(value);
		T valueKey = findValueKey(value);
		boolean success = wrapper.removeByValueKey(key,valueKey);
		List<String> cacheKeys = findCacheKeys(value);
		if(cacheKeys != null){
			for(String cacheKey : cacheKeys){
				wrapper.removeByValueKey(cacheKey, valueKey);
			}
		}
		return success;
	}

	public boolean removeKeyValue(K key) {
		if(key == null){
			return false;
		}
		List<V> vs = find(key);
		if(vs == null){
			return removeByKey(key);
		}
		else {
			return removeAll(vs);
		}
	}

	@Override
	public boolean removeByValueKey(K key, T valueKey) {
		return wrapper.removeByValueKey(key,valueKey);
	}

	@Override
	public boolean removeByKeys(Collection<K> keys) {
		return wrapper.removeByKeys(keys);
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
	public boolean hasValueKey(K key, T valueKey) {
		return wrapper.hasValueKey(key,valueKey);
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
