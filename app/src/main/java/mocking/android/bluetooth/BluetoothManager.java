package mocking.android.bluetooth;

import emul.bluetooth.BluetoothLE;

/**
 * Created by moonhyeonah on 2016. 5. 22..
 */
public class BluetoothManager {
    private BluetoothLE bluetoothLE;

    public BluetoothAdapter getAdapter() {
        return new BluetoothAdapter(bluetoothLE);
    }

    public BluetoothManager(BluetoothLE bluetootLE) {
        this.bluetoothLE = bluetootLE;
    }
}
