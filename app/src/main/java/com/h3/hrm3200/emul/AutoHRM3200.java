package com.h3.hrm3200.emul;

import com.h3.hrm3200.emul.model.AppTime_0x80_0x81_State;
import com.h3.hrm3200.emul.model.CANCEL_DownloadStoredData_0x82_0x01_0x83;
import com.h3.hrm3200.emul.model.DeviceTimeReplyState;
import com.h3.hrm3200.emul.model.DisconnectByApp;
import com.h3.hrm3200.emul.model.InitializeData;
import com.h3.hrm3200.emul.model.OK_0x11_State;
import com.h3.hrm3200.emul.model.OK_0x15_State;
import com.h3.hrm3200.emul.model.REQ_Disconnection_0x82_0x02_State;
import com.h3.hrm3200.emul.model.REQ_DownloadStoredData_0x82_0x03_0x83;
import com.h3.hrm3200.emul.model.REQ_RealtimeData_0x82_0x01;
import com.h3.hrm3200.emul.model.REQ_SessionInfo_0x84_0x85;
import com.h3.hrm3200.emul.model.RealtimeDataReply;
import com.h3.hrm3200.emul.model.SendEndOfSession;
import com.h3.hrm3200.emul.model.SendSessionCount;
import com.h3.hrm3200.emul.model.SendSessionInfo;
import com.h3.hrm3200.emul.model.ServiceDiscoveryHRM3200;
import com.h3.hrm3200.emul.model.SharedSessionInfo;
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
import emul.bluetooth.model.util.BasicPathGen;
import emul.bluetooth.model.util.BasicPaths;
import emul.bluetooth.model.util.Path;
import emul.bluetooth.model.util.Vertex;
import emul.bluetooth.model.util.VertexBLEState;
import mocking.android.bluetooth.BLEService;
import mocking.android.bluetooth.BluetoothGatt;
import mocking.android.bluetooth.BluetoothGattCharacteristic;
import mocking.android.bluetooth.BluetoothProfile;
import mocking.android.bluetooth.IBLEChangeCharacteristic;
import mocking.android.bluetooth.IBLEDisconnect;
import mocking.android.bluetooth.IBLEDiscoverService;

/**
 * Created by khChoi on 2017-08-07.
 *
 * [요구사항]
 *  - 상태 다이어그램은 정상적인 상황만을 기술한다.
 *  - 앱에서 디바이스로 잘못된 명령어를 내리는 경우는 100% 버그이고 수정해야 함
 *  - 디바이스에서 앱으로 잘못된 명령어를 올리거나 특히 비정상 연결해제되면 앱은 적절히 대응해야 함
 */

public class AutoHRM3200 extends AutoBluetoothLE {
    ArrayList<BLEState> path;
    ArrayList<Vertex> vertices;
    int numberOfEdges;

    public AutoHRM3200() {
        if(false) {
            path = new ArrayList<BLEState>();

            new Scenario_BLEScan_Connect_Discovery_RealtimeData_DisconnectionByApp(this, path);
            // new Scenario_BLEScan_Connect_Discovery_RealtimeData_DisconnectionByDevice(this, path);

            // BUG : 디바이스에 저장된 데이터를 다운받지 않는 선택을 할 때 sessionData 객체를 초기화하지 않아 NullReferenceException 발생
            // new Scenario_BLEScan_Connect_Discovery_RealtimeDataByUI_DisconnectionByApp(this, path);

            // BUG : 디바이스에 저장된 데이터를 다운받지 않는 선택을 할 때 sessionData 객체를 초기화하지 않아 NullReferenceException 발생
            // new Scenario_BLEScan_Connect_Discovery_RealtimeDataByUI_DisconnectionByDevice(this, path);

            // BUG??? : 디바이스에 저장된 데이터를 모두 다운받은 다음 앱에서 Disconnect 명령을 내리면
            //          디바이스에서 Disconnect 완료 신호를 받지 않고 종료
            // new Scenario_BLEScan_Connect_Discovery_DownloadData_DisconnectionByDevice(this, path);

            // Initialize a path for testing
            this.setPath(path);
            this.setIndex(0);
        }
        else {
            _AutoHRM3200("");
        }
    }

