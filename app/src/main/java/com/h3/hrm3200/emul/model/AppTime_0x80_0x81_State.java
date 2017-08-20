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
public class AppTime_0x80_0x81_State extends BLEWriteCharacteristicState {
    // storeddata : 0x01 ==> no stored data, 0x00 ==> stored data available
    // downloadable : 0x01 ==> not downloadable, 0x00 ==> ddownloadable
    private int storeddata;
    private int downloadable;

    public AppTime_0x80_0x81_State(BluetoothLE bluetoothLE, int storeddata, int downloadable) {
        super(bluetoothLE);

        this.storeddata = storeddata;
        this.downloadable = downloadable;
    }

    @Override
    public void action(BluetoothGattCharacteristic btGattCharacteristic,
                       IBLEChangeCharacteristic ibleChangeCharacteristic) {

        // Condition: This should be called in doWriteCharacteristic
        //            and something in the following:

        byte in[] = btGattCharacteristic.getValue();
        if ((in[1] & 0xff) == 0x80) {

            // out[3] 0x01: 저장 데이터 없음 && out[4] 0x01 : 다운로드 불가 상태(측정중)
            if (storeddata == 0x01 && downloadable == 0x01) {
                ibleChangeCharacteristic.setResult(AutoHRM3200.serviceUuid, AutoHRM3200.characteristicUuid,
                        new byte[]{(byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x01, (byte) 0x01, (byte) 0xEF});
            }
            // out[3] 0x00: 저장 데이터 있음 && out[4] 0x01 : 다운로드 불가 상태(측정중)
            else if (storeddata == 0x00 && downloadable == 0x01) {
                ibleChangeCharacteristic.setResult(AutoHRM3200.serviceUuid, AutoHRM3200.characteristicUuid,
                        new byte[]{(byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0xEF});
            }
            // out[4] : 0x00 : 다운로드 가능 상태, 이 메시지 전송후 0x82 메시지 기다려야 함
            // out[3] 0x00: 저장 데이터 있음 && out[4] 0x00 : 다운로드 가능 상태
            else if (storeddata == 0x00 && downloadable == 0x00) {
                ibleChangeCharacteristic.setResult(AutoHRM3200.serviceUuid, AutoHRM3200.characteristicUuid,
                        new byte[]{(byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0xEF});
            } else {
                throw new BLEStateException("doWriteCharacteristic: AppTime_0x80_State : "
                        + "storeddata = " + storeddata + ", "
                        + "downloadable = " + downloadable);
            }

            return;
        }

        throw new BLEStateException("doWriteCharacteristic: AppTime_0x80_State : " + (in[1] & 0xff) + "==" + 0x80);
    }
}
