package cn.ufunny.game.libs.cache.inherit;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by ryan on 2015/11/24.
 */
public interface FieldIdentifiable {

    public default List<String> findFieldIdentifies() {
        return null;
    }

    public default <E>  String buildFieldIdentify(Field field,E e){
        StringBuilder builder = new StringBuilder();
        builder.append(field.getName());
        builder.append(":");
        builder.append(e.toString());
        return builder.toString();
    }

    public static <E>  String buildFieldIdentify(String name, E e){
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(":");
        builder.append(e.toString());
        return builder.toString();
    }

}
