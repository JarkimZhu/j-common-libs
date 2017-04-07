package me.jarkimzhu.libs.network.http;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

/**
 * @author JarkimZhu
 *         Created on 2016/10/26.
 * @since jdk1.8
 */
public interface IHttpClientSession<E> extends Closeable {
    /**
     * GET and POST http methods.
     */
    enum Method {
        GET(false), POST(true), PUT(true), DELETE(false), PATCH(true), HEAD(false), OPTIONS(false), TRACE(false);

        private final boolean hasBody;

        Method(boolean hasBody) {
            this.hasBody = hasBody;
        }

        /**
         * Check if this HTTP method has/needs a request body
         *
         * @return if body needed
         */
        public final boolean hasBody() {
            return hasBody;
        }
    }

    /**
     * Set the request URL to fetch. The protocol must be HTTP or HTTPS.
     *
     * @param url URL to connect to
     * @return this IHttpClientSession, for chaining
     */
    IHttpClientSession<E> url(URL url);

    /**
     * Set the request URL to fetch. The protocol must be HTTP or HTTPS.
     *
     * @param url URL to connect to
     * @return this IHttpClientSession, for chaining
     */
    IHttpClientSession<E> url(String url);

    /**
     * Set the request user-agent header.
     *
     * @param userAgent user-agent to use
     * @return this IHttpClientSession, for chaining
     */
    IHttpClientSession<E> userAgent(String userAgent);

    /**
     * Set the request timeouts (connect and read). If a timeout occurs, an IOException will be thrown. The default
     * timeout is 3 seconds (3000 millis). A timeout of zero is treated as an infinite timeout.
     *
     * @param millis number of milliseconds (thousandths of a second) before timing out connects or reads.
     * @return this IHttpClientSession, for chaining
     */
    IHttpClientSession<E> timeout(int millis);

    /**
     * Set the request referrer (aka "referer") header.
     *
     * @param referrer referrer to use
     * @return this IHttpClientSession, for chaining
     */
    IHttpClientSession<E> referrer(String referrer);

    /**
     * Configures the IHttpClientSession to (not) follow server redirects. By default this is <b>true</b>.
     *
     * @param followRedirects true if server redirects should be followed.
     * @return this IHttpClientSession, for chaining
     */
    IHttpClientSession<E> followRedirects(boolean followRedirects);

    /**
     * Configures the IHttpClientSession to (not) allow circular redirects. By default this is <b>false</b>
     * @param circularRedirectsAllowed true allow circular redirects
     * @return this IHttpClientSession, for chaining
     */
    IHttpClientSession<E> circularRedirectsAllowed(boolean circularRedirectsAllowed);

    /**
     * Set the request method to use, GET or POST. Default is GET.
     *
     * @param method HTTP request method
     * @return this IHttpClientSession, for chaining
     */
    IHttpClientSession<E> method(Method method);

    /**
     * Add a request data parameter. Request parameters are sent in the request query string for GETs, and in the
     * request body for POSTs. A request may have multiple values of the same name.
     *
     * @param key   data key
     * @param value data value
     * @return this IHttpClientSession, for chaining
     */
    IHttpClientSession<E> data(String key, String value);

    /**
     * Add an input stream as a request data paramater. For GETs, has no effect, but for POSTS this will upload the
     * input stream.
     *
     * @param key      data key (form item name)
     * @param filename the name of the file to present to the remove server. Typically just the name, not path,
     *                 component.
     * @param file     the input file to upload, that you probably obtained from a {@link java.io.File}.
     *                 You must close the InputStream in a {@code finally} block.
     * @return this IHttpClientSession, for chaining
     */
    IHttpClientSession<E> data(String key, String filename, File file);

    /**
     * Adds all of the supplied data to the request data parameters
     *
     * @param data map of data parameters
     * @return this IHttpClientSession, for chaining
     */
    IHttpClientSession<E> data(Map<String, String> data);

    /**
     * Add a number of request data parameters. Multiple parameters may be set at once, e.g.: <code>.data("name",
     * "jsoup", "language", "Java", "language", "English");</code> creates a query string like:
     * <code>{@literal ?name=jsoup&language=Java&language=English}</code>
     *
     * @param keyvals a set of key value pairs.
     * @return this IHttpClientSession, for chaining
     */
    IHttpClientSession<E> data(String... keyvals);

    /**
     * Reset request setup url and data properties.
     * @return this IHttpClientSession, for chaining
     */
    IHttpClientSession<E> resetRequest();

    /**
     * Set a request header.
     *
     * @param name  header name
     * @param value header value
     * @return this IHttpClientSession, for chaining
     * //@see org.jsoup.IHttpClientSession.Request#headers()
     */
    IHttpClientSession<E> header(String name, String value);

