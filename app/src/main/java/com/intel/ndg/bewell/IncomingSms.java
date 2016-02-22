package com.intel.ndg.bewell;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.intel.wearable.platform.core.device.IWearableController;
import com.intel.wearable.platform.core.notification.INotificationController;
import com.intel.wearable.platform.core.notification.WearableNotification;

/**
 * Created by Vadim on 2/20/2016.
 */
public class IncomingSms extends BroadcastReceiver {

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            final String care_giver_phone_number = prefs.getString("caregiver_phone_number", "");
            final String care_giver_message = prefs.getString("caregiver_message", "");

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "caregiver_phone: " + care_giver_phone_number + "; senderNum: "+ senderNum + "; message: " + message);

                    // Show Alert
                    if (phoneNumber.contains(care_giver_phone_number) &&
                            care_giver_message.toLowerCase().contentEquals(message.toLowerCase())) {
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context,
                                "caregiver_phone: " + care_giver_phone_number + ", senderNum: " + senderNum + ", message: " + message, duration);
                        toast.show();

                        IWearableController wearableController = DevicePairingActivity.sWearableController;
                        INotificationController mNotificationController = wearableController.getNotificationController();
                        if (mNotificationController != null) {
                            WearableNotification.LedPattern ledPattern =
                                    new WearableNotification.LedPattern(
                                            WearableNotification.LedPattern.Type.LED_BLINK,
                                            0, //id
                                            null, //rgb color list
                                            2, //repetition count
                                            255);//intensity
                            ledPattern.addDuration(new WearableNotification.DurationPattern(500,500));
                            int r = 255;
                            int g = 1;
                            int b = 1;
                            ledPattern.addRGBColor(new WearableNotification.RGBColor(r, g, b));
                            WearableNotification notification = new WearableNotification(ledPattern);
                            mNotificationController.sendNotification(notification);
                        }
                    }

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }
}