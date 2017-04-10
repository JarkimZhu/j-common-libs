
package cn.ufunny.game.libs.cache.ehcache.model;

import cn.ufunny.game.libs.cache.iface.ICacheObject;
import cn.ufunny.game.libs.cache.ehcache.support.mark.IMarker;
import cn.ufunny.game.libs.cache.ehcache.support.expire.Expirable;
import net.sf.ehcache.Element;

/**
 * ElementCacheObject
 * 
 * @author Administrator
 *
 */
public class ElementCacheObject<V> extends Element implements ICacheObject<V>,IMarker,Expirable {

	/**
	 * Auto generate
	 */
	private static final long serialVersionUID = -6270032866556361095L;
	
	private volatile long settleTime;
	private volatile int dirty;
	private volatile boolean mark;

	/**
	 * @param key
	 * @param value
	 */
	public ElementCacheObject(Object key, Object value) {
		super(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V getData() {
		return (V) this.getObjectValue();
	}
	
	@Override
	public void setData(V data) {
		throw new RuntimeException("Ehcache not support setData, please call put method instead");
	}
	
	@Override
	public void settle() {
		this.settleTime = getLastUpdateTime();
	}

	@Override
	public void unsettle() {
		this.settleTime = getLastUpdateTime() - 1;
		this.dirty++;
	}

	@Override
	public void change() {
	}

	@Override
	public boolean hasChange() {
		return this.settleTime != getLastUpdateTime();
	}

	@Override
	public boolean isDirty() {
		return dirty >= 6;
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

}
