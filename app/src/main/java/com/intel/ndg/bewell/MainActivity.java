package com.intel.ndg.bewell;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.intel.wearable.platform.core.ICoreInitListener;
import com.intel.wearable.platform.core.util.Logger;

public class MainActivity extends AppCompatActivity {

    private Intent mSettingsIntent;
    private Intent mDeviceScanningIntent;
    private static Handler sActivityHandler;
    private static Runnable sActivityHandlerTask;
    private static Integer sInactivityThreshold;

    private SharedPreferences.OnSharedPreferenceChangeListener mSPChangeListener;

    public static Context mContext;

    public static TextView sDeviceStatusText;
    public static TextView sLatestInquiryDateTime;
    public static TextView sLatestResponseDateTime;
    public static TextView sLatestResponseText;
    public static TextView sLatestActivityDateTime;
    public static TextView sLatestActivityText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, R.string.copyright, Snackbar.LENGTH_LONG)
                        .setAction("Copyright", null).show();

            }
        });

        final TextView sdkStatus = (TextView) findViewById(R.id.sdk_status_text);
        sdkStatus.setText("Initialization failed!");

        sDeviceStatusText = (TextView) findViewById(R.id.device_status_text);
        sLatestInquiryDateTime = (TextView) findViewById(R.id.latest_inquiry_date_time);
        sLatestResponseDateTime = (TextView) findViewById(R.id.latest_response_date_time);
        sLatestResponseText = (TextView) findViewById(R.id.latest_response_text);
        sLatestActivityDateTime = (TextView) findViewById(R.id.latest_activity_date_time);
        sLatestActivityText = (TextView) findViewById(R.id.latest_activity_text);

        Application.init(new ICoreInitListener() {
            @Override
            public void onInitialized() {
                Logger.d("Core.onInitialized() in Application");
                sdkStatus.setText("Initialized successfully!");
            }
        });

        // Register shared preferences change listener
        mSPChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                          String key) {
                        Log.i("PreferencesListener", "Changed shared preferences for " + key);
                        if (key.equals("inactivity_threshold")) {
                            sInactivityThreshold = 1000 * Integer.decode(sharedPreferences.getString(key, ""));
                            // Start inactivity monitoring task
                            sActivityHandler = new Handler();
                            sActivityHandlerTask = new Runnable() {
                                @Override
                                public void run() {
                                    InactivityNotification inNot = new InactivityNotification(mContext);
                                    sActivityHandler.removeCallbacks(sActivityHandlerTask);
                                    if (sInactivityThreshold > 0 )
                                        sActivityHandler.postDelayed(sActivityHandlerTask, sInactivityThreshold);
                                }
                            };
                            sActivityHandler.postDelayed(sActivityHandlerTask, sInactivityThreshold);
                        }
                    }
                };
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.registerOnSharedPreferenceChangeListener(mSPChangeListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                mSettingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(mSettingsIntent);
                return true;
            case R.id.device_scanning:
                mDeviceScanningIntent = new Intent(this, DeviceScanningActivity.class);
                startActivity(mDeviceScanningIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void updateResponseUI(final String date, final String message){
        ((Activity) mContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                sLatestResponseDateTime.setText(date);
                sLatestResponseText.setText(message);
            }
        });
    }

    public static void updateActivityUI(final String date, final String message){
        ((Activity) mContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                sLatestActivityDateTime.setText(date);
                sLatestActivityText.setText(message);
            }
        });
    }

    public static void resetInactivityHandler() {
        ((Activity) mContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                sActivityHandler.removeCallbacks(sActivityHandlerTask);
                sActivityHandler.postDelayed(sActivityHandlerTask, sInactivityThreshold);
            }
        });
    }
}
