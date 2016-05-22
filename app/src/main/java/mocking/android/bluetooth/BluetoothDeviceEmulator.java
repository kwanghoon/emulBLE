package mocking.android.bluetooth;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by moonhyeonah on 2016. 5. 22..
 */
public class BluetoothDeviceEmulator extends Thread {
    private String btdev_address = "00:11:22:AA:BB:CC";
    private String btdev_name = "HRM3200";

    private Handler app_handler;

    // app_handler.sendMessage ( ... );


    public static BluetoothDeviceEmulator create(Handler app_handler) {

        return new BluetoothDeviceEmulator(app_handler);
    }

    public BluetoothDeviceEmulator(Handler app_handler) {
        this.app_handler = app_handler;
    }

    private Handler handler ;

    public void run() {
        Looper.prepare();

        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case BluetoothAdapter.LESCANCALLBACK_ONLESCAN_REQUEST:
                        Message reply_msg = Message.obtain();
                        reply_msg.what = BluetoothAdapter.LESCANCALLBACK_ONLESCAN_REPLY;
                        // Put device, rssi, and scanRecord out of the msg
                        reply_msg.obj = new BluetoothDevice(btdev_address, btdev_name);
                        app_handler.sendMessage(reply_msg);

                        break;
                }


            }
        };

        Looper.loop();
    }


    public Handler getHandler() {
        while ( handler == null )
            ;

        return handler;
    }
}
