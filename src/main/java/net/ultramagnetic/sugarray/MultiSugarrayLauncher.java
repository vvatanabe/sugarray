package net.ultramagnetic.sugarray;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HTTPリクエストを複数のスレッドでバックグラウンド実行するランチャー
 */
public class MultiSugarrayLauncher extends SugarrayLauncher {

    public MultiSugarrayLauncher() {
        super();
    }

    /**
     * マルチスレッドのExecutorServiceインスタンス(コアの数スレッドを生成)
     */
    private static final ExecutorService EXECUTOR = Executors
            .newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    protected synchronized ExecutorService getExecutor() {
        return EXECUTOR;
    }

}
