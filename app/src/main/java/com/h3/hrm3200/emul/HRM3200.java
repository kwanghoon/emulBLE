package com.h3.hrm3200.emul;


import android.os.Message;

import com.h3.hrm3200.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

import emul.bluetooth.BluetoothLE;
import mocking.android.bluetooth.BLEService;
import mocking.android.bluetooth.BluetoothGatt;
import mocking.android.bluetooth.BluetoothGattCharacteristic;
import mocking.android.bluetooth.IBLEChangeCharacteristic;
import mocking.android.bluetooth.IBLEDisconnect;
import mocking.android.bluetooth.IBLEDiscoverService;

/**
 * Created by moonh on 2016-10-05.
 */

public class HRM3200 extends BluetoothLE {

    public void doDiscoverService(IBLEDiscoverService ibleDiscoverService) {
        Log.v("HRM3200", "doDiscoverService...");

        ArrayList<BLEService> bleServiceList = new ArrayList<BLEService>();

        bleServiceList.add(new BLEService(UUID.fromString("00001800-0000-1000-8000-00805f9b34fb")));

        bleServiceList.add(new BLEService(UUID.fromString("00001801-0000-1000-8000-00805f9b34fb")));

        bleServiceList.add(new BLEService(UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb"),
                                        UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"),
                                        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                                        BluetoothGattCharacteristic.PERMISSION_WRITE));

        bleServiceList.add(new BLEService(UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb"),
                                        UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb"),
                                        BluetoothGattCharacteristic.PROPERTY_NOTIFY | BluetoothGattCharacteristic.PROPERTY_READ,
                                        BluetoothGattCharacteristic.PERMISSION_WRITE,
                                        UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")));

        bleServiceList.add(new BLEService(UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb"),
                                        UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb"),
                                        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                                        BluetoothGattCharacteristic.PERMISSION_WRITE,
                                        UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")));

        bleServiceList.add(new BLEService(UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb"),
                                        UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb"),
                                        BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
                                        BluetoothGattCharacteristic.PERMISSION_WRITE,
                                        UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")));

        ibleDiscoverService.setResult(BluetoothGatt.GATT_SUCCESS, bleServiceList);

        callAfter(500, "doDeviceTimeReply");
    }

    //
    UUID serviceUuid = UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb");
    UUID characteristicUuid = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");

    public void doDeviceTimeReply(IBLEChangeCharacteristic ibleChangeCharacteristic) {
        Log.v("HRM3200-Emul", "doDeviceTimeReply...");

        ibleChangeCharacteristic.setResult(
                serviceUuid, characteristicUuid,
                new byte[]{(byte) 0x80, (byte) 0x10, (byte) 0x08, (byte) 0x0F, (byte) 0x05,
                    (byte) 0x13, (byte) 0x11, (byte) 0x0B, (byte) 0x2D, (byte) 0x01, (byte) 0x00, (byte) 0xEF}
        );
    }

    boolean onMeasuring;
    public void doRealtimeDataReply(IBLEChangeCharacteristic ibleChangeCharacteristic) {
        Log.v("HRM3200", "doRealtimedataReply - Begin");
        ibleChangeCharacteristic.setResult(serviceUuid, characteristicUuid,
                new byte[]{(byte) 0x80, (byte) 0x1A, (byte) 0x07, (byte) 0xFF, (byte) 0x50,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xEF});

        Log.v("HRM3200", "doRealtimedataReply - End: " + onMeasuring);
        if (onMeasuring == true) {
            callAfter(500, "doRealtimeDataReply");
        }
    }

    private int sessionNumber = -1;

    public void doWriteCharacteristic(BluetoothGattCharacteristic btGattCharacteristic,
                                      IBLEChangeCharacteristic ibleChangeCharacteristic) {
        byte in[] = btGattCharacteristic.getValue();

        if ((in[1] & 0xff) == 0x11) {
            // do nothing
            // 바로 다음에 0x80이 온다.
        }
        else if ((in[1] & 0xff) == 0x80) {
            // 저장데이터 유무/다운로드 가능여부(0x81) 전송

            Random random = new Random();
            int r = random.nextInt() % 3;
            r = 0;

            switch (r) {
                // out[4] : 0x01 : 다운로드 불가 상태(측정중), 이 메시지 전송후 바로 측정 데이타 전송해야 함
                case 0:
                    // out[3] 0x01: 저장 데이터 없음 && out[4] 0x01 : 다운로드 불가 상태(측정중)
                    ibleChangeCharacteristic.setResult(serviceUuid, characteristicUuid,
                            new byte[] { (byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x01, (byte) 0x01, (byte) 0xEF });

                    callAfter(500, "doRealtimeDataReply");
                    onMeasuring = true;
                    break;

                case 1:
                    // out[3] 0x00: 저장 데이터 있음 && out[4] 0x01 : 다운로드 불가 상태(측정중)
                    ibleChangeCharacteristic.setResult(serviceUuid, characteristicUuid,
                            new byte[] { (byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0xEF });

                    callAfter(500, "doRealtimeDataReply");
                    onMeasuring = true;
                    break;

                // out[4] : 0x00 : 다운로드 가능 상태, 이 메시지 전송후 0x82 메시지 기다려야 함
                case 2:
                    // out[3] 0x00: 저장 데이터 있음 && out[4] 0x00 : 다운로드 가능 상태
                    ibleChangeCharacteristic.setResult(serviceUuid, characteristicUuid,
                            new byte[] { (byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0xEF });
                    break;

                default:
                    // out[3] 0x00: 저장 데이터 있음 && out[4] 0x00 : 다운로드 가능 상태
                    ibleChangeCharacteristic.setResult(serviceUuid, characteristicUuid,
                            new byte[] { (byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0xEF });
                    break;
            }
        }

        else if ((in[1] & 0xff) == 0x82 && (in[3] & 0xff) == 0x02) {  // 측정종료 요청
            onMeasuring = false;

            callDoDisconnectAfter(500);
        }

        else if ((in[1] & 0xff) == 0x82 && (in[3] & 0xff) == 0x01) {  // 실시간 자료 요청인 경우
            ibleChangeCharacteristic.setResult(serviceUuid, characteristicUuid,
                    new byte[] { (byte) 0x80, (byte) 0x83, (byte) 0x01, (byte) 0x00, (byte) 0xEF });

            callAfter(500, "doRealtimeDataReply");
            onMeasuring = true;
        }

        else if ((in[1] & 0xff) == 0x82 && (in[3] & 0xff) == 0x03) {  // 저장 자료 요청인 경우
            ibleChangeCharacteristic.setResult(serviceUuid, characteristicUuid,
                    new byte[] { (byte) 0x80, (byte) 0x83, (byte) 0x01, (byte) 0x00, (byte) 0xEF });

            callAfter(500, "doSendSessionCount");
        }

        else if ((in[1] & 0xff) == 0x84) {                           // 세션데이타 요청인 경우
            sessionNumber = in[3] & 0xff;

            ibleChangeCharacteristic.setResult(serviceUuid, characteristicUuid,
                    new byte[] { (byte) 0x80, (byte) 0x85, (byte) 0x01, (byte) 0x00, (byte) 0xEF });    // OK

            callAfter(500, "doSendSessionInfo");
        }

        else if ((in[1] & 0xff) == 0x15) {                           // 세션데이타 요청인 경우

            callAfter(0, "doStoredDataReply");

            /*
            byte[] storedData;
            //= new byte[]{(byte) 0x80, (byte) 0x16, (byte) 0x09, (byte) sessionNumber, (byte) 0x00,
            //        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xEF};

            Random random = new Random();
            int heartrate = 110;
            int calorie = -1;
            int steps = 0;


            // Heart Rate
            int r = random.nextInt() % 3;
            switch (r) {
                case 0:
                    heartrate--;
                    break;
                case 1:
                    heartrate++;
                    break;
                default:
                    break;
            }
            if (heartrate < 50)
                heartrate = 50;
            else if (heartrate > 150)
                heartrate = 150;

            // Calorie & steps
            calorie = calorie + 2;

            steps++;

            storedData = new byte[]{(byte) 0x80, (byte) 0x16, (byte) 0x09, (byte) sessionNumber, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xEF};
            storedData[5] = (byte) i;
            storedData[6] = (byte) heartrate;
            storedData[8] = (byte) (calorie / 256);
            storedData[9] = (byte) (calorie % 256);
            storedData[10] = (byte) (steps / 256);
            storedData[11] = (byte) (steps % 256);

            reply_msg = Message.obtain();
            reply_msg.what = PortingLayer.WRITE_CHARACTERISTIC_REPLY;
            reply_msg.arg1 = PortingLayer.HRM_READ;
            reply_msg.obj = storedData;
            app_handler.sendMessage(reply_msg);


            // end of session
            reply_msg = Message.obtain();
            reply_msg.what = PortingLayer.WRITE_CHARACTERISTIC_REPLY;
            reply_msg.arg1 = PortingLayer.HRM_READ;
            reply_msg.obj = new byte[]{(byte) 0x80, (byte) 0x18, (byte) 0x01, (byte) sessionNumber, (byte) 0xEF};
            app_handler.sendMessageDelayed(reply_msg, 5000);
*/
        }

        else {

        }
    }

    public void doSendSessionCount(IBLEChangeCharacteristic ibleChangeCharacteristic) {
        ibleChangeCharacteristic.setResult(
                serviceUuid, characteristicUuid,
                new byte[] { (byte) 0x80, (byte) 0x12, (byte) 0x01, (byte) 0x05, (byte) 0xEF });
    }

    public void doSendSessionInfo(IBLEChangeCharacteristic ibleChangeCharacteristic) {

        int count = 100;                    // 한 세션의 자료수
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR) - 2000;
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int min = now.get(Calendar.MINUTE);
        int sec = now.get(Calendar.SECOND);

        ibleChangeCharacteristic.setResult(
                serviceUuid, characteristicUuid,
                new byte[] { (byte) 0x80, (byte) 0x14, (byte) 0x0B, (byte) sessionNumber,
                        (byte) 0x00, (byte) count, (byte) 0x01, (byte) year, (byte) month, (byte) day,
                        (byte) hour, (byte) min, (byte) sec, (byte) 0x00,
                        (byte) 0xEF });
    }


    private int dataCount = 0;
    int heartrate = 110;
    int calorie = -1;
    int steps = 0;
    public void doStoredDataReply(IBLEChangeCharacteristic ibleChangeCharacteristic) {

        byte[] storedData;

        Random random = new Random();

        // Heart Rate
        int r = random.nextInt() % 3;
        switch (r) {
            case 0:
                heartrate--;
                break;
            case 1:
                heartrate++;
                break;
            default:
                break;
        }
        if (heartrate < 50)
            heartrate = 50;
        else if (heartrate > 150)
            heartrate = 150;

        // Calorie & steps
        calorie = calorie + 2;

        steps++;

        dataCount++;
        ibleChangeCharacteristic.setResult(serviceUuid, characteristicUuid,
                new byte[] { (byte) 0x80, (byte) 0x16, (byte) 0x09, (byte) sessionNumber, (byte) 0x00, (byte) dataCount,
                        (byte) heartrate, (byte) 0x00, (byte) (calorie / 256), (byte) (calorie % 256),
                        (byte) (steps / 256), (byte) (steps % 256), (byte) 0xEF });

        if (dataCount < 100) {
            callAfter(10, "doStoredDataReply");
        }
        else if(dataCount == 100) {
            callAfter(10, "doSendEndOfSession");
            dataCount = 0;
        }

    }

    public void doSendEndOfSession(IBLEChangeCharacteristic ibleChangeCharacteristic) {
        ibleChangeCharacteristic.setResult(
                serviceUuid, characteristicUuid,
                new byte[] { (byte) 0x80, (byte) 0x18, (byte) 0x01, (byte) sessionNumber, (byte) 0xEF });
    }
}

// service uuid
// "00001800-0000-1000-8000-00805f9b34fb"
// "00001801-0000-1000-8000-00805f9b34fb"
// "0000180a-0000-1000-8000-00805f9b34fb"
// "0000180d-0000-1000-8000-00805f9b34fb" // Heart rate
// "0000180f-0000-1000-8000-00805f9b34fb" // battery
// "0000ff00-0000-1000-8000-00805f9b34fb" // HRM
// characteristic uuid
// "00002a00-0000-1000-8000-00805f9b34fb"
// "00002a01-0000-1000-8000-00805f9b34fb"
// "00002a04-0000-1000-8000-00805f9b34fb"
// "00002a19-0000-1000-8000-00805f9b34fb" // battery
// "00002a25-0000-1000-8000-00805f9b34fb"
// "00002a26-0000-1000-8000-00805f9b34fb"
// "00002a27-0000-1000-8000-00805f9b34fb"
// "00002a28-0000-1000-8000-00805f9b34fb"
// "00002a29-0000-1000-8000-00805f9b34fb"
// "00002a37-0000-1000-8000-00805f9b34fb" // Heart rate
// "00002a38-0000-1000-8000-00805f9b34fb"
// "00002a39-0000-1000-8000-00805f9b34fb"
// "00002a50-0000-1000-8000-00805f9b34fb"
// "0000ff01-0000-1000-8000-00805f9b34fb" // HRM read
// "0000ff02-0000-1000-8000-00805f9b34fb" // HRM write
// descripter uuid == > CLIENT_CHARACTERISTIC_CONFIG
// "00002902-0000-1000-8000-00805f9b34fb" // 3 times
