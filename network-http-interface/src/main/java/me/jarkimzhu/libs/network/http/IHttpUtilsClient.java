package me.jarkimzhu.libs.network.http;

import java.io.Closeable;
import java.util.Map;

public interface IHttpUtilsClient extends Closeable {
    /**
     * 使用get方式获取响应内容
     *
     * @param url 访问地址 hostname+domain+querystring
     * @return 响应内容
     */
    String get(String url);

    /**
     * 使用post方式获取响应内容
     *
     * @param url    访问地址 hostname+domain
     * @param params post参数标准form格式
     * @return 响应内容
     */
    String post(String url, Map<String, String> params);

    /**
     * 使用post方式获取响应内容
     *
     * @param url    访问地址 hostname+domain
     * @param params post内容，可为json、xml或其他文本格式
     * @return 响应内容
     */
    String post(String url, String params);

    /**
     * 使用post方式获取响应内容,可以指定请求header中的content-type
     *
     * @param url         访问地址 hostname+domain
     * @param params      post内容，可为json、xml或其他文本格式
     * @param contentType 请求中header的content-type
     * @return 响应内容
     */
    String post(String url, String params, String contentType);

    /**
     * 执行http请求并获取响应内容
     *
     * @param method      http请求方式
     * @param url         访问地址
     * @param params      访问参数
     * @param contentType 请求中header的content-type
     * @return 响应内容
     */
    String execute(String method, String url, String params, String contentType);

    /**
     * 执行http请求并获取响应内容和指定的header信息
     *
     * @param method      http请求方式
     * @param url         访问地址
     * @param params      访问参数
     * @param contentType 请求中header的content-type
     * @param headerNames 需要获取的response中的header name
     * @return 响应内容和指定header的信息
     */
    Map<String, String> execute(String method, String url, String params, String contentType, String... headerNames);

    /**
     * 以get方式执行http请求获取响应header信息
     *
     * @param url        访问地址
     * @param headerName 指定获取的header name
     * @return header信息
     */
    String getHeader(String url, String headerName);

    /**
     * 以get方式执行http请求获取响应headers信息
     *
     * @param url         访问地址
     * @param headerNames 指定获取的header names
     * @return 按参数顺序返回header信息
     */
    String[] getHeaders(String url, String... headerNames);

    /**
     * 以get方式执行http请求获取响应状态码
     *
     * @param url 访问地址
     * @return http状态码
     */
    int getStatusCode(String url);
}
