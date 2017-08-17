package com.h3.hrm3200.emul.model;

import com.h3.hrm3200.emul.AutoHRM3200;

import emul.bluetooth.BluetoothLE;
import emul.bluetooth.model.BLENotificationState;
import mocking.android.bluetooth.IBLEChangeCharacteristic;

/**
 * Created by khChoi on 2017-08-17.
 */

public class SendSessionCount extends BLENotificationState {
    private int sessionCount;
    public SendSessionCount(BluetoothLE bluetoothLE, int sessionCount) {
        super(bluetoothLE);
        this.sessionCount = sessionCount;
    }

    @Override
    public void action(IBLEChangeCharacteristic ibleChangeCharacteristic) {
        ibleChangeCharacteristic.setResult(
                AutoHRM3200.serviceUuid, AutoHRM3200.characteristicUuid,
                new byte[] { (byte) 0x80, (byte) 0x12, (byte) 0x01, (byte) sessionCount, (byte) 0xEF });
    }
}
