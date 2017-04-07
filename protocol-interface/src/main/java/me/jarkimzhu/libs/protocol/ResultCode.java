package me.jarkimzhu.libs.protocol;

/**
 * @author JarkimZhu
 *         Created on 2016/2/25.
 * @version 0.0.1-SNAPSHOT
 * @since JDK1.8
 */
public class ResultCode {

    public static final ResultCode SUCCESS = new ResultCode(0, "Success");
    public static final ResultCode REQUIREMENT_FLASE = new ResultCode(10, "requirement flase");
    public static final ResultCode INVALID_PARAM = new ResultCode(1, "Invalid parameter(s)");
    public static final ResultCode INVALID_SESSION = new ResultCode(100, "Invalid session");
    public static final ResultCode SERVER_ERROR = new ResultCode(500, "Server internal error");
    public static final ResultCode NET_WORK_ERROR = new ResultCode(600, "network error");
    public static final ResultCode INVALID_TOKEN = new ResultCode(1000, "Auth token failed");
    public static final ResultCode LOGIN_FAILED = new ResultCode(1001, "Login failed");
    public static final ResultCode INVALID_IP = new ResultCode(1002, "Invalid ip address");
    public static final ResultCode INVALID_SIGNATURE = new ResultCode(1005, "Invalid Signature");

    protected ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof ResultCode && this.code == ((ResultCode) obj).getCode();
    }

    public boolean equals(int code) {
        return this.code == code;
    }

    private final int code;
    private final String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
