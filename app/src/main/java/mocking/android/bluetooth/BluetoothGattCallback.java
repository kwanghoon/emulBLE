package mocking.android.bluetooth;

/**
 * Created by moonhyeonah on 2016. 5. 22..
 */
public abstract class BluetoothGattCallback {
    public abstract void onConnectionStateChange (BluetoothGatt gatt, int status, int newState);
    public abstract void onServicesDiscovered (BluetoothGatt gatt, int status);
    public abstract void onCharacteristicChanged (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
    public abstract void onCharacteristicRead (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
}
