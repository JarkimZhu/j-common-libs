package cn.ufunny.game.libs.cache.map.support.annotation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.ufunny.game.libs.cache.support.annotation.AbstractImmutableValueCacheSupport;


/**
 * @author ryan
 *
 * @param <K>
 * @param <V>
 */
public abstract class DefaultLocalHashValueCacheSupport<K,V> extends AbstractImmutableValueCacheSupport<K, V> {

	private Map<K, V> cache = Maps.newHashMap();

	private Map<String, K> keyCache = Maps.newHashMap();


	@Override
	public V find(K key) {
		checkExpire();
		return cache.get(key);
	}

	/**
	 * find cache value by name key
	 * @param name
	 * @param t
	 * @return
	 */
	protected <T> V findByNameKey(String name,T t){
		checkExpire();
		String key = buildKey(name, t);
		if(keyCache.containsKey(key)){
			K k = keyCache.get(key);
			return find(k);
		}
		return null;
	}

	@Override
	public List<V> findAll() {
		checkExpire();
		List<V> vs = Lists.newArrayList(cache.values());
		return vs;
	}

	@Override
	public List<V> findByKeys(Collection<K> keys) {
		List<V> vs = Lists.newArrayListWithCapacity(keys.size());
		for(K key : keys){
			vs.add(find(key));
		}
		return vs;
	}

	@Override
	public boolean reload(){
		synchronized (this) {
			return initCache();
		}
	}

	private boolean selfInit(List<V> vs){
		if(vs == null || vs.size() == 0){
			return false;
		}
		HashMap<K, V> map = Maps.newHashMap();
		HashMap<String, K> keyMap = Maps.newHashMap();
		for(V v : vs){
			K k = findDefaultKey(v);
			if(k != null){
				map.put(k, v);
				List<String> keys = findCacheKeys(v);
				for(String key : keys){
					keyMap.put(key, k);
				}
			}
		}
		this.cache = map;
		this.keyCache = keyMap;
		return true;
	}

	private boolean initCache(){
		List<V> vs = loadAllFromDatabase();
		boolean success = selfInit(vs);
		customInit(vs);
		return success;
	}

	@Override
	protected void customInit(List<V> vs) {
	}

	@Override
	public boolean hasKey(K key){
		checkExpire();
		return  cache.containsKey(key);
	}


	@Override
	public boolean hasFieldKey(String key) {
		checkExpire();
		return keyCache.containsKey(key);
	}

	@Override
	public String findCacheName() {
		return this.getClass().getSimpleName();
	}


	public int size(){
		checkExpire();
		return cache.size();
	}

}
