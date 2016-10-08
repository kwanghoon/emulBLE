package mocking.android.bluetooth;

import android.os.Handler;
import android.os.Message;

import com.h3.hrm3200.Log;

import java.util.ArrayList;
import java.util.UUID;

import emul.bluetooth.BluetoothLE;

/**
 * Created by moonhyeonah on 2016. 5. 24..
 */
public class PortingLayer {
    // Constants
    //    for BluetoothAdapter
    public static final int LESCANCALLBACK_ONLESCAN_REQUEST=1;
    public static final int LESCANCALLBACK_ONLESCAN_REPLY=2;

    //    for BluetoothDevice
    public static final int CONNECT_GATT_REQUEST=10;
    public static final int CONNECT_GATT_REPLY=11;

    //    for BluetoothGatt
    public static final int DISCOVER_SERVICES_REQUEST=20;
    public static final int DISCOVER_SERVICES_REPLY=21;

    public static final int CHANGE_CHARACTERISTIC_REPLY=25;

    public static final int DEVICE_TIME_REQUEST=30;
    public static final int DEVICE_TIME_REPLY=31;

    public static final int WRITE_CHARACTERISTIC_REQUEST =40;
    public static final int WRITE_CHARACTERISTIC_REPLY =41;

    public static final int DISCONNECT_REQUEST=50;
    public static final int DISCONNECT_REPLY=51;

    public static final int QUIT=-1;

    // Characteristic 구분
    public static final int HRM_READ=1000;
    public static final int HRM_BATTERY=1001;

    private BluetoothAdapter.LeScanCallback leScanCallback;
    private BluetoothAdapter.BondedDeviceAddCallback bondedDeviceAddCallback;

    private String deviceAddress;

    public void setBondedDeviceAddCallback(
            BluetoothAdapter.BondedDeviceAddCallback bondedDeviceAddCallback) {
        this.bondedDeviceAddCallback = bondedDeviceAddCallback;
    }

    public void requestLeScan(BluetoothAdapter.LeScanCallback leScanCallback) {
        this.leScanCallback = leScanCallback;

        Message msg = Message.obtain();
        msg.what = LESCANCALLBACK_ONLESCAN_REQUEST;
        emulHandler.sendMessage(msg);
    }

    private void invokeLeScan(Message msg) {
        // Get device, rssi, and scanRecord out of the msg
        // and invoke invoke_LeScanCallBack_onLeScan(...)
        String arr[] = (String[])msg.obj; // String address, name
        BluetoothDevice btdev = new BluetoothDevice(arr[0], arr[1], this);
        bondedDeviceAddCallback.addBondedDevice(btdev);
        leScanCallback.onLeScan(btdev, 0, null); // TODO: scanRecord==null ?

        this.deviceAddress = arr[0];
    }

    public BluetoothDevice getRemoteDevice() {
        return btdevemulator.getRemoteDevice();
    }

    // BluetootDevice
    private BluetoothGattCallback btGattCallback;
    private BluetoothGatt gatt;

    public void connectGatt(BluetoothGatt gatt, BluetoothGattCallback btGattCallback) {
        this.gatt = gatt;
        this.btGattCallback = btGattCallback;

        Message msg = Message.obtain();
        msg.what = CONNECT_GATT_REQUEST;
        emulHandler.sendMessage(msg);
    }

    private void invokeConnectionStateChange(Message msg) {
        int arr[] = (int[])msg.obj; // e.g., int BluetoothGatt.Success, BluetoothProfile.Connected
        btGattCallback.onConnectionStateChange(gatt, arr[0], arr[1]);
    }

    // BluetoothGatt
    public void discoverServices() {
        Log.v("PortingLayer", "discoverServices");
        Message msg = Message.obtain();
        msg.what = DISCOVER_SERVICES_REQUEST;
        emulHandler.sendMessage(msg);
    }

