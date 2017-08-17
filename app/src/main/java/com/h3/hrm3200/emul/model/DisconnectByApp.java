package com.h3.hrm3200.emul.model;

import com.h3.hrm3200.Log;

import emul.bluetooth.BluetoothLE;
import emul.bluetooth.model.BLEDisconnectState;

/**
 * Created by khChoi on 2017-08-17.
 */
public class DisconnectByApp extends BLEDisconnectState {
    public DisconnectByApp(BluetoothLE bluetoothLE, int succ_or_fail, int state) {
        super(bluetoothLE, succ_or_fail, state);
    }

    @Override
    public void setupAction() {
        Log.v("HRM3200-Emul", "DisconnectByApp : Press the Disconnect button ...");
    }
}
