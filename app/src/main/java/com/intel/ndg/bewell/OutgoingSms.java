package com.intel.ndg.bewell;

import android.telephony.SmsManager;

/**
 * Created by vadimgore on 2/23/16.
 */
public class OutgoingSms {
    private final SmsManager mSmsManager = SmsManager.getDefault();
    public enum resposeType {
        OKAY,
        NOT_OKAY,
        EMERGENCY
    };

    public void sendCRResponse(resposeType type) {
        switch (type) {
            case OKAY:
                mSmsManager.sendTextMessage("6507966522", null, "I am Okay!", null, null);
                break;
            case NOT_OKAY:
                mSmsManager.sendTextMessage("6507966522", null, "I need help", null, null);
                break;
            case EMERGENCY:
                mSmsManager.sendTextMessage("6507966522", null, "I have emergency", null, null);
                break;
            default:
                // do nothing
        }
    }
}
