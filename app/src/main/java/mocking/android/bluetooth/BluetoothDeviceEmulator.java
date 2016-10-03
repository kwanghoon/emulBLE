package mocking.android.bluetooth;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.h3.hrm3200.Log;

import java.util.Calendar;
import java.util.Random;

import emul.bluetooth.BluetoothLE;

/**
 * Created by moonhyeonah on 2016. 5. 22..
 */

/*
TODO:
현재의 BlueootoothDeviceEmulator의 이름은 BluetoothScanEmulator로 바꾸는 것이 바람직하다.
그리고 LESCANCALLBACK_ONLESCAN_REQUEST case만 포함하는 핸들러를 갖추도록 바꾼다.

이와 별도로 BluetoothDeviceEmulator 클래스를 따로 두어 leConnect()를 부를때 객체를 만든다.
이 클래스에는 CONNECT_GATT_REQUEST와 그 이후의 ..._REQUEST를 처리하는 핸드러를 갖추도록 바꾼다.

이렇게 변경하면 여러 개의 BLE 디바이스를 처리할 수 있는 구조가 된다.
 */
public class BluetoothDeviceEmulator extends Thread {

    public BluetoothDevice getRemoteDevice() {
        return new BluetoothDevice(
                    bluetoothLE.getBluetoothDeviceAddress(),
                    bluetoothLE.getBluetoothDeviceName());
    }

    private Handler app_handler;

    // app_handler.sendMessage ( ... );

    private BluetoothLE bluetoothLE;

    public static BluetoothDeviceEmulator create(Handler app_handler, BluetoothLE bluetoothLE) {
        BluetoothDeviceEmulator btdevemul = new BluetoothDeviceEmulator(app_handler);
        btdevemul.bluetoothLE = bluetoothLE;
        return btdevemul;
    }

    public BluetoothDeviceEmulator(Handler app_handler) {
        this.app_handler = app_handler;
    }

    private Handler handler ;

    // for doBLEScan
    String[] addrNameArr;

    // for doConnect
    int succ_or_fail;
    int connect_state;

    // for doDiscoverService
//    int succ_or_fail;



    public void run() {
        Looper.prepare();

        handler = new Handler() {
            public void handleMessage(Message msg) {
                Message reply_msg;
                switch(msg.what){
                    case PortingLayer.LESCANCALLBACK_ONLESCAN_REQUEST:
//                        reply_msg = Message.obtain();
//                        reply_msg.what = PortingLayer.LESCANCALLBACK_ONLESCAN_REPLY;
//                        // Put device, rssi, and scanRecord out of the msg
//                        reply_msg.obj = new String[] {
//                                bluetoothLE.getBluetoothDeviceAddress(),
//                                bluetoothLE.getBluetoothDeviceName()
//                        };
//                        app_handler.sendMessage(reply_msg);


                        bluetoothLE.doBLEScan(new IBLEScan() {
                            public void setBLEAddressName(String[] arg) {
                                addrNameArr = arg;
                            }
                        });

                        reply_msg = Message.obtain();
                        reply_msg.what = PortingLayer.LESCANCALLBACK_ONLESCAN_REPLY;
                        // Put device, rssi, and scanRecord out of the msg
                        reply_msg.obj = addrNameArr;
                        app_handler.sendMessage(reply_msg);


                        break;

                    case PortingLayer.CONNECT_GATT_REQUEST:

                        bluetoothLE.doConnect(new IBLEConnect() {
                            @Override
                            public void setConnectResult(int succ_or_fail, int state) {
                                BluetoothDeviceEmulator.this.succ_or_fail = succ_or_fail;
                                BluetoothDeviceEmulator.this.connect_state = state;
                            }
                        });

                        reply_msg = Message.obtain();
                        reply_msg.what = PortingLayer.CONNECT_GATT_REPLY;
                        reply_msg.obj = new int[] {succ_or_fail, connect_state};
                        app_handler.sendMessage(reply_msg);

                        break;

                    case PortingLayer.DISCOVER_SERVICES_REQUEST:

                        bluetoothLE.doDiscoverService(new IBLEDiscoverService() {
                            @Override
                            public void setResult(int succ_or_fail) {
                                BluetoothDeviceEmulator.this.succ_or_fail = succ_or_fail;
                            }
                        });
                        reply_msg = Message.obtain();
                        reply_msg.what = PortingLayer.DISCOVER_SERVICES_REPLY;
                        reply_msg.obj = new int[] {succ_or_fail};
                        app_handler.sendMessage(reply_msg);

                        break;

                    case PortingLayer.DEVICE_TIME_REQUEST:
                        reply_msg = Message.obtain();
                        reply_msg.what = PortingLayer.DEVICE_TIME_REPLY;
                        reply_msg.arg1 = PortingLayer.HRM_READ;
                        reply_msg.obj = new byte[]{(byte) 0x80, (byte) 0x10, (byte) 0x08, (byte) 0x0F, (byte) 0x05,
                                (byte) 0x13, (byte) 0x11, (byte) 0x0B, (byte) 0x2D, (byte) 0x01, (byte) 0x00, (byte) 0xEF};
                        app_handler.sendMessage(reply_msg);

                        break;

                    case PortingLayer.DATA_REQUEST:
                        deviceStateMachine(msg);
                        /*
                        byte in[] = ((BluetoothGattCharacteristic)msg.obj).getValue();

                        if ((in[1] & 0xff) == 0x82 && (in[3] & 0xff) == 0x02) {  // 측정종료 요청
                            createHRMData.interrupt();

                            reply_msg = Message.obtain();
                            reply_msg.what = PortingLayer.CONNECT_GATT_REPLY;
                            reply_msg.obj = new int[] {BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_DISCONNECTED};
                            app_handler.sendMessage(reply_msg);

                            break;
                        }
                        reply_msg = Message.obtain();
                        reply_msg.what = PortingLayer.DATA_REPLY;
                        reply_msg.arg1 = PortingLayer.HRM_READ;
                        reply_msg.obj = stateMachine(in);
                        app_handler.sendMessage(reply_msg);
                        */
                        break;

                    case PortingLayer.DISCONNECT_REQUEST:
                        reply_msg = Message.obtain();
                        reply_msg.what = PortingLayer.DISCONNECT_REPLY;
                        reply_msg.obj = new int[] {BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_DISCONNECTED};
                        app_handler.sendMessage(reply_msg);

                        break;
                }


            }
        };

        Looper.loop();
    }

