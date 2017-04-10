package cn.ufunny.game.libs.cache.iface;

import java.util.List;

/**
 * Created by ryan on 2015/10/24.
 */
public interface ISaver<V> {

    public void save();

    public boolean hasFinished();

    public List<ICacheObject<V>> findChangeData();

    public List<V> findDirtyData();

    public String findCacheName();

}
