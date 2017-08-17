package com.h3.hrm3200.emul;

import com.h3.hrm3200.emul.model.AppTime_0x80_State;
import com.h3.hrm3200.emul.model.DeviceTimeReplyState;
import com.h3.hrm3200.emul.model.DisconnectByApp;
import com.h3.hrm3200.emul.model.InitializeData;
import com.h3.hrm3200.emul.model.OK_0x11_State;
import com.h3.hrm3200.emul.model.OK_0x15_State;
import com.h3.hrm3200.emul.model.REQ_Disconnection_0x82_0x02_State;
import com.h3.hrm3200.emul.model.REQ_DownloadStoredData_0x82_0x03;
import com.h3.hrm3200.emul.model.REQ_RealtimeData_0x82_0x01;
import com.h3.hrm3200.emul.model.REQ_SessionInfo;
import com.h3.hrm3200.emul.model.RealtimeDataReply;
import com.h3.hrm3200.emul.model.SendEndOfSession;
import com.h3.hrm3200.emul.model.SendSessionCount;
import com.h3.hrm3200.emul.model.SendSessionInfo;
import com.h3.hrm3200.emul.model.ServiceDiscoverFollowedByDeviceTimeReply;
import com.h3.hrm3200.emul.model.StoredDataReply;
import com.h3.hrm3200.emul.scenario.Scenario_BLEScan_Connect_Discovery_DownloadData_DisconnectionByDevice;
import com.h3.hrm3200.emul.scenario.Scenario_BLEScan_Connect_Discovery_RealtimeDataByUI_DisconnectionByApp;
import com.h3.hrm3200.emul.scenario.Scenario_BLEScan_Connect_Discovery_RealtimeDataByUI_DisconnectionByDevice;
import com.h3.hrm3200.emul.scenario.Scenario_BLEScan_Connect_Discovery_RealtimeData_DisconnectionByApp;
import com.h3.hrm3200.emul.scenario.Scenario_BLEScan_Connect_Discovery_RealtimeData_DisconnectionByDevice;

import java.util.ArrayList;
import java.util.UUID;

import emul.bluetooth.AutoBluetoothLE;
import emul.bluetooth.model.BLEConnectState;
import emul.bluetooth.model.BLEDisconnectState;
import emul.bluetooth.model.BLEScanState;
import emul.bluetooth.model.BLEServiceDiscoverState;
import emul.bluetooth.model.BLEState;
import emul.bluetooth.model.BLEStateException;
import mocking.android.bluetooth.BLEService;
import mocking.android.bluetooth.BluetoothGatt;
import mocking.android.bluetooth.BluetoothGattCharacteristic;
import mocking.android.bluetooth.BluetoothProfile;
import mocking.android.bluetooth.IBLEChangeCharacteristic;
import mocking.android.bluetooth.IBLEDisconnect;
import mocking.android.bluetooth.IBLEDiscoverService;

/**
 * Created by khChoi on 2017-08-07.
 */

public class AutoHRM3200 extends AutoBluetoothLE {
    ArrayList<BLEState> path;

    public AutoHRM3200() {
        path = new ArrayList<BLEState>();

        // new Scenario_BLEScan_Connect_Discovery_RealtimeData_DisconnectionByApp(this, path);
        new Scenario_BLEScan_Connect_Discovery_RealtimeData_DisconnectionByDevice(this, path);
        // BUG : new Scenario_BLEScan_Connect_Discovery_RealtimeDataByUI_DisconnectionByApp(this, path);
        // Bug : new Scenario_BLEScan_Connect_Discovery_RealtimeDataByUI_DisconnectionByDevice(this, path);
        // new Scenario_BLEScan_Connect_Discovery_DownloadData_DisconnectionByDevice(this, path);

        // Initialize a path for testing
        this.setPath(path);
        this.setIndex(0);
    }

