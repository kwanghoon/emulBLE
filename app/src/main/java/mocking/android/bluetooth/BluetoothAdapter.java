package mocking.android.bluetooth;

import android.os.Handler;
import android.os.Message;

import com.h3.hrm3200.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moonhyeonah on 2016. 5. 22..
 */
public class BluetoothAdapter {
    public final static String ACTION_REQUEST_ENABLE = "android.bluetooth.adapter.action.REQUEST_ENABLE";

    // ??
    private static List<BluetoothDevice> bondedDevices = new ArrayList<BluetoothDevice>();

    private boolean blutooth_enabled;

    private PortingLayer portingLayer;

    public BluetoothAdapter() {
        blutooth_enabled = true;
    }

    public interface LeScanCallback {
        public void onLeScan (BluetoothDevice device, int rssi, byte[] scanRecord);
    }

    public interface BondedDeviceAddCallback {
        public void addBondedDevice(BluetoothDevice btdev);
    }

    public boolean isEnabled() {
        return blutooth_enabled;
    }

    public boolean startLeScan (BluetoothAdapter.LeScanCallback callback) {
        portingLayer = new PortingLayer();

        // 1. create a BluetoothDeviceEmulator
        portingLayer.createBTDevEmulator();

        // 2. send BluetootDeviceEmulator a message to do startLeScan
        portingLayer.setBondedDeviceAddCallback(new BondedDeviceAddCallback() {
            @Override
            public void addBondedDevice(BluetoothDevice btdev) {
                if (btdev != null) {
                    bondedDevices.add(btdev);
                }
            }
        });
        portingLayer.requestLeScan(callback);

        return true;
    }

    public void stopLeScan(BluetoothAdapter.LeScanCallback callback) {
    }

    public BluetoothDevice getRemoteDevice(String address) {
        Log.w("Mocking", "getRemoteDevice : " + address);

        for (BluetoothDevice btdev : bondedDevices) {
            if (btdev.getAddress().equals(address))
                return  btdev;
        }
        return null;
    }


}
