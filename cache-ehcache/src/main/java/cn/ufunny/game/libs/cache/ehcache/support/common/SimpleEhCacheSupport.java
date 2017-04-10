package cn.ufunny.game.libs.cache.ehcache.support.common;

import cn.ufunny.game.libs.cache.ehcache.support.EhCacheManager;
import cn.ufunny.game.libs.cache.ehcache.support.Ehcacheable;
import com.google.common.collect.Lists;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by ryan on 2016/4/22.
 */
public abstract class SimpleEhCacheSupport<K,V> implements Ehcacheable {

    @Resource
    protected EhCacheManager ehCacheManager;

    private Cache cache;


    @Override
    public void initCache() {
        String cacheName = findCacheName();
        if(cacheName == null){
            cacheName = this.getClass().getSimpleName();
        }
        Cache cache = ehCacheManager.getCacheManager().getCache(cacheName);
        if(cache == null){
            ehCacheManager.getCacheManager().addCache(cacheName);
            cache = ehCacheManager.getCacheManager().getCache(cacheName);
        }
        this.cache = cache;
    }


    public V find(K key) {
        Element element = cache.get(key);
        if(element == null){
            return null;
        }
        V v = (V)element.getObjectValue();
        return v;
    }

    public List<V> findAll() {
        List<K> keys = cache.getKeys();
        Map<Object, Element> map = cache.getAll(keys);
        if(map == null){
            return null;
        }
        List<V> vs = Lists.newArrayListWithCapacity(map.size());
        for(Element element : map.values()){
            V v = (V)element.getObjectValue();
            vs.add(v);
        }
        return vs;
    }

    public List<K> findKeys() {
        List<K> keys = cache.getKeys();
        return keys;
    }

    public boolean put(K key,V value) {
        if(key == null){
            return false;
        }
        Element element = new Element(key,value);
        cache.put(element);
        return true;
    }

    public void flush(){
        cache.flush();
    }


    public void clear(){
        cache.removeAll();
    }

}
