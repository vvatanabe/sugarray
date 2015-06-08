package net.ultramagnetic.sugarray.error;

import net.ultramagnetic.sugarray.SugarrayResponse;

@SuppressWarnings("serial")
public class ClientError extends NetworkError {
    public ClientError(SugarrayResponse response) {
        super(response);
    }
}
