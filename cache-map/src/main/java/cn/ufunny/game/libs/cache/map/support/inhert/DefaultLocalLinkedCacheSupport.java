package cn.ufunny.game.libs.cache.map.support.inhert;

import cn.ufunny.game.libs.cache.inherit.Identifiable;
import cn.ufunny.game.libs.cache.support.inhert.AbstractImmutableValueCacheSupport;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


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
public abstract class DefaultLocalLinkedCacheSupport<K extends Comparable<K>,V extends Identifiable<K>> extends AbstractImmutableValueCacheSupport<K, V> {
	
	
	protected TreeMap<K, V> cache = Maps.newTreeMap();

	/**
	 * return first value if not found
	 */
	@Override
	public V find(K key) {
		checkExpire();
		V v = cache.firstEntry().getValue();
		for(Map.Entry<K, V> entry : cache.entrySet()){
			if(key.compareTo(entry.getKey()) >= 0){
				v = entry.getValue();
			}
			else {
				break;
			}
		}
		return v;
	}

	public V findDesc(K key) {
		checkExpire();
		V v = cache.lastEntry().getValue();
		for(Map.Entry<K, V> entry : cache.entrySet()){
			if(key.compareTo(entry.getKey()) >= 0){
				v = entry.getValue();
			}
			else {
				break;
			}
		}
		return v;
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
		TreeMap<K, V> map = Maps.newTreeMap();
		for(V v : vs){
			K k = v.findIdentify();
			if(k!= null){
				map.put(k, v);
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
	public List<V> findAll() {
		if(hasExpire()){
			load();
		}
		List<V> vs = Lists.newArrayList();
		vs.addAll(cache.values());
		return vs;
	}

	/**
	 * return list does not contains value if not contain key
	 */
	@Override
	public List<V> findByKeys(Collection<K> keys) {
		List<V> vs = Lists.newArrayListWithCapacity(keys.size());
		for(K key : keys){
			V v = cache.get(key);
			if(v != null){
				vs.add(v);
			}
		}
		return vs;
	}

	public int size(){
		checkExpire();
		return cache.size();
	}
	
}
