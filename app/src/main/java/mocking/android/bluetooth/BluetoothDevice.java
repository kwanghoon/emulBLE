package mocking.android.bluetooth;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * Created by moonhyeonah on 2016. 5. 22..
 */
public class BluetoothDevice {

    private String address;
    private String name;

    private PortingLayer portingLayer;

    public BluetoothDevice(String address, String name, PortingLayer portingLayer) {
        this.address = address;
        this.name = name;

        this.portingLayer = portingLayer;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public BluetoothGatt connectGatt (Context context, boolean autoConnect, BluetoothGattCallback callback) {
        BluetoothGatt gatt = new BluetoothGatt(this, portingLayer);

        portingLayer.connectGatt(gatt, callback);

        return gatt;
    }


}
