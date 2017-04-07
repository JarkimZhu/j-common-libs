package me.jarkimzhu.libs.network.http.impl.handler;

import org.apache.http.conn.ssl.TrustStrategy;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author JarkimZhu on 2016/10/20.
 */
public class TrustAnyStrategy implements TrustStrategy {

    @Override
    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        return true;
    }

}