    public void _AutoHRM3200(String str) {
        buildGraph();

        BasicPathGen.print(vertices);
        Vertex start = BasicPathGen.findInitialState(vertices);
        Path path = new Path();
        BasicPaths basicPaths = new BasicPaths();

        int bound = BasicPathGen.numOfEdges(vertices);

        BasicPathGen.DFS(start, path, bound, basicPaths );

        basicPaths.print();
    }

    private void buildGraph() {
        vertices = new ArrayList<Vertex>();

        // VERTEX:
        VertexBLEState vtx_BLEScan =
                new VertexBLEState("BLEScan", Vertex.INITIAL_STATE, new BLEScanState("00:11:22:AA:BB:CC", "HRM3200"));

        vertices.add(vtx_BLEScan);

        // VERTEX:
        VertexBLEState vtx_ConnectSuccess =
                new VertexBLEState("BLEConnect Success",
                        new BLEConnectState(BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_CONNECTED));
        VertexBLEState vtx_ConnectFail =
                new VertexBLEState("BLEConnect Failure", Vertex.FINAL_STATE,
                        new BLEConnectState(BluetoothGatt.GATT_FAILURE, BluetoothProfile.STATE_CONNECTED));

        vertices.add(vtx_ConnectSuccess);
        vertices.add(vtx_ConnectFail);

        // EDGE: vtx_BLEScan ---> vtx_Connect
        vtx_BLEScan.getAdjacencyList().add(vtx_ConnectSuccess);
        vtx_BLEScan.getAdjacencyList().add(vtx_ConnectFail);

        // TODO: vtx_ConnectFail is a final state?

        // Service Discovery
        //  1. GATT_SUCCESS,  HRM3200 Service List
        //  2. GATT_FAILURE,  Empty Service List
        //  3. and many more ...

        // VERTEX:
        VertexBLEState vtx_DiscoverServiceSuccess =
                new VertexBLEState("ServiceDiscovery Success",
                        new ServiceDiscoveryHRM3200(BluetoothGatt.GATT_SUCCESS,
                                ServiceDiscoveryHRM3200.bleServiceList()));

        vertices.add(vtx_DiscoverServiceSuccess);

        // EDGE: vtx_Connect ---> vtx_DiscoverService
        vtx_ConnectSuccess.getAdjacencyList().add(vtx_DiscoverServiceSuccess);

        // VERTEX :
        VertexBLEState vtx_DiscoverServiceFailure =
                new VertexBLEState("ServiceDiscovery Failure",
                        new ServiceDiscoveryHRM3200(BluetoothGatt.GATT_FAILURE,
                                new ArrayList<BLEService>()));

        vertices.add(vtx_DiscoverServiceFailure);

        // EDGE :  vtx_ConnectSuccess ---> vtx_DiscoverServiceFailure
        vtx_ConnectSuccess.getAdjacencyList().add(vtx_DiscoverServiceFailure);

        // VERTEX:
        VertexBLEState vtx_State0 = new VertexBLEState("State0", new DeviceTimeReplyState(this));

        vertices.add(vtx_State0);

        // EDGE: vtx_DiscoverService ---> vtx_State0
        vtx_DiscoverServiceSuccess.getAdjacencyList().add(vtx_State0);

        // VERTEX:
        VertexBLEState vtx_State1_0x11 = new VertexBLEState("State1 0x11", new OK_0x11_State(this));

        vertices.add(vtx_State1_0x11);

        // EDGE: vtx_State0 ---> vtx_State1_0x11
        vtx_State0.getAdjacencyList().add(vtx_State1_0x11);

        // VERTEX:
        // 1) 0x01, 0x01
        // 2) 0x00, 0x01
        // 3) 0x00, 0x00
        VertexBLEState vtx_State1_0x80_0x81_0x01_0x01 = new VertexBLEState("State1 0x80 0x01_0x01", new AppTime_0x80_0x81_State(this, 0x01, 0x01));
        VertexBLEState vtx_State1_0x80_0x81_0x00_0x01 = new VertexBLEState("State1 0x80 0x00_0x01", new AppTime_0x80_0x81_State(this, 0x00, 0x01));
        VertexBLEState vtx_State1_0x80_0x81_0x00_0x00 = new VertexBLEState("State1 0x80 0x00_0x00", new AppTime_0x80_0x81_State(this, 0x00, 0x00));

        vertices.add(vtx_State1_0x80_0x81_0x01_0x01);
        vertices.add(vtx_State1_0x80_0x81_0x00_0x01);
        vertices.add(vtx_State1_0x80_0x81_0x00_0x00);

        // EDGE: vtx_State1_0x11 ---> vtxState1_0x80_0x01_0x01   (Measure data in realtime)
        // EDGE: vtx_State1_0x11 ---> vtxState1_0x80_0x00_0x01   (
        // EDGE: vtx_State1_0x11 ---> vtxState1_0x80_0x00_0x00
        vtx_State1_0x11.getAdjacencyList().add(vtx_State1_0x80_0x81_0x01_0x01);
        vtx_State1_0x11.getAdjacencyList().add(vtx_State1_0x80_0x81_0x00_0x01);
        vtx_State1_0x11.getAdjacencyList().add(vtx_State1_0x80_0x81_0x00_0x00);

        // 1. Realtime Data Reply
        // TODO: SetNotification 명령어를 확인하는 상태 추가 필요

        // VERTEX:
        VertexBLEState vtx_State5_0x1A_1 = new VertexBLEState("State5 1", new RealtimeDataReply(this));
        VertexBLEState vtx_State5_0x1A_2 = new VertexBLEState("State5 2", new RealtimeDataReply(this));
        VertexBLEState vtx_State5_0x1A_3 = new VertexBLEState("State5 3", new RealtimeDataReply(this));
        VertexBLEState vtx_State5_0x1A_4 = new VertexBLEState("State5 4", new RealtimeDataReply(this));
        VertexBLEState vtx_State5_0x1A_5 = new VertexBLEState("State5 5", new RealtimeDataReply(this));

        vertices.add(vtx_State5_0x1A_1);
        vertices.add(vtx_State5_0x1A_2);
        vertices.add(vtx_State5_0x1A_3);
        vertices.add(vtx_State5_0x1A_4);
        vertices.add(vtx_State5_0x1A_5);

        // EDGE :
        vtx_State1_0x80_0x81_0x01_0x01.getAdjacencyList().add(vtx_State5_0x1A_1);  // 1st data after 0x81 0x01 0x01
        vtx_State1_0x80_0x81_0x00_0x01.getAdjacencyList().add(vtx_State5_0x1A_1);  // 1st data after 0x81 0x00 0x01

        vtx_State5_0x1A_1.getAdjacencyList().add(vtx_State5_0x1A_2);        // 2nd data
        vtx_State5_0x1A_2.getAdjacencyList().add(vtx_State5_0x1A_3);        // 3rd data
        vtx_State5_0x1A_3.getAdjacencyList().add(vtx_State5_0x1A_4);        // 4th data
        vtx_State5_0x1A_4.getAdjacencyList().add(vtx_State5_0x1A_5);        // 5th data

        // 2. Download Stored Data
        VertexBLEState vtx_State6_REQ_0x82_0x03_0x83 = new VertexBLEState("State6 REQ_0x82_0x03_0x83",
                new REQ_DownloadStoredData_0x82_0x03_0x83(this));

        vertices.add(vtx_State6_REQ_0x82_0x03_0x83);

        // EDGE : vtx_State1_0x80_0x81_0x00_0x00 ---> vtx_State6_REQ_0x82_0x03_0x83
        vtx_State1_0x80_0x81_0x00_0x00.getAdjacencyList().add(vtx_State6_REQ_0x82_0x03_0x83);

        // Session count
        final int session_count = 2;

        // VERTEX :
        VertexBLEState vtx_State7_SendSessionCount = new VertexBLEState("State7 Session Count",
                new SendSessionCount(this, session_count));

        vertices.add(vtx_State7_SendSessionCount);

        // EDGE : vtx_State6_REQ_0x82_0x03_0x83 ---> vtx_State7_SendSessionCount
        vtx_State6_REQ_0x82_0x03_0x83.getAdjacencyList().add(vtx_State7_SendSessionCount);

        SharedSessionInfo sharedSessionInfo = new SharedSessionInfo();

        // 1st Session //////////////////////////////////////////////////////////////////////////

        // VERTEX :
        VertexBLEState vtx_ReqSessionInfo_1 = new VertexBLEState("State8 Req Session Info 1",
                new REQ_SessionInfo_0x84_0x85(this, sharedSessionInfo));

        vertices.add(vtx_ReqSessionInfo_1);

        // EDGE : vtx_State7_SendSessionCount ---> vtx_ReqSessionInfo_1
        vtx_State7_SendSessionCount.getAdjacencyList().add(vtx_ReqSessionInfo_1);

        sharedSessionInfo.dataTotalCount = 10;

        // VERTEX
        VertexBLEState vtx_SendSessionInfo_1 = new VertexBLEState("State10 Send Session Info 1",
                new SendSessionInfo(this, sharedSessionInfo));

        vertices.add(vtx_SendSessionInfo_1);

        // EDGE : vtx_ReqSessionInfo_1 ---> vtx_SendSessionInfo_1
        vtx_ReqSessionInfo_1.getAdjacencyList().add(vtx_SendSessionInfo_1);

        // VERTEX :
        VertexBLEState vtx_OK_0x15_State_1 = new VertexBLEState("State11 OK 0x15 1",
                new OK_0x15_State(this));

        vertices.add(vtx_OK_0x15_State_1);

        // EDGE : vtx_SendSessionInfo_1 ---> vtx_OK_0x15_State_1
        vtx_SendSessionInfo_1.getAdjacencyList().add(vtx_OK_0x15_State_1);

        // VERTICES as many as dataTotalCount
        VertexBLEState vtx_StoredDataReply_1_1 = new VertexBLEState("State12 Stored Data Reply 1 1",
                new StoredDataReply(this, sharedSessionInfo));
        VertexBLEState vtx_StoredDataReply_1_2 = new VertexBLEState("State12 Stored Data Reply 1 2",
                new StoredDataReply(this, sharedSessionInfo));
        VertexBLEState vtx_StoredDataReply_1_3 = new VertexBLEState("State12 Stored Data Reply 1 3",
                new StoredDataReply(this, sharedSessionInfo));
        VertexBLEState vtx_StoredDataReply_1_4 = new VertexBLEState("State12 Stored Data Reply 1 4",
                new StoredDataReply(this, sharedSessionInfo));
        VertexBLEState vtx_StoredDataReply_1_5 = new VertexBLEState("State12 Stored Data Reply 1 5",
                new StoredDataReply(this, sharedSessionInfo));
        VertexBLEState vtx_StoredDataReply_1_6 = new VertexBLEState("State12 Stored Data Reply 1 6",
                new StoredDataReply(this, sharedSessionInfo));
        VertexBLEState vtx_StoredDataReply_1_7 = new VertexBLEState("State12 Stored Data Reply 1 7",
                new StoredDataReply(this, sharedSessionInfo));
        VertexBLEState vtx_StoredDataReply_1_8 = new VertexBLEState("State12 Stored Data Reply 1 8",
                new StoredDataReply(this, sharedSessionInfo));
        VertexBLEState vtx_StoredDataReply_1_9 = new VertexBLEState("State12 Stored Data Reply 1 9",
                new StoredDataReply(this, sharedSessionInfo));
        VertexBLEState vtx_StoredDataReply_1_10 = new VertexBLEState("State12 Stored Data Reply 1 10",
                new StoredDataReply(this, sharedSessionInfo));

        VertexBLEState vtx_SendEndofSession_1 = new VertexBLEState("Send End of Session 1",
                new SendEndOfSession(this, sharedSessionInfo));

        vertices.add(vtx_StoredDataReply_1_1);
        vertices.add(vtx_StoredDataReply_1_2);
        vertices.add(vtx_StoredDataReply_1_3);
        vertices.add(vtx_StoredDataReply_1_4);
        vertices.add(vtx_StoredDataReply_1_5);
        vertices.add(vtx_StoredDataReply_1_6);
        vertices.add(vtx_StoredDataReply_1_7);
        vertices.add(vtx_StoredDataReply_1_8);
        vertices.add(vtx_StoredDataReply_1_9);
        vertices.add(vtx_StoredDataReply_1_10);

        vertices.add(vtx_SendEndofSession_1);

        // EDGES :
        vtx_OK_0x15_State_1.getAdjacencyList().add(vtx_StoredDataReply_1_1);

        vtx_StoredDataReply_1_1.getAdjacencyList().add(vtx_StoredDataReply_1_2);
        vtx_StoredDataReply_1_2.getAdjacencyList().add(vtx_StoredDataReply_1_3);
        vtx_StoredDataReply_1_3.getAdjacencyList().add(vtx_StoredDataReply_1_4);
        vtx_StoredDataReply_1_4.getAdjacencyList().add(vtx_StoredDataReply_1_5);
        vtx_StoredDataReply_1_5.getAdjacencyList().add(vtx_StoredDataReply_1_6);
        vtx_StoredDataReply_1_6.getAdjacencyList().add(vtx_StoredDataReply_1_7);
        vtx_StoredDataReply_1_7.getAdjacencyList().add(vtx_StoredDataReply_1_8);
        vtx_StoredDataReply_1_8.getAdjacencyList().add(vtx_StoredDataReply_1_9);
        vtx_StoredDataReply_1_9.getAdjacencyList().add(vtx_StoredDataReply_1_10);

        vtx_StoredDataReply_1_10.getAdjacencyList().add(vtx_SendEndofSession_1);

        // 2nd Session //////////////////////////////////////////////////////////////////////////

        // VERTEX :
        VertexBLEState vtx_ReqSessionInfo_2 = new VertexBLEState("State8 Req Session Info 2",
                new REQ_SessionInfo_0x84_0x85(this, sharedSessionInfo));

        vertices.add(vtx_ReqSessionInfo_2);

        // EDGE : vtx_SendEndofSession_1 ---> vtx_ReqSessionInfo_2
        vtx_SendEndofSession_1.getAdjacencyList().add(vtx_ReqSessionInfo_2);

        sharedSessionInfo.dataTotalCount = 7;

        // VERTEX
        VertexBLEState vtx_SendSessionInfo_2 = new VertexBLEState("State10 Send Session Info 2",
                new SendSessionInfo(this, sharedSessionInfo));

        vertices.add(vtx_SendSessionInfo_2);

        // EDGE : vtx_ReqSessionInfo_2 ---> vtx_SendSessionInfo_2
        vtx_ReqSessionInfo_2.getAdjacencyList().add(vtx_SendSessionInfo_2);

        // VERTEX :
        VertexBLEState vtx_OK_0x15_State_2 = new VertexBLEState("State11 OK 0x15 2",
                new OK_0x15_State(this));

        vertices.add(vtx_OK_0x15_State_2);

        // EDGE : vtx_SendSessionInfo_2 ---> vtx_OK_0x15_State_2
        vtx_SendSessionInfo_2.getAdjacencyList().add(vtx_OK_0x15_State_2);

        // VERTICES as many as dataTotalCount
        VertexBLEState vtx_StoredDataReply_2_1 = new VertexBLEState("State12 Stored Data Reply 2 1",
                new StoredDataReply(this, sharedSessionInfo));
        VertexBLEState vtx_StoredDataReply_2_2 = new VertexBLEState("State12 Stored Data Reply 2 2",
                new StoredDataReply(this, sharedSessionInfo));
        VertexBLEState vtx_StoredDataReply_2_3 = new VertexBLEState("State12 Stored Data Reply 2 3",
                new StoredDataReply(this, sharedSessionInfo));
        VertexBLEState vtx_StoredDataReply_2_4 = new VertexBLEState("State12 Stored Data Reply 2 4",
                new StoredDataReply(this, sharedSessionInfo));
        VertexBLEState vtx_StoredDataReply_2_5 = new VertexBLEState("State12 Stored Data Reply 2 5",
                new StoredDataReply(this, sharedSessionInfo));
        VertexBLEState vtx_StoredDataReply_2_6 = new VertexBLEState("State12 Stored Data Reply 2 6",
                new StoredDataReply(this, sharedSessionInfo));
        VertexBLEState vtx_StoredDataReply_2_7 = new VertexBLEState("State12 Stored Data Reply 2 7",
                new StoredDataReply(this, sharedSessionInfo));

        VertexBLEState vtx_SendEndofSession_2 = new VertexBLEState("Send End of Session 2",
                new SendEndOfSession(this, sharedSessionInfo));

        vertices.add(vtx_StoredDataReply_2_1);
        vertices.add(vtx_StoredDataReply_2_2);
        vertices.add(vtx_StoredDataReply_2_3);
        vertices.add(vtx_StoredDataReply_2_4);
        vertices.add(vtx_StoredDataReply_2_5);
        vertices.add(vtx_StoredDataReply_2_6);
        vertices.add(vtx_StoredDataReply_2_7);

        vertices.add(vtx_SendEndofSession_2);

        // EDGES :
        vtx_OK_0x15_State_2.getAdjacencyList().add(vtx_StoredDataReply_2_1);

        vtx_StoredDataReply_2_1.getAdjacencyList().add(vtx_StoredDataReply_2_2);
        vtx_StoredDataReply_2_2.getAdjacencyList().add(vtx_StoredDataReply_2_3);
        vtx_StoredDataReply_2_3.getAdjacencyList().add(vtx_StoredDataReply_2_4);
        vtx_StoredDataReply_2_4.getAdjacencyList().add(vtx_StoredDataReply_2_5);
        vtx_StoredDataReply_2_5.getAdjacencyList().add(vtx_StoredDataReply_2_6);
        vtx_StoredDataReply_2_6.getAdjacencyList().add(vtx_StoredDataReply_2_7);

        vtx_StoredDataReply_2_7.getAdjacencyList().add(vtx_SendEndofSession_2);

        // End of All Sessions

        // 3. Cancel to download stored data and to continue to receive realtime data

        // VERTEX :
        VertexBLEState vtx_State4_CANCEL_0x82_0x01_0x83 = new VertexBLEState("State4 CANCEL_0x82_0x01_0x83",
                new CANCEL_DownloadStoredData_0x82_0x01_0x83(this));

        vertices.add(vtx_State4_CANCEL_0x82_0x01_0x83);

        // EDGE : vtx_State1_0x80_0x81_0x00_0x00 ---> vtx_State4_CANCEL_0x82_0x01_0x83
        vtx_State1_0x80_0x81_0x00_0x00.getAdjacencyList().add(vtx_State4_CANCEL_0x82_0x01_0x83);

        // EDGE : vtx_State4_CANCEL_0x82_0x01_0x83 ---> vtx_State5_0x1A
        vtx_State4_CANCEL_0x82_0x01_0x83.getAdjacencyList().add(vtx_State5_0x1A_1);

        // Disconnection
        //     by App
        VertexBLEState vtx_StateReqDisconnect_0x82_0x02 = new VertexBLEState("REQ_Disconnection 0x82 0x02",
                new REQ_Disconnection_0x82_0x02_State(this));
        VertexBLEState vtx_StateDisconnectByApp = new VertexBLEState("Disconnection by App", Vertex.FINAL_STATE,
                new DisconnectByApp(this, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_DISCONNECTED));

        vertices.add(vtx_StateReqDisconnect_0x82_0x02);
        vertices.add(vtx_StateDisconnectByApp);

        // EDGE : 1) Disconnection by App after measuring data in realtime
        //           vtx_State5_0x1A_5 ---> vtx_StateReqDisconnect_0x82_0x02
        //           vtx_StateReqDisconnect_0x82_0x02 ---> vtx_StateDisconnectByApp

        vtx_State5_0x1A_5.getAdjacencyList().add(vtx_StateReqDisconnect_0x82_0x02);
        vtx_StateReqDisconnect_0x82_0x02.getAdjacencyList().add(vtx_StateDisconnectByApp);

        //         2) Disconnection by App after failure in service discovery
        //           vtx_DiscoverServiceFailure ---> vtx_StateReqDisconnect_0x82_0x02

        vtx_DiscoverServiceFailure.getAdjacencyList().add(vtx_StateReqDisconnect_0x82_0x02);


        //     by Device
        VertexBLEState vtx_StateDisconnectByDevice = new VertexBLEState("Disconnection by Device", Vertex.FINAL_STATE,
                new BLEDisconnectState(this, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_DISCONNECTED));

        vertices.add(vtx_StateDisconnectByDevice);

        // EDGE : 1) Disconnection by Device after measuring data in realtime
        //            vtx_State5_0x1A_5 ---> vtx_StateDiconnectByDevice

        //        2) Discoonection by Device after downloading stored data
        //           vtx_SendEndofSession_2 ---> vtx_StateDisconnectByApp

        vtx_State5_0x1A_5.getAdjacencyList().add(vtx_StateDisconnectByDevice);

        vtx_SendEndofSession_2.getAdjacencyList().add(vtx_StateDisconnectByApp);


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
            if (state instanceof ServiceDiscoveryHRM3200) {
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
                    || state instanceof AppTime_0x80_0x81_State
                    || state instanceof REQ_RealtimeData_0x82_0x01
                    || state instanceof REQ_DownloadStoredData_0x82_0x03_0x83
                    || state instanceof CANCEL_DownloadStoredData_0x82_0x01_0x83
                    || state instanceof OK_0x15_State
                    || state instanceof REQ_SessionInfo_0x84_0x85
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