    private int sessionNumber = -1;
    private void deviceStateMachine(Message msg) {
        byte in[] = ((BluetoothGattCharacteristic)msg.obj).getValue();
        Message reply_msg;


        if ((in[1] & 0xff) == 0x82 && (in[3] & 0xff) == 0x02) {  // 측정종료 요청
            createHRMData.interrupt();

            reply_msg = Message.obtain();
            reply_msg.what = PortingLayer.CONNECT_GATT_REPLY;
            reply_msg.obj = new int[] {BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_DISCONNECTED};
            app_handler.sendMessage(reply_msg);
        }

        else if ((in[1] & 0xff) == 0x80) {
            // 저장데이터 유무/다운로드 가능여부(0x81) 전송

            reply_msg = Message.obtain();
            reply_msg.what = PortingLayer.DATA_REPLY;
            reply_msg.arg1 = PortingLayer.HRM_READ;

            Random random = new Random();
            int r = random.nextInt() % 3;
            //r = 2;

            switch (r) {
                // out[4] : 0x01 : 다운로드 불가 상태(측정중), 이 메시지 전송후 바로 측정 데이타 전송해야 함
                case 0:
                    // out[3] 0x01: 저장 데이터 없음 && out[4] 0x01 : 다운로드 불가 상태(측정중)
                    reply_msg.obj = new byte[]{(byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x01, (byte) 0x01, (byte) 0xEF};
                    createHRMData = new Thread( new MyRunnable() );
                    createHRMData.start();
                    break;

                case 1:
                    // out[3] 0x00: 저장 데이터 있음 && out[4] 0x01 : 다운로드 불가 상태(측정중)
                    reply_msg.obj = new byte[]{(byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0xEF};
                    createHRMData = new Thread( new MyRunnable() );
                    createHRMData.start();
                    break;

                // out[4] : 0x00 : 다운로드 가능 상태, 이 메시지 전송후 0x82 메시지 기다려야 함
                case 2:
                    // out[3] 0x00: 저장 데이터 있음 && out[4] 0x00 : 다운로드 가능 상태
                    reply_msg.obj = new byte[]{(byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0xEF};
                    break;

                default:
                    // out[3] 0x00: 저장 데이터 있음 && out[4] 0x00 : 다운로드 가능 상태
                    reply_msg.obj = new byte[]{(byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0xEF};
                    break;
            }
            app_handler.sendMessage(reply_msg);
        }

        else if ((in[1] & 0xff) == 0x82 && (in[3] & 0xff) == 0x01) {  // 실시간 자료 요청인 경우
            reply_msg = Message.obtain();
            reply_msg.what = PortingLayer.DATA_REPLY;
            reply_msg.arg1 = PortingLayer.HRM_READ;
            reply_msg.obj = new byte[]{(byte) 0x80, (byte) 0x83, (byte) 0x01, (byte) 0x00, (byte) 0xEF};
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            app_handler.sendMessage(reply_msg);

            createHRMData = new Thread( new MyRunnable() );
            createHRMData.start();
        }
//            else if ((in[1] & 0xff) == 0x82 && (in[3] & 0xff) == 0x02) {  // 측정종료 요청
//                createHRMData.interrupt();
//                out = new byte[0];
//            }
        else if ((in[1] & 0xff) == 0x82 && (in[3] & 0xff) == 0x03) {  // 저장 자료 요청인 경우
            reply_msg = Message.obtain();
            reply_msg.what = PortingLayer.DATA_REPLY;
            reply_msg.arg1 = PortingLayer.HRM_READ;
            reply_msg.obj = new byte[]{(byte) 0x80, (byte) 0x83, (byte) 0x01, (byte) 0x00, (byte) 0xEF};
            app_handler.sendMessage(reply_msg);

            sendSessionCount();
        }

        else if ((in[1] & 0xff) == 0x84) {                           // 세션데이타 요청인 경우
            sessionNumber = in[3] & 0xff;

            reply_msg = Message.obtain();
            reply_msg.what = PortingLayer.DATA_REPLY;
            reply_msg.arg1 = PortingLayer.HRM_READ;
            reply_msg.obj = new byte[]{(byte) 0x80, (byte) 0x85, (byte) 0x01, (byte) 0x00, (byte) 0xEF}; // OK
            app_handler.sendMessage(reply_msg);

            sendSessionInfo(sessionNumber);
        }

        else if ((in[1] & 0xff) == 0x15) {                           // 세션데이타 요청인 경우
            byte[] storedData;
            //= new byte[]{(byte) 0x80, (byte) 0x16, (byte) 0x09, (byte) sessionNumber, (byte) 0x00,
            //        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xEF};

            Random random = new Random();
            int heartrate = 110;
            int calorie = -1;
            int steps = 0;



            for (int i=0; i<100; i++) {
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
                storedData[5] = (byte)i;
                storedData[6] = (byte)heartrate;
                storedData[8] = (byte)(calorie/256);
                storedData[9] = (byte)(calorie%256);
                storedData[10] = (byte)(steps/256);
                storedData[11] = (byte)(steps%256);

                reply_msg = Message.obtain();
                reply_msg.what = PortingLayer.DATA_REPLY;
                reply_msg.arg1 = PortingLayer.HRM_READ;
                reply_msg.obj = storedData;
                app_handler.sendMessage(reply_msg);
            }

            // end of session
            reply_msg = Message.obtain();
            reply_msg.what = PortingLayer.DATA_REPLY;
            reply_msg.arg1 = PortingLayer.HRM_READ;
            reply_msg.obj = new byte[]{(byte) 0x80, (byte) 0x18, (byte) 0x01, (byte) sessionNumber, (byte) 0xEF};
            app_handler.sendMessageDelayed(reply_msg, 5000);

        }

        else {

        }

    }

    private Thread createHRMData;

    class MyRunnable implements Runnable {

        public MyRunnable() {
            super();
        }

        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                    return;
                }
//                Random random = new Random();
                byte[] realTimeData = new byte[]{(byte) 0x80, (byte) 0x1A, (byte) 0x07, (byte) 0xFF, (byte) 0x50,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xEF};
//                int r = (random.nextInt()) % 101 + 50;
//                realTimeData[4] = (byte)r;
//                Log.w("Mocking", "createHRMData : " + r);

                Message reply_msg = Message.obtain();
                reply_msg.what = PortingLayer.DATA_REPLY;
                reply_msg.arg1 = PortingLayer.HRM_READ;
                reply_msg.obj = realTimeData;
                app_handler.sendMessage(reply_msg);
            }
        }
    }
/*
    private byte[] stateMachine( byte[] in ){
        byte[] out;

        if ( in != null && in.length > 0 ) {
            if ((in[1] & 0xff) == 0x80) {
            // 저장데이터 유무/다운로드 가능여부(0x81) 전송

                Random random = new Random();
                int r = random.nextInt() % 3;
                r = 2;

                switch (r) {
                    // out[4] : 0x01 : 다운로드 불가 상태(측정중), 이 메시지 전송후 바로 측정 데이타 전송해야 함
                    case 0:
                        // out[3] 0x01: 저장 데이터 없음 && out[4] 0x01 : 다운로드 불가 상태(측정중)
                        out = new byte[]{(byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x01, (byte) 0x01, (byte) 0xEF};
                        createHRMData = new Thread( new MyRunnable() );
                        createHRMData.start();
                        break;

                    case 1:
                        // out[3] 0x00: 저장 데이터 있음 && out[4] 0x01 : 다운로드 불가 상태(측정중)
                        out = new byte[]{(byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0xEF};
                        createHRMData = new Thread( new MyRunnable() );
                        createHRMData.start();
                        break;

                    // out[4] : 0x00 : 다운로드 가능 상태, 이 메시지 전송후 0x82 메시지 기다려야 함
                    case 2:
                        // out[3] 0x00: 저장 데이터 있음 && out[4] 0x00 : 다운로드 가능 상태
                        out = new byte[]{(byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0xEF};
                        break;

                    default:
                        // out[3] 0x00: 저장 데이터 있음 && out[4] 0x00 : 다운로드 가능 상태
                        out = new byte[]{(byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0xEF};
                        break;
                }

            }

            else if ((in[1] & 0xff) == 0x82 && (in[3] & 0xff) == 0x01) {  // 실시간 자료 요청인 경우
                out = new byte[]{(byte) 0x80, (byte) 0x83, (byte) 0x01, (byte) 0x00, (byte) 0xEF};
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                createHRMData = new Thread( new MyRunnable() );
                createHRMData.start();
            }
//            else if ((in[1] & 0xff) == 0x82 && (in[3] & 0xff) == 0x02) {  // 측정종료 요청
//                createHRMData.interrupt();
//                out = new byte[0];
//            }
            else if ((in[1] & 0xff) == 0x82 && (in[3] & 0xff) == 0x03) {  // 저장 자료 요청인 경우
                out = new byte[]{(byte) 0x80, (byte) 0x83, (byte) 0x01, (byte) 0x00, (byte) 0xEF};

                sendSessionCount();
            }

            else if ((in[1] & 0xff) == 0x84) {                           // 세션데이타 요청인 경우
                out = new byte[]{(byte) 0x80, (byte) 0x85, (byte) 0x01, (byte) 0x00, (byte) 0xEF}; // OK

                sendSessionInfo((int)(in[3] & 0xff));
            }

            else if ((in[1] & 0xff) == 0x15) {                           // 세션데이타 요청인 경우
                out = new byte[]{(byte) 0x80, (byte) 0x85, (byte) 0x01, (byte) 0x00, (byte) 0xEF}; // OK

                sendSessionInfo((int)(in[3] & 0xff));
            }

            else {
                out = new byte[]{(byte) 0x80, (byte) 0x10, (byte) 0x08, (byte) 0x0F, (byte) 0x05,
                        (byte) 0x13, (byte) 0x11, (byte) 0x0B, (byte) 0x2D, (byte) 0x01, (byte) 0x00, (byte) 0xEF};
            }
        }
        else {
            out = new byte[0];
        }
        return out;
    }
*/
    private void sendSessionCount() {
        Message reply_msg = Message.obtain();
        reply_msg.what = PortingLayer.DATA_REPLY;
        reply_msg.arg1 = PortingLayer.HRM_READ;
        reply_msg.obj = new byte[]{(byte) 0x80, (byte) 0x12, (byte) 0x01, (byte) 0x05, (byte) 0xEF};
        app_handler.sendMessageDelayed(reply_msg, 5000);
    }

    private void sendSessionInfo(int sessionNumber) {

        int count = 100;
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR) - 2000;
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int min = now.get(Calendar.MINUTE);
        int sec = now.get(Calendar.SECOND);

        Message reply_msg = Message.obtain();
        reply_msg.what = PortingLayer.DATA_REPLY;
        reply_msg.arg1 = PortingLayer.HRM_READ;
        reply_msg.obj = new byte[]{(byte) 0x80, (byte) 0x14, (byte) 0x0B, (byte) sessionNumber,
                        (byte) 0x00, (byte) count, (byte) 0x01, (byte) year, (byte) month, (byte) day,
                        (byte) hour, (byte) min, (byte) sec, (byte) 0x00,
                        (byte) 0xEF};
        app_handler.sendMessageDelayed(reply_msg, 5000);
    }

    public Handler getHandler() {
        while ( handler == null )
            ;

        return handler;
    }
}
