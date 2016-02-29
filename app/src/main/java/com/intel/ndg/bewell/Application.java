package com.intel.ndg.bewell;

import android.content.Context;

import com.intel.wearable.platform.core.Core;
import com.intel.wearable.platform.core.ICoreInitListener;

/**
 * Created by Vadim on 2/20/2016.
 */
public class Application extends android.app.Application {

    private static Application instance;
    private static boolean sIsInitialized = false;
    private static boolean sAwatingCRResponse = false;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static final void init(ICoreInitListener initListener){
        if (!sIsInitialized) {
            sIsInitialized = true;
            byte[] key = new byte[64];
            Core.init(getContext(), initListener, key);
        }
    }

    public static final Context getContext(){
        return instance.getApplicationContext();
    }

    public static final boolean isInitialized(){
        return sIsInitialized;
    }
    public static final boolean isAwatingCRResponse() { return sAwatingCRResponse; }
    public static void setCRResponseWaitingState(boolean state) { sAwatingCRResponse = state; }
}
