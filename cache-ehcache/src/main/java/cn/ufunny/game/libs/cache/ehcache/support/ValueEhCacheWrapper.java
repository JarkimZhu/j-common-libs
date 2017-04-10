package cn.ufunny.game.libs.cache.ehcache.support;

import cn.ufunny.game.libs.cache.ehcache.model.ElementCacheObject;
import cn.ufunny.game.libs.cache.iface.ICacheObject;
import com.google.common.collect.Lists;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by ryan on 2015/12/31.
 */
public class ValueEhCacheWrapper<K,V> {

    private Class<K> kClazz;

    private Class<V> vClazz;

    private Cache cache;

    private long expireTime;
    private boolean updateExpire;


    public  ValueEhCacheWrapper(Cache cache,Class<K> kClazz,Class<V> vClass,long expireTime,boolean updateExpire){
        this.kClazz = kClazz;
        this.vClazz = vClass;
        this.cache = cache;
        if(expireTime > 0){
            this.expireTime = expireTime;
        }
        this.updateExpire = updateExpire;
    }

    public <E> V findByKey(E key) {
        Element element = cache.get(key);
        if(element == null){
            return null;
        }
        @SuppressWarnings("unchecked")
        ICacheObject<V> value = (ICacheObject<V>) element;
        return value.getData();
    }

    public List<V> findAll() {
        return findByKeys(findKeys());
    }

    public <E> List<V> findByKeys(Collection<E> keys) {
        Map<Object, Element> all = cache.getAll(keys);
        if(all == null){
            return null;
        }
        List<V> result = Lists.newArrayList();
        for (Element el : all.values()) {
            @SuppressWarnings("unchecked")
            ICacheObject<V> value = (ICacheObject<V>) el;
            V v = value.getData();
            if(v != null){
                result.add(v);
            }
        }
        return result;
    }

    public <E> boolean putByKey(E key, V value) {
        if(key == null){
            return false;
        }
        cache.put(new ElementCacheObject<>(key, value));
        return true;
    }

    public  <E> boolean putIfAbsentByKey(E key, V value) {
        if(key == null){
            return false;
        }
        Element old = cache.putIfAbsent(new ElementCacheObject<>(key, value));
        return old == null;
    }

    public <E> boolean removeByKey(E key) {
        return cache.remove(key);
    }

    public <E> boolean removeByKeys(Collection<E> keys) {
        if(keys == null){
            return false;
        }
        boolean success = true;
        for(E key : keys){
            success = removeByKey(key) && success;
        }
        return success;
    }

    public boolean clean() {
        cache.removeAll();
        return true;
    }

    public <E> boolean hasKey(E key) {
        return cache.isKeyInCache(key);
    }

    private <E> boolean hasKeyType(E key){
        return key.getClass().getSimpleName().equals(this.kClazz.getSimpleName());
    }

    private <E> boolean hasValueType(E value){
        return value.getClass().getSimpleName().equals(this.vClazz.getSimpleName());
    }

    public Collection<ICacheObject<V>> values() {
        Map<Object, Element> all = cache.getAll(findKeys());
        if(all == null){
            return null;
        }
        List<ICacheObject<V>> result = Lists.newArrayList();
        for (Element element : all.values()) {
            @SuppressWarnings("unchecked")
            ICacheObject<V> value = (ICacheObject<V>) element;
            if(value.getData() != null){
                result.add(value);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<K> findKeys(){
        List<K> keys = Lists.newArrayList();
        List<Object> ks = cache.getKeys();
        for(Object k : ks){
            if(hasKeyType(k)){
                keys.add((K)k);
            }
        }
        return keys;
    }

    public int size(){
        return cache.getSize();
    }

    public <E> boolean hasKeyValueChange(E key){
        Element element = cache.get(key);
        if(element == null){
            return false;
        }
        @SuppressWarnings("unchecked")
        ICacheObject<V> value = (ICacheObject<V>) element;
        if(value.getData() == null){
            return false;
        }
        return value.hasChange();
    }

    public void markKey(K key) {
        ElementCacheObject element = (ElementCacheObject) cache.get(key);
        if(element == null){
            element = new ElementCacheObject<>(key, null);
            element.mark();
            ElementCacheObject old = (ElementCacheObject)cache.putIfAbsent(element);
            if(old != null){
                old.mark();
            }
        }
        else {
            element.mark();
        }
    }

    public boolean hasMarkKey(K key){
        ElementCacheObject element = (ElementCacheObject) cache.get(key);
        if(element == null){
            return false;
        }
        return element.hasMark();
    }


    public void removeUnChangeExpireData(){
        if(this.expireTime == 0){
            return;
        }
        List<Object> ks = cache.getKeys();
        Map<Object, Element> all = cache.getAll(ks);
        if(all != null){
            for(Element element : all.values()){
                ElementCacheObject cacheObject = (ElementCacheObject)element;
                if(cacheObject.hasExpired(this.expireTime,this.updateExpire)){
                    cache.remove(cacheObject.getObjectKey());
                }
            }
        }
    }

}
