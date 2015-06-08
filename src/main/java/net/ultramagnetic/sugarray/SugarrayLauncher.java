package net.ultramagnetic.sugarray;

import android.content.Context;

import net.ultramagnetic.sugarray.Sugarray.HttpResponseListener;
import net.ultramagnetic.sugarray.Sugarray.ProgressUpdateListener;
import net.ultramagnetic.sugarray.error.NetworkStateError;
import net.ultramagnetic.sugarray.error.SugarrayError;
import net.ultramagnetic.sugarray.util.Logger;
import net.ultramagnetic.sugarray.util.NetworkUtils;
import net.ultramagnetic.sugarray.util.NetworkUtils.NetworkState;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * ExecutorService(スレッドプール)の生成処理を抽象化した、HTTPリクエストをバックグラウンド実行するランチャー
 */
abstract class SugarrayLauncher {

    private static final String TAG = SugarrayLauncher.class.getSimpleName();

    private Future<?> mFuture;

    protected SugarrayLauncher() {
    }

    /**
     * HTTPリクエストを実行します。
     *
     * @param option
     * @param httpHandler
     * @param httpResponseListener
     * @param progressUpdateListener
     */
    public void launch(final Context context, final RequestOption option,
                       final HttpHandler httpHandler,
                       final HttpResponseListener httpResponseListener,
                       final ProgressUpdateListener progressUpdateListener) {
        if (httpHandler == null) {
            throw new IllegalArgumentException("HttpHandler is null.");
        }
        if (httpResponseListener == null) {
            throw new IllegalArgumentException("HttpResponseListener is null.");
        }
        mFuture = getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    httpResponseListener.preStart();
                    NetworkState networkState = NetworkUtils
                            .checkNetworkEnable(context);
                    switch (networkState) {
                        case ENABLE_MOBILE:
                            Logger.d(TAG,
                                    "The device is connected to the mobile network.");
                            break;
                        case ENABLE_WIFI:
                            Logger.d(TAG,
                                    "The device is connected to the wifi network.");
                            break;
                        case ENABLE_UNKNOWN:
                            Logger.d(TAG,
                                    "The device is connected to the unknown network.");
                            break;
                        default:
                            throw new NetworkStateError(networkState,
                                    "The device is not connected to the network.");
                    }
                    SugarrayResponse response = httpHandler.perform(option,
                            progressUpdateListener);
                    httpResponseListener.onSuccess(response);
                } catch (SugarrayError e) {
                    Logger.e(TAG, "SugarrayError.", e);
                    httpResponseListener.onError(e);
                } finally {
                    httpResponseListener.onFinish();
                }
            }
        });
    }

    /**
     * キュー内の未実行タスクを全てキャンセルします。
     */
    public void shutdown() {
        this.getExecutor().shutdown();
    }

    /**
     * 対象のタスクをキャンセルします。
     */
    public void cancel() {
        mFuture.cancel(true);
    }

    /**
     * ExecutorServiceを返します。
     *
     * @return ExecutorServiceのインスタンス
     */
    protected abstract ExecutorService getExecutor();
}
