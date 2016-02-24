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
                if (Application.ismAwatingCRResponse()) {
                    switch (((WearableUserEvent.TappingEvent) wearableUserEvent).getTappingEventType()) {
                        case DOUBLE_TAP:
                        case TRIPLE_TAP:
                            new OutgoingSms().sendCRResponse(OutgoingSms.resposeType.OKAY);
                            break;
                    }
                }
                break;
            case BUTTON:
                System.out.println((
                        (WearableUserEvent.ButtonEvent)
                                wearableUserEvent).getButtonEventType().name());
                if (Application.ismAwatingCRResponse()) {
                    switch (((WearableUserEvent.ButtonEvent) wearableUserEvent).getButtonEventType()) {
                        case SINGLE_PRESS:
                            // Respond to Care Giver 'I am okay!'
                            new OutgoingSms().sendCRResponse(OutgoingSms.resposeType.NOT_OKAY);
                            break;
                        case DOUBLE_PRESS:
                            // Respond to Care Giver 'I am  not well'
                            new OutgoingSms().sendCRResponse(OutgoingSms.resposeType.EMERGENCY);
                            break;
                        case LONG_PRESS:

                            break;
                        default:
                            // unknown button event, ignore
                    }
                    Application.setCRResponseWaitingState(false);
                }
                break;
            default:
                //unknown user event, ignore
                break;
        }

    }
}