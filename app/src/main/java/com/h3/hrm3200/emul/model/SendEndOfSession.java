package com.h3.hrm3200.emul.model;

import com.h3.hrm3200.emul.AutoHRM3200;

import emul.bluetooth.BluetoothLE;
import emul.bluetooth.model.BLENotificationState;
import mocking.android.bluetooth.IBLEChangeCharacteristic;

/**
 * Created by khChoi on 2017-08-17.
 */

public class SendEndOfSession extends BLENotificationState {
    private SharedSessionInfo sharedSessionInfo;
    public SendEndOfSession(BluetoothLE bluetoothLE, SharedSessionInfo sharedSessionInfo) {
        super(bluetoothLE);
        this.sharedSessionInfo = sharedSessionInfo;
    }

    @Override
    public void action(IBLEChangeCharacteristic ibleChangeCharacteristic) {
        ibleChangeCharacteristic.setResult(
                AutoHRM3200.serviceUuid, AutoHRM3200.characteristicUuid,
                new byte[] { (byte) 0x80, (byte) 0x18, (byte) 0x01, (byte) sharedSessionInfo.sessionNumber, (byte) 0xEF });
    }
}
