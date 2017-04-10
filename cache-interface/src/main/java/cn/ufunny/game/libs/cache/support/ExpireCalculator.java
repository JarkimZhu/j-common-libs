package cn.ufunny.game.libs.cache.support;

/**
 * Created by ryan on 2015/12/31.
 */
public class ExpireCalculator {

    private long loadTime = 0;

    //default
    private long expire = 86400000;   //24h 86400 000


    public ExpireCalculator() {
    }

    public ExpireCalculator(long expire) {
        this.expire = expire;
    }


    protected boolean hasExpire() {
        return loadTime + expire <= System.currentTimeMillis();
    }

    protected void updateLoad(){
        this.loadTime = System.currentTimeMillis();
    }

}
