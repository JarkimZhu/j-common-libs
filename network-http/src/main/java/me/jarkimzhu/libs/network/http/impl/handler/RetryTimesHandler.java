package me.jarkimzhu.libs.network.http.impl.handler;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;

/**
 * @author JarkimZhu
 *         Created on 2016/10/26.
 * @since jdk1.8
 */
public class RetryTimesHandler extends DefaultHttpRequestRetryHandler implements HttpRequestRetryHandler {

    public RetryTimesHandler(int retryTimes) {
        super(retryTimes, true);
    }

}
