package com.h3.hrm3200.emul.model;

import com.h3.hrm3200.emul.AutoHRM3200;

import java.util.Random;

import emul.bluetooth.BluetoothLE;
import emul.bluetooth.model.BLENotificationState;
import mocking.android.bluetooth.IBLEChangeCharacteristic;

/**
 * Created by khChoi on 2017-08-17.
 */

public class StoredDataReply extends BLENotificationState {
    private SharedSessionInfo sharedSessionInfo;



    public StoredDataReply(BluetoothLE bluetoothLE, SharedSessionInfo sharedSessionInfo) {
        super(bluetoothLE);
        this.sharedSessionInfo = sharedSessionInfo;
    }

    @Override
    public void action(IBLEChangeCharacteristic ibleChangeCharacteristic) {
        byte[] storedData;

        Random random = new Random();

        // Heart Rate
        int r = random.nextInt() % 3;
        switch (r) {
            case 0:
                sharedSessionInfo.heartrate--;
                break;
            case 1:
                sharedSessionInfo.heartrate++;
                break;
            default:
                break;
        }
        if (sharedSessionInfo.heartrate < 50)
            sharedSessionInfo.heartrate = 50;
        else if (sharedSessionInfo.heartrate > 150)
            sharedSessionInfo.heartrate = 150;

        // Calorie & steps
        sharedSessionInfo.calorie = sharedSessionInfo.calorie + 2;

        sharedSessionInfo.steps++;

        sharedSessionInfo.dataCount++;
        ibleChangeCharacteristic.setResult(AutoHRM3200.serviceUuid, AutoHRM3200.characteristicUuid,
                new byte[] { (byte) 0x80, (byte) 0x16, (byte) 0x09, (byte) sharedSessionInfo.sessionNumber, (byte) 0x00, (byte) sharedSessionInfo.dataCount,
                        (byte) sharedSessionInfo.heartrate, (byte) 0x00, (byte) (sharedSessionInfo.calorie / 256), (byte) (sharedSessionInfo.calorie % 256),
                        (byte) (sharedSessionInfo.steps / 256), (byte) (sharedSessionInfo.steps % 256), (byte) 0xEF });
    }
}
