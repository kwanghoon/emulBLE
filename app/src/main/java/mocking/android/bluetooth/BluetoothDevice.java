package mocking.android.bluetooth;

import android.content.Context;

/**
 * Created by moonhyeonah on 2016. 5. 22..
 */
public class BluetoothDevice {
    private String address;
    private String name;

    public BluetoothDevice(String address, String name) {
        this.address = address;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public BluetoothGatt connectGatt (Context context, boolean autoConnect, BluetoothGattCallback callback) {
        return null;
    }
}
