package com.h3.hrm3200.emul.model;

import com.h3.hrm3200.Log;
import com.h3.hrm3200.emul.AutoHRM3200;

import emul.bluetooth.BluetoothLE;
import emul.bluetooth.model.BLENotificationState;
import mocking.android.bluetooth.IBLEChangeCharacteristic;

/**
 * Created by khChoi on 2017-08-17.
 */ // For BLENotificatioNState : Device Time Reply
public class DeviceTimeReplyState extends BLENotificationState {
    public DeviceTimeReplyState(BluetoothLE bluetoothLE) {
        super(bluetoothLE);
    }

    @Override
    public void action(IBLEChangeCharacteristic ibleChangeCharacteristic) {
        // Condition: This should be called in doNotification
        //            Except this, no extra requirement.

        // Action:
        Log.v("HRM3200-Emul", "DeviceTimeReplyState");

        ibleChangeCharacteristic.setResult(
                AutoHRM3200.serviceUuid, AutoHRM3200.characteristicUuid,
                new byte[]{(byte) 0x80, (byte) 0x10, (byte) 0x08, (byte) 0x0F, (byte) 0x05,
                        (byte) 0x13, (byte) 0x11, (byte) 0x0B, (byte) 0x2D, (byte) 0x01, (byte) 0x00, (byte) 0xEF}
        );
    }
}
