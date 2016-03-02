package com.intel.ndg.bewell;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.intel.wearable.platform.core.device.IWearableController;
import com.intel.wearable.platform.core.device.WearableBatteryStatus;
import com.intel.wearable.platform.core.device.WearableControllerFactory;
import com.intel.wearable.platform.core.device.WearableToken;
import com.intel.wearable.platform.core.device.listeners.IWearableControllerListener;
import com.intel.wearable.platform.core.event.user.UserEventController;
import com.intel.wearable.platform.core.model.datastore.WearableIdentity;

public class DevicePairingActivity extends AppCompatActivity implements IWearableControllerListener {

    public static final String WEARABLE_TOKEN = "WEARABLE_TOKEN";

    private static final String TAG = DevicePairingActivity.class.getSimpleName();
    private WearableToken mToken;
    private TextView mDeviceNameTextView;
    private TextView mDeviceIDTextView;
    private TextView mPairingTitleTextView;
    private ProgressBar mPairingProgressBar;
    private Button mConnectDeviceButton;
    private Button mPairDeviceButton;
    private PairingUiState mPairingUiState;
    private TextView mBatteryLevelTextView;
    private TextView mSoftwareRevisionTextView;

    private static IWearableController mWearableController;
    //public static INotificationController NotificationController;

    enum PairingUiState{
        ERROR,
        NOT_PAIRED,
        PAIRING,
        UNPAIRING,
        PAIRED,
        CONNECTING,
        DISCONNECTING,
        CONNECTED
    }

    enum PairingErrorType{
        NO_ERRORS,
        NO_EXTRA,
        NO_TOKEN,
        NO_CONTROLLER,
        EXCEPTION
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_pairing);
        //pairing_title_text
        mPairingTitleTextView = (TextView) findViewById(R.id.pairing_title_text);
        mDeviceNameTextView = (TextView) findViewById(R.id.device_name_TextView);
        mDeviceIDTextView = (TextView) findViewById(R.id.device_id_TextView);
        mPairingProgressBar = (ProgressBar)findViewById(R.id.pairingProgressBar);
        mConnectDeviceButton = (Button)findViewById(R.id.connect_deviceButton);
        mPairDeviceButton = (Button)findViewById(R.id.pair_device_button);
        mBatteryLevelTextView = (TextView) findViewById(R.id.battery_levelTextView);
        mSoftwareRevisionTextView = (TextView) findViewById(R.id.software_revisionTextView);

