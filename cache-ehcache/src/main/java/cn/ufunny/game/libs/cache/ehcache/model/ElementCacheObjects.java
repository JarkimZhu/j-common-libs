
package cn.ufunny.game.libs.cache.ehcache.model;


import cn.ufunny.game.libs.cache.ehcache.support.mark.IMarker;
import cn.ufunny.game.libs.cache.ehcache.support.expire.Expirable;
import cn.ufunny.game.libs.cache.model.CacheObject;
import net.sf.ehcache.Element;

import java.util.Map;

/**
 *
 * @param <K>
 * @param <V>
 */
public class ElementCacheObjects<K, V> extends Element implements IMarker,Expirable {

	/**
	 * Auto generate
	 */
	private static final long serialVersionUID = -6270032866556361095L;

	private volatile boolean mark;


	public ElementCacheObjects(Object key, Map<K, CacheObject<V>> values) {
		super(key, values);
	}


	@SuppressWarnings("unchecked")
	public Map<K, CacheObject<V>> findObjects(){
		return (Map<K, CacheObject<V>>)this.getObjectValue();
	}

	@Override
	public boolean hasMark() {
		return this.mark;
	}

	@Override
	public void mark() {
		this.mark = true;
	}


	@Override
	public boolean hasExpired(long expireTime,boolean update) {
		if(hasChange()){
			return false;
		}
		return hasTimeExpired(expireTime,update);
	}

	private boolean hasTimeExpired(long expireTime,boolean update){
		if(update){
			return this.getLastUpdateTime() + expireTime <= System.currentTimeMillis();
		}
		return this.getLastAccessTime() + expireTime <= System.currentTimeMillis();
	}

	private boolean hasChange(){
		Map<K, CacheObject<V>> map = findObjects();
		if(map == null){
			return false;
		}
		else {
			boolean change = false;
			for(CacheObject cacheObject : map.values()){
				if(cacheObject.hasChange()){
					change = true;
					break;
				}
			}
			return change;
		}
	}

}
