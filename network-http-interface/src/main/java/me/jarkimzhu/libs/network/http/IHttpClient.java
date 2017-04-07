package me.jarkimzhu.libs.network.http;

import java.io.Closeable;
import java.net.URL;

/**
 * @author JarkimZhu
 * Created on 2016/10/26.
 * @since jdk1.8
 */
public interface IHttpClient<E> extends Closeable {
    IHttpClientSession<E> createHttpClientSession(String url);
    IHttpClientSession<E> createHttpClientSession(URL url);
}
