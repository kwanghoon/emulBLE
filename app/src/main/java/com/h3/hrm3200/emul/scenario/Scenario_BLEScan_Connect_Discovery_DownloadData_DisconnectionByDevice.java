package com.h3.hrm3200.emul.scenario;

import com.h3.hrm3200.emul.model.AppTime_0x80_0x81_State;
import com.h3.hrm3200.emul.model.DeviceTimeReplyState;
import com.h3.hrm3200.emul.model.DisconnectByApp;
import com.h3.hrm3200.emul.model.InitializeData;
import com.h3.hrm3200.emul.model.OK_0x11_State;
import com.h3.hrm3200.emul.model.OK_0x15_State;
import com.h3.hrm3200.emul.model.REQ_DownloadStoredData_0x82_0x03_0x83;
import com.h3.hrm3200.emul.model.REQ_SessionInfo_0x84_0x85;
import com.h3.hrm3200.emul.model.RealtimeDataReply;
import com.h3.hrm3200.emul.model.SendEndOfSession;
import com.h3.hrm3200.emul.model.SendSessionCount;
import com.h3.hrm3200.emul.model.SendSessionInfo;
import com.h3.hrm3200.emul.model.ServiceDiscoverFollowedByDeviceTimeReply;
import com.h3.hrm3200.emul.model.SharedSessionInfo;
import com.h3.hrm3200.emul.model.StoredDataReply;

import java.util.ArrayList;

import emul.bluetooth.BluetoothLE;
import emul.bluetooth.model.BLEConnectState;
import emul.bluetooth.model.BLEDisconnectState;
import emul.bluetooth.model.BLEScanState;
import emul.bluetooth.model.BLEServiceDiscoverState;
import emul.bluetooth.model.BLEState;
import emul.bluetooth.model.Scenario;
import mocking.android.bluetooth.BluetoothGatt;
import mocking.android.bluetooth.BluetoothProfile;

/**
 * Created by khChoi on 2017-08-17.
 */

public class Scenario_BLEScan_Connect_Discovery_DownloadData_DisconnectionByDevice extends Scenario {
    private BluetoothLE bluetoothLE;

    public Scenario_BLEScan_Connect_Discovery_DownloadData_DisconnectionByDevice(BluetoothLE bluetoothLE, ArrayList<BLEState> path) {
        super(path);
        this.bluetoothLE = bluetoothLE;
        this.buildPath();
    }

    private void buildPath() {
        // BLE Scan
        BLEScanState bleScanState = new BLEScanState("00:11:22:AA:BB:CC", "HRM3200");
        path().add(bleScanState);

        // BLE Connect
        BLEConnectState bleConnectState =
                new BLEConnectState(BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_CONNECTED);
        path().add(bleConnectState);

        // Service Discovery
        BLEServiceDiscoverState bleServiceDiscoverState = new ServiceDiscoverFollowedByDeviceTimeReply();
        path().add(bleServiceDiscoverState);

        // State0: Notification of Device Time
        DeviceTimeReplyState deviceTimeReplyState = new DeviceTimeReplyState(bluetoothLE);
        path().add(deviceTimeReplyState);

        // State1: OK (0x11)
        OK_0x11_State ok_0x11_state = new OK_0x11_State(bluetoothLE);
        path().add(ok_0x11_state);

        // State1-2: App Time (0x80)
        AppTime_0x80_0x81_State appTime_0x80_0x81_state = new AppTime_0x80_0x81_State(bluetoothLE, 0x00, 0x00);
        // 0x10, 0x00
        // 0x00, 0x00
        path().add(appTime_0x80_0x81_state);

        // State6:
        REQ_DownloadStoredData_0x82_0x03_0x83 req_downloadStoredData_0x82_0x03_0x83 =
                new REQ_DownloadStoredData_0x82_0x03_0x83(bluetoothLE);
        path().add(req_downloadStoredData_0x82_0x03_0x83);

        // Session count
        int session_count = 2;

        // State7:
        SendSessionCount sendSessionCount = new SendSessionCount(bluetoothLE, session_count);
        path().add(sendSessionCount);

        // Shared information among states
        SharedSessionInfo sharedSessionInfo = new SharedSessionInfo();

        for(int i=0; i<session_count; i++) {
            // Initialize session in REQ_SessionInfo
            REQ_SessionInfo_0x84_0x85 req_sessionInfo_0x84_0x85 = new REQ_SessionInfo_0x84_0x85(bluetoothLE, sharedSessionInfo);
            path().add(req_sessionInfo_0x84_0x85);

            sharedSessionInfo.dataTotalCount = 15; // a simple setting for the number of data per session

            // State10
            SendSessionInfo sendSessionInfo = new SendSessionInfo(bluetoothLE, sharedSessionInfo);
            path().add(sendSessionInfo);

            // State11
            OK_0x15_State ok_0x15_state = new OK_0x15_State(bluetoothLE);
            path().add(ok_0x15_state);

            InitializeData initializeData =
                    new InitializeData(bluetoothLE, sharedSessionInfo);
            path().add(initializeData);

            for (int j = 0; j < sharedSessionInfo.dataTotalCount; j++) {
                StoredDataReply storedDataReply = new StoredDataReply(bluetoothLE, sharedSessionInfo);
                path().add(storedDataReply);
            }

            SendEndOfSession sendEndOfSession = new SendEndOfSession(bluetoothLE, sharedSessionInfo);
            path().add(sendEndOfSession);
        }

//        // A final state disconnected by App
//
//        // User presses the stop button.
//        REQ_Disconnection_0x82_0x02_State req_disconnection_0x82_0x02_state = new REQ_Disconnection_0x82_0x02_State(this);
//        path().add(req_disconnection_0x82_0x02_state);
//
        // Disconnection by App
        DisconnectByApp disconnectByApp = new DisconnectByApp(bluetoothLE, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_DISCONNECTED);
        path().add(disconnectByApp);

        // Another final state disconnected by Device

        //BLEDisconnectState bleDisconnectState =
        //        new BLEDisconnectState(bluetoothLE, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_DISCONNECTED);
        //path().add(bleDisconnectState);
    }
}
