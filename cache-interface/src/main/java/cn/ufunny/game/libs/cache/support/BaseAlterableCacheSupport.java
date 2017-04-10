package cn.ufunny.game.libs.cache.support;

import cn.ufunny.game.libs.cache.iface.ICacheObject;
import cn.ufunny.game.libs.cache.iface.support.IAlterableCacheSupport;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ryan on 2015/12/18.
 */
public interface BaseAlterableCacheSupport <K, V> extends IAlterableCacheSupport<K,V> {


    @Override
    public default List<V> findDirtyOrChangeData() {
        Collection<ICacheObject<V>> all = values();
        List<V> dirtyDatas = new ArrayList<>();
        for (ICacheObject<V> cacheObject : all) {
            if(cacheObject.isDirty() || cacheObject.hasChange()) {
                V v = cacheObject.getData();
                if(v != null){
                    dirtyDatas.add(v);
                }
            }
        }
        return dirtyDatas;
    }

    public default List<ICacheObject<V>> findAllChangedData() {
        Collection<ICacheObject<V>> allData = values();
        List<ICacheObject<V>> result = Lists.newArrayList();
        for (ICacheObject<V> v : allData) {
            if(v.hasChange() && !v.isDirty()) {
                result.add(v);
            }
        }
        return result;
    }


    public Collection<ICacheObject<V>> values();


}
