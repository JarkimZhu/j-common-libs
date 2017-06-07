package me.jarkimzhu.libs.protocol.json.dto;

import com.alibaba.fastjson.annotation.JSONField;
import me.jarkimzhu.libs.protocol.ResultCode;
import me.jarkimzhu.libs.protocol.ReturnBean;
import me.jarkimzhu.libs.protocol.ServiceException;
import me.jarkimzhu.libs.utils.annotation.SerializedName;

/**
 * @author JarkimZhu
 * Created on 2015/12/21.
 * @version 0.1.0-SNAPSHOT
 * @since JDK1.8
 */
public class JsonResultProtocol {
    protected int result;
    @SerializedName
    private Object message;
    @JSONField(serialize = false)
    private String serializedName = "message";

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public long getTimestamp() {
        return System.currentTimeMillis();
    }

    public String getSerializedName() {
        return serializedName;
    }

    public void setSerializedName(String serializedName) {
        this.serializedName = serializedName;
    }

    public JsonResultProtocol setup(int code) {
        this.result = code;
        return this;
    }

    public JsonResultProtocol setup(ResultCode resultCode) {
        this.result = resultCode.getCode();
        this.message = resultCode.getMessage();
        return this;
    }

    public JsonResultProtocol setup(ResultCode resultCode, String serializedName) {
        this.result = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.serializedName = serializedName;
        return this;
    }

    public JsonResultProtocol setup(ResultCode resultCode, Object message) {
        this.result = resultCode.getCode();
        this.message = message;
        return this;
    }

    public JsonResultProtocol setup(int result, Object message) {
        this.result = result;
        this.message = message;
        return this;
    }

    public JsonResultProtocol setup(int result, Object message, String serializedName) {
        this.result = result;
        this.message = message;
        this.serializedName = serializedName;
        return this;
    }

    public JsonResultProtocol setup(ResultCode resultCode, Object message, String serializedName) {
        this.result = resultCode.getCode();
        this.message = message;
        this.serializedName = serializedName;
        return this;
    }

    public JsonResultProtocol setupAsException(Throwable e) {
        this.result = ResultCode.SERVER_ERROR.getCode();
        this.message = e.getMessage();
        return this;
    }

    public JsonResultProtocol setupAsServiceException(ServiceException e) {
        this.result = e.getServiceCode();
        this.message = e.getMessage();
        return this;
    }

    public JsonResultProtocol setup(ReturnBean bean) {
        this.result = bean.getResultCode();
        if (this.result == ResultCode.SUCCESS.getCode()) {
            this.message = bean.getEntity();
        } else {
            this.message = bean.getMessage();
        }
        return this;
    }
}
