package net.ultramagnetic.sugarray.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * @author watanayu
 */
public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private NetworkUtils() {
        ;
    }

    public enum NetworkState {
        ENABLE_UNKNOWN, ENABLE_MOBILE, ENABLE_WIFI, DISABLE_ACTIVE_NETWORK, DISABLE_ALL;
    }

    public static NetworkState checkNetworkEnable(Context context) {
        if (context == null) {
            Logger.e(TAG, "Context is null.");
            throw new IllegalArgumentException("Context is null.");
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            Logger.e(TAG, "ConnectivityManager instance is null.");
            throw new IllegalStateException(
                    "ConnectivityManager instance is null");
        }
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null) {
            Logger.e(TAG, "NetworkInfo instance is null.");
            return NetworkState.DISABLE_ACTIVE_NETWORK;
        }
        if (!info.isConnected()) {
            Logger.d(TAG, "Network state is all disable.");
            return NetworkState.DISABLE_ALL;
        }
        if ("MOBILE".equals(info.getTypeName())) {
            Logger.d(TAG, "Network state is mobile enable.");
            return NetworkState.ENABLE_MOBILE;
        }
        if ("WIFI".equals(info.getTypeName()) && isWifiEnable(context)) {
            Logger.d(TAG, "Network state is wifi enable.");
            return NetworkState.ENABLE_WIFI;
        }
        return NetworkState.ENABLE_UNKNOWN;
    }

    private static boolean isWifiEnable(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiManager.getWifiState();
        switch (wifiState) {
            case WifiManager.WIFI_STATE_DISABLING:
                return false;
            case WifiManager.WIFI_STATE_DISABLED:
                return false;
            case WifiManager.WIFI_STATE_ENABLING:
                return true;
            case WifiManager.WIFI_STATE_ENABLED:
                return true;
            case WifiManager.WIFI_STATE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

}
