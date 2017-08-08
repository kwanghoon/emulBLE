package emul.bluetooth.model;

import java.util.ArrayList;

import mocking.android.bluetooth.IBLEDisconnect;

/**
 * Created by khChoi on 2017-08-05.
 */

public class BLEDisconnectState extends BLEState {
    private int succ_or_fail;
    private int state;

    public BLEDisconnectState(int succ_or_fail, int state) {
        this(null, succ_or_fail, state);
    }

    public BLEDisconnectState(ArrayList<BLEState> next, int succ_or_fail, int state) {
        this.next = next;
        this.succ_or_fail = succ_or_fail;
        this.state = state;
    }

    public void action(IBLEDisconnect ibleDisconnect) {
        // Condition : This should be called in doDisconnect().

        ibleDisconnect.setDisconnectResult(succ_or_fail, state);
    }
}
