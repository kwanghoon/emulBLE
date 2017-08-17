package com.h3.hrm3200.emul.model;

import emul.bluetooth.BluetoothLE;
import emul.bluetooth.model.BLENotificationState;
import mocking.android.bluetooth.IBLEChangeCharacteristic;

/**
 * Created by khChoi on 2017-08-17.
 */

public class InitializeData extends BLENotificationState {
    private SharedSessionInfo sharedSessionInfo;
    public InitializeData(BluetoothLE bluetoothLE, SharedSessionInfo sharedSessionInfo) {
        super(bluetoothLE);
        this.sharedSessionInfo = sharedSessionInfo;
    }

    @Override
    public void action(IBLEChangeCharacteristic ibleChangeCharacteristic) {
        // Initialize the shared session info
        sharedSessionInfo.dataCount = 0;

        sharedSessionInfo.heartrate = 110;
        sharedSessionInfo.calorie = -1;
        sharedSessionInfo.steps = 0;
    }
}
