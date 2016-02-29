package com.intel.ndg.bewell;

import android.telephony.SmsManager;

/**
 * Created by vadimgore on 2/23/16.
 */
public class OutgoingSms {
    private final SmsManager mSmsManager = SmsManager.getDefault();

    public void sendCRResponse(String phone_number, String message) {
        mSmsManager.sendTextMessage(phone_number, null, message, null, null);
    }
}
