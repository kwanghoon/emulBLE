package com.h3.hrm3200.emul;

import com.h3.hrm3200.Log;

import java.util.ArrayList;
import java.util.UUID;

import emul.bluetooth.AutoBluetoothLE;
import emul.bluetooth.model.BLEConnectState;
import emul.bluetooth.model.BLEDisconnectState;
import emul.bluetooth.model.BLENotificationState;
import emul.bluetooth.model.BLEScanState;
import emul.bluetooth.model.BLEServiceDiscoverState;
import emul.bluetooth.model.BLEState;
import emul.bluetooth.model.BLEWriteCharacteristicState;
import mocking.android.bluetooth.BLEService;
import mocking.android.bluetooth.BluetoothGatt;
import mocking.android.bluetooth.BluetoothGattCharacteristic;
import mocking.android.bluetooth.BluetoothProfile;
import mocking.android.bluetooth.IBLEChangeCharacteristic;
import mocking.android.bluetooth.IBLEDiscoverService;

/**
 * Created by khChoi on 2017-08-07.
 */

public class AutoHRM3200 extends AutoBluetoothLE {
    ArrayList<BLEState> path;

    boolean onMeasuring;

    public AutoHRM3200() {
        path = new ArrayList<BLEState>();

        BLEScanState bleScanState = new BLEScanState("00:11:22:AA:BB:CC", "HRM3200");
        path.add(bleScanState);

        BLEConnectState bleConnectState =
                new BLEConnectState(BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_CONNECTED);
        path.add(bleConnectState);

        BLEServiceDiscoverState bleServiceDiscoverState = new ServiceDiscoverFollowedByDeviceTimeReply();
        path.add(bleServiceDiscoverState);

        DeviceTimeReplyState deviceTimeReplyState = new DeviceTimeReplyState();
        path.add(deviceTimeReplyState);

        OK_0x11_State ok_0x11_state = new OK_0x11_State(this);
        path.add(ok_0x11_state);

        AppTime_0x80_State appTime_0x80_state = new AppTime_0x80_State(this, 0x10, 0x10);
        // 0x10, 0x00
        // 0x00, 0x00
        path.add(appTime_0x80_state);

        for (int i = 0; i<10; i++) {
            RealtimeDataReply realtimeDataReply = new RealtimeDataReply();
            path.add(realtimeDataReply);
        }

        DisconnectByApp disconnectByApp = new DisconnectByApp(this);
        path.add(disconnectByApp);

        BLEDisconnectState bleDisconnectState =
                new BLEDisconnectState(BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_DISCONNECTED);
        path.add(bleDisconnectState);

        this.setPath(path);
        this.setIndex(0);
    }

    @Override
    public void doDiscoverService(IBLEDiscoverService ibleDiscoverService) {
        if (path != null && index() < path.size()) {
            BLEState state = path.get(index());
            if (state instanceof ServiceDiscoverFollowedByDeviceTimeReply) {
                ServiceDiscoverFollowedByDeviceTimeReply srvDiscoverState = (ServiceDiscoverFollowedByDeviceTimeReply)state;
                srvDiscoverState.action(ibleDiscoverService);

                incIndex();
            }
        }

        // Exception ???
    }

    // For BLEServiceDiscoverState
    class ServiceDiscoverFollowedByDeviceTimeReply extends BLEServiceDiscoverState {
        public ServiceDiscoverFollowedByDeviceTimeReply() {
            super(BluetoothGatt.GATT_SUCCESS, bleServiceList());
        }

        public void action(IBLEDiscoverService ibleDiscoverService) {
            // Condtion: This method should be called in doDiscoverService()

            // Return succ_or_fail and bleServiceList
            super.action(ibleDiscoverService);

            AutoHRM3200.super.callAfter(500, "doNotification");
        }
    }