        final Bundle extras = getIntent().getExtras();
        if (null != extras) {
            mToken = (WearableToken) extras.getSerializable(WEARABLE_TOKEN);
            if (null != mToken) {
                mDeviceNameTextView.setText(mToken.getDisplayName());
                mDeviceIDTextView.setText(mToken.getAddress());

                mWearableController = WearableControllerFactory.getWearableController(mToken, this);
                if (mWearableController.isConnected()) {
                    resetUi(PairingUiState.CONNECTED, PairingErrorType.NO_ERRORS);
                    mWearableController.getBatteryStatus();
                } else if (mWearableController.isPaired()) {
                    resetUi(PairingUiState.PAIRED, PairingErrorType.NO_ERRORS);
                } else {
                    resetUi(PairingUiState.NOT_PAIRED, PairingErrorType.NO_ERRORS);
                }
            } else {
                resetUi(PairingUiState.ERROR, PairingErrorType.NO_TOKEN);
            }
        } else{
            resetUi(PairingUiState.ERROR, PairingErrorType.NO_EXTRA);
        }
    }

    public static IWearableController getWearableController() {
        return mWearableController;
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop ");
        //CoreIQUtil.getInstance().setCoreIQTControllerListener(null);
        super.onStop();
    }

    private boolean pair(){
        String errorMsg = null;
        try {
            Log.d(TAG, "mWearableController.pair()");
            if (!mWearableController.isPaired()) {
                resetUi(PairingUiState.PAIRING, PairingErrorType.NO_ERRORS);
                mWearableController.pair();
            } else {
                resetUi(PairingUiState.PAIRED, PairingErrorType.NO_ERRORS);
            }
        } catch (Throwable throwable) {
            errorMsg = throwable.getMessage();
            Log.d(TAG, "pair: " + errorMsg);
            resetUi(PairingUiState.ERROR, PairingErrorType.EXCEPTION, errorMsg);
        }
        if(errorMsg != null) {
            Toast.makeText(getApplication(), errorMsg, Toast.LENGTH_LONG).show();
            Log.d(TAG, "pair: " + errorMsg);
        }

        return errorMsg == null;
    }

    private boolean unpair(){
        String errorMsg = null;
        try {
            Log.d(TAG, "mController.unpair()");
            if (mWearableController.isPaired()) {
                resetUi(PairingUiState.UNPAIRING, PairingErrorType.NO_ERRORS);
                mWearableController.unpair();
            } else {
                resetUi(PairingUiState.NOT_PAIRED, PairingErrorType.NO_ERRORS);
            }
        } catch (Throwable throwable) {
            errorMsg = throwable.getMessage();
            Log.d(TAG, "unpair: " + errorMsg);
            resetUi(PairingUiState.ERROR, PairingErrorType.EXCEPTION, errorMsg);
        }

        if(errorMsg != null) {
            Toast.makeText(getApplication(), errorMsg, Toast.LENGTH_LONG).show();
            Log.d(TAG, "unpair: " + errorMsg);
        }

        return errorMsg == null;
    }

    private boolean connect(){
        String errorMsg = null;
        try {
            Log.d(TAG, "mWearableController.connect()");
            if (!mWearableController.isConnected()) {
                resetUi(PairingUiState.CONNECTING, PairingErrorType.NO_ERRORS);
                mWearableController.connect();
            } else {
                resetUi(PairingUiState.CONNECTED, PairingErrorType.NO_ERRORS);
            }
        } catch (Throwable throwable) {
            errorMsg = throwable.getMessage();
            Log.d(TAG, "connect: " + errorMsg);
            resetUi(PairingUiState.ERROR, PairingErrorType.EXCEPTION, errorMsg);
        }

        if( errorMsg != null ) {
            Toast.makeText(getApplication(), errorMsg, Toast.LENGTH_LONG).show();
        }

        return errorMsg == null;
    }

    private boolean disconnect(){
        String errorMsg = null;
        try {
            Log.d(TAG, "mWearableController.disconnect()");
            if (mWearableController.isConnected()) {
                resetUi(PairingUiState.DISCONNECTING, PairingErrorType.NO_ERRORS);
                mWearableController.disconnect();
            } else {
                resetUi(mWearableController.isPaired() ? PairingUiState.PAIRED : PairingUiState.NOT_PAIRED,
                        PairingErrorType.NO_ERRORS);
            }


        } catch (Throwable throwable) {
            errorMsg = throwable.getMessage();
            Log.d(TAG, "disconnect: " + errorMsg);
            resetUi(PairingUiState.ERROR, PairingErrorType.EXCEPTION, errorMsg);
        }

        if( errorMsg != null ) {
            Toast.makeText(getApplication(), errorMsg, Toast.LENGTH_LONG).show();
        }

        return errorMsg == null;
    }

    public void onConnectClicked(View view) {
        boolean status = true;
        if(mPairingUiState == PairingUiState.CONNECTED) {
            status = disconnect();
        }
        else{
            status = connect();
        }

//        resetUi(mWearableController.isPaired() ? PairingUiState.PAIRED : PairingUiState.NOT_PAIRED,
//                PairingErrorType.NO_ERRORS);

        Log.d(TAG, "connect/disconnect status ok ? " + status);
    }

    public void onPairClicked(View view) {
        boolean status = true;
        if(mPairingUiState == PairingUiState.NOT_PAIRED) {
            status = pair();
        }
        else if(mPairingUiState == PairingUiState.PAIRED) {
            status = unpair();
        }

        resetUi(mWearableController.isPaired() ? PairingUiState.PAIRED : PairingUiState.NOT_PAIRED,
                PairingErrorType.NO_ERRORS);

        Log.d(TAG, "pair/unpair status ok ? " + status);
    }

    private void resetUi(PairingUiState pairingUiState, PairingErrorType errorType, String... erroMsg){

        mPairingUiState = pairingUiState;
        switch (pairingUiState){
            case ERROR:
                String error = "error: ";
                switch (errorType){
                    case NO_EXTRA:
                        error += "no extra";
                        break;
                    case NO_TOKEN:
                        error += "no token";
                        break;
                    case NO_CONTROLLER:
                        error += "no controller";
                        break;
                    case EXCEPTION:
                        error += erroMsg[0];
                        break;
                }
                mPairingTitleTextView.setText(error);
                mPairingProgressBar.setVisibility(View.GONE);
                mConnectDeviceButton.setText(R.string.connect_device_button);
                mPairDeviceButton.setText(R.string.pair_device_button);
                mConnectDeviceButton.setEnabled(true);
                mPairDeviceButton.setEnabled(true);
                break;
            case PAIRING:
                mPairingTitleTextView.setText(R.string.pairing_to_text);
                mPairingProgressBar.setVisibility(View.VISIBLE);
                mConnectDeviceButton.setText(R.string.connect_device_button);
                mPairDeviceButton.setText(R.string.pair_device_button);
                mConnectDeviceButton.setEnabled(false);
                mPairDeviceButton.setEnabled(false);
                MainActivity.sDeviceStatusText.setText("Pairing");
                break;
            case UNPAIRING:
                mPairingTitleTextView.setText(R.string.unpairing_from_text);
                mPairingProgressBar.setVisibility(View.VISIBLE);
                mConnectDeviceButton.setText(R.string.connect_device_button);
                mPairDeviceButton.setText(R.string.unpair_device_button);
                mConnectDeviceButton.setEnabled(false);
                mPairDeviceButton.setEnabled(false);
                MainActivity.sDeviceStatusText.setText("Unpairing");
                break;
            case PAIRED:
                mPairingTitleTextView.setText(R.string.paired_to_text);
                mPairingProgressBar.setVisibility(View.GONE);
                mConnectDeviceButton.setText(R.string.connect_device_button);
                mPairDeviceButton.setText(R.string.unpair_device_button);
                mConnectDeviceButton.setEnabled(true);
                mPairDeviceButton.setEnabled(true);
                MainActivity.sDeviceStatusText.setText("Paired");
                break;
            case CONNECTING:
                mPairingTitleTextView.setText(R.string.connecting_to_text);
                mPairingProgressBar.setVisibility(View.VISIBLE);
                mConnectDeviceButton.setText(R.string.connect_device_button);
                mPairDeviceButton.setText(R.string.pair_device_button);
                mConnectDeviceButton.setEnabled(false);
                mPairDeviceButton.setEnabled(false);
                MainActivity.sDeviceStatusText.setText("Connecting");
                break;
            case CONNECTED:
                mPairingTitleTextView.setText(R.string.connected_to_text);
                mPairingProgressBar.setVisibility(View.GONE);
                mConnectDeviceButton.setText(R.string.disconnect_device_button);
                mPairDeviceButton.setText(R.string.unpair_device_button);
                mConnectDeviceButton.setEnabled(true);
                mPairDeviceButton.setEnabled(false);
                MainActivity.sDeviceStatusText.setText("Connected");
                // Subscribe to user events
                UserEventController.subscribe(new WearableUserEventManager(getApplicationContext()));
                break;
            case DISCONNECTING:
                // Unsubscribe to user events
                MainActivity.sDeviceStatusText.setText("Disconnecting");
                UserEventController.unsubscribe();
                break;
            case NOT_PAIRED:
                mPairingTitleTextView.setText(R.string.not_paired_to_text);
                mPairingProgressBar.setVisibility(View.GONE);
                mConnectDeviceButton.setText(R.string.connect_device_button);
                mPairDeviceButton.setText(R.string.pair_device_button);
                mConnectDeviceButton.setEnabled(false);
                mPairDeviceButton.setEnabled(true);
                MainActivity.sDeviceStatusText.setText("Not paired");
                break;
        }
    }

    @Override
    public void onConnecting(IWearableController iWearableController) {
        Log.d(TAG, "onConnecting ");
    }

    @Override
    public void onConnected(IWearableController iWearableController) {
        Log.d(TAG, "onConnected ");
        resetUi(PairingUiState.CONNECTED, PairingErrorType.NO_ERRORS);
        mWearableController.getBatteryStatus();
        WearableIdentity wearableIdentity = mWearableController.getWearableIdentity();
        if (wearableIdentity != null) {
            String deviceSoftwareRevision = wearableIdentity.getSoftwareRevision();
            if (deviceSoftwareRevision != null)
                mSoftwareRevisionTextView.setText(deviceSoftwareRevision);
        }
    }

    @Override
    public void onDisconnecting(IWearableController iWearableController) {
        Log.d(TAG, "onDisconnecting ");
    }

    @Override
    public void onDisconnected(IWearableController iWearableController) {
        Log.d(TAG, "onDisconnected ");
        resetUi(mWearableController.isPaired() ? PairingUiState.PAIRED : PairingUiState.NOT_PAIRED,
                PairingErrorType.NO_ERRORS);
    }

    @Override
    public void onPairedStatusChanged(IWearableController iWearableController, boolean isPaired) {
        Log.d(TAG, "onPairedStatusChanged: " + isPaired);
        resetUi(isPaired ? PairingUiState.PAIRED : PairingUiState.NOT_PAIRED, PairingErrorType.NO_ERRORS);
    }

    @Override
    public void onBatteryStatusUpdate(IWearableController iWearableController, WearableBatteryStatus wearableBatteryStatus) {
        byte batteryLevel = wearableBatteryStatus.getBatteryLevel();
        Log.d(TAG, "onBatteryStatusUpdate: " + batteryLevel);
        if(mBatteryLevelTextView != null) {
            mBatteryLevelTextView.setText("" + batteryLevel);
        }
    }

    @Override
    public void onFailure(IWearableController iWearableController, com.intel.wearable.platform.core.error.Error error) {
        resetUi(PairingUiState.ERROR, PairingErrorType.EXCEPTION, error.getErrorMessage());
        Toast.makeText(getApplication(), error.getErrorMessage(), Toast.LENGTH_LONG).show();
        Log.d(TAG, "onFailure: " + error.getErrorMessage());
        //unpair();
        //resetUi(PairingUiState.NOT_PAIRED, PairingErrorType.NO_ERRORS);
    }
}

