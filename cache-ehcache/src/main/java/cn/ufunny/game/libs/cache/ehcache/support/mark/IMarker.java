package cn.ufunny.game.libs.cache.ehcache.support.mark;

/**
 * Created by ryan on 2016/2/1.
 */

/**
 * ryan:
 * cache support
 * purpose: mark the key when put or putIfAbsent call,need initiative call mark method
 */
public interface IMarker {

    public boolean hasMark();

    public void mark();

}