    ArrayList<BLEService> bleServiceList() {
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

    // HRM3200 Service UUID and characteristic UUID
    private UUID serviceUuid = UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb");
    private UUID characteristicUuid = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");

    @Override
    public void doNotification(IBLEChangeCharacteristic ibleChangeCharacteristic) {
        if (path != null && index() < path.size()) {
            BLEState state = path.get(index());

            if (state instanceof DeviceTimeReplyState) {
                DeviceTimeReplyState deviceTimeReplyState = (DeviceTimeReplyState)state;
                deviceTimeReplyState.action(ibleChangeCharacteristic);

                incIndex();
            }
            else if (state instanceof RealtimeDataReply) {
                RealtimeDataReply realtimeDataReply = (RealtimeDataReply)state;
                realtimeDataReply.action(ibleChangeCharacteristic);

                incIndex();
            }
        }

        // Exception ???
    }

    // For BLENotificatioNState : Device Time Reply
    class DeviceTimeReplyState extends BLENotificationState {
        public void action(IBLEChangeCharacteristic ibleChangeCharacteristic) {
            // Condition: This should be called in doNotification

            // Return nothing, but notify something through ibleChangeCharacteritic.
            Log.v("HRM3200-Emul", "DeviceTimeReplyState...");

            ibleChangeCharacteristic.setResult(
                    serviceUuid, characteristicUuid,
                    new byte[]{(byte) 0x80, (byte) 0x10, (byte) 0x08, (byte) 0x0F, (byte) 0x05,
                            (byte) 0x13, (byte) 0x11, (byte) 0x0B, (byte) 0x2D, (byte) 0x01, (byte) 0x00, (byte) 0xEF}
            );
        }
    }

    // Realtime Data Reply
    class RealtimeDataReply extends BLENotificationState {
        public void action(IBLEChangeCharacteristic ibleChangeCharacteristic) {
            Log.v("HRM3200", "RealtimeDataReply - Begin");
            ibleChangeCharacteristic.setResult(serviceUuid, characteristicUuid,
                    new byte[]{(byte) 0x80, (byte) 0x1A, (byte) 0x07, (byte) 0xFF, (byte) 0x50,
                            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xEF});

            Log.v("HRM3200", "RealtimeDataReply - End: " + onMeasuring);
            if (onMeasuring == true) {
                callAfter(500, "doNotification");
            }
        }
    }

    @Override
    public void doWriteCharacteristic(BluetoothGattCharacteristic btGattCharacteristic,
                                      IBLEChangeCharacteristic ibleChangeCharacteristic) {
        if (path != null && index() < path.size()) {
            BLEState state = path.get(index());

            if (state instanceof OK_0x11_State) {
                OK_0x11_State ok_0x11_state = (OK_0x11_State) state;

                byte in[] = btGattCharacteristic.getValue();
                if ((in[1] & 0xff) == 0x11) {
                    ok_0x11_state.action(ibleChangeCharacteristic);
                    return;
                }
            }

            else if (state instanceof AppTime_0x80_State) {
                AppTime_0x80_State appTime_0x80_state = (AppTime_0x80_State) state;

                byte in[] = btGattCharacteristic.getValue();
                if ((in[1] & 0xff) == 0x80) {
                    appTime_0x80_state.action(ibleChangeCharacteristic);
                    return;
                }
            }

            else if (state instanceof DisconnectByApp) {
                DisconnectByApp disconnectByApp = (DisconnectByApp) state;

                byte in[] = btGattCharacteristic.getValue();
                if ((in[1] & 0xff) == 0x82 && (in[3] & 0xff) == 0x02) {
                    onMeasuring = false;

                    callDoDisconnectAfter(500);
                    return;
                }
            }
        }

        // Exception ???
    }

    class OK_0x11_State extends BLEWriteCharacteristicState {
        public OK_0x11_State(AutoHRM3200 bluetoothLE) {
            super(bluetoothLE);
        }

        public void action(IBLEChangeCharacteristic ibleChangeCharacteristic) {
            // do nothing
            // 바로 다음에 0x80이 온다.
        }
    }

    class AppTime_0x80_State extends BLEWriteCharacteristicState {
        // storeddata : 0x01 ==> no stored data, 0x00 ==> stored data available
        // downloadable : 0x01 ==> not downloadable, 0x00 ==> ddownloadable
        private int storeddata;
        private int downloadable;

        public AppTime_0x80_State(AutoHRM3200 bluetoothLE, int storeddata, int downloadable) {
            super(bluetoothLE);

            this.storeddata = storeddata;
            this.downloadable = downloadable;
        }

        public void action(IBLEChangeCharacteristic ibleChangeCharacteristic) {
            // out[3] 0x01: 저장 데이터 없음 && out[4] 0x01 : 다운로드 불가 상태(측정중)
            if (storeddata == 0x01 && downloadable == 0x01   ) {
                ibleChangeCharacteristic.setResult(serviceUuid, characteristicUuid,
                        new byte[]{(byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x01, (byte) 0x01, (byte) 0xEF});

                callAfter(500, "doNotification");
                onMeasuring = true;
            }
            // out[3] 0x00: 저장 데이터 있음 && out[4] 0x01 : 다운로드 불가 상태(측정중)
            else if (storeddata == 0x00 && downloadable == 0x01) {
                ibleChangeCharacteristic.setResult(serviceUuid, characteristicUuid,
                        new byte[]{(byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0xEF});

                callAfter(500, "doNotification");
                onMeasuring = true;
            }
            // out[4] : 0x00 : 다운로드 가능 상태, 이 메시지 전송후 0x82 메시지 기다려야 함
            // out[3] 0x00: 저장 데이터 있음 && out[4] 0x00 : 다운로드 가능 상태
            else if ( storeddata == 0x00 && downloadable == 0x00 ) {
                ibleChangeCharacteristic.setResult(serviceUuid, characteristicUuid,
                        new byte[]{(byte) 0x80, (byte) 0x81, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0xEF});
            }
            else {
                // Exceptions ...
            }
        }
    }

    class DisconnectByApp extends BLEWriteCharacteristicState {
        public DisconnectByApp(AutoHRM3200 bluetoothLE) {
            super(bluetoothLE);
        }
    }

}
