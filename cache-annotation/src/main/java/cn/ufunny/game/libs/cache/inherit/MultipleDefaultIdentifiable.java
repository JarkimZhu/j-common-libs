package cn.ufunny.game.libs.cache.inherit;

import java.util.List;

/**
 * Created by ryan on 2015/11/24.
 */
public interface MultipleDefaultIdentifiable<K> {

    public List<K> findIdentifies();

}
