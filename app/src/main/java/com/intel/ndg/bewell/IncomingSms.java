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

import com.intel.wearable.platform.core.device.IWearableController;
import com.intel.wearable.platform.core.notification.INotificationController;
import com.intel.wearable.platform.core.notification.WearableNotification;

import java.text.SimpleDateFormat;

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
            final String wellbeing_message = prefs.getString("wellbeing_message", "");
            final String activity_message = prefs.getString("activity_message", "");
            final Boolean led_switch = prefs.getBoolean("led_switch", false);
            final Integer led_color = Integer.decode(prefs.getString("led_list", ""));
            final Boolean haptic_switch = prefs.getBoolean("haptic_switch", false);
            final Integer haptic_intensity = Integer.decode(prefs.getString("haptic_list", ""));
            final Boolean receipt_switch = prefs.getBoolean("receipt_switch", false);
            final OutgoingSms smsSender = new OutgoingSms();

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "caregiver_phone: " + care_giver_phone_number + "; senderNum: "+ senderNum + "; message: " + message);


                    if (phoneNumber.contains(care_giver_phone_number) &&
                            wellbeing_message.toLowerCase().contentEquals(message.toLowerCase())) {
                        /*
                        // Show Alert
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context,
                                "caregiver_phone: " + care_giver_phone_number + ", senderNum: " + senderNum + ", message: " + message, duration);
                        toast.show();
                        */
                        IWearableController wearableController = DevicePairingActivity.getWearableController();
                        INotificationController mNotificationController = wearableController.getNotificationController();
                        WearableNotification.VibrationPattern vibrationPattern = null;
                        WearableNotification.LedPattern ledPattern = null;

                        if (mNotificationController != null) {
                            if (led_switch) {
                                ledPattern = new WearableNotification.LedPattern(
                                                WearableNotification.LedPattern.Type.LED_BLINK,
                                                0, //id
                                                null, //rgb color list
                                                2, //repetition count
                                                255);//intensity
                                ledPattern.addDuration(new WearableNotification.DurationPattern(500, 500));
                                int r = (led_color >> 16) & 0xFF;
                                int g = (led_color >> 8) & 0xFF;
                                int b = (led_color >> 0) & 0xFF;
                                ledPattern.addRGBColor(new WearableNotification.RGBColor(r, g, b));
                            }
                            if (haptic_switch) {
                                vibrationPattern = new WearableNotification.VibrationPattern(
                                                WearableNotification.VibrationPattern.Type.VIBRA_SQUARE,
                                                haptic_intensity, 2);
                                vibrationPattern.addDuration(
                                        new WearableNotification.DurationPattern(500,500));
                            }

                            // Build notification
                            WearableNotification notification = null;
                            if (ledPattern != null & vibrationPattern != null) {
                                notification = new WearableNotification(vibrationPattern, ledPattern, 0);
                            } else if (ledPattern != null) {
                                notification = new WearableNotification(ledPattern);
                            } else if (vibrationPattern != null) {
                                notification = new WearableNotification(vibrationPattern);
                            }
                            mNotificationController.sendNotification(notification);

                            if (receipt_switch) {
                                smsSender.sendCRResponse(care_giver_phone_number,
                                        "Auto response: notification delivered!");
                            }

                            // Setting CR Response waiting state
                            Application.setCRResponseWaitingState(true);

                            // Setting latest SMS request date and time
                            long date = System.currentTimeMillis();
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
                            String dateString = sdf.format(date);
                            MainActivity.sLatestInquiryDateTime.setText(dateString);
                        }
                    }
                    else if ((phoneNumber.contains(care_giver_phone_number) &&
                            activity_message.toLowerCase().contentEquals(message.toLowerCase()))) {

                        String activity_report = Application.getActivityReport();
                        smsSender.sendCRResponse(care_giver_phone_number, activity_report);
                    }

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }
}