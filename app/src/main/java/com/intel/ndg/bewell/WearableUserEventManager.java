package com.intel.ndg.bewell;

/**
 * Created by Vadim on 2/23/2016.
 */

import com.intel.wearable.platform.core.event.user.IWearableUserEventListener;
import com.intel.wearable.platform.core.event.user.WearableUserEvent;

public class WearableUserEventManager implements IWearableUserEventListener {
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
                break;
            case BUTTON:
                System.out.println((
                        (WearableUserEvent.ButtonEvent)
                                wearableUserEvent).getButtonEventType().name());
                break;
            default:
                //unknown user event, ignore
                break;
        }

    }
}