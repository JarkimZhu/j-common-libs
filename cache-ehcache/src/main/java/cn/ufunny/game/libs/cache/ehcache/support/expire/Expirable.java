package cn.ufunny.game.libs.cache.ehcache.support.expire;

/**
 * Created by ryan on 2016/4/5.
 */

/**
 * ryan:
 * cache object support
 * the cache element need implements this class
 */
public interface Expirable {

    public boolean hasExpired(long expireTime,boolean update);

}
