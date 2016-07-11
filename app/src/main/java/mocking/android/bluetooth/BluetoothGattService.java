package mocking.android.bluetooth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by moonhyeonah on 2016. 5. 23..
 */
public class BluetoothGattService {
    public static final int SERVICE_TYPE_PRIMARY = 0;
    protected List<BluetoothGattCharacteristic> characteristics;
    //protected List<BluetoothGattService> services;

    private UUID uuid;

    BluetoothGattService (UUID uuid, int serviceType) {
        this.uuid = uuid;
        this.characteristics = new ArrayList<BluetoothGattCharacteristic>();
    }

    public List<BluetoothGattCharacteristic> getCharacteristics() {
        return characteristics;
    }
    public UUID getUuid() {
        return uuid;
    }

    public boolean addCharacteristic (BluetoothGattCharacteristic characteristic) {
        characteristics.add(characteristic);
        return true;
    }

    public boolean addCharacteristic (UUID characteristicUuid, int properties, int permissions) {
        characteristics.add(new BluetoothGattCharacteristic(characteristicUuid, properties, permissions));
        return true;
    }

    BluetoothGattCharacteristic getCharacteristic(UUID uuid) {
        for(BluetoothGattCharacteristic characteristic : characteristics) {
            if (uuid.equals(characteristic.getUuid()))
                return characteristic;
        }
        return null;
    }
}
