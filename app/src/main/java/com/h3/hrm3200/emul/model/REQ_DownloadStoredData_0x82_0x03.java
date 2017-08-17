package com.h3.hrm3200.emul.model;

import com.h3.hrm3200.emul.AutoHRM3200;

import emul.bluetooth.BluetoothLE;
import emul.bluetooth.model.BLEWriteCharacteristicState;
import mocking.android.bluetooth.BluetoothGattCharacteristic;
import mocking.android.bluetooth.IBLEChangeCharacteristic;

/**
 * Created by khChoi on 2017-08-17.
 */
public class REQ_DownloadStoredData_0x82_0x03 extends BLEWriteCharacteristicState {

    public REQ_DownloadStoredData_0x82_0x03(BluetoothLE bluetoothLE) {
        super(bluetoothLE);
    }

    @Override
    public void action(BluetoothGattCharacteristic btGattCharacteristic,
                       IBLEChangeCharacteristic ibleChangeCharacteristic) {
        // Condition: This should be called in doWriteCharacteristic
        //            and something in the following:

        byte in[] = btGattCharacteristic.getValue();
        if ((in[1] & 0xff) == 0x82 && (in[3] & 0xff) == 0x03) {
            ibleChangeCharacteristic.setResult(AutoHRM3200.serviceUuid, AutoHRM3200.characteristicUuid,
                    new byte[]{(byte) 0x80, (byte) 0x83, (byte) 0x01, (byte) 0x00, (byte) 0xEF});

            return;
        }
    }
}