    private void invokeServicesDiscovered(Message msg) {
        int succ_or_fail = msg.arg1; // e.g., int BluetoothGatt.Success
        ArrayList<BLEService> bleServiceList = (ArrayList<BLEService>)msg.obj;

        UUID serviceUuid, characteristicUuid, descriptorUuid;


        for (BLEService bleService : bleServiceList) {
            switch ( bleService.kind() ) {
                case BLEService.SERVICE_UUID_ONLY:
                    serviceUuid = bleService.serviceUuid();
                    gatt.addService(serviceUuid);
                    break;

                case BLEService.SERVICE_UUID_WITH_CHARACTERISTICS:
                    serviceUuid = bleService.serviceUuid();
                    gatt.addService(serviceUuid);
                    characteristicUuid = bleService.characteristicUuid();
                    gatt.getService(serviceUuid).addCharacteristic(characteristicUuid,
                            bleService.properties(), bleService.permission());
                    break;

                case BLEService.SERVICE_UUID_WITH_CHARACTERISTICS_AND_DESCRIPTOR:
                    serviceUuid = bleService.serviceUuid();
                    gatt.addService(serviceUuid);
                    characteristicUuid = bleService.characteristicUuid();
                    gatt.getService(serviceUuid).addCharacteristic(characteristicUuid,
                            bleService.properties(), bleService.permission());
                    descriptorUuid = bleService.descriptorUuid();
                    gatt.getService(serviceUuid).getCharacteristic(characteristicUuid).addDescriptor(descriptorUuid);
                    break;

                default:
                    // something wrong!!
            }

        }

        btGattCallback.onServicesDiscovered(gatt, succ_or_fail);
    }

    private void invokeCharacteristicChanged(Message msg) {
        MessageChangeCharacteristic msgbody = (MessageChangeCharacteristic)msg.obj;
        byte data[];
        UUID serviceUuid, characteristicUuid;

        serviceUuid = msgbody.serviceUuid;
        characteristicUuid = msgbody.characteristicUuid;
        data = msgbody.bytes;

        BluetoothGattCharacteristic characteristic = gatt.getService(serviceUuid).getCharacteristic(characteristicUuid);
        characteristic.setValue(data);
        btGattCallback.onCharacteristicChanged(gatt, characteristic);
    }


    // Common
    private BluetoothDeviceEmulator btdevemulator;
    private Handler emulHandler;

    Handler myHandler = new Handler() {
        @Override
        // A method to receive a message from the BluetoothDeviceEmulator
        public void handleMessage(Message msg) {
            switch(msg.what) {
                // BluetoothAdapter
                case LESCANCALLBACK_ONLESCAN_REPLY:
                    invokeLeScan(msg);
                    break;

                // BluetootDevice
                case CONNECT_GATT_REPLY:
                    invokeConnectionStateChange(msg);
                    break;

                // BluetootGatt
                case DISCOVER_SERVICES_REPLY:
                    invokeServicesDiscovered(msg);
                    break;

                //
                case CHANGE_CHARACTERISTIC_REPLY:
                    invokeCharacteristicChanged(msg);
                    break;

                //
                case DEVICE_TIME_REPLY:
                    invokeCharacteristicChanged(msg);
                    break;

                //
                case WRITE_CHARACTERISTIC_REPLY:
                    invokeCharacteristicChanged(msg);
                    break;

                //
                case DISCONNECT_REPLY:
                    invokeConnectionStateChange(msg);

                    // btdevemulator를 kill 시킨다.
                    emulHandler.sendEmptyMessage(QUIT);
                    break;
            }

        }
    };

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        Message msg = Message.obtain();
        msg.what = WRITE_CHARACTERISTIC_REQUEST;
        msg.obj = characteristic;
        emulHandler.sendMessage(msg);
    }

    public void createBTDevEmulator(BluetoothLE bluetoothLE) {
        btdevemulator = BluetoothDeviceEmulator.create(myHandler, bluetoothLE);
        btdevemulator.start();

        emulHandler = btdevemulator.getHandler();
    }

    public void disconnect() {
        Message msg = Message.obtain();
        msg.what = DISCONNECT_REQUEST;
        emulHandler.sendMessage(msg);
    }
}
