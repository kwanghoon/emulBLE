package mocking.android.bluetooth;


import android.os.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by moonhyeonah on 2016. 5. 22..
 */
public class BluetoothGatt {
    public final static int GATT_SUCCESS = 0;
    private List<BluetoothGattService> services;

    private BluetoothDevice bluetoothDevice;
    private PortingLayer portingLayer;

    public BluetoothGatt(BluetoothDevice bluetoothDevice, PortingLayer portingLayer) {
        this.bluetoothDevice = bluetoothDevice;
        this.portingLayer = portingLayer;
        this.services = new ArrayList<BluetoothGattService>();
    }

    public boolean connect() {
        // TODO
        if (bluetoothDevice != null) {
            return true;
        }
        else {
            return false;
        }

    }
    public void disconnect() {
        portingLayer.disconnect();
    }

    public void close() {

    }

    public boolean discoverServices() {
        portingLayer.discoverServices();

        return true;
    }

    public List<BluetoothGattService> getServices() {
        return services;
    }

    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
        return false;
    }

    public boolean writeDescriptor(BluetoothGattDescriptor descriptor) {
        return false;
    }

    public boolean readCharacteristic (BluetoothGattCharacteristic characteristic) {
        return false;
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if ( (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) == 0 )
            return false;

        portingLayer.writeCharacteristic(characteristic);
        return true;
    }

    public BluetoothDevice getDevice() {
        return bluetoothDevice;
    }


    public void addService(UUID serviceUuid) {
        BluetoothGattService service = new BluetoothGattService(serviceUuid, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        services.add(service);
    }
    public BluetoothGattService getService(UUID uuid) {
        for (BluetoothGattService service : services) {
            if (service.getUuid().equals(uuid))
                return service;
        }
        return null;
    }
}
