package com.intel.ndg.bewell;

import android.content.Context;
import android.util.Log;

import com.intel.wearable.platform.body.Body;
import com.intel.wearable.platform.body.listen.ActivityIntervalListener;
import com.intel.wearable.platform.body.model.ActivityInterval;
import com.intel.wearable.platform.body.model.ActivityStepInterval;
import com.intel.wearable.platform.body.model.BiologicalSex;
import com.intel.wearable.platform.body.model.Profile;
import com.intel.wearable.platform.body.persistence.BodyDataStore;
import com.intel.wearable.platform.core.ICoreInitListener;
import com.intel.wearable.platform.core.model.datastore.UserIdentity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vadim on 2/20/2016.
 */
public class Application extends android.app.Application {

    private static Application instance;
    private static boolean sIsInitialized = false;
    private static boolean sAwatingCRResponse = false;

    private static Integer total_step_count = 0;
    private static double total_distance = 0.0;
    private static double total_calories = 0.0;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static void init(ICoreInitListener initListener){
        if (!sIsInitialized) {
            sIsInitialized = true;
            byte[] key = new byte[64];
            Body.init(getContext(), initListener, key);
            // Set sample user profile
            Profile profile = BodyDataStore.getCurrentProfile();
            if (profile.height == Profile.UNKNOWN || profile.weight == Profile.UNKNOWN) {
                final UserIdentity identity = new UserIdentity(
                        null, // UUID is set on save
                        "External Service Id",
                        "First Name",
                        "Last Name",
                        "Email Address",
                        "Phone Number"
                );
                BodyDataStore.setCurrentUserIdentity(identity);
                BodyDataStore.setBiologicalSex(BiologicalSex.FEMALE);
                BodyDataStore.setHeight(168);
                BodyDataStore.setWeight(56);
            }

            class Listener implements ActivityIntervalListener {
                public void onActivityInterval(final ActivityInterval interval) {
                    Log.i("Listener", String.format("ActivityInterval(%s) received.", interval.uuid));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

                    total_step_count += ((ActivityStepInterval) interval).stepCount;
                    total_distance += ((ActivityStepInterval) interval).metersTraveled;
                    total_calories += ((ActivityStepInterval) interval).caloriesBurned;

                    MainActivity.updateActivityUI(dateFormat.format(new Date()),
                            String.valueOf(total_step_count),
                            String.format("%.2f", total_distance),
                            String.format("%.0f", total_calories),
                            String.valueOf(interval.status),
                            String.valueOf(interval.type));

                    MainActivity.resetInactivityHandler();
                }
            }
            Body.addActivityListener(new Listener());
        }
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    public static boolean isInitialized(){
        return sIsInitialized;
    }
    public static boolean isAwatingCRResponse() { return sAwatingCRResponse; }
    public static void setCRResponseWaitingState(boolean state) { sAwatingCRResponse = state; }
    public static String getActivityReport() {
        return String.format("Steps %d, Distance %.2f meters, Calories %.0f",
                total_step_count, total_distance, total_calories);
    }
}
