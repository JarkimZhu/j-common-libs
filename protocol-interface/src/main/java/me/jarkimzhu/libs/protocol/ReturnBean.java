package me.jarkimzhu.libs.protocol;

/**
 * @author liufei
 *         Created by dev on 2016/4/21.
 * @author chenhui
 *         Update by dev on 2016/06/01
 */
public class ReturnBean<T> {

    private int resultCode;    //返回，状态码
    private T entity;          //返回，结果对象
    private String message;

    public ReturnBean() {
        this.resultCode = ResultCode.SUCCESS.getCode();
    }

    /**
     * 默认状态
     */
    public ReturnBean(int code) {
        this.resultCode = code;
    }

    public ReturnBean(ResultCode resultCode) {
        this.resultCode = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public ReturnBean<T> setResultCode(int resultCode) {
        this.resultCode = resultCode;
        return this;
    }

    public ReturnBean<T> setResultCode(ResultCode result) {
        this.resultCode = result.getCode();
        this.message = result.getMessage();
        return this;
    }

    public T getEntity() {
        return entity;
    }

    public ReturnBean<T> setEntity(T entity) {
        this.entity = entity;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ReturnBean<T> setMessage(String message) {
        this.message = message;
        return this;
    }
}
