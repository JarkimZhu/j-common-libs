package cn.ufunny.game.libs.cache.model;

/**
 * Created by ryan on 2015/10/28.
 */
public enum  Operation {

    None,
    Insert,
    Update,
    Delete,
    ;

    public int getOperate(){
        return this.ordinal();
    }

}
