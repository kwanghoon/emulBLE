package com.h3.hrm3200.emul.model;

import com.h3.hrm3200.Log;
import com.h3.hrm3200.emul.AutoHRM3200;

import emul.bluetooth.BluetoothLE;
import emul.bluetooth.model.BLENotificationState;
import mocking.android.bluetooth.IBLEChangeCharacteristic;

/**
 * Created by khChoi on 2017-08-17.
 */ // Realtime Data Reply
public class RealtimeDataReply extends BLENotificationState {
    public RealtimeDataReply(BluetoothLE bluetoothLE) {
        super(bluetoothLE);
    }

    @Override
    public void action(IBLEChangeCharacteristic ibleChangeCharacteristic) {
        // Condition: This should be called in doNotification
        //            Except this, no extra requirement.

        // Action:
        Log.v("HRM3200-Emul", "RealtimeDataReply");
        ibleChangeCharacteristic.setResult(AutoHRM3200.serviceUuid, AutoHRM3200.characteristicUuid,
                new byte[]{(byte) 0x80, (byte) 0x1A, (byte) 0x07, (byte) 0xFF, (byte) 0x50,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xEF});
    }
}
