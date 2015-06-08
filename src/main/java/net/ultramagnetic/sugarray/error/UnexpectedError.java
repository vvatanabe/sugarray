package net.ultramagnetic.sugarray.error;

/**
 * 意図しないエラーを示す例外クラス
 */
@SuppressWarnings("serial")
public class UnexpectedError extends SugarrayError {

    public UnexpectedError(String message) {
        this(message, null);
    }

    public UnexpectedError(Throwable tr) {
        this(null, tr);
    }

    public UnexpectedError(String message, Throwable tr) {
        super(message, tr, null);
    }

}
