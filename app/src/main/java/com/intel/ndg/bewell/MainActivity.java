package com.intel.ndg.bewell;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.intel.wearable.platform.core.ICoreInitListener;
import com.intel.wearable.platform.core.util.Logger;

public class MainActivity extends AppCompatActivity {

    private Intent mSettingsIntent;
    private Intent mDeviceScanningIntent;

    public static Context mContext;

    public static TextView sDeviceStatusText;
    public static TextView sLatestInquiryDateTime;
    public static TextView sLatestResponseDateTime;
    public static TextView sLatestResponseText;

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

        Application.init(new ICoreInitListener() {
            @Override
            public void onInitialized() {
                Logger.d("Core.onInitialized() in Application");
                sdkStatus.setText("Initialized successfully!");
            }
        });

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
}
