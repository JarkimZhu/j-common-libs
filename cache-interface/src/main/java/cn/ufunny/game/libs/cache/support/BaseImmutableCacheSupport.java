package cn.ufunny.game.libs.cache.support;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ryan on 2015/12/31.
 */
public interface BaseImmutableCacheSupport {

    static final ExpireCalculator expireCalculator = new ExpireCalculator();
    static final Logger logger = LoggerFactory.getLogger(BaseImmutableCacheSupport.class);

    public default void checkExpire(){
        if(hasExpire()){
            load();
        }
    }

    public default boolean hasExpire() {
        return expireCalculator.hasExpire();
    }

    public default boolean load(){
        logger.debug("class load:{}",this.getClass().getSimpleName());
        boolean success = reload();
        expireCalculator.updateLoad();
        return success;
    }

    public boolean reload();

    public void customInit();

}
