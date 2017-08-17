package com.h3.hrm3200.emul.model;

import com.h3.hrm3200.emul.AutoHRM3200;

import java.util.Calendar;

import emul.bluetooth.BluetoothLE;
import emul.bluetooth.model.BLENotificationState;
import mocking.android.bluetooth.IBLEChangeCharacteristic;

/**
 * Created by khChoi on 2017-08-17.
 */

public class SendSessionInfo extends BLENotificationState {
    private SharedSessionInfo sharedSessionInfo;
    public SendSessionInfo(BluetoothLE bluetoothLE, SharedSessionInfo sharedSessionInfo) {
        super(bluetoothLE);
        this.sharedSessionInfo = sharedSessionInfo;
    }

    @Override
    public void action(IBLEChangeCharacteristic ibleChangeCharacteristic) {
        //int count = 100;                    // 한 세션의 자료수
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR) - 2000;
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int min = now.get(Calendar.MINUTE);
        int sec = now.get(Calendar.SECOND);

        ibleChangeCharacteristic.setResult(
                AutoHRM3200.serviceUuid, AutoHRM3200.characteristicUuid,
                new byte[] { (byte) 0x80, (byte) 0x14, (byte) 0x0B, (byte) sharedSessionInfo.sessionNumber,
                        (byte) 0x00, (byte) sharedSessionInfo.dataTotalCount, (byte) 0x01, (byte) year, (byte) month, (byte) day,
                        (byte) hour, (byte) min, (byte) sec, (byte) 0x00,
                        (byte) 0xEF });
    }
}