    /**
     * Set a cookie to be sent in the request.
     *
     * @param name  name of cookie
     * @param value value of cookie
     * @return this IHttpClientSession, for chaining
     */
    IHttpClientSession<E> cookie(String name, String value);

    /**
     * Adds each of the supplied cookies to the request.
     *
     * @param cookies map of cookie name {@literal ->} value pairs
     * @return this IHttpClientSession, for chaining
     */
    IHttpClientSession<E> cookies(Map<String, String> cookies);

    /**
     * Sets the default post data character set for x-www-form-urlencoded post data
     *
     * @param charset character set to encode post data
     * @return this IHttpClientSession, for chaining
     */
    IHttpClientSession<E> postDataCharset(String charset);

    /**
     * Execute the request as a GET, and parse the result.
     *
     * @return parsed Document
     * @throws java.net.MalformedURLException  if the request URL is not a HTTP or HTTPS URL, or is otherwise malformed
     * @throws java.net.SocketTimeoutException if the IHttpClientSession times out
     * @throws IOException                     on error
     */
    E get() throws IOException;

    /**
     * Execute the request as a POST, and parse the result.
     *
     * @return parsed Document
     * @throws java.net.MalformedURLException  if the request URL is not a HTTP or HTTPS URL, or is otherwise malformed
     * @throws java.net.SocketTimeoutException if the IHttpClientSession times out
     * @throws IOException                     on error
     */
    E post() throws IOException;

    /**
     * Execute the request.
     *
     * @return a response object
     * @throws java.net.MalformedURLException  if the request URL is not a HTTP or HTTPS URL, or is otherwise malformed
     * @throws java.net.SocketTimeoutException if the IHttpClientResponse times out
     * @throws IOException                     on error
     */
    IHttpClientResponse execute() throws IOException;


    /**
     * Common methods for Requests and Responses
     *
     * @param <T> Type of Base, either Request or Response
     */
    interface IHttpClientMessage<T extends IHttpClientMessage> {

        /**
         * Get the URL
         *
         * @return URL
         */
        URL url();

        /**
         * Set the URL
         *
         * @param url new URL
         * @return this, for chaining
         */
        T url(URL url);

        /**
         * Get the request method
         *
         * @return method
         */
        Method method();

        /**
         * Set the request method
         *
         * @param method new method
         * @return this, for chaining
         */
        T method(Method method);

        /**
         * Get the value of a header. This is a simplified header model, where a header may only have one value.
         * <p>
         * Header names are case insensitive.
         * </p>
         *
         * @param name name of header (case insensitive)
         * @return value of header, or null if not set.
         * @see #hasHeader(String)
         * @see #cookie(String)
         */
        String header(String name);

        /**
         * Set a header. This method will overwrite any existing header with the same case insensitive name.
         *
         * @param name  Name of header
         * @param value Value of header
         * @return this, for chaining
         */
        T header(String name, String value);

        /**
         * Check if a header is present
         *
         * @param name name of header (case insensitive)
         * @return if the header is present in this request/response
         */
        boolean hasHeader(String name);

        /**
         * Check if a header is present, with the given value
         *
         * @param name  header name (case insensitive)
         * @param value value (case insensitive)
         * @return if the header and value pair are set in this req/res
         */
        boolean hasHeaderWithValue(String name, String value);

        /**
         * Remove a header by name
         *
         * @param name name of header to remove (case insensitive)
         * @return this, for chaining
         */
        T removeHeader(String name);

        /**
         * Retrieve all of the request/response headers as a map
         *
         * @return headers
         */
        Map<String, String> headers();

        /**
         * Get a cookie value by name from this request/response.
         * <p>
         * Response objects have a simplified cookie model. Each cookie set in the response is added to the response
         * object's cookie key=value map. The cookie's path, domain, and expiry date are ignored.
         * </p>
         *
         * @param name name of cookie to retrieve.
         * @return value of cookie, or null if not set
         */
        String cookie(String name);

        /**
         * Set a cookie in this request/response.
         *
         * @param name  name of cookie
         * @param value value of cookie
         * @return this, for chaining
         */
        T cookie(String name, String value);

        /**
         * Check if a cookie is present
         *
         * @param name name of cookie
         * @return if the cookie is present in this request/response
         */
        boolean hasCookie(String name);

        /**
         * Remove a cookie by name
         *
         * @param name name of cookie to remove
         * @return this, for chaining
         */
        T removeCookie(String name);

        /**
         * Retrieve all of the request/response cookies as a map
         *
         * @return cookies
         */
        Map<String, String> cookies();

        /**
         * Set the content type (e.g. "text/plan");
         *
         * @param contentType contentType of request
         * @return this, for chaining
         */
        T contentType(String contentType);

        /**
         * Get the content type (e.g. "text/html");
         *
         * @return the response content type
         */
        String contentType();
    }

