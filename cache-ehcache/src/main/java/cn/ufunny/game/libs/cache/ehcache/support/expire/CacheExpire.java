package cn.ufunny.game.libs.cache.ehcache.support.expire;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ryan on 2016/4/6.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CacheExpire {

    long expireTime() default 1800000;//30min

    boolean updateExpire() default true;

}
