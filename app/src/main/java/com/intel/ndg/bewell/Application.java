package com.intel.ndg.bewell;

import android.content.Context;

import com.intel.wearable.platform.core.Core;
import com.intel.wearable.platform.core.ICoreInitListener;
import java.util.logging.Logger;

/**
 * Created by Vadim on 2/20/2016.
 */
public class Application extends android.app.Application {

    private static Application instance;
    private static boolean mIsInitialized = false;
    private static boolean mAwatingCRResponse = false;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static final void init(ICoreInitListener initListener){
        if (!mIsInitialized) {
            mIsInitialized = true;
            byte[] key = new byte[64];
            Core.init(getContext(), initListener, key);
        }
    }

    public static final Context getContext(){
        return instance.getApplicationContext();
    }

    public static final boolean isInitialized(){
        return mIsInitialized;
    }
    public static final boolean ismAwatingCRResponse() { return mAwatingCRResponse; }
    public static void setCRResponseWaitingState(boolean state) { mAwatingCRResponse = state; }
}
