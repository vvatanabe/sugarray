package net.ultramagnetic.sugarray;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

/**
 * httpリクエストに必要な設定情報をまとめたクラス
 */
public class RequestOption {
    final String mUrl;
    final SugarrayConstants.Method mMethod;
    final Map<String, String> mHeaders;
    final Map<String, String> mQuery;
    final byte[] mBody;
    final File mFile;
    final String mProxyhost;
    final int mProxyPort;
    final SSLSocketFactory mSSLSocketFactory;
    final int mRetryMax;
    final int mRetryInterval;
    final int mTimeout;

    private RequestOption(Bulider bulider) {
        if (bulider.mUrl == null) {
            throw new IllegalArgumentException("URL is null");
        }
        mUrl = bulider.mUrl;
        if (bulider.mMethod == null) {
            throw new IllegalArgumentException("Method is null");
        }
        mMethod = bulider.mMethod;
        if (bulider.mBody == null) {
            bulider.mBody = new byte[0];
        }
        mBody = bulider.mBody;
        mFile = bulider.mFile;
        mProxyhost = bulider.mProxyhost;
        mProxyPort = bulider.mProxyPort;
        mSSLSocketFactory = bulider.mSSLSocketFactory;
        mRetryMax = bulider.mRetryMax;
        mRetryInterval = bulider.mRetryInterval;
        mTimeout = bulider.mTimeout;
        if (bulider.mHeaders == null) {
            bulider.mHeaders = new HashMap<String, String>();
        }
        mHeaders = bulider.mHeaders;
        if (bulider.mQuery == null) {
            bulider.mQuery = new HashMap<String, String>();
        }
        mQuery = bulider.mQuery;
    }

    public static class Bulider {
        private String mUrl;
        private SugarrayConstants.Method mMethod;
        private Map<String, String> mHeaders;
        private Map<String, String> mQuery;
        private byte[] mBody;
        private File mFile;
        private String mProxyhost;
        private int mProxyPort;
        private SSLSocketFactory mSSLSocketFactory;
        private int mRetryMax;
        private int mRetryInterval;
        private int mTimeout;

        public Bulider url(String url) {
            mUrl = url;
            return this;
        }

        public Bulider method(SugarrayConstants.Method method) {
            mMethod = method;
            return this;
        }

        public Bulider headers(Map<String, String> headers) {
            mHeaders = headers;
            return this;
        }

        public Bulider query(Map<String, String> query) {
            mQuery = query;
            return this;
        }

        public Bulider body(byte[] body) {
            mBody = body;
            return this;
        }

        public Bulider file(File file) {
            mFile = file;
            return this;
        }

        public Bulider proxyhost(String proxyhost) {
            mProxyhost = proxyhost;
            return this;
        }

        public Bulider proxyPort(int proxyPort) {
            mProxyPort = proxyPort;
            return this;
        }

        public Bulider sslSocketFactory(SSLSocketFactory sslSocketFactory) {
            mSSLSocketFactory = sslSocketFactory;
            return this;
        }

        public Bulider retryMax(int retryMax) {
            mRetryMax = retryMax;
            return this;
        }

        public Bulider retryInterval(int retryInterval) {
            mRetryInterval = retryInterval;
            return this;
        }

        public Bulider timeout(int timeout) {
            mTimeout = timeout;
            return this;
        }

        public RequestOption bulid() {
            return new RequestOption(this);
        }

    }
}
