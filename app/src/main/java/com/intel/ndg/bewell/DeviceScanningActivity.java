package com.intel.ndg.bewell;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.intel.wearable.platform.core.device.IWearableScanner;
import com.intel.wearable.platform.core.device.WearableScannerFactory;
import com.intel.wearable.platform.core.device.WearableToken;
import com.intel.wearable.platform.core.device.listeners.IWearableScannerListener;
import com.intel.wearable.platform.core.util.Logger;

import java.util.ArrayList;

public class DeviceScanningActivity extends AppCompatActivity {

    private Button mScanButton;
    private ProgressBar mScanProgressBar;
    private TextView mScanStatus;
    private ListView mDevicesListView;
    private DevicesListAdapter mDevicesListAdapter;

    private boolean mScanning = false;
    private static final int REQUEST_ENABLE_BT = 1;
    private ArrayList<WearableToken> mDevicesArray;
    private IWearableScanner mScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scanning);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mScanner = WearableScannerFactory.getDefaultScanner();

        mScanButton = (Button)findViewById(R.id.scan_button);
        mScanProgressBar = (ProgressBar) findViewById(R.id.scan_progressBar);
        mScanStatus = (TextView) findViewById(R.id.scan_status);
        mDevicesListView = (ListView) findViewById(R.id.devices_ListView);
        mDevicesArray = new ArrayList<>();
        mDevicesListAdapter = new DevicesListAdapter(this, R.id.devices_ListView, mDevicesArray);
        mDevicesListView.setAdapter(mDevicesListAdapter);
        mDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                //pair device:
                if (mScanning) {
                    stopScan();
                }
                WearableToken token = mDevicesArray.get(position);
                if (token != null) {
                    Intent pairingActivityIntent = new Intent(getApplicationContext(), DevicePairingActivity.class);
                    pairingActivityIntent.putExtra(DevicePairingActivity.WEARABLE_TOKEN, token);
                    startActivity(pairingActivityIntent);
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, R.string.copyright, Snackbar.LENGTH_LONG)
                        .setAction("Copyright", null).show();

            }
        });

    }

    public void onScanClicked(View view) {
        if( ! mScanning ){
            startScan();
        }
        else{
            stopScan();
        }
    }

    public void startScan(){
        mScanButton.setText(R.string.stop_scan_button_text);
        mScanning = true;
        mScanProgressBar.setVisibility(View.VISIBLE);
        mScanStatus.setText("Scanning in progress...");
        mDevicesArray.clear();
        mDevicesListAdapter.notifyDataSetChanged();

        mScanner.startScan(new IWearableScannerListener() {
            @Override
            public void onWearableFound(IWearableScanner iWearableScanner, WearableToken wearableToken) {
                mScanStatus.setText("Devices found");
                mDevicesArray.add(wearableToken);
                mDevicesListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScannerError(IWearableScanner wearableScanner, com.intel.wearable.platform.core.error.Error error) {
                stopScan();
                if (error.getErrorCode() == com.intel.wearable.platform.core.error.Error.BLE_ERROR_BT_DISABLED) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    Logger.d("Scanning error: ", "Code: " + error.getErrorCode(), "Message: " + error.getErrorMessage());
                    Toast.makeText(getApplication(), error.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void stopScan(){

        mScanner.stopScan();

        mScanButton.setText(R.string.scan_button_text);
        mScanning = false;
        mScanProgressBar.setVisibility(View.GONE);
        mScanStatus.setText("Scannig finished");
    }
}
