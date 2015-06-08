package net.ultramagnetic.sugarray;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HTTPリクエストを単一のスレッドでバックグラウンド実行するランチャー
 */
public class SingleSugarrayLauncher extends SugarrayLauncher {

    public SingleSugarrayLauncher() {
        super();
    }

    /**
     * シングルスレッドのExecutorServiceインスタンス
     */
    private static final ExecutorService EXECUTOR = Executors
            .newSingleThreadExecutor();

    @Override
    protected synchronized ExecutorService getExecutor() {
        return EXECUTOR;
    }

}