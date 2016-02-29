package com.intel.ndg.bewell;

/**
 * Created by Vadim on 2/23/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.intel.wearable.platform.core.event.user.IWearableUserEventListener;
import com.intel.wearable.platform.core.event.user.WearableUserEvent;

public class WearableUserEventManager implements IWearableUserEventListener {

    private String mCGPhoneNumber = null;
    private String mDoubleTapResponse = null;
    private String mTrippleTapResponse = null;
    private String mSingleButtonResponse = null;
    private String mDoubleButtonResponse = null;
    private String mLongButtonResponse = null;

    public WearableUserEventManager(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        mCGPhoneNumber = prefs.getString("caregiver_phone_number", "");
        mDoubleTapResponse = prefs.getString("double_tap", "");
        mTrippleTapResponse = prefs.getString("tripple_tap", "");
        mSingleButtonResponse = prefs.getString("single_button_press", "");
        mDoubleButtonResponse = prefs.getString("double_button_press", "");
        mLongButtonResponse = prefs.getString("long_button_press", "");
    }

    @Override
    public void onWearableUserEvent(WearableUserEvent userEvent) {
        //process notification of the user event issued on the wearable device
        WearableUserEvent.UserEvent wearableUserEvent = userEvent.getUserEvent();
        switch (wearableUserEvent.getUserEventType()) {
            case TAPPING:
                // The exact type of tapping event can be shown as follows:
                System.out.println(((
                        WearableUserEvent.TappingEvent)
                        wearableUserEvent).getTappingEventType().name());
                if (Application.isAwatingCRResponse()) {
                    switch (((WearableUserEvent.TappingEvent) wearableUserEvent).getTappingEventType()) {
                        case DOUBLE_TAP:
                            new OutgoingSms().sendCRResponse(mCGPhoneNumber, mDoubleTapResponse);
                            break;
                        case TRIPLE_TAP:
                            new OutgoingSms().sendCRResponse(mCGPhoneNumber, mTrippleTapResponse);
                            break;
                        default:
                            break;
                    }
                }
                break;
            case BUTTON:
                System.out.println((
                        (WearableUserEvent.ButtonEvent)
                                wearableUserEvent).getButtonEventType().name());
                switch (((WearableUserEvent.ButtonEvent) wearableUserEvent).getButtonEventType()) {
                    case SINGLE_PRESS:
                        new OutgoingSms().sendCRResponse(mCGPhoneNumber, mSingleButtonResponse);
                        break;
                    case DOUBLE_PRESS:
                        new OutgoingSms().sendCRResponse(mCGPhoneNumber, mDoubleButtonResponse);
                        break;
                    case LONG_PRESS:
                        new OutgoingSms().sendCRResponse(mCGPhoneNumber, mLongButtonResponse);
                        break;
                    default:
                        // unknown button event, ignore
                        break;
                }
                break;
            default:
                //unknown user event, ignore
                break;
        }

        if (Application.isAwatingCRResponse()) {
            Application.setCRResponseWaitingState(false);
        }
    }
}
