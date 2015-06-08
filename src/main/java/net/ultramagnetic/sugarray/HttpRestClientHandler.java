package net.ultramagnetic.sugarray;

import net.ultramagnetic.sugarray.util.Logger;
import net.ultramagnetic.sugarray.util.StringUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;

import javax.net.ssl.SSLSocketFactory;

/**
 * DefaultHttpClientでREST処理を実装したハンドラ
 */
public class HttpRestClientHandler extends HttpRestHandler {

    private static final String TAG = HttpRestClientHandler.class
            .getSimpleName();

    public HttpRestClientHandler() {
        super();
    }

    @Override
    protected HttpResponse execute(RequestOption option,
                                   Sugarray.ProgressUpdateListener progressUpdateListener) throws IOException {
        if (option == null) {
            throw new IllegalArgumentException(TAG + ": option is null.");
        }
        if (progressUpdateListener == null) {
            progressUpdateListener = new Sugarray.ProgressUpdateListener() {
                @Override
                public void onProgressUpdate(long totalBytes, long progressBytes) {
                    Logger.d(TAG, "Total: " + totalBytes + "byte, Progress: "
                            + progressBytes + "byte");
                }
            };
        }
        HttpUriRequest httpRequest = createHttpRequest(option,
                progressUpdateListener);
        DefaultHttpClient client = new DefaultHttpClient();
        client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0,
                false));
        HttpParams httpParams = client.getParams();
        HttpConnectionParams.setSoTimeout(httpParams, option.mTimeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, option.mTimeout);
        return client.execute(httpRequest);
    }

    /**
     * HttpUriRequestを生成する
     *
     * @param option
     * @param progressUpdateListener
     * @return
     */
    private static <T> HttpUriRequest createHttpRequest(
            final RequestOption option,
            final Sugarray.ProgressUpdateListener progressUpdateListener)
            throws IOException {
        StringBuilder queryBuilder = new StringBuilder();
        Iterator<String> queryIterator = option.mQuery.keySet().iterator();
        while (queryIterator.hasNext()) {
            String key = queryIterator.next();
            String value = option.mQuery.get(key);
            queryBuilder.append(key + "=" + value);
            if (queryIterator.hasNext()) {
                queryBuilder.append("&");
            }
        }
        String makeUrl = option.mUrl;
        if (!queryBuilder.toString().isEmpty()) {
            makeUrl += "?" + queryBuilder.toString();
        }
        makeUrl = StringUtils.encodeURL(makeUrl);
        URL url = new URL(makeUrl);
        SSLSocketFactory sslSocketFactor = option.mSSLSocketFactory;
        if ("https".equals(url.getProtocol()) && sslSocketFactor != null) {
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("https",
                    (SocketFactory) option.mSSLSocketFactory, 443));
        }
        HttpUriRequest httpUriRequest = null;
        switch (option.mMethod) {
            case GET:
                httpUriRequest = new HttpGet(makeUrl);
                break;
            case DELETE:
                httpUriRequest = new HttpDelete(makeUrl);
                break;
            case POST:
                httpUriRequest = new HttpPost(makeUrl);
                ((HttpPost) httpUriRequest).setEntity(new CountingHttpEntity(
                        new ByteArrayEntity(option.mBody), progressUpdateListener));
                break;
            case PUT:
                httpUriRequest = new HttpPut(makeUrl);
                ((HttpPut) httpUriRequest).setEntity(new CountingHttpEntity(
                        new ByteArrayEntity(option.mBody), progressUpdateListener));
                break;
            default:
                throw new IllegalStateException("Unknown request method.");
        }
        Iterator<String> headersIterator = option.mHeaders.keySet().iterator();
        while (headersIterator.hasNext()) {
            String key = headersIterator.next();
            String value = option.mHeaders.get(key);
            Header h = new BasicHeader(key, value);
            httpUriRequest.addHeader(h);
        }
        if (StringUtils.isNotEmpty(option.mProxyhost)
                && (0 <= option.mProxyPort && option.mProxyPort <= 65535)) {
            httpUriRequest.getParams().setParameter(
                    ConnRoutePNames.DEFAULT_PROXY,
                    new HttpHost(option.mProxyhost, option.mProxyPort));
        }
        return httpUriRequest;
    }

    /**
     * 進捗状況を取得するためにカスタムしたHttpEntity。
     */
    static class CountingHttpEntity extends HttpEntityWrapper {

        private final Sugarray.ProgressUpdateListener listener;

        public CountingHttpEntity(final HttpEntity entity,
                                  final Sugarray.ProgressUpdateListener listener) {
            super(entity);
            this.listener = listener;
        }

        @Override
        public void writeTo(final OutputStream out) throws IOException {
            this.wrappedEntity
                    .writeTo(out instanceof CountingOutputStream ? out
                            : new CountingOutputStream(out, this.listener, this
                            .getContentLength()));
        }

        static class CountingOutputStream extends FilterOutputStream {

            private final Sugarray.ProgressUpdateListener listener;
            private final long totalBytes;
            private long progressBytes;

            CountingOutputStream(final OutputStream out,
                                 final Sugarray.ProgressUpdateListener listener, long totalBytes) {
                super(out);
                this.listener = listener;
                this.progressBytes = 0;
                this.totalBytes = totalBytes;
            }

            @Override
            public void write(final byte[] b, final int off, final int len)
                    throws IOException {
                out.write(b, off, len);
                this.progressBytes += len;
                this.listener.onProgressUpdate(totalBytes, progressBytes);
            }

            @Override
            public void write(final int b) throws IOException {
                out.write(b);
                this.progressBytes++;
                this.listener.onProgressUpdate(totalBytes, this.progressBytes);
            }
        }
    }

}
