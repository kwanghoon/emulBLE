package com.h3.hrm3200;

import android.content.Context;
import java.util.List;
import java.util.UUID;

/**
 * Created by moonhyeonah on 2016. 5. 22..
 */
public class MockingBle {
}

class CallbackClass {
    static BluetoothAdapter.LeScanCallback callback;
}

class BleDevice {

    public void scan() {
        String address = "00:11:22:AA:BB:CC";
        BluetoothDevice device = new BluetoothDevice(address);
        int rssi = 0;
        byte[] scanRecord = new byte[1];

        CallbackClass.callback.onLeScan(device, rssi, scanRecord);
    }
}

class BluetoothManager {

    public BluetoothAdapter getAdapter() {
        return new BluetoothAdapter();
    }
}

class BluetoothAdapter {

    final String ACTION_REQUEST_ENABLE = "android.bluetooth.adapter.action.REQUEST_ENABLE";

    //static BluetoothAdapter.LeScanCallback callback;

    public interface LeScanCallback {
        public void onLeScan (BluetoothDevice device, int rssi, byte[] scanRecord);
    }


    public boolean isEnabled() {
        return true;
    }

    public boolean startLeScan (BluetoothAdapter.LeScanCallback callback) {
        CallbackClass.callback = callback;
        return true;
    }

    public void stopLeScan(BluetoothAdapter.LeScanCallback callback) {

    }

    public BluetoothDevice getRemoteDevice(String address) {
        return new BluetoothDevice(address);
    }
}

class BluetoothDevice {
    private String address;
    private String name;

    public BluetoothDevice(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public BluetoothGatt connectGatt (Context context, boolean autoConnect, BluetoothGattCallback callback) {
        return new BluetoothGatt();
    }
}

class BluetoothGatt {
    final int GATT_SUCCESS = 0;
    private List<BluetoothGattService> services;

    public boolean connect() {
        return true;
    }

    public boolean discoverServices() {
        return true;
    }
    public List<BluetoothGattService> getServices() {
        return services;
    }

    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
        return true;
    }

    public boolean writeDescriptor(BluetoothGattDescriptor descriptor) {
        return true;
    }

    public boolean readCharacteristic (BluetoothGattCharacteristic characteristic) {
        return true;
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        return true;
    }
}

abstract class BluetoothGattCallback {

    public abstract void onConnectionStateChange (BluetoothGatt gatt, int status, int newState);
    public abstract void onServicesDiscovered (BluetoothGatt gatt, int status);
    public abstract void onCharacteristicChanged (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
    public abstract void onCharacteristicRead (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
}

class BluetoothGattService {
    private List<BluetoothGattCharacteristic> characteristics;
    private  UUID uuid;

    public List<BluetoothGattCharacteristic> getCharacteristics() {
        return characteristics;
    }
    public UUID getUuid() {
        return uuid;
    }
}

class BluetoothGattCharacteristic {
    static final int FORMAT_UINT8 = 0x00000011;
    static final int FORMAT_UINT16 = 0x00000012;
    static final int PROPERTY_NOTIFY = 0x00000010;              // MHEALTH_MEASUREMENT_READ
    static final int PROPERTY_WRITE_NO_RESPONSE = 0x00000004;   // MHEALTH_MEASUREMENT_WRITE

    private UUID uuid;
    private byte[] value;
    protected List<BluetoothGattDescriptor> mDescriptors;

    public UUID getUuid() {
        return uuid;
    }

    public BluetoothGattDescriptor getDescriptor(UUID uuid) {
        for (BluetoothGattDescriptor characteristic : mDescriptors) {
        }
        return null;
    }
    public int getProperties(){
        return BluetoothGattCharacteristic.FORMAT_UINT16;
    }
    public Integer getIntValue(int formatType, int offset) {
        int val = value[offset] & 0xff;
        return val;
    }
    public byte[] getValue() {
        return value;
    }
    public boolean setValue(byte[] value) {
        this.value = value;
        return true;
    }
}

class BluetoothGattDescriptor {
    public static final byte[] ENABLE_NOTIFICATION_VALUE = new byte[1];

    // BluetoothGatt.writeDescriptor() 호출하면 불리는?
    public boolean setValue(byte[] value) {
        return true;
    }

}