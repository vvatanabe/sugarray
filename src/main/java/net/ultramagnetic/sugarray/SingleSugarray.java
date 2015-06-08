package net.ultramagnetic.sugarray;

import android.content.Context;

public class SingleSugarray extends Sugarray {

    private SingleSugarray(Context context) {
        super(context);
    }

    public static Sugarray init(Context context) {
        return new SingleSugarray(context);
    }

    @Override
    protected SugarrayLauncher createLauncher() {
        return new SingleSugarrayLauncher();
    }
}