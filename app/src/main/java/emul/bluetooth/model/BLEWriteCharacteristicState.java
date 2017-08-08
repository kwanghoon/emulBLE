package emul.bluetooth.model;

import java.util.ArrayList;
import java.util.UUID;

import emul.bluetooth.BluetoothLE;
import mocking.android.bluetooth.BluetoothGattCharacteristic;
import mocking.android.bluetooth.IBLEChangeCharacteristic;

/**
 * Created by khChoi on 2017-08-05.
 */

public class BLEWriteCharacteristicState extends BLEState {
    private BluetoothLE bluetoothLE;

    public BLEWriteCharacteristicState(BluetoothLE bluetoothLE) {
            this(bluetoothLE, null);
    }

    public BLEWriteCharacteristicState(BluetoothLE bluetoothLE, ArrayList<BLEState> next) {
        this.bluetoothLE = bluetoothLE;
        this.next = next;
    }

    public void action(IBLEChangeCharacteristic ibleChangeCharacgerisitc,
                       BluetoothGattCharacteristic btGattCharacteristic,
                       UUID serviceUuid,
                       UUID characteristicUuid,
                       byte[] bytes,
                       String nextMethodName) {
        // Condtion: This should be called in doWriteCharacteristic()

        // Return Bluetooth Gatt Characteristic
        ibleChangeCharacgerisitc.setResult(serviceUuid, characteristicUuid, bytes);

        bluetoothLE.callAfter(500, nextMethodName);
    }
}