    /**
     * Represents a HTTP request.
     */
    interface IHttpClientRequest extends IHttpClientMessage<IHttpClientRequest> {
        /**
         * Get the proxy used for this request.
         *
         * @return the proxy; <code>null</code> if not enabled.
         */
        Proxy proxy();

        /**
         * Update the proxy for this request.
         *
         * @param proxy the proxy ot use; <code>null</code> to disable.
         * @return this Request, for chaining
         */
        IHttpClientRequest proxy(Proxy proxy);

        /**
         * Set the HTTP proxy to use for this request.
         *
         * @param host the proxy hostname
         * @param port the proxy port
         * @return this Connection, for chaining
         */
        IHttpClientRequest proxy(String host, int port);

        /**
         * Get the request timeout, in milliseconds.
         *
         * @return the timeout in milliseconds.
         */
        int timeout();

        /**
         * Update the request timeout.
         *
         * @param millis timeout, in milliseconds
         * @return this Request, for chaining
         */
        IHttpClientRequest timeout(int millis);

        /**
         * Get the current followRedirects configuration.
         *
         * @return true if followRedirects is enabled.
         */
        boolean followRedirects();

        /**
         * Configures the request to (not) follow server redirects. By default this is <b>true</b>.
         *
         * @param followRedirects true if server redirects should be followed.
         * @return this Request, for chaining
         */
        IHttpClientRequest followRedirects(boolean followRedirects);

        /**
         * Get the current circularRedirectsAllowed configuration.
         * @return true if circularRedirectsAllowed is allowed.
         */
        boolean circularRedirectsAllowed();

        /**
         * Configures the request to (not) follow server redirects. By default this is <b>true</b>.
         *
         * @param circularRedirectsAllowed true if allow circular redirects.
         * @return this Request, for chaining
         */
        IHttpClientRequest circularRedirectsAllowed(boolean circularRedirectsAllowed);

        /**
         * Add a request data parameter. Request parameters are sent in the request query string for GETs, and in the
         * request body for POSTs. A request may have multiple values of the same name.
         *
         * @param key   data key
         * @param value data value
         * @return this IHttpClientSession, for chaining
         */
        IHttpClientRequest data(String key, String value);

        /**
         * Add an input stream as a request data paramater. For GETs, has no effect, but for POSTS this will upload the
         * input stream.
         *
         * @param key      data key (form item name)
         * @param filename the name of the file to present to the remove server. Typically just the name, not path,
         *                 component.
         * @param file     the input file to upload, that you probably obtained from a {@link java.io.File}.
         *                 You must close the InputStream in a {@code finally} block.
         * @return this IHttpClientSession, for chaining
         */
        IHttpClientRequest data(String key, String filename, File file);

        /**
         * Get request data
         *
         * @return
         */
        Collection data();

        /**
         * Reset url and data properties
         * @return this IHttpClientSession, for chaining
         */
        IHttpClientRequest reset();

        /**
         * Set a POST (or PUT) request body. Useful when a server expects a plain request body, not a set for URL
         * encoded form key/value pairs. E.g.:
         * <code><pre>IHttpClient.connect(url)
         * .body(json)
         * .header("Content-Type", "application/json")
         * .post();</pre></code>
         * If any data key/vals are supplied, they will be sent as URL query params.
         *
         * @return this Request, for chaining
         */
        IHttpClientRequest body(String body);

        /**
         * Get the current request body.
         *
         * @return null if not set.
         */
        String body();

        /**
         * Sets the post data character set for x-www-form-urlencoded post data
         *
         * @param charset character set to encode post data
         * @return this Request, for chaining
         */
        IHttpClientRequest postDataCharset(String charset);

        /**
         * Gets the post data character set for x-www-form-urlencoded post data
         *
         * @return character set to encode post data
         */
        String postDataCharset();
    }

    /**
     * Represents a HTTP response.
     */
    interface IHttpClientResponse extends IHttpClientMessage<IHttpClientResponse>, Closeable {

        /**
         * Get the status code of the response.
         *
         * @return status code
         */
        int statusCode();

        /**
         * Get the status message of the response.
         *
         * @return status message
         */
        String statusMessage();

        /**
         * Get the character set name of the response, derived from the content-type header.
         *
         * @return character set name
         */
        String charset();

        /**
         * Set / override the response character set. When the document body is parsed it will be with this charset.
         *
         * @param charset to decode body as
         * @return this Response, for chaining
         */
        IHttpClientResponse charset(String charset);

        /**
         * Get the body of the response as a plain string.
         *
         * @return body
         */
        String body() throws IOException;

        /**
         * Get the body of the response as an array of bytes.
         *
         * @return body InputStream
         */
        InputStream bodyAsInputStream() throws IOException;
    }
}
