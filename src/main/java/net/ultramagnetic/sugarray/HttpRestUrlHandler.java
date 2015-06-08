package net.ultramagnetic.sugarray;


import net.ultramagnetic.sugarray.util.Logger;
import net.ultramagnetic.sugarray.util.StringUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * HttpURLConnectionでREST処理を実装したハンドラ
 */
public class HttpRestUrlHandler extends HttpRestHandler {

    private static final String TAG = HttpRestUrlHandler.class.getSimpleName();

    public HttpRestUrlHandler() {
        ;
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
                    Logger.i(TAG, "Total: " + totalBytes + "byte, Progress: "
                            + progressBytes + "byte");
                }
            };
        }
        HttpURLConnection conn = null;
        try {
            conn = createHttpURLConnection(option);
            if ((option.mMethod == SugarrayConstants.Method.POST || option.mMethod == SugarrayConstants.Method.PUT)
                    && option.mBody != null) {
                DataOutputStream out = new DataOutputStream(
                        conn.getOutputStream());
                byte buf[] = new byte[1024];
                long totalBytes = option.mBody.length;
                long sentBytes = 0;
                int readBytes;
                Logger.d(TAG, new String(option.mBody, "UTF-8"));
                InputStream in = new ByteArrayInputStream(option.mBody);
                while ((readBytes = in.read(buf)) != -1) {
                    out.write(buf, 0, readBytes);
                    sentBytes += readBytes;
                    progressUpdateListener.onProgressUpdate(totalBytes,
                            sentBytes);
                }
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == -1) {
                String message = "Could not retrieve response code from HttpUrlConnection. responseCode: "
                        + responseCode;
                Logger.e(TAG, message);
                throw new IOException(message);
            }
            Logger.d(TAG,
                    "URL: " + conn.getURL().toString() + "\n" + "Method: "
                            + conn.getRequestMethod().toString() + "\n"
                            + "Response status line: " + responseCode + " "
                            + conn.getResponseMessage());
            ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
            StatusLine responseStatus = new BasicStatusLine(protocolVersion,
                    responseCode, conn.getResponseMessage());
            BasicHttpResponse response = new BasicHttpResponse(responseStatus);
            response.setEntity(createEntityFromConnection(conn));
            Map<String, List<String>> headerFields = conn.getHeaderFields();
            if (headerFields != null && !headerFields.isEmpty()) {
                Logger.d(TAG, "Response header<K,V>: ---------- ");
                for (Entry<String, List<String>> header : headerFields
                        .entrySet()) {
                    if (header.getKey() != null) {
                        Logger.d(TAG, "" + header.getKey() + ": "
                                + header.getValue().get(0));
                        Header h = new BasicHeader(header.getKey(), header
                                .getValue().get(0));
                        response.addHeader(h);
                    }
                }
                Logger.d(TAG, "Response header<K,V>: ---------- ");
            }
            return response;
        } catch (IOException e) {
            Logger.e(TAG, "IOException.", e);
            if (conn != null) {
                conn.disconnect();
            }
            throw e;
        }
    }

    /**
     * HttpEntityを生成する
     *
     * @param connection
     * @return
     */
    private static HttpEntity createEntityFromConnection(
            final HttpURLConnection connection) {
        BasicHttpEntity entity = new BasicHttpEntity();
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException ioe) {
            inputStream = connection.getErrorStream();
        }
        entity.setContent(inputStream);
        entity.setContentLength(connection.getContentLength());
        entity.setContentEncoding(connection.getContentEncoding());
        entity.setContentType(connection.getContentType());
        return entity;
    }

    /**
     * HttpURLConnectionを生成する
     *
     * @param option
     * @return
     * @throws java.io.IOException
     */
    private static HttpURLConnection createHttpURLConnection(
            final RequestOption option) throws IOException {
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
        SugarrayConstants.Method method = option.mMethod;
        Map<String, String> headers = option.mHeaders;
        String proxyhost = option.mProxyhost;
        Integer proxyPort = option.mProxyPort;
        int timeout = option.mTimeout;
        SSLSocketFactory sslSocketFactor = option.mSSLSocketFactory;
        HttpURLConnection conn = null;
        if (StringUtils.isNotEmpty(proxyhost)
                && (0 <= proxyPort && proxyPort <= 65535)) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
                    proxyhost, proxyPort));
            conn = (HttpURLConnection) url.openConnection(proxy);
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setChunkedStreamingMode(0);
        conn.setReadTimeout(timeout);
        conn.setConnectTimeout(timeout);
        conn.setRequestMethod(method.name());
        if (method == SugarrayConstants.Method.GET || method == SugarrayConstants.Method.DELETE) {
            // TODO Java6でコンパイルするとDeleteメソッドで呼び出した際にエラーとなるため退避
            // if (method == Method.GET || method == Method.DELETE) {
            conn.setDoOutput(false);
        } else {
            conn.setDoOutput(true);
        }
        if ("https".equals(url.getProtocol()) && sslSocketFactor != null) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactor);
        }
        Iterator<String> iterator = headers.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = headers.get(key);
            conn.setRequestProperty(key, value);
            Logger.d(TAG, "" + key + ": "
                    + value);
        }
        Logger.d(TAG, "url: "
                + makeUrl);
        return conn;
    }
}