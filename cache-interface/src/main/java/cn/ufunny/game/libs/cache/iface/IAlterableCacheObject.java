package cn.ufunny.game.libs.cache.iface;

/**
 * Created by ryan on 2015/10/28.
 */
public interface IAlterableCacheObject<V> {

    public V getData();

    public void setData(V data);

    public void settle();

    public void unsettle();

    public void insert();

    public boolean hasInsert();

    public void change();

    public boolean hasChange();

    public void delete();

    public boolean hasDelete();

    public boolean hasDirty();

}
