package cn.ufunny.game.libs.cache.model;

import cn.ufunny.game.libs.cache.iface.IAlterableCacheObject;

import java.io.Serializable;

/**
 * Created by ryan on 2015/10/28.
 */
public class AlterableCacheObject<V> implements IAlterableCacheObject<V>,Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 906240962624356863L;

	private V data;

    private volatile int lastOperate;

    private volatile int operate;

    private volatile boolean dirty;

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
        this.lastOperate = operate;
        this.operate = Operation.None.getOperate();
    }

    @Override
    public void unsettle() {
        this.operate = lastOperate;
        this.dirty = true;
    }

    @Override
    public void insert() {
        this.operate = Operation.Insert.getOperate();
    }

    @Override
    public boolean hasInsert() {
        return this.operate == Operation.Insert.getOperate();
    }

    @Override
    public void change() {
        if (this.operate == Operation.None.getOperate()) {
            this.operate = Operation.Update.getOperate();
        }
    }

    @Override
    public boolean hasChange() {
        return this.operate == Operation.Update.getOperate();
    }

    @Override
    public void delete() {
        this.operate = Operation.Delete.getOperate();
    }

    @Override
    public boolean hasDelete() {
        return this.operate == Operation.Delete.getOperate();
    }

    @Override
    public boolean hasDirty() {
        return this.dirty;
    }

}
