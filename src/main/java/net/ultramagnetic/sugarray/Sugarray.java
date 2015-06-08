package net.ultramagnetic.sugarray;

import android.content.Context;
import android.os.Build;

import net.ultramagnetic.sugarray.error.SugarrayError;
import net.ultramagnetic.sugarray.util.Base64;
import net.ultramagnetic.sugarray.util.FileUtils;
import net.ultramagnetic.sugarray.util.Logger;
import net.ultramagnetic.sugarray.util.StringUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

public abstract class Sugarray {

    /**
     * ログ出力用タグ
     */
    private static final String TAG = Sugarray.class.getSimpleName();
    /**
     * URL
     */
    private String mUrl;
    /**
     * RESTメソッド
     */
    private SugarrayConstants.Method mMethod;
    /**
     * HTTPヘッダー
     */
    private final Map<String, String> mHeaders = new HashMap<String, String>();
    /**
     * クエリパラメーター
     */
    private final Map<String, String> mQuery = new HashMap<String, String>();
    /**
     * メッセージボディ
     */
    private byte[] mBody;
    /**
     * ファイル
     */
    private File mFile;
    /**
     * プロキシホスト名
     */
    private String mProxyhost;
    /**
     * プロキシポート番号
     */
    private int mProxyPort;
    /**
     * SSL設定
     */
    private SSLSocketFactory mSSLSocketFactory;
    /**
     * 最大リトライ回数
     */
    private int mRetryMax = SugarrayConstants.DefaultRetryConfig.DEFAULT_RETRY_MAX;
    /**
     * リトライ間隔(MS)
     */
    private int mRetryInterval = SugarrayConstants.DefaultRetryConfig.DEFAULT_RETRY_INTERVAL;
    /**
     * タイムアウト時間(MS)
     */
    private int mTimeout = SugarrayConstants.DefaultRetryConfig.DEFAULT_TIMEOUT;
    /**
     * コンテキスト
     */
    private final Context mContext;

    protected Sugarray(Context context) {
        mContext = context;
    }

    /**
     * URLを受け取りヘッダーにホスト情報をセットする。
     *
     * @param url URL
     * @return Sugarrayのインスタンス
     */
    public Sugarray setUrl(String url) {
        try {
            mUrl = url;
            set(SugarrayConstants.HeaderFields.HOST, new URL(url).getHost());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("url is minus:" + mUrl);
        }
        return this;
    }

    /**
     * メソッドを受け取る、
     *
     * @param url URL
     * @return Sugarrayのインスタンス
     */
    public Sugarray method(SugarrayConstants.Method method) {
        mMethod = method;
        return this;
    }

    /**
     * GetメソッドでURLを受け取る、
     *
     * @param url URL
     * @return Sugarrayのインスタンス
     */
    public Sugarray get(String url) {
        mMethod = SugarrayConstants.Method.GET;
        return setUrl(url);
    }

    /**
     * PostメソッドでURLを受け取る、
     *
     * @param url URL
     * @return Sugarrayのインスタンス
     */
    public Sugarray post(String url) {
        mMethod = SugarrayConstants.Method.POST;
        return setUrl(url);
    }

    /**
     * PutメソッドでURLを受け取る、
     *
     * @param url URL
     * @return Sugarrayのインスタンス
     */
    public Sugarray put(String url) {
        mMethod = SugarrayConstants.Method.PUT;
        return setUrl(url);
    }

    /**
     * DeleteメソッドでURLを受け取る、
     *
     * @param url URL
     * @return Sugarrayのインスタンス
     */
    public Sugarray delete(String url) {
        mMethod = SugarrayConstants.Method.DELETE;
        return setUrl(url);
    }

    /**
     * HTTPヘッダーをセットする。
     *
     * @param field フィールド名
     * @param value ヘッダー値
     * @return Sugarrayのインスタンス
     */
    public Sugarray set(String field, String value) {
        if (StringUtils.isNotEmpty(field)) {
            mHeaders.put(field, value);
        }
        return this;
    }

