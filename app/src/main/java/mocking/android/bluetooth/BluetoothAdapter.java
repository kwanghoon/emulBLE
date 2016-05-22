package mocking.android.bluetooth;

import android.os.Handler;
import android.os.Message;

/**
 * Created by moonhyeonah on 2016. 5. 22..
 */
public class BluetoothAdapter {
    public final static int LESCANCALLBACK_ONLESCAN_REQUEST=1;
    public final static int LESCANCALLBACK_ONLESCAN_REPLY=2;

    public final static String ACTION_REQUEST_ENABLE = "android.bluetooth.adapter.action.REQUEST_ENABLE";

    BluetoothAdapter.LeScanCallback callback;

    private boolean blutooth_enabled;

    private BluetoothDeviceEmulator btdevemulator;
    private Handler emul_handler;

    public BluetoothAdapter() {
        blutooth_enabled = true;
    }

    public interface LeScanCallback {
        public void onLeScan (BluetoothDevice device, int rssi, byte[] scanRecord);
    }


    public boolean isEnabled() {
        return blutooth_enabled;
    }

    public boolean startLeScan (BluetoothAdapter.LeScanCallback callback) {
        this.callback = callback;
        // 1. create a BluetoothDeviceEmulator
        btdevemulator = BluetoothDeviceEmulator.create(handler);
        btdevemulator.start();

        emul_handler = btdevemulator.getHandler();

        // 2. send BluetootDeviceEmulator a message to do startLeScan
        Message msg = Message.obtain();
        msg.what = LESCANCALLBACK_ONLESCAN_REQUEST;
        emul_handler.sendMessage(msg);

        return true;
    }

    public void stopLeScan(BluetoothAdapter.LeScanCallback callback) {
    }

    public BluetoothDevice getRemoteDevice(String address) {
        return null;
    }

    // A method to receive a message from the BluetoothDeviceEmulator
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LESCANCALLBACK_ONLESCAN_REPLY) {
                // Get device, rssi, and scanRecord out of the msg
                // and invoke invoke_LeScanCallBack_onLeScan(...)
                BluetoothDevice btdev = (BluetoothDevice)msg.obj;
                callback.onLeScan(btdev, 0, null); // TODO: scanRecord==null ?
            }

        }
    };

    // A method to invoke the callback LeScanCallback.onLeScan()
    private void invoke_LeScanCallBack_onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        callback.onLeScan(device, rssi, scanRecord);
    }

}
