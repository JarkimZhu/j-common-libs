package cn.ufunny.game.libs.cache.map.support.inhert;

import cn.ufunny.game.libs.cache.inherit.ValueIdentifiable;
import cn.ufunny.game.libs.cache.support.inhert.AbstractImmutableValuesCacheSupport;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author ryan
 *
 * @param <K>
 * @param <V>
 */
public abstract class DefaultLocalHashValuesCacheSupport<K,T,V extends ValueIdentifiable<K,T>> extends AbstractImmutableValuesCacheSupport<K, T,V> {
	
	private  Map<K, HashMap<T, V>> cache = Maps.newHashMap();

	private Map<String, K> keyCache = Maps.newHashMap();

	@Override
	public List<V> find(K key) {
		checkExpire();
		if(cache.containsKey(key)){
			return Lists.newArrayList(cache.get(key).values());
		}
		return null;
	}

	@Override
	public V findByValueKey(K key, T valueKey) {
		checkExpire();
		if(hasValueKey(key, valueKey)){
			return cache.get(key).get(valueKey);
		}
		return null;
	}

	@Override
	public List<V> findByKeys(Collection<K> keys) {
		List<V> vs = Lists.newArrayList();
		for(K key : keys){
			vs.addAll(find(key));
		}
		return vs;
	}

	@Override
	public List<V> findAll() {
		checkExpire();
		List<V> vs = Lists.newArrayList();
		for(Map.Entry<K, HashMap<T, V>> entry : cache.entrySet()){
			vs.addAll(entry.getValue().values());
		}
		return vs;
	}
	
	/**
	 * find cache value by name key
	 * @return
	 */
	protected <E> List<V> findByNameKey(String key){
		checkExpire();
		if(keyCache.containsKey(key)){
			K k = keyCache.get(key);
			return find(k);
		}
		return null;
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
		HashMap<K, HashMap<T, V>> map = Maps.newHashMap();
		HashMap<String, K> keyMap = Maps.newHashMap();
		for(V v : vs){
			K k = v.findIdentify();
			if(k != null){
				if(!map.containsKey(k)){
					HashMap<T, V> valueMap = Maps.newHashMap();
					map.put(k, valueMap);

				}
				HashMap<T, V> valueMap = map.get(k);
				T t = v.findValueIdentify();
				valueMap.put(t, v);
				List<String> keys = v.findFieldIdentifies();
				if(keys != null){
					for(String key : keys){
						keyMap.put(key, k);
					}
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
	public String findCacheName() {
		return this.getClass().getSimpleName();
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
	public boolean hasValueKey(K key, T valueKey) {
		checkExpire();
		if(!hasKey(key)){
			return false;
		}
		return cache.get(key).containsKey(valueKey);
	}

	public int size(){
		checkExpire();
		return cache.size();
	}
	
}
