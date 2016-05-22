package mocking.android.bluetooth;

/**
 * Created by moonhyeonah on 2016. 5. 22..
 */
public class BluetoothManager {
    public BluetoothAdapter getAdapter() {
        return new BluetoothAdapter();
    }
}
