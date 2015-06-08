package net.ultramagnetic.sugarray.error;

import net.ultramagnetic.sugarray.SugarrayResponse;

/**
 * サーバーサイドに起因するエラーを示す例外クラス
 */
@SuppressWarnings("serial")
public class ServerError extends NetworkError {
    public ServerError(SugarrayResponse response) {
        super(response);
    }
}
