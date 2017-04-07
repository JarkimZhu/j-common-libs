package me.jarkimzhu.libs.network.http.impl;

import me.jarkimzhu.libs.network.http.IHttpClientSession;
import me.jarkimzhu.libs.utils.CommonUtils;
import org.apache.commons.codec.CharEncoding;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author JarkimZhu
 *         Created on 2016/10/26.
 * @since jdk1.8
 */
class ApacheHttpClientSession implements IHttpClientSession<String> {

    private static final Logger logger = LoggerFactory.getLogger(ApacheHttpClientSession.class);

    private CloseableHttpClient httpClient;
    private HttpClientContext httpClientContext;
    private IHttpClientRequest request;
    private IHttpClientResponse response;

    ApacheHttpClientSession(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        this.request = new ApacheHttpClientRequest();
        this.httpClientContext = HttpClientContext.create();
    }

    @Override
    public IHttpClientSession<String> url(URL url) {
        request.url(url);
        return this;
    }

    @Override
    public IHttpClientSession<String> url(String url) {
        if (CommonUtils.isBlank(url)) {
            throw new IllegalArgumentException("Must supply a valid URL");
        }
        try {
            request.url(new URL(url));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + url, e);
        }
        return this;
    }

    @Override
    public IHttpClientSession<String> userAgent(String userAgent) {
        request.header("User-Agent", userAgent);
        return this;
    }

    @Override
    public IHttpClientSession<String> timeout(int millis) {
        request.timeout(millis);
        return this;
    }

    @Override
    public IHttpClientSession<String> referrer(String referrer) {
        request.header("Referer", referrer);
        return this;
    }

    @Override
    public IHttpClientSession<String> followRedirects(boolean followRedirects) {
        return null;
    }

    @Override
    public IHttpClientSession<String> circularRedirectsAllowed(boolean circularRedirectsAllowed) {
        request.circularRedirectsAllowed(circularRedirectsAllowed);
        return this;
    }

    @Override
    public IHttpClientSession<String> method(Method method) {
        request.method(method);
        return this;
    }

    @Override
    public IHttpClientSession<String> data(String key, String value) {
        request.data(key, value);
        return this;
    }

    @Override
    public IHttpClientSession<String> data(String key, String filename, File file) {
        request.data(key, filename, file);
        return this;
    }

    @Override
    public IHttpClientSession<String> data(Map<String, String> data) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            request.data(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public IHttpClientSession<String> data(String... keyvals) {
        for (int i = 0; i < keyvals.length; i += 2) {
            String key = keyvals[i];
            String value = keyvals[i + 1];
            request.data(key, value);
        }
        return this;
    }

    @Override
    public IHttpClientSession<String> resetRequest() {
        request.reset();
        return this;
    }

    @Override
    public IHttpClientSession<String> header(String name, String value) {
        request.header(name, value);
        return this;
    }

    @Override
    public IHttpClientSession<String> cookie(String name, String value) {
        request.cookie(name, value);
        return this;
    }

    @Override
    public IHttpClientSession<String> cookies(Map<String, String> cookies) {
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            request.cookie(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public IHttpClientSession<String> postDataCharset(String charset) {
        request.postDataCharset(charset);
        return this;
    }

    @Override
    public String get() throws IOException {
        request.method(Method.GET);
        String body = execute().body();
        response = null;
        return body;
    }

    @Override
    public String post() throws IOException {
        request.method(Method.POST);
        String body = execute().body();
        response = null;
        return body;
    }

    @Override
    public IHttpClientResponse execute() throws IOException {
        HttpRequestBase apacheRequest;
        if (Method.POST == request.method()) {
            apacheRequest = new HttpPost(request.url().toString());
            if (!CommonUtils.isBlank(request.body())) {
                StringEntity entity = new StringEntity(request.body());
                entity.setContentEncoding(request.postDataCharset());
                entity.setContentType(request.contentType());
                ((HttpPost) apacheRequest).setEntity(entity);
            } else {
                _postForm(((HttpPost) apacheRequest));
            }
        } else {
            apacheRequest = new HttpGet(request.url().toString());

        }

        if(request.circularRedirectsAllowed()) {
            RequestConfig config = RequestConfig.custom().setCircularRedirectsAllowed(true).build();
            apacheRequest.setConfig(config);
        }

        for(Map.Entry<String, String> entry : request.headers().entrySet()) {
            apacheRequest.addHeader(entry.getKey(), entry.getValue());
        }

        CloseableHttpResponse apacheResponse = httpClient.execute(apacheRequest, httpClientContext);
        request.reset();

        return response = new ApacheHttpClientResponse(apacheResponse);
    }

    private void _postForm(HttpPost apacheRequest) {
        Collection data = request.data();
        boolean hasFile = false;
        for (Object d : data) {
            if (d instanceof FormBodyPart) {
                hasFile = true;
                break;
            }
        }
        if (hasFile) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .setCharset(Charset.forName(request.postDataCharset()));
            for (Object d : data) {
                if (d instanceof FormBodyPart) {
                    builder.addPart((FormBodyPart) d);
                } else if (d instanceof NameValuePair) {
                    NameValuePair pair = (NameValuePair) d;
                    StringBody stringBody = new StringBody(pair.getValue(), ContentType.TEXT_PLAIN);
                    builder.addPart(pair.getName(), stringBody);
                }
            }
            apacheRequest.setEntity(builder.build());
        } else {
            HttpEntity entity;
            if(request.postDataCharset() != null) {
                //noinspection unchecked
                entity = new UrlEncodedFormEntity(data, Charset.forName(request.postDataCharset()));
            } else {
                //noinspection unchecked
                entity = new UrlEncodedFormEntity(data);
            }
            apacheRequest.setEntity(entity);
        }
    }

    @Override
    public void close() throws IOException {
        if (response != null) {
            response.close();
        }
    }

    @SuppressWarnings({"unchecked"})
    static abstract class ApacheHttpClientMessage<T extends IHttpClientMessage> implements IHttpClientMessage<T> {

        URL url;
        Method method;
        Map<String, String> headers;
        Map<String, String> cookies;
        String contentType;

        private ApacheHttpClientMessage() {
            headers = new LinkedHashMap<>();
            cookies = new LinkedHashMap<>();
        }

        @Override
        public URL url() {
            return url;
        }

        @Override
        public T url(URL url) {
            Objects.requireNonNull(url, "URL must not be null");
            this.url = url;
            return (T) this;
        }

        @Override
        public Method method() {
            return method;
        }

        @Override
        public T method(Method method) {
            Objects.requireNonNull(method, "Method must not be null");
            this.method = method;
            return (T) this;
        }

        @Override
        public String header(String name) {
            Objects.requireNonNull(name, "Header name must not be null");
            return getHeaderCaseInsensitive(name);
        }

        @Override
        public T header(String name, String value) {
            Objects.requireNonNull(name, "Header name must not be empty");
            Objects.requireNonNull(value, "Header value must not be null");
            removeHeader(name); // ensures we don't get an "accept-encoding" and a "Accept-Encoding"
            headers.put(name, value);
            return (T) this;
        }

        @Override
        public boolean hasHeader(String name) {
            Objects.requireNonNull(name, "Header name must not be empty");
            return getHeaderCaseInsensitive(name) != null;
        }

        /**
         * Test if the request has a header with this value (case insensitive).
         */
        @Override
        public boolean hasHeaderWithValue(String name, String value) {
            return hasHeader(name) && header(name).equalsIgnoreCase(value);
        }

        @Override
        public T removeHeader(String name) {
            Objects.requireNonNull(name, "Header name must not be empty");
            Map.Entry<String, String> entry = scanHeaders(name); // remove is case insensitive too
            if (entry != null)
                headers.remove(entry.getKey()); // ensures correct case
            return (T) this;
        }

        @Override
        public Map<String, String> headers() {
            return headers;
        }

        private String getHeaderCaseInsensitive(String name) {
            Objects.requireNonNull(name, "Header name must not be null");
            // quick evals for common case of title case, lower case, then scan for mixed
            String value = headers.get(name);
            if (value == null)
                value = headers.get(name.toLowerCase());
            if (value == null) {
                Map.Entry<String, String> entry = scanHeaders(name);
                if (entry != null)
                    value = entry.getValue();
            }
            return value;
        }

        private Map.Entry<String, String> scanHeaders(String name) {
            String lc = name.toLowerCase();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (entry.getKey().toLowerCase().equals(lc))
                    return entry;
            }
            return null;
        }

        @Override
        public String cookie(String name) {
            Objects.requireNonNull(name, "Cookie name must not be empty");
            return cookies.get(name);
        }

        @Override
        public T cookie(String name, String value) {
            Objects.requireNonNull(name, "Cookie name must not be empty");
            Objects.requireNonNull(value, "Cookie value must not be null");
            cookies.put(name, value);
            return (T) this;
        }

        @Override
        public boolean hasCookie(String name) {
            Objects.requireNonNull(name, "Cookie name must not be empty");
            return cookies.containsKey(name);
        }

        @Override
        public T removeCookie(String name) {
            Objects.requireNonNull(name, "Cookie name must not be empty");
            cookies.remove(name);
            return (T) this;
        }

        @Override
        public Map<String, String> cookies() {
            return cookies;
        }

        @Override
        public T contentType(String contentType) {
            this.contentType = contentType;
            return (T) this;
        }

        @Override
        public String contentType() {
            return contentType;
        }
    }

    private static class ApacheHttpClientRequest extends ApacheHttpClientMessage<IHttpClientRequest> implements IHttpClientRequest {

        private Proxy proxy;
        private int timeoutMilliseconds;
        private boolean followRedirects;
        private boolean circularRedirectsAllowed;
        private List<Object> data;
        private String body;
        private String postDataCharset;

        private ApacheHttpClientRequest() {
            timeoutMilliseconds = 3000;
            followRedirects = true;
            method = Method.GET;
            postDataCharset = CharEncoding.UTF_8;
            headers.put("Accept-Encoding", "gzip");
        }

        @Override
        public Proxy proxy() {
            return proxy;
        }

        @Override
        public IHttpClientRequest proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        @Override
        public IHttpClientRequest proxy(String host, int port) {
            this.proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port));
            return this;
        }

        @Override
        public int timeout() {
            return timeoutMilliseconds;
        }

        @Override
        public IHttpClientRequest timeout(int millis) {
            timeoutMilliseconds = millis;
            return this;
        }

        @Override
        public boolean followRedirects() {
            return followRedirects;
        }

        @Override
        public IHttpClientRequest followRedirects(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        @Override
        public boolean circularRedirectsAllowed() {
            return circularRedirectsAllowed;
        }

        @Override
        public IHttpClientRequest circularRedirectsAllowed(boolean circularRedirectsAllowed) {
            this.circularRedirectsAllowed = circularRedirectsAllowed;
            return this;
        }

        @Override
        public IHttpClientRequest data(String key, String value) {
            if (data == null) {
                data = new ArrayList<>();
            }
            data.add(new BasicNameValuePair(key, value));
            return this;
        }

        @Override
        public IHttpClientRequest data(String key, String filename, File file) {
            if (data == null) {
                data = new ArrayList<>();
            }
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            String type = fileNameMap.getContentTypeFor(file.getName());
            ContentType contentType;
            if (type == null) {
                contentType = ContentType.DEFAULT_BINARY;
            } else {
                contentType = ContentType.create(type);
            }
            FileBody fileBody = new FileBody(file, contentType, filename);
            data.add(FormBodyPartBuilder.create(key, fileBody).build());
            return this;
        }

        @Override
        public Collection data() {
            return data;
        }

        @Override
        public IHttpClientRequest reset() {
            if(data != null) {
                data.clear();
            }
            url = null;
            return this;
        }

        @Override
        public IHttpClientRequest body(String body) {
            this.body = body;
            return this;
        }

        @Override
        public String body() {
            return body;
        }

        @Override
        public IHttpClientRequest postDataCharset(String charset) {
            postDataCharset = charset;
            return this;
        }

        @Override
        public String postDataCharset() {
            return postDataCharset;
        }
    }

    private static class ApacheHttpClientResponse extends ApacheHttpClientMessage<IHttpClientResponse> implements IHttpClientResponse {
        private CloseableHttpResponse response;
        private int statusCode;
        private String statusMessage;
        private String charset = CharEncoding.UTF_8;
        private String body;
        private InputStream inputStream;
        private boolean executed = false;

        private ApacheHttpClientResponse(CloseableHttpResponse response) {
            this.response = response;
            final StatusLine statusLine = response.getStatusLine();

            statusCode = statusLine.getStatusCode();
            statusMessage = statusLine.getReasonPhrase();

            for (HeaderIterator it = response.headerIterator(); it.hasNext(); ) {
                Header h = it.nextHeader();
                header(h.getName(), h.getValue());
            }
        }

        @Override
        public int statusCode() {
            return statusCode;
        }

        @Override
        public String statusMessage() {
            return statusMessage;
        }

        @Override
        public String charset() {
            return charset;
        }

        @Override
        public IHttpClientResponse charset(String charset) {
            this.charset = charset;
            return this;
        }

        @Override
        public String body() throws IOException {
            if (!executed) {
                final HttpEntity entity = response.getEntity();
                try {
                    body = entity == null ? null : EntityUtils.toString(entity, charset);
                    return body;
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    EntityUtils.consume(entity);
                    throw e;
                } finally {
                    if (response != null) {
                        try {
                            response.close();
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    executed = true;
                }
            } else {
                return body;
            }

        }

        @Override
        public InputStream bodyAsInputStream() throws IOException {
            if (!executed) {
                final HttpEntity entity = response.getEntity();
                try {
                    return inputStream = entity.getContent();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    EntityUtils.consume(entity);
                    throw e;
                } finally {
                    executed = true;
                }
            } else {
                return inputStream;
            }
        }

        @Override
        public void close() throws IOException {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            if (response != null) {
                response.close();
                response = null;
            }
            body = null;
        }
    }
}
