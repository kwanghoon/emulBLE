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
public class REQ_Disconnection_0x82_0x02_State extends BLEWriteCharacteristicState {
    public REQ_Disconnection_0x82_0x02_State(BluetoothLE bluetoothLE) {
        super(bluetoothLE);
    }

    @Override
    public void action(BluetoothGattCharacteristic btGattCharacteristic,
                       IBLEChangeCharacteristic ibleChangeCharacteristic) {

        // Condition: This should be called in doWriteCharacteristic
        //            and something in the following:

        byte in[] = btGattCharacteristic.getValue();
        if ((in[1] & 0xff) == 0x82 && (in[3] & 0xff) == 0x02) {
            // nothing to do
            return;
        }

        throw new BLEStateException("doWriteCharacteristic: REQ_Disconnection_0x82_0x02_State : "
                + (in[1] & 0xff) + "==" + 0x82 + ", "
                + (in[3] & 0xff) + "==" + 0x02);
    }
}
