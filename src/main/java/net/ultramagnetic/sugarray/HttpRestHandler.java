package net.ultramagnetic.sugarray;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import net.ultramagnetic.sugarray.error.ClientError;
import net.ultramagnetic.sugarray.error.ServerError;
import net.ultramagnetic.sugarray.error.SugarrayError;
import net.ultramagnetic.sugarray.error.UnexpectedError;
import net.ultramagnetic.sugarray.util.Logger;
import net.ultramagnetic.sugarray.util.StringUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Http通信のロジック部分を抽象化したREST処理を行うハンドラ
 */
@SuppressLint("DefaultLocale")
abstract class HttpRestHandler implements HttpHandler {

    private static final String TAG = HttpRestHandler.class.getSimpleName();

    public HttpRestHandler() {
    }

    @Override
    public SugarrayResponse perform(final RequestOption option,
                                    final Sugarray.ProgressUpdateListener progressUpdateListener)
            throws SugarrayError {
        if (option == null) {
            throw new IllegalArgumentException(TAG + ": option is null.");
        }
        int currentRetry = 0;
        while (true) {
            try {
                HttpResponse httpResponse = execute(option,
                        progressUpdateListener);
                return createSugarrayResponse(httpResponse);
            } catch (MalformedURLException e) {
                throw new UnexpectedError("Bad URL. " + option.mUrl, e);
            } catch (ProtocolException e) {
                throw new UnexpectedError("Invalid protocol.", e);
            } catch (FileNotFoundException e) {
                throw new UnexpectedError("File not found.", e);
            } catch (SocketTimeoutException e) {
                throw new UnexpectedError("Socket timeout.", e);
            } catch (ConnectTimeoutException e) {
                throw new UnexpectedError("Connect timeout.", e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new UnexpectedError("io error.", e);
            } catch (SugarrayError e) {
                if (e instanceof ServerError && currentRetry < option.mRetryMax) {
                    try {
                        Thread.sleep(option.mRetryInterval);
                    } catch (InterruptedException ie) {
                        throw e;
                    }
                    currentRetry++;
                    continue;
                }
                throw e;
            }
        }
    }

    /**
     * HttpResponseをパッケージ定義のレスポンス形式に変換する。
     *
     * @param httpResponse
     * @return SugarrayResponse
     * @throws net.ultramagnetic.sugarray.error.SugarrayError
     * @throws java.io.IOException
     */
    private SugarrayResponse createSugarrayResponse(HttpResponse httpResponse)
            throws SugarrayError, IOException {

        StatusLine statusLine = httpResponse.getStatusLine();
        int statusCode = statusLine.getStatusCode();

        Header contentTypeHeader = httpResponse.getEntity().getContentType();
        String contentType = contentTypeHeader == null ? null
                : contentTypeHeader.getValue();

        String[] values = {};
        if (StringUtils.isNotEmpty(contentType)) {
            values = contentType.split(";");
        }

        String charset = "";
        for (String value : values) {
            value = value.trim();
            if (value.toLowerCase().startsWith("charset=")) {
                charset = value.substring("charset=".length());
            }
        }

        if ("".equals(charset)) {
            charset = "UTF-8";
        }

        Map<String, String> responseHeaders = convertHeaders(httpResponse
                .getAllHeaders());

        if (statusCode == HttpStatus.SC_NOT_MODIFIED) {
            // TODO Cache取得処理を追加する
        }

        byte[] responseContents = entityToBytes(httpResponse.getEntity());

        SugarrayResponse response = new SugarrayResponse(statusCode,
                responseContents, contentType, charset, responseHeaders, false);

        if (HttpStatus.SC_BAD_REQUEST <= statusCode
                && statusCode < HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            throw new ClientError(response);
        }
        if (HttpStatus.SC_INTERNAL_SERVER_ERROR <= statusCode) {
            throw new ServerError(response);
        }

        return response;
    }

    /**
     * HttpEntityをバイナリに変換する
     *
     * @param entity
     * @return バイナリ
     * @throws java.io.IOException
     */
    private static byte[] entityToBytes(HttpEntity entity) throws IOException {
        ByteArrayOutputStream bytes = null;
        BufferedInputStream bis = null;
        byte[] buffer = new byte[1024];
        try {
            InputStream in = null;
            if (isGZipHttpResponse(entity)) {
                in = new GZIPInputStream(entity.getContent());
            } else {
                in = entity.getContent();
            }
            if (in == null) {
                return new byte[0];
            }
            int contentLength = (int) entity.getContentLength();
            if (contentLength == -1) {
                bytes = new ByteArrayOutputStream();
                bis = new BufferedInputStream(in);
            } else {
                bytes = new ByteArrayOutputStream(contentLength);
                bis = new BufferedInputStream(in, contentLength);
            }

            while (true) {
                int len = bis.read(buffer);
                if (len < 0) {
                    break;
                }
                bytes.write(buffer, 0, len);
            }
            return bytes.toByteArray();
        } catch (IllegalStateException e) {
            Logger.e(
                    TAG,
                    "This entity is not repeatable and the stream has already been obtained previously.",
                    e);
            return new byte[0];
        } catch (IOException e) {
            Logger.e(TAG, "The stream could not be created.", e);
            throw e;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    Logger.e(TAG, "BufferedInputStream close error.", e);
                }
            }
            if (bytes != null) {
                try {
                    bytes.close();
                } catch (IOException e) {
                    Logger.e(TAG, "ByteArrayOutputStream close error.", e);
                }
            }
            if (entity != null) {
                try {
                    entity.consumeContent();
                } catch (IOException e) {
                    Logger.e(TAG, "Consume content error.", e);
                }
            }
        }
    }

    /**
     * GZIPが有効かどうか判断する
     *
     * @param response
     * @return
     */
    private static boolean isGZipHttpResponse(HttpEntity entity) {
        Header header = entity.getContentEncoding();
        if (header == null)
            return false;

        String value = header.getValue();
        return (!TextUtils.isEmpty(value) && value.contains("gzip"));
    }

    /**
     * レスポンスヘッダーをマップ形式に変換する
     *
     * @param headers
     * @return マップ形式のヘッダー
     */
    private static Map<String, String> convertHeaders(Header[] headers) {
        Map<String, String> result = new HashMap<String, String>();
        if (headers == null || headers.length == 0) {
            return result;
        }
        for (Header header : headers) {
            result.put(header.getName(), header.getValue());
        }
        return result;
    }

    /**
     * Http通信を行う。
     *
     * @param option
     * @param progressUpdateListener
     * @return HttpResponse
     * @throws java.io.IOException
     */
    protected abstract HttpResponse execute(final RequestOption option,
                                            final Sugarray.ProgressUpdateListener progressUpdateListener)
            throws IOException;
}