package me.jarkimzhu.libs.network.http.impl;

import me.jarkimzhu.libs.network.http.IHttpUtilsClient;
import me.jarkimzhu.libs.network.http.impl.handler.RedirectAnyStrategy;
import me.jarkimzhu.libs.network.http.impl.handler.RetryTimesHandler;
import me.jarkimzhu.libs.network.http.impl.handler.TrustAnyStrategy;
import me.jarkimzhu.libs.utils.CommonUtils;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApacheHttpUtilsClient implements IHttpUtilsClient {

    private static final Logger logger = LoggerFactory.getLogger(ApacheHttpUtilsClient.class);
    private static final ResponseHandler<String> basicResponseHandler = new BasicResponseHandler();

    private CloseableHttpClient httpClient;

    public ApacheHttpUtilsClient(int socketTimeout, int maxTotal) {
        try {
            init(socketTimeout, maxTotal, 3, null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public ApacheHttpUtilsClient(int socketTimeout, int maxTotal, int tryTimes) {
        try {
            init(socketTimeout, maxTotal, tryTimes, null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public ApacheHttpUtilsClient(int socketTimeout, int maxTotal, int tryTimes, String ssl) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        init(socketTimeout, maxTotal, tryTimes, ssl);
    }

    private void init(int socketTimeout, int maxTotal, int tryTimes, String ssl) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(socketTimeout)
                .build();

        PoolingHttpClientConnectionManager cm;
        if (CommonUtils.isBlank(ssl)) {
            cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(maxTotal);
            cm.setDefaultMaxPerRoute(maxTotal);
            cm.setDefaultSocketConfig(socketConfig);
            httpClient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .setRetryHandler(new RetryTimesHandler(tryTimes))
                    .setRedirectStrategy(new RedirectAnyStrategy())
                    .build();
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
            cm.setDefaultSocketConfig(socketConfig);
        }

        //构建客户端
        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setRetryHandler(new RetryTimesHandler(tryTimes))
                .setRedirectStrategy(new RedirectAnyStrategy())
                .build();
    }

    @Override
    public String get(String url) {
        HttpGet httpGet = new HttpGet(url);
        try {
            String response = httpClient.execute(httpGet, basicResponseHandler);
            logger.trace("request {} by GET, response:\n {}", url, response);
            return response;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String post(String url, Map<String, String> params) {
        try {
            ArrayList<NameValuePair> pairs = new ArrayList<>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                BasicNameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                pairs.add(pair);
            }
            HttpEntity entity = new UrlEncodedFormEntity(pairs, Charsets.UTF_8);
            return _post(url, entity);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String post(String url, String params) {
        try {
            StringEntity entity = null;
            if (params != null) {
                entity = new StringEntity(params, Charsets.UTF_8);
                entity.setContentEncoding(CharEncoding.UTF_8);
            }
            return _post(url, entity);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String post(String url, String params, String contentType) {
        try {
            StringEntity entity = null;
            if (params != null) {
                entity = new StringEntity(params, Charsets.UTF_8);
                entity.setContentEncoding(CharEncoding.UTF_8);
                if (contentType != null) {
                    entity.setContentType(contentType);
                }
            }
            return _post(url, entity);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String execute(String method, String url, String params, String contentType) {
        if ("POST".equalsIgnoreCase(method)) {
            return this.post(url, params, contentType);
        } else {
            return this.get(url);
        }
    }

    @Override
    public Map<String, String> execute(String method, String url, String params, String contentType, String... headerNames) {
        HttpUriRequest request;
        if ("POST".equalsIgnoreCase(method)) {
            request = new HttpPost(url);
            if (params != null) {
                StringEntity entity = new StringEntity(params, Charsets.UTF_8);
                entity.setContentEncoding(CharEncoding.UTF_8);
                if (contentType != null) {
                    entity.setContentType(contentType);
                }
                ((HttpPost) request).setEntity(entity);
            }
        } else {
            request = new HttpGet(url);
        }
        CloseableHttpResponse response = null;
        try {
            HashMap<String, String> result = new HashMap<>();
            response = httpClient.execute(request);
            for (String headerName : headerNames) {
                Header header = response.getFirstHeader(headerName);
                if (header != null) {
                    result.put(headerName, header.getValue());
                }
            }

            final StatusLine statusLine = response.getStatusLine();
            final HttpEntity entity = response.getEntity();
            if (statusLine.getStatusCode() >= 300) {
                EntityUtils.consume(entity);
                throw new HttpResponseException(statusLine.getStatusCode(),
                        statusLine.getReasonPhrase());
            }
            String content = entity == null ? null : EntityUtils.toString(entity);
            result.put("content", content);
            return result;
        } catch (ClientProtocolException e) {
            logger.error("ClientProtocolException", e);
        } catch (IOException e) {
            logger.error("IOException", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    private String _post(String url, HttpEntity entity) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        try {
            if (entity != null) {
                httpPost.setEntity(entity);
            }
            String response = httpClient.execute(httpPost, basicResponseHandler);
            logger.trace("request {} by POST, response:\n {}", url, response);
            return response;
        } catch (Exception e) {
            logger.error("post to url[{" + url + "}] had error", e);
            throw e;
        }
    }

    @Override
    public String getHeader(String url, String headerName) {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            Header header = response.getFirstHeader(headerName);
            if (header != null) {
                return header.getValue();
            }
        } catch (ClientProtocolException e) {
            logger.error("ClientProtocolException", e);
        } catch (IOException e) {
            logger.error("IOException", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    @Override
    public String[] getHeaders(String url, String... headerNames) {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            String[] results = new String[headerNames.length];
            for (int i = 0; i < headerNames.length; i++) {
                String headerName = headerNames[i];
                Header header = response.getFirstHeader(headerName);
                if (header != null) {
                    results[i] = header.getValue();
                }
            }
            return results;
        } catch (ClientProtocolException e) {
            logger.error("ClientProtocolException", e);
        } catch (IOException e) {
            logger.error("IOException", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    @Override
    public int getStatusCode(String url) {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            return response.getStatusLine().getStatusCode();
        } catch (ClientProtocolException e) {
            logger.error("ClientProtocolException", e);
        } catch (IOException e) {
            logger.error("IOException", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return -1;
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }

    protected String getRedirectUrl(String url) {
        return getHeader(url, "location");
    }

}
