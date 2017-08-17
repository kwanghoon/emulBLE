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
public class OK_0x11_State extends BLEWriteCharacteristicState {
    public OK_0x11_State(BluetoothLE bluetoothLE) {
        super(bluetoothLE);
    }

    @Override
    public void action(BluetoothGattCharacteristic btGattCharacteristic,
                       IBLEChangeCharacteristic ibleChangeCharacteristic) {
        // Condition: This should be called in doWriteCharacteristic
        //            and something in the following:

        byte in[] = btGattCharacteristic.getValue();
        if ((in[1] & 0xff) == 0x11) {
            // do nothing
            // 바로 다음에 0x80이 온다.

            return;
        }

        throw new BLEStateException("doWriteCharacteristic: OK_0x11_State : " + (in[1] & 0xff) + "==" + 0x11);
    }
}
