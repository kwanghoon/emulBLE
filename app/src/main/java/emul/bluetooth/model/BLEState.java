package emul.bluetooth.model;

import java.util.ArrayList;
import java.util.UUID;

import mocking.android.bluetooth.BluetoothGattCharacteristic;
import mocking.android.bluetooth.IBLEChangeCharacteristic;
import mocking.android.bluetooth.IBLEConnect;
import mocking.android.bluetooth.IBLEDisconnect;
import mocking.android.bluetooth.IBLEDiscoverService;
import mocking.android.bluetooth.IBLEScan;

/**
 * Created by khChoi on 2017-08-05.
 */

public abstract class BLEState {
    protected ArrayList<BLEState> next = new ArrayList<BLEState>();

    // Setup actions to activate in the previous state
    // This method is intended to be implemented as:
    //     callAfter(500, "method name");
    public void setupAction() { }

    // Actions
    public void action(IBLEScan iblescan) { }   // for BLEScanState
    public void action(IBLEConnect ibleconnect) { }     // for BLEConnectState
    public void action(IBLEDiscoverService ibleDiscoverService) { }     // for BLEServiceDiscoverState
    public void action(IBLEDisconnect ibleDisconnect) { }   // for BLEDisconnectState
    public void action(IBLEChangeCharacteristic ibleChangeCharacteristic) {}    // for BLENotificationState
    public void action(BluetoothGattCharacteristic btGattCharacteristic,
                       IBLEChangeCharacteristic ibleChangeCharacteristic) {}      // for LBEWriteCharacteristicState
    public void action(IBLEChangeCharacteristic ibleChangeCharacgerisitc,       // for LBEWriteCharacteristicState
                       BluetoothGattCharacteristic btGattCharacteristic,
                       UUID serviceUuid,
                       UUID characteristicUuid,
                       byte[] bytes) { }

    public BLEState next(int index) {
        return this.next.get(index);
    }

    public void addNext(BLEState state) { this.next.add(state); }

    public int size() { return next.size(); }
}
