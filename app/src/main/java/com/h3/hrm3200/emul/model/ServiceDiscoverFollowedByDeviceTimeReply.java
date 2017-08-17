package com.h3.hrm3200.emul.model;

import java.util.ArrayList;
import java.util.UUID;

import emul.bluetooth.model.BLEServiceDiscoverState;
import mocking.android.bluetooth.BLEService;
import mocking.android.bluetooth.BluetoothGatt;
import mocking.android.bluetooth.BluetoothGattCharacteristic;
import mocking.android.bluetooth.IBLEDiscoverService;

/**
 * Created by khChoi on 2017-08-17.
 */ // For BLEServiceDiscoverState
public class ServiceDiscoverFollowedByDeviceTimeReply extends BLEServiceDiscoverState {
    public ServiceDiscoverFollowedByDeviceTimeReply() {
        super(BluetoothGatt.GATT_SUCCESS, bleServiceList());
    }

    static ArrayList<BLEService> bleServiceList() {
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

        return bleServiceList;
    }

    @Override
    public void action(IBLEDiscoverService ibleDiscoverService) {
        // Condition: This method should be called in doDiscoverService()
        //           Except this, no extra requirement.

        // Do the default action : Return succ_or_fail and bleServiceList
        super.action(ibleDiscoverService);
    }
}
