package net.ultramagnetic.sugarray;

import net.ultramagnetic.sugarray.error.SugarrayError;

/**
 * HTTP通信の実処理を行うハンドラのインターフェイス
 */
public interface HttpHandler {

    /**
     * HTTP通信を行う
     *
     * @param option
     * @param progressUpdateListener
     * @return SugarrayResponse
     * @throws net.ultramagnetic.sugarray.error.SugarrayError
     */
    public SugarrayResponse perform(final RequestOption option,
                                    final Sugarray.ProgressUpdateListener progressUpdateListener)
            throws SugarrayError;

}