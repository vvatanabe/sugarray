package net.ultramagnetic.sugarray;

/**
 * 同ライブラリの定数クラス
 */
public class SugarrayConstants {

    /**
     * HTTPリクエストのデフォルトリトライ方針を定義しています。
     */
    static class DefaultRetryConfig {
        /**
         * 最大リトライ回数
         */
        static final int DEFAULT_RETRY_MAX = 1;
        /**
         * リトライ間隔
         */
        static final int DEFAULT_RETRY_INTERVAL = 1000 * 5;
        /**
         * タイムアウト時間
         */
        static final int DEFAULT_TIMEOUT = 1000 * 30;
    }

    /**
     * HTTPリクエストヘッダーのフィールド名を定義しています。
     */
    static class HeaderFields {
        static final String HOST = "Host";
        static final String CONTENT_TYPE = "Content-Type";
        static final String ACCEPT = "Accept";
        static final String CONNECTION = "Connection";
        static final String USER_AGENT = "User-Agent";
        static final String AUTHORIZATION = "Authorization";
        static final String CONTENT_LENGTH = "Content-Length";
    }

    /**
     * HTTPリクエストのメソッドを定義しています。
     */
    public static enum Method {
        GET, POST, PUT, DELETE
    }

    /**
     * HTTPリクエストヘッダーのコンテンツタイプの値を定義しています。
     */
    public static enum ContentType {
        TXT(100, "text/plain"), HTML(101, "text/html"), JSON(102,
                "application/json"), XML(103, "application/xml"), GIF(200,
                "image/gif"), PNG(201, "image/png"), JPG(202, "image/jpeg"), MULTIPART(
                300, "multipart/form-data");

        private final int number;
        private final String value;

        private ContentType(int number, String value) {
            this.number = number;
            this.value = value;
        }

        public int number() {
            return number;
        }

        public String value() {
            return value;
        }

        public static ContentType fromString(String value) {
            for (ContentType contentType : ContentType.values()) {
                if (contentType.value().equals(value)) {
                    return contentType;
                }
            }
            return null;
        }
    }

    /**
     * HTTPリクエストヘッダーのコネクションの値を定義しています。
     */
    public static enum Connection {
        KEEPALIVE("Keep-Alive"), CLOSE("close");

        private final String value;

        private Connection(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        public static Connection fromString(String value) {
            for (Connection connection : Connection.values()) {
                if (connection.value().equals(value)) {
                    return connection;
                }
            }
            return null;
        }
    }

}
