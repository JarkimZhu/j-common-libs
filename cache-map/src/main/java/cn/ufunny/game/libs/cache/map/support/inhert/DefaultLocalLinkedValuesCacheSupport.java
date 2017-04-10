package cn.ufunny.game.libs.cache.map.support.inhert;

import cn.ufunny.game.libs.cache.inherit.ValueIdentifiable;
import cn.ufunny.game.libs.cache.support.inhert.AbstractImmutableValuesCacheSupport;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.*;


/**
 *  cache by TreeMap
 *  default order   ascending
 *  find achieve  for(;;i++)  bigger then next
 *  
 * @author ryan
 *
 * @param <K>
 * @param <V>
 */
public abstract class DefaultLocalLinkedValuesCacheSupport<K extends Comparable<K>,T,V extends ValueIdentifiable<K,T>> extends AbstractImmutableValuesCacheSupport<K,T, V> {


	protected TreeMap<K, HashMap<T, V>> cache = Maps.newTreeMap();

	
	/**
	 * return first value if not found
	 */
	@Override
	public List<V> find(K key) {
		checkExpire();
		List<V> vs = Lists.newArrayList(cache.firstEntry().getValue().values());
		for(Map.Entry<K, HashMap<T, V>> entry : cache.entrySet()){
			if(key.compareTo(entry.getKey()) >= 0){
				vs = Lists.newArrayList(entry.getValue().values());
			}
			else {
				break;
			}
		}
		return vs;
	}

	public List<V> findDesc(K key) {
		checkExpire();
		List<V> vs = Lists.newArrayList(cache.lastEntry().getValue().values());
		for(Map.Entry<K, HashMap<T, V>> entry : cache.entrySet()){
			if(key.compareTo(entry.getKey()) >= 0){
				vs = Lists.newArrayList(entry.getValue().values());
			}
			else {
				break;
			}
		}
		return vs;
	}

	@Override
	public V findByValueKey(K key, T valueKey) {
		if(!hasValueKey(key, valueKey)){
			return null;
		}
		return cache.get(key).get(valueKey);
	}
	
	/**
	 * return list does not contains value if not contain key
	 */
	@Override
	public List<V> findByKeys(Collection<K> keys) {
		List<V> vs = Lists.newArrayList();
		for(K key : keys){
			if(hasKey(key)){
				List<V> list = Lists.newArrayList(cache.get(key).values());
				vs.addAll(list);
			}
			
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
		TreeMap<K, HashMap<T, V>>  map = Maps.newTreeMap();
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
			}
		}
		cache = map;
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
	public boolean hasKey(K key) {
		checkExpire();
		return cache.containsKey(key);
	}

	@Override
	public boolean hasFieldKey(String key) {
		checkExpire();
		return false;
	}

	@Override
	public boolean hasValueKey(K key, T valueKey) {
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
