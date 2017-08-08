package emul.bluetooth.model;

import java.util.ArrayList;

import mocking.android.bluetooth.IBLEConnect;

/**
 * Created by khChoi on 2017-08-05.
 */

public class BLEConnectState extends BLEState {
    private int succ_or_fail;
    private int state;

    public BLEConnectState(int succ_or_fail, int state) {
        this(null, succ_or_fail, state);
    }

    public BLEConnectState(ArrayList<BLEState> next, int succ_or_fail, int state) {
        this.next = next;
        this.succ_or_fail = succ_or_fail;
        this.state = state;
    }

    public void action(IBLEConnect ibleconnect) {
        // Condition: This action should be called in doConnect.

        // Return succ_or_fail and state
        ibleconnect.setConnectResult(succ_or_fail, state);
    }
}
