package com.h3.hrm3200.emul.model;

import com.h3.hrm3200.emul.AutoHRM3200;

import emul.bluetooth.BluetoothLE;
import emul.bluetooth.model.BLEStateException;
import emul.bluetooth.model.BLEWriteCharacteristicState;
import mocking.android.bluetooth.BluetoothGattCharacteristic;
import mocking.android.bluetooth.IBLEChangeCharacteristic;

/**
 * Created by khChoi on 2017-08-17.
 */

public class REQ_SessionInfo_0x84_0x85 extends BLEWriteCharacteristicState {
    private SharedSessionInfo sharedSessionInfo;
    public REQ_SessionInfo_0x84_0x85(BluetoothLE bluetoothLE, SharedSessionInfo sharedSessionInfo) {
        super(bluetoothLE);
        this.sharedSessionInfo = sharedSessionInfo;
    }

    @Override
    public void action(BluetoothGattCharacteristic btGattCharacteristic,
                       IBLEChangeCharacteristic ibleChangeCharacteristic) {
        // Condition: This should be called in doWriteCharacteristic
        //            and something in the following:

        byte in[] = btGattCharacteristic.getValue();
        if ((in[1] & 0xff) == 0x84) {
            sharedSessionInfo.sessionNumber = in[3] & 0xff;

            ibleChangeCharacteristic.setResult(AutoHRM3200.serviceUuid, AutoHRM3200.characteristicUuid,
                    new byte[] { (byte) 0x80, (byte) 0x85, (byte) 0x01, (byte) 0x00, (byte) 0xEF });    // OK

            return;
        }

        throw new BLEStateException("doWriteCharacteristic: REQ_SessionInfo : " + (in[1] & 0xff) + "==" + 0x84);
    }
}
