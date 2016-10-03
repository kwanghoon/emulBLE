package mocking.android.bluetooth;

import android.os.Handler;
import android.os.Message;

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

    public static final int DEVICE_TIME_REQUEST=30;
    public static final int DEVICE_TIME_REPLY=31;

    public static final int DATA_REQUEST=40;
    public static final int DATA_REPLY=41;

    public static final int DISCONNECT_REQUEST=50;
    public static final int DISCONNECT_REPLY=51;

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
        Message msg = Message.obtain();
        msg.what = DISCOVER_SERVICES_REQUEST;
        emulHandler.sendMessage(msg);
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

    private void invokeServicesDiscovered(Message msg) {
        int arr[] = (int[])msg.obj; // e.g., int BluetoothGatt.Success

        UUID serviceUuid, characteristicUuid, descriptorUuid;

        serviceUuid = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
        gatt.addService(serviceUuid);
        serviceUuid = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
        gatt.addService(serviceUuid);

        serviceUuid = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
        gatt.addService(serviceUuid);
        characteristicUuid = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
        gatt.getService(serviceUuid).addCharacteristic(characteristicUuid,
                BluetoothGattCharacteristic.PROPERTY_NOTIFY, BluetoothGattCharacteristic.PERMISSION_WRITE);

        serviceUuid = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
        gatt.addService(serviceUuid);
        characteristicUuid = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
        gatt.getService(serviceUuid).addCharacteristic(characteristicUuid,
                BluetoothGattCharacteristic.PROPERTY_NOTIFY | BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_WRITE);
        descriptorUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"); // TODO:
        gatt.getService(serviceUuid).getCharacteristic(characteristicUuid).addDescriptor(descriptorUuid);

        serviceUuid = UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb");
        gatt.addService(serviceUuid);
        characteristicUuid = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");
        gatt.getService(serviceUuid).addCharacteristic(characteristicUuid,
                BluetoothGattCharacteristic.PROPERTY_NOTIFY, BluetoothGattCharacteristic.PERMISSION_WRITE);
        gatt.getService(serviceUuid).getCharacteristic(characteristicUuid).addDescriptor(descriptorUuid);

        characteristicUuid = UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb");
        gatt.getService(serviceUuid).addCharacteristic(characteristicUuid,
                BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE, BluetoothGattCharacteristic.PERMISSION_WRITE);
        gatt.getService(serviceUuid).getCharacteristic(characteristicUuid).addDescriptor(descriptorUuid);


        btGattCallback.onServicesDiscovered(gatt, arr[0]);

        // 디바이스에 장치시간 요청
        msg = Message.obtain();
        msg.what = DEVICE_TIME_REQUEST;
        emulHandler.sendMessage(msg);
    }

    private void invokeCharacteristicChanged(Message msg) {
        int arg1 = msg.arg1;
        byte data[] = (byte[])msg.obj;
        UUID serviceUuid, characteristicUuid;

        if(arg1 == HRM_READ) {
            serviceUuid = UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb");
            characteristicUuid = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");
        }
        else {
            serviceUuid = UUID.randomUUID();
            characteristicUuid = UUID.randomUUID();
        }
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
                case DEVICE_TIME_REPLY:
                    invokeCharacteristicChanged(msg);
                    break;

                //
                case DATA_REPLY:
                    invokeCharacteristicChanged(msg);
                    break;

                //
                case DISCONNECT_REPLY:
                    invokeConnectionStateChange(msg);
                    break;
            }

        }
    };

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        Message msg = Message.obtain();
        msg.what = DATA_REQUEST;
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
