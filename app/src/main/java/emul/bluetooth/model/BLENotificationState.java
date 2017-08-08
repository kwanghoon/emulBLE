package emul.bluetooth.model;

import java.util.ArrayList;

import emul.bluetooth.model.BLEState;
import mocking.android.bluetooth.IBLEChangeCharacteristic;

/**
 * Created by khChoi on 2017-08-07.
 */

public class BLENotificationState extends BLEState {

    public BLENotificationState() {
        this(null);
    }

    public BLENotificationState(ArrayList<BLEState> next) {
        this.next = next;
    }

    public void action(IBLEChangeCharacteristic ibleChangeCharacteristic) {
        // Condition: This should be called in doNotification

        // Return nothing, but notify something through ibleChangeCharacteritic.
    }
}
