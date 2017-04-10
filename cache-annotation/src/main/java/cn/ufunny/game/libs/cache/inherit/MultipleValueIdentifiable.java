package cn.ufunny.game.libs.cache.inherit;

/**
 * Created by ryan on 2015/11/24.
 */
public interface MultipleValueIdentifiable<K,T> extends MultipleIdentifiable<K> {

    public T findValueIdentify();

}
