package emul.bluetooth.model;

import java.util.ArrayList;

import emul.bluetooth.BluetoothLE;
import mocking.android.bluetooth.IBLEDisconnect;

/**
 * Created by khChoi on 2017-08-05.
 */

public class BLEDisconnectState extends BLEState {
    private BluetoothLE bluetoothLE;
    private int succ_or_fail;
    private int state;

    public BLEDisconnectState(BluetoothLE bluetoothLE, int succ_or_fail, int state) {
        this.bluetoothLE = bluetoothLE;
        this.succ_or_fail = succ_or_fail;
        this.state = state;
    }

    public BLEDisconnectState(ArrayList<BLEState> next, int succ_or_fail, int state) {
        this.next = next;
        this.succ_or_fail = succ_or_fail;
        this.state = state;
    }

    @Override
    public void setupAction() {
        bluetoothLE.callDoDisconnectAfter(500);  // By default, the disconnection is made by a BLE device, not by a phone.
    }

    @Override
    public void action(IBLEDisconnect ibleDisconnect) {
        // Condition : This should be called in doDisconnect().

        ibleDisconnect.setDisconnectResult(succ_or_fail, state);
    }
}
