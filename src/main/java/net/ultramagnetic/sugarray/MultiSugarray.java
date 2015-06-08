package net.ultramagnetic.sugarray;

import android.content.Context;

public class MultiSugarray extends Sugarray {

    private MultiSugarray(Context context) {
        super(context);
    }

    public static Sugarray init(Context context) {
        return new MultiSugarray(context);
    }

    @Override
    protected SugarrayLauncher createLauncher() {
        return new MultiSugarrayLauncher();
    }
}