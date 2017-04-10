package cn.ufunny.game.libs.cache.model;

import java.io.Serializable;

import cn.ufunny.game.libs.cache.iface.ICacheObject;

/**
 * @author JarkimZhu
 * @param <V> cache value
 *
 */
public class CacheObject<V> implements ICacheObject<V>, Serializable {
	
	/**
	 * auto generate
	 */
	private static final long serialVersionUID = 3040996008415781828L;

	private V data;
	
	private volatile long version;
	private volatile long settleVersion;
	private volatile boolean dirty;
	
	public CacheObject(V data) {
		this.data = data;
	}
	
	@Override
	public V getData() {
		return data;
	}
	
	@Override
	public void setData(V data) {
		this.data = data;
	}

	@Override
	public void settle() {
		this.settleVersion = this.version;
	}

	@Override
	public void unsettle() {
		this.settleVersion = this.version - 1;
		this.dirty = true;
	}

	@Override
	public void change() {
		this.version++;
	}

	@Override
	public boolean hasChange() {
		return this.version != this.settleVersion;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

}
