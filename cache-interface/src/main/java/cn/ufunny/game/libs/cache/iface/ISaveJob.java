package cn.ufunny.game.libs.cache.iface;

import java.util.List;

/**
 * @author JarkimZhu
 * @param <V>
 *
 */
public interface ISaveJob<V> extends Runnable {

	public boolean isFinished();

	public <E> void setChangedDatas(List<ICacheObject<E>> allToSave);

}
