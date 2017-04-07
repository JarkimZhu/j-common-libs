package me.jarkimzhu.libs.network.http.impl.handler;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultRedirectStrategy;

/**
 * @author JarkimZhu
 *         Created on 2016/10/31.
 * @since jdk1.8
 */
public class RedirectAnyStrategy extends DefaultRedirectStrategy {

    /**
     * Redirectable methods.
     */
    private static final String[] REDIRECT_METHODS = new String[] {
            HttpGet.METHOD_NAME,
            HttpHead.METHOD_NAME,
            HttpPost.METHOD_NAME
    };

    @Override
    protected boolean isRedirectable(String method) {
        for (final String m: REDIRECT_METHODS) {
            if (m.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
}
