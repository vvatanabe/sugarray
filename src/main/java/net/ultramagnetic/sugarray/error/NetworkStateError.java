package net.ultramagnetic.sugarray.error;

import net.ultramagnetic.sugarray.util.NetworkUtils.NetworkState;

/**
 * デバイスのネットワーク状態に起因するエラーを示す例外クラス
 */
@SuppressWarnings("serial")
public class NetworkStateError extends NetworkError {

    private final NetworkState mNetworkState;

    public NetworkStateError(NetworkState networkState, String message) {
        super(message);
        mNetworkState = networkState;
    }

    public NetworkState getNetworkState() {
        return mNetworkState;
    }
}
