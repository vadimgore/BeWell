package com.intel.ndg.bewell;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.intel.wearable.platform.core.ICoreInitListener;
import com.intel.wearable.platform.core.util.Logger;

public class MainActivity extends AppCompatActivity {

    private Intent mSettingsIntent;
    private Intent mDeviceScanningIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        final TextView statusTextView = (TextView) findViewById(R.id.StatusTextView);
        statusTextView.setText("Core NOT initialized!");


        Application.init(new ICoreInitListener() {
            @Override
            public void onInitialized() {
                Logger.d("Core.onInitialized() in Application");
                statusTextView.setText("Core initialized.");
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
}
