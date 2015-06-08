package net.ultramagnetic.sugarray.error;

import net.ultramagnetic.sugarray.SugarrayResponse;
import net.ultramagnetic.sugarray.util.Logger;

/**
 * Sugarrayパッケージ内全ての例外の基底クラス。
 */
@SuppressWarnings("serial")
public class SugarrayError extends Exception {

    private static final String TAG = SugarrayError.class.getSimpleName();

    public final SugarrayResponse mResponse;

    public SugarrayError(SugarrayResponse response) {
        this(null, null, response);
    }

    public SugarrayError(String message) {
        this(message, null, null);
    }

    public SugarrayError(Throwable tr) {
        this(null, tr, null);
    }

    public SugarrayError(String message, Throwable tr, SugarrayResponse response) {
        super(message, tr);
        mResponse = response;
        Logger.e(TAG, "SugarrayError: " + message, tr);
    }
}
