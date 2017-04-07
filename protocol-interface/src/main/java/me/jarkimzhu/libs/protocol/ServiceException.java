package me.jarkimzhu.libs.protocol;

/**
 * @author JarkimZhu
 *         Created on 2016/11/10.
 * @since jdk1.8
 */
public class ServiceException extends RuntimeException {

    private int serviceCode;

    public ServiceException(int serviceCode) {
        super();
        this.serviceCode = serviceCode;
    }

    public ServiceException(int serviceCode, String message) {
        super(message);
        this.serviceCode = serviceCode;
    }

    public ServiceException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.serviceCode = resultCode.getCode();
    }

    public int getServiceCode() {
        return serviceCode;
    }
}
