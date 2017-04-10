package cn.ufunny.game.libs.cache.iface;

/**
 * @author JarkimZhu
 * @param <V>
 *
 */
public interface ICacheObject<V> {
	public V getData();
	public void setData(V data);
	public void settle();
	public void unsettle();
	public void change();
	public boolean hasChange();
	public boolean isDirty();
}