    /**
     * HTTPヘッダーをセットする。
     *
     * @param headers   クエリのマップ
     * @return SugarrayRequestのインスタンス
     */
    public Sugarray set(Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            mHeaders.putAll(headers);
        }
        return this;
    }

    /**
     * 対象のHTTPヘッダーを削除する
     *
     * @param field フィールド名
     * @return Sugarrayのインスタンス
     */
    public Sugarray unset(String field) {
        if (StringUtils.isNotEmpty(field)) {
            mHeaders.remove(field);
        }
        return this;
    }

    /**
     * ヘッダーにコンテンツタイプをセットする。
     *
     * @param contentType コンテンツタイプの値(enum)
     * @return Sugarrayのインスタンス
     */
    public Sugarray type(SugarrayConstants.ContentType contentType) {
        if (contentType != null) {
            type(contentType.value());
        }
        return this;
    }

    /**
     * ヘッダーにコンテンツタイプをセットする。
     *
     * @param contentType コンテンツタイプの値(文字列)
     * @return Sugarrayのインスタンス
     */
    public Sugarray type(String contentType) {
        if (StringUtils.isNotEmpty(contentType)) {
            set(SugarrayConstants.HeaderFields.CONTENT_TYPE, contentType);
        }
        return this;
    }

    /**
     * ヘッダーにアセプトをセットする。
     *
     * @param type アセプトの値
     * @return Sugarrayのインスタンス
     */
    public Sugarray accept(String type) {
        if (StringUtils.isNotEmpty(type)) {
            set(SugarrayConstants.HeaderFields.ACCEPT, type);
        }
        return this;
    }

    /**
     * ヘッダーにコネクションをセットする。
     *
     * @param connection コネクションの値
     * @return SugarrayRequestのインスタンス
     */
    public Sugarray connection(SugarrayConstants.Connection connection) {
        set(SugarrayConstants.HeaderFields.CONNECTION, connection.value());
        return this;
    }

    /**
     * ヘッダーにコネクションをセットする。
     *
     * @param connection コネクションの値(文字列)
     * @return SugarrayRequestのインスタンス
     */
    public Sugarray connection(String connection) {
        if (SugarrayConstants.Connection.fromString(connection) != null) {
            set(SugarrayConstants.HeaderFields.CONNECTION, connection);
        }
        return this;
    }

    /**
     * ヘッダーにユーザーエージェントをセットする。
     *
     * @param useragent ユーザーエージェントの値(文字列)
     * @return SugarrayRequestのインスタンス
     */
    public Sugarray useragent(String useragent) {
        if (StringUtils.isNotEmpty(useragent)) {
            set(SugarrayConstants.HeaderFields.USER_AGENT, useragent);
        }
        return this;
    }

    /**
     * クエリパラメーターをセットする。
     *
     * @param key   クエリのキー
     * @param value クエリの値
     * @return SugarrayRequestのインスタンス
     */
    public Sugarray query(String key, String value) {
        if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
            mQuery.put(key, value);
        }
        return this;
    }

    /**
     * クエリパラメーターをセットする。
     *
     * @param querys   クエリのマップ
     * @return SugarrayRequestのインスタンス
     */
    public Sugarray query(Map<String, String> querys) {
        if (querys != null && !querys.isEmpty()) {
            mQuery.putAll(querys);
        }
        return this;
    }

    /**
     * タイムアウト時間をセットする。
     *
     * @param timeout タイムアウト時間
     * @return Sugarrayのインスタンス
     */
    public Sugarray timeout(int timeout) {
        mTimeout = timeout;
        return this;
    }

    /**
     * 最大リトライ回数とリトライ間隔をセットする。
     *
     * @param max      最大リトライ回数
     * @param interval リトライ間隔
     * @return Sugarrayのインスタンス
     */
    public Sugarray retry(int max, int interval) {
        mRetryMax = max;
        mRetryInterval = interval;
        return this;
    }

    /**
     * Basic認証、ダイジェスト認証のユーザ名、パスワードをセットする。
     *
     * @param user ユーザ名
     * @param pass パスワード
     * @return Sugarrayのインスタンス
     */
    public Sugarray auth(String user, String pass) {
        if (StringUtils.isNotEmpty(user) && pass != null) {
            String raw = String.format("%s:%s", user, pass);
            String value = String.format(
                    "Basic %s",
                    Base64.encodeToString(raw.getBytes(), Base64.URL_SAFE
                            | Base64.NO_WRAP));
            set(SugarrayConstants.HeaderFields.AUTHORIZATION, value);

        }
        return this;
    }

    /**
     * メッセージボディをセットする。
     *
     * @param body メッセージボディのデータ(文字列)
     * @return Sugarrayのインスタンス
     */
    public Sugarray send(String body) {
        return send(body, null);
    }

    /**
     * メッセージボディをセットする。
     *
     * @param body        メッセージボディのデータ(文字列)
     * @param charsetName メッセージボディのキャラセット(文字列)
     * @return Sugarrayのインスタンス
     */
    public Sugarray send(String body, String charsetName) {
        if (StringUtils.isEmpty(body)) {
            return this;
        }
        if (StringUtils.isEmpty(charsetName)) {
            return send(body.getBytes(Charset.defaultCharset()));
        }
        Charset charset = null;
        try {
            charset = Charset.forName(charsetName);
        } catch (IllegalCharsetNameException e) {
            Logger.e(TAG, "Illegal charset name.", e);
            return send(body.getBytes(Charset.defaultCharset()));
        } catch (UnsupportedCharsetException e) {
            Logger.e(TAG, "Unsupported charset.", e);
            return send(body.getBytes(Charset.defaultCharset()));
        }
        return send(body.getBytes(charset));
    }

    /**
     * メッセージボディをセットする。
     *
     * @param body メッセージボディのデータ(バイナリ)
     * @return Sugarrayのインスタンス
     */
    public Sugarray send(byte[] body) {
        mBody = body;
        return this;
    }

    /**
     * メッセージボディをセットする。
     *
     * @param file メッセージボディのデータ(ファイル)
     * @return Sugarrayのインスタンス
     */
    public Sugarray send(File file) {
        if (FileUtils.isNotFile(file)) {
            return this;
        }
        mBody = FileUtils.readFileToBytes(file);
        return this;
    }

    /**
     * プロキシをセットする。
     *
     * @param proxyhost プロキシサーバーのホスト名
     * @param proxyPort プロキシサーバーのポート番号
     * @return Sugarrayのインスタンス
     */
    public Sugarray proxy(String proxyhost, int proxyPort) {
        if (StringUtils.isNotEmpty(proxyhost)
                && (0 <= proxyPort && proxyPort <= 65535)) {
            mProxyhost = proxyhost;
            mProxyPort = proxyPort;
        }
        return this;
    }

    /**
     * SSLの証明書情報をセットする。
     *
     * @param sslSocketFactory
     * @return Sugarrayのインスタンス
     */
    public Sugarray https(SSLSocketFactory sslSocketFactory) {
        mSSLSocketFactory = sslSocketFactory;
        return this;
    }

    /**
     * HTTPリクエストを実行する。
     *
     * @return Sugarrayのインスタンス
     */
    public Sugarray end() {
        return end(null);
    }

    /**
     * HTTPリクエストを実行する。
     *
     * @param httpResponseListener HTTPレスポンスのリスナー
     * @return Sugarrayのインスタンス
     */
    public Sugarray end(HttpResponseListener httpResponseListener) {
        return end(httpResponseListener, null);
    }

    /**
     * HTTPリクエストを実行する。
     *
     * @param httpResponseListener   HTTPレスポンスのリスナー
     * @param progressUpdateListener 進行状況のリスナー
     * @return Sugarrayのインスタンス
     */
    public Sugarray end(HttpResponseListener httpResponseListener,
                        ProgressUpdateListener progressUpdateListener) {
        return end(httpResponseListener, progressUpdateListener, null);
    }

    /**
     * HTTPリクエストを実行する。
     *
     * @param httpResponseListener   HTTPレスポンスのリスナー
     * @param progressUpdateListener 進行状況のリスナー
     * @param httpHandler            HTTP通信の実処理
     * @return Sugarrayのインスタンス
     */
    public Sugarray end(HttpResponseListener httpResponseListener,
                        ProgressUpdateListener progressUpdateListener,
                        HttpHandler httpHandler) {
        if (StringUtils.isEmpty(mUrl)) {
            throw new IllegalArgumentException("url is minus:" + mUrl);
        }
        if (httpResponseListener == null) {
            httpResponseListener = new DefaultHttpResponseListener();
        }
        if (httpHandler == null) {
            httpHandler = createHttpHandler();
        }
        if (progressUpdateListener == null) {
            progressUpdateListener = new DefaultProgressUpdateListener();
        }
        final RequestOption option = new RequestOption
                .Bulider()
                .url(mUrl)
                .method(mMethod)
                .headers(mHeaders)
                .query(mQuery)
                .body(mBody)
                .file(mFile)
                .proxyhost(mProxyhost)
                .proxyPort(mProxyPort)
                .sslSocketFactory(mSSLSocketFactory)
                .retryMax(mRetryMax)
                .retryInterval(mRetryInterval)
                .timeout(mTimeout)
                .bulid();

        createLauncher()
                .launch(mContext, option, httpHandler, httpResponseListener, progressUpdateListener);
        return this;
    }

    protected abstract SugarrayLauncher createLauncher();

    /**
     * リクエスト情報をリセットする。
     *
     * @return Sugarrayのインスタンス
     */
    public Sugarray reset() {
        final Sugarray self = this;
        return new Sugarray(mContext) {
            @Override
            protected SugarrayLauncher createLauncher() {
                return self.createLauncher();
            }
        };
    }

    /**
     * SDKのバージョンによって最適なHTTPハンドラーを生成する。
     *
     * @return
     */
    private HttpHandler createHttpHandler() {
        return Build.VERSION.SDK_INT >= 9 ? new HttpRestUrlHandler()
                : new HttpRestClientHandler();
    }

    /**
     * HttpResponseListenerのデフォルト実装
     */
    private static class DefaultHttpResponseListener extends
            HttpResponseListener {
        private static final String TAG = DefaultHttpResponseListener.class
                .getSimpleName();

        @Override
        public void onSuccess(SugarrayResponse response) {
            Logger.d(TAG, "onSuccess: " + response.stringfyBody());
        }

    }

    /**
     * DefaultProgressUpdateListenerのデフォルト実装
     */
    private static class DefaultProgressUpdateListener implements
            ProgressUpdateListener {
        @Override
        public void onProgressUpdate(long totalBytes, long progressBytes) {
        }
    }

    /**
     * HTTPレスポンスを処理するリスナー。
     */
    public static abstract class HttpResponseListener {
        /**
         * ログ出力用タグ
         */
        private static final String TAG = HttpResponseListener.class
                .getSimpleName();

        /**
         * 通信開始前に行いたい処理を実行する
         */
        public void preStart() {
            Logger.d(TAG, "preStart");
        }

        ;

        /**
         * 通信成功時に行いたい処理を実行する。
         *
         * @param response HTTPレスポンス
         */
        public abstract void onSuccess(SugarrayResponse response);

        /**
         * 通信失敗時に行いたい処理を実行する。
         *
         * @param error エラー情報
         */
        public void onError(SugarrayError error) {
            Logger.d(TAG, "onError");
        }

        ;

        /**
         * 通信終了時に行いたい処理を実行する。
         */
        public void onFinish() {
            Logger.d(TAG, "onFinish");
        }

        ;

    }

    /**
     * HTTP通信の進捗を処理するリスナー
     */
    public static interface ProgressUpdateListener {
        /**
         * @param totalBytes    メッセージボディの総サイズ(byte)
         * @param progressBytes 処理したメッセージボディの進捗(byte)
         */
        public void onProgressUpdate(long totalBytes, long progressBytes);
    }

}