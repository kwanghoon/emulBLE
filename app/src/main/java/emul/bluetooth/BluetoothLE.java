package emul.bluetooth;

import android.os.Message;

import mocking.android.bluetooth.BluetoothGatt;
import mocking.android.bluetooth.BluetoothProfile;
import mocking.android.bluetooth.IBLEConnect;
import mocking.android.bluetooth.IBLEDiscoverService;
import mocking.android.bluetooth.IBLEScan;
import mocking.android.bluetooth.PortingLayer;

/**
 * Created by moonhyeonah on 2016. 10. 3..
 */
public class BluetoothLE {
    private String btdev_address = "00:11:22:AA:BB:CC";
    private String btdev_name = "HRM3200";

    // Setter/Getter functions
    public void setBluetoothDeviceAddress(String bedev_address) {
        this.btdev_address = btdev_address;
    }

    public void setBluetoothDeviceName(String name) {
        this.btdev_name = name;
    }

    public String getBluetoothDeviceAddress() { return btdev_address; }

    public String getBluetoothDeviceName() { return btdev_name; }

    // Callback functions

    public void doBLEScan(IBLEScan iblescan) {
        iblescan.setBLEAddressName(new String[]{
                btdev_address,
                btdev_name
        });
    }

    public void doConnect(IBLEConnect ibleconnect) {
        ibleconnect.setConnectResult(
                BluetoothGatt.GATT_SUCCESS,
                BluetoothProfile.STATE_CONNECTED);
    }

    public void doDiscoverService(IBLEDiscoverService ibleDiscoverService) {
        ibleDiscoverService.setResult(BluetoothGatt.GATT_SUCCESS);
    }

    public void doDisconnect() {

    }

    public void doCharacteristicChanged() {

    }

    public void doReadCharacteristic() {

    }

    public void doWriteCharacteristic() {

    }

    public void doNotification() {

    }


}
