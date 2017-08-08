package emul.bluetooth;

import android.os.Message;

import java.util.ArrayList;

import emul.bluetooth.model.BLEConnectState;
import emul.bluetooth.model.BLEDisconnectState;
import emul.bluetooth.model.BLEScanState;
import emul.bluetooth.model.BLEServiceDiscoverState;
import emul.bluetooth.model.BLEState;
import emul.bluetooth.model.BLEWriteCharacteristicState;
import mocking.android.bluetooth.BluetoothGattCharacteristic;
import mocking.android.bluetooth.IBLEChangeCharacteristic;
import mocking.android.bluetooth.IBLEConnect;
import mocking.android.bluetooth.IBLEDisconnect;
import mocking.android.bluetooth.IBLEDiscoverService;
import mocking.android.bluetooth.IBLEScan;

/**
 * Created by khChoi on 2017-08-07.
 */

public class AutoBluetoothLE extends BluetoothLE {
    private ArrayList<BLEState> path;
    private int index;

    public void setPath(ArrayList<BLEState> path) {
        this.path = path;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int index() { return index; }

    public void incIndex() { index++; }

    @Override
    public void doBLEScan(IBLEScan iblescan) {
        if (path != null && index < path.size()) {
            BLEState state = path.get(index);
            if (state instanceof BLEScanState) {
                BLEScanState scanState = (BLEScanState)state;
                scanState.action(iblescan);

                index++;
            }
        }

        // Exception ???
    }

    @Override
    public void doConnect(IBLEConnect ibleconnect) {
        if (path != null && index < path.size()) {
            BLEState state = path.get(index);
            if (state instanceof BLEConnectState) {
                BLEConnectState connectState = (BLEConnectState)state;
                connectState.action(ibleconnect);

                index++;
            }
        }

        // Exception ???
    }

    @Override
    public void doDiscoverService(IBLEDiscoverService ibleDiscoverService) {
        if (path != null && index < path.size()) {
            BLEState state = path.get(index);
            if (state instanceof BLEServiceDiscoverState) {
                BLEServiceDiscoverState connectState = (BLEServiceDiscoverState)state;
                connectState.action(ibleDiscoverService);

                index++;
            }
        }

        // Exception ???
    }

    @Override
    public void doDisconnect(IBLEDisconnect ibleDisconnect) {
        if (path != null && index < path.size()) {
            BLEState state = path.get(index);
            if (state instanceof BLEDisconnectState) {
                BLEDisconnectState disconnectState = (BLEDisconnectState)state;
                disconnectState.action(ibleDisconnect);

                index++;
            }
        }

        // Exception ???
    }

    @Override
    public void doReadCharacteristic(Message msg) {
        super.doReadCharacteristic(msg);
    }

    @Override
    public void doWriteCharacteristic(BluetoothGattCharacteristic btGattCharacteristic, IBLEChangeCharacteristic ibleChangeCharacteristic) {
        if (path != null && index < path.size()) {
            BLEState state = path.get(index);
            if (state instanceof BLEWriteCharacteristicState) {
                BLEWriteCharacteristicState bleWriteCharacteristicState = (BLEWriteCharacteristicState)state;
               // bleWriteCharacteristicState.action();

                index++;
            }
        }

        // Exception ???
    }

    @Override
    public void doNotification(IBLEChangeCharacteristic ibleChangeCharacteristic) {
        if (path != null && index < path.size()) {
            BLEState state = path.get(index);
            if (state instanceof BLEWriteCharacteristicState) {
                BLEWriteCharacteristicState bleWriteCharacteristicState = (BLEWriteCharacteristicState)state;
                // bleWriteCharacteristicState.action();

                index++;
            }
        }

        // Exception ???
    }
}
