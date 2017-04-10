package cn.ufunny.game.libs.cache.inherit;

/**
 * Created by ryan on 2015/11/24.
 */
public interface ValueIdentifiable<K,T> extends Identifiable<K> {

    public T findValueIdentify();

}
