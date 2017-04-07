package me.jarkimzhu.libs.network.http.impl;

import me.jarkimzhu.libs.network.http.IHttpClient;
import me.jarkimzhu.libs.network.http.IHttpClientSession;
import me.jarkimzhu.libs.network.http.impl.handler.RedirectAnyStrategy;
import me.jarkimzhu.libs.network.http.impl.handler.RetryTimesHandler;
import me.jarkimzhu.libs.network.http.impl.handler.TrustAnyStrategy;
import me.jarkimzhu.libs.utils.CommonUtils;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * @author JarkimZhu
 *         Created on 2016/10/26.
 * @since jdk1.8
 */
public class ApacheHttpClient implements IHttpClient<String> {

    private static final Logger logger = LoggerFactory.getLogger(ApacheHttpClient.class);

    private CloseableHttpClient httpClient;

    public ApacheHttpClient() {
        try {
            init(3000, 2, 0, null);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public ApacheHttpClient(int timeout, int maxTotal, int retryTimes) {
        try {
            init(timeout, maxTotal, retryTimes, null);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public ApacheHttpClient(int timeout, int maxTotal, int retryTimes, String ssl) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        init(timeout, maxTotal, retryTimes, ssl);
    }

    private void init(int socketTimeout, int maxTotal, int tryTimes, String ssl) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        // 创建全局的requestConfig
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(socketTimeout)
                .setSocketTimeout(socketTimeout)
                .setCookieSpec(CookieSpecs.DEFAULT).build();
        PoolingHttpClientConnectionManager cm;

        if (CommonUtils.isBlank(ssl)) {
            cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(maxTotal);
            cm.setDefaultMaxPerRoute(maxTotal);
        } else {
            //设置连接参数
            RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
            PlainConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
            registryBuilder.register("http", plainSF);
            //指定信任密钥存储对象和连接套接字工厂
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustAnyStrategy()).build();
            SSLConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext);
            registryBuilder.register("https", sslSF);
            Registry<ConnectionSocketFactory> registry = registryBuilder.build();
            //设置连接管理器
            cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(maxTotal);
            cm.setDefaultMaxPerRoute(maxTotal);
        }

        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new RetryTimesHandler(tryTimes))
                .setRedirectStrategy(new RedirectAnyStrategy())
                .build();
    }

    @Override
    public IHttpClientSession<String> createHttpClientSession(String url) {
        return new ApacheHttpClientSession(httpClient).url(url);
    }

    @Override
    public IHttpClientSession<String> createHttpClientSession(URL url) {
        return new ApacheHttpClientSession(httpClient).url(url);
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }
}
