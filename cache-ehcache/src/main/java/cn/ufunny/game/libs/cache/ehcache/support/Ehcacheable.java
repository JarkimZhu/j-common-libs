package cn.ufunny.game.libs.cache.ehcache.support;



/**
 * Created by ryan on 2015/12/31.
 */
public interface Ehcacheable {

    public void initCache();

    public String findCacheName();

}
