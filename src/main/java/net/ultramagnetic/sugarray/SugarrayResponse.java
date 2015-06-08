package net.ultramagnetic.sugarray;

import net.ultramagnetic.sugarray.util.StringUtils;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTPレスポンスの表したクラスです。
 */
public class SugarrayResponse {
    /**
     * ステータスコード
     */
    public final int mStatusCode;
    /**
     * メッセージボディ
     */
    public final byte[] mBody;
    /**
     * コンテンツタイプ
     */
    public final String mContentType;
    /**
     * メッセージボディのキャラセット
     */
    public final Charset mCharset;
    /**
     * HTTPヘッダー
     */
    public final Map<String, String> mHeaders;
    /**
     * レスポンスが更新されていればfalse
     */
    public final boolean mNotModified;
    /**
     * メッセージボディをストリングに変換した際のキャッシュ
     */
    private String mBodyStringCache;

    /**
     * @param statusCode
     * @param body
     * @param contentType
     * @param charsetName
     * @param headers
     * @param notModified
     */
    public SugarrayResponse(int statusCode, byte[] body, String contentType,
                            String charsetName, Map<String, String> headers, boolean notModified) {
        mStatusCode = statusCode;
        mBody = body != null ? body : new byte[0];
        mContentType = contentType;
        mCharset = createCharset(charsetName);
        mHeaders = headers != null ? headers : new HashMap<String, String>();
        mNotModified = notModified;
    }

    /**
     * @return メッセージボディを文字列に変換したもの
     */
    public String stringfyBody() {
        if (mBodyStringCache == null) {
            mBodyStringCache = new String(mBody, mCharset);
        }
        return mBodyStringCache;
    }

    /**
     * キャラセットの文字列からキャラセットのインスタンスを生成する。
     *
     * @param charsetName
     * @return Charset
     */
    private static Charset createCharset(String charsetName) {
        if (StringUtils.isEmpty(charsetName)
                || !Charset.isSupported(charsetName)) {
            return Charset.defaultCharset();
        }
        return Charset.forName(charsetName);
    }
}