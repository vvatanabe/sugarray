package net.ultramagnetic.sugarray.error;

import net.ultramagnetic.sugarray.SugarrayResponse;

@SuppressWarnings("serial")
public class NetworkError extends SugarrayError {

    public NetworkError(SugarrayResponse response) {
        this(null, null, response);
    }

    public NetworkError(String message) {
        this(message, null, null);
    }

    public NetworkError(Throwable tr) {
        this(null, tr, null);
    }

    public NetworkError(String message, Throwable tr, SugarrayResponse response) {
        super(message, tr, response);
    }
}
