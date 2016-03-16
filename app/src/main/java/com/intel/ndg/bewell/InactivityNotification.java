package com.intel.ndg.bewell;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.intel.wearable.platform.core.device.IWearableController;
import com.intel.wearable.platform.core.notification.INotificationController;
import com.intel.wearable.platform.core.notification.WearableNotification;

/**
 * Created by Vadim on 3/14/2016.
 */
public class InactivityNotification {

    public InactivityNotification(Context context) {

        try {
            final OutgoingSms sms = new OutgoingSms();

            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            final String mCGPhoneNumber = prefs.getString("caregiver_phone_number", "");
            final String mInactivityThreshold = prefs.getString("inactivity_threshold", "");
            final Boolean activity_switch = prefs.getBoolean("user_activity_switch", false);
            final Boolean led_switch = prefs.getBoolean("activity_led_switch", false);
            final Integer led_color = Integer.decode(prefs.getString("activity_led_list", ""));
            final Boolean haptic_switch = prefs.getBoolean("activity_haptic_switch", false);
            final Integer haptic_intensity = Integer.decode(prefs.getString("activity_haptic_list", ""));

            if (!activity_switch) {
                Log.e("InactivityNotification", "Activity notifications disabled");
                return;
            }
            // Send SMS notification to care giver
            sms.sendCRResponse(mCGPhoneNumber, "The care recipient has been inactive for " +
                    mInactivityThreshold + " seconds");

            // Show inactivity notification on wearable device
            IWearableController wearableController = DevicePairingActivity.getWearableController();
            if (wearableController == null || !wearableController.isConnected()) {
                Log.e("InactivityNotification", "Wearable device not connected");
                return;
            }

            INotificationController mNotificationController = wearableController.getNotificationController();
            WearableNotification.VibrationPattern vibrationPattern = null;
            WearableNotification.LedPattern ledPattern = null;

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

        } catch (Exception e) {
            Log.e("InactivityNotification", "Exception " + e);
        }
    }
}
