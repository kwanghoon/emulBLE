package com.h3.hrm3200.emul.scenario;

import com.h3.hrm3200.emul.model.AppTime_0x80_0x81_State;
import com.h3.hrm3200.emul.model.DeviceTimeReplyState;
import com.h3.hrm3200.emul.model.OK_0x11_State;
import com.h3.hrm3200.emul.model.REQ_RealtimeData_0x82_0x01;
import com.h3.hrm3200.emul.model.RealtimeDataReply;
import com.h3.hrm3200.emul.model.ServiceDiscoveryHRM3200;

import java.util.ArrayList;

import emul.bluetooth.BluetoothLE;
import emul.bluetooth.model.BLEConnectState;
import emul.bluetooth.model.BLEDisconnectState;
import emul.bluetooth.model.BLEScanState;
import emul.bluetooth.model.BLEServiceDiscoverState;
import emul.bluetooth.model.BLEState;
import emul.bluetooth.model.Scenario;
import mocking.android.bluetooth.BLEService;
import mocking.android.bluetooth.BluetoothGatt;
import mocking.android.bluetooth.BluetoothProfile;

/**
 * Created by khChoi on 2017-08-17.
 */

public class Scenario_BLEScan_Connect_Discovery_RealtimeDataByUI_DisconnectionByDevice extends Scenario {
    private BluetoothLE bluetoothLE;

    public Scenario_BLEScan_Connect_Discovery_RealtimeDataByUI_DisconnectionByDevice(BluetoothLE bluetoothLE, ArrayList<BLEState> path) {
        super(path);
        this.bluetoothLE = bluetoothLE;
        this.buildPath();
    }

    public void buildPath() {
        // BLE Scan
        BLEScanState bleScanState = new BLEScanState("00:11:22:AA:BB:CC", "HRM3200");
        path().add(bleScanState);

        // BLE Connect
        BLEConnectState bleConnectState =
                new BLEConnectState(BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_CONNECTED);
        path().add(bleConnectState);

        // Service Discovery
        BLEServiceDiscoverState bleServiceDiscoverState = new ServiceDiscoveryHRM3200(BluetoothGatt.GATT_FAILURE,
                new ArrayList<BLEService>());
        path().add(bleServiceDiscoverState);

        // State0: Notification of Device Time
        DeviceTimeReplyState deviceTimeReplyState = new DeviceTimeReplyState(bluetoothLE);
        path().add(deviceTimeReplyState);

        // State1: OK (0x11)
        OK_0x11_State ok_0x11_state = new OK_0x11_State(bluetoothLE);
        path().add(ok_0x11_state);

        // State1-2: App Time (0x80)
        AppTime_0x80_0x81_State appTime_0x80_0x81_state = new AppTime_0x80_0x81_State(bluetoothLE, 0x00, 0x00);
        path().add(appTime_0x80_0x81_state);

        REQ_RealtimeData_0x82_0x01 req_realtimeData_0x82_0x01 = new REQ_RealtimeData_0x82_0x01(bluetoothLE);
        path().add(req_realtimeData_0x82_0x01);

        // State5:
        for (int i = 0; i<10; i++) {
            RealtimeDataReply realtimeDataReply = new RealtimeDataReply(bluetoothLE);
            path().add(realtimeDataReply);
        }

//        // A final state disconnected by App
//
//        // User presses the stop button.
//        REQ_Disconnection_0x82_0x02_State req_disconnection_0x82_0x02_state = new REQ_Disconnection_0x82_0x02_State(this);
//        path.add(req_disconnection_0x82_0x02_state);
//
//        // Disconnection by App
//        DisconnectByApp disconnectByApp = new DisconnectByApp(this, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_DISCONNECTED);
//        path.add(disconnectByApp);

        // Another final state disconnected by Device

        BLEDisconnectState bleDisconnectState =
                new BLEDisconnectState(bluetoothLE, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_DISCONNECTED);
        path().add(bleDisconnectState);
    }
}