    //////////////////////////////////////////////////////////////////////////
    //  Service Discovery
    //    - doDiscoverService
    //    - Classes for relevant states
    //////////////////////////////////////////////////////////////////////////
    @Override
    public void doDiscoverService(IBLEDiscoverService ibleDiscoverService) {
        if (path != null && index() < path.size()) {
            BLEState state = path.get(index());
            if (state instanceof ServiceDiscoverFollowedByDeviceTimeReply) {
                state.action(ibleDiscoverService);

                incIndex();

                BLEState nextstate = path.get(index());
                nextstate.setupAction();

                return;
            }

            throw new BLEStateException("doDiscoverService: " + state.getClass());
        }

        throw new BLEStateException("doDiscoverService: fails "
                + (path != null) + " "
                + (index() < path.size()) );
    }

    // HRM3200 Service UUID and characteristic UUID
    public static UUID serviceUuid = UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb");
    public static UUID characteristicUuid = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");

    //////////////////////////////////////////////////////////////////////////
    //  Notification (Change Characterisitcs)
    //    - doNotification
    //    - Classes for relevant states
    //////////////////////////////////////////////////////////////////////////

    @Override
    public void doNotification(IBLEChangeCharacteristic ibleChangeCharacteristic) {
        if (path != null && index() < path.size()) {
            BLEState state = path.get(index());

            if (state instanceof DeviceTimeReplyState
                    || state instanceof RealtimeDataReply
                    || state instanceof SendSessionInfo
                    || state instanceof SendSessionCount
                    || state instanceof InitializeData
                    || state instanceof StoredDataReply
                    || state instanceof SendEndOfSession)
            {
                state.action(ibleChangeCharacteristic);

                incIndex();

                BLEState nextstate = path.get(index());
                nextstate.setupAction();

                return;
            }

            throw new BLEStateException("doNotification: " + state.getClass());
        }

        throw new BLEStateException("doNotification: path fails "
                + (path != null) + " "
                + (index() < path.size()) );
    }

    //////////////////////////////////////////////////////////////////////////
    //  Write Characterisitcs  (Commands by Apps)
    //    - doWriteCharacteristic
    //    - Classes for relevant states
    //////////////////////////////////////////////////////////////////////////

    @Override
    public void doWriteCharacteristic(BluetoothGattCharacteristic btGattCharacteristic,
                                      IBLEChangeCharacteristic ibleChangeCharacteristic) {
        if (path != null && index() < path.size()) {
            BLEState state = path.get(index());

            if (state instanceof OK_0x11_State
                    || state instanceof AppTime_0x80_State
                    || state instanceof REQ_RealtimeData_0x82_0x01
                    || state instanceof REQ_DownloadStoredData_0x82_0x03
                    || state instanceof OK_0x15_State
                    || state instanceof REQ_SessionInfo
                    || state instanceof REQ_Disconnection_0x82_0x02_State)
            {
                state.action(btGattCharacteristic, ibleChangeCharacteristic);

                incIndex();

                BLEState nextstate = path.get(index());
                nextstate.setupAction();

                return;
            }

            throw new BLEStateException("doWriteCharacteristic: " + state.getClass().getCanonicalName());
        }

        throw new BLEStateException("doWriteCharacteristic: path fails "
                + (path != null) + " "
                + (index() < path.size()) );
    }

    //////////////////////////////////////////////////////////////////////////
    //  Disconnection
    //    - doDisconnect
    //    - Classes for relevant states
    //////////////////////////////////////////////////////////////////////////

    @Override
    public void doDisconnect(IBLEDisconnect ibleDisconnect) {
        if (path != null && index() < path.size()) {
            BLEState state = path.get(index());

            if (state instanceof BLEDisconnectState
                    || state instanceof DisconnectByApp)
            {
                // We arrive at the final state!!
                state.action(ibleDisconnect);

                // No incIndex();
                return;
            }

            throw new BLEStateException("doDisconnect: " + state.getClass().getCanonicalName());
        }

        throw new BLEStateException("doDisconnect: path fails "
                + (path != null) + " "
                + (index() < path.size()) );
    }

}
