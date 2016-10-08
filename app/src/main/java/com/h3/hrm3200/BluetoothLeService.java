package com.h3.hrm3200;

import android.app.Service;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothGatt;
//import android.bluetooth.BluetoothGattCallback;
//import android.bluetooth.BluetoothGattCharacteristic;
//import android.bluetooth.BluetoothGattDescriptor;
//import android.bluetooth.BluetoothGattService;
//import android.bluetooth.BluetoothManager;
//import android.bluetooth.BluetoothProfile;
import mocking.android.bluetooth.BluetoothManager;
import mocking.android.bluetooth.BluetoothAdapter;
import mocking.android.bluetooth.BluetoothDevice;
import mocking.android.bluetooth.BluetoothGatt;
import mocking.android.bluetooth.BluetoothGattCallback;
import mocking.android.bluetooth.BluetoothGattCharacteristic;
import mocking.android.bluetooth.BluetoothGattDescriptor;
import mocking.android.bluetooth.BluetoothGattService;
import mocking.android.bluetooth.BluetoothProfile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
//import android.util.Log;
import android.widget.Toast;

import com.h3.hrm3200.emul.HRM3200;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class BluetoothLeService extends Service {
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTING =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTING";
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";

    // 실시간 측정 데이타의 수신 종료(DB 저장 성공) 세션상세화면으로의 전환을 위해 추가
    public final static String ACTION_DATA_STORE_AND_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_DATA_STORE_AND_DISCONNECTED";

    public final static String ACTION_GATT_ABNORMAL_TERMINATION =
            "com.example.bluetooth.le.ACTION_GATT_ABNORMAL_TERMINATION";

    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    // UUID (Service & Characteristic)
    // Heart Rate
    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(GattAttributes.HEART_RATE_MEASUREMENT);
    public final static UUID UUID_HEART_RATE_SERVICE =
            UUID.fromString(GattAttributes.HEART_RATE_SERVICE);

    // Battery Level
    public final static UUID UUID_BATTERY_SERVICE = UUID
            .fromString(GattAttributes.BATTERY_SERVICE);
    public final static UUID UUID_BATTERY_LEVEL = UUID
            .fromString(GattAttributes.BATTERY_LEVEL);

    // Health (defined by H3 System)
    public final static UUID UUID_MHEALTH_MEASUREMENT_WRITE = UUID
            .fromString(GattAttributes.MHEALTH_MEASUREMENT_WRITE);
    public final static UUID UUID_MHEALTH_MEASUREMENT_READ = UUID
            .fromString(GattAttributes.MHEALTH_MEASUREMENT_READ);
    public final static UUID UUID_MHEALTH_SERVICE = UUID
            .fromString(GattAttributes.MHEALTH_SERVICE);

    private final static String TAG = "BluetoothLeService";

    // Menu : ble data 중 처리할 것 지정
    private boolean HEART_RATE_MENU = false; // standard Heart Rate 처리 안함
    private boolean BATTERY_MENU    = true;
    private boolean MHEALTH_MENU    = true;

    // For Health (defined by H3 System) Characteristics
    private BluetoothGattCharacteristic health_measurement_characteristic_write;
    private BluetoothGattCharacteristic health_measurement_characteristic_read;

    // For reading Battery Level Characteristics (after setting notification on it)
    private BluetoothGattCharacteristic battery_level_characteristic;

    public static final String ACTION_READ_BATTERY_LEVEL = "android.intent.action.READ_BATTERY_LEVEL";
    public static final String ACTION_UNBIND_SERVICE = "android.intent.action.UNBIND_SERVICE";

    public static final String ACTION_CONNECT = "android.intent.action.CONNECT";
    public static final String CONNECT_ADDRESS_KEY = "address";
//    public static final String STORED_DATA_REQUEST_KEY = "stored data request";

    public static final String ACTION_DISCONNECT = "android.intent.action.DISCONNECT";


    public static final String ACTION_REQUEST_TO_DOWNLOAD_STORED_DATA = "android.intent.action.DOWNLOAD_STORED_DATA";
    public static final String ACTION_REQUEST_REALTIME_DATA = "android.intent.action.REALTIME_DATA";


    // HRM3200 Protocol States
    private int HRM3200_state;

    private static final int HRM3200_STATE0 = 0; // 초기 상태 (CONNECTED)
    private static final int HRM3200_STATE1 = 1; // 장비시간(0x10) 수신
    private static final int HRM3200_STATE2 = 2; // OK(0x11)과 전용앱시간(0x80) 송신
    private static final int HRM3200_STATE3 = 3; // 저장데이터 유무/다운로드 가능 여부/(0x81) 수신
    private static final int HRM3200_STATE4 = 4; // 실시간데이터 요청(0x82-01) 송신
    private static final int HRM3200_STATE5 = 5; // 실시간데이터 요청 OK(0x83) 수신
    private static final int HRM3200_STATE6 = 6; // 저장데이터 요청(0x82-03) 송신
    private static final int HRM3200_STATE7 = 7; // 저장데이터 요청 OK(0x83) 수신
    private static final int HRM3200_STATE8 = 8; // 전체 세션 갯수 (0x12) 수신
    private static final int HRM3200_STATE9 = 9; // 세션 번호 지정 데이터 요청 (0x84) 송신
    private static final int HRM3200_STATE10 = 10; // 세션 데이터 요청 OK(0x85) 수신
    private static final int HRM3200_STATE11 = 11; // 세션 정보 (0x14) 수신
    private static final int HRM3200_STATE12 = 12; // OK9(0x15) 송신

    private static final int HRM3200_STATE100 = 100; // 종료 상태 (DISCONNECTED)

    private static final int HRM3200_STATE_FAIL = -1; // 프로토콜 순서와 다른 상태가 됨

    private static boolean DEBUG_HRM3200_STATE = false; // true: 현재 state 상관없음 (프로토콜 순서 체크 안함)

    private boolean check(int state) {
        return DEBUG_HRM3200_STATE || HRM3200_state==state;
    }

    private int HRM3200_state_prev = HRM3200_STATE100;

    private void set(int state) {
        HRM3200_state_prev = HRM3200_state;
        HRM3200_state = state;

        Log.i(TAG, "set: " + HRM3200_state + " " +
                (state==HRM3200_STATE100 ? (" <- " + HRM3200_state_prev) : ""));
    }
    ////////////////////////////////////////////////////////

    private Handler handler = new Handler();

    private SessionData sessionData;

    // STORED_DATA_REQUEST
    private boolean stored_data_request_flag = false; // false : 실시간 데이터 요청

    // MEASURING 상태를 prefName에 기록하는 것을 한번만 하기 위한 플래그
    boolean doOnceFlag = true;
    private int userWeight;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //2015.4.28
        SharedPreferences userinfoPref = getSharedPreferences(UserSettingActivity.prefName, 0);
        userWeight = userinfoPref.getInt(UserSettingActivity.keyWeight, 0);

        if (intent==null || intent.getAction() == null) {
            return super.onStartCommand(intent, flags, startId);
        }

        Log.i(TAG, "onStartCommand: " + intent.getAction());

        if (ACTION_CONNECT.equals(intent.getAction())) {

            // Connect only when a disconnected state!
            if (mConnectionState==STATE_DISCONNECTED) {

                String address = intent.getStringExtra(CONNECT_ADDRESS_KEY);
//                stored_data_request_flag = intent.getBooleanExtra(STORED_DATA_REQUEST_KEY, false);

                if (address != null) {
                    boolean status = connect(address);
                    if (status == false) {
                        Log.i(TAG, "Connection Failed: ");

                        if (mConnectionState==STATE_CONNECTING) {
                            Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
                        }
                        mConnectionState = STATE_DISCONNECTED;
                        broadcastUpdate(ACTION_GATT_ABNORMAL_TERMINATION);

                        setConnectionFlagInPreference(StartButtonView.NO_DEVICE);
                        setServiceDiscoveredFlagInPreference(false);
                    }
                }

            }
            else {
                Toast.makeText(this, "There exists a device connected or connecting!", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "There exists a device connected!");
            }

        }
        else if (ACTION_DISCONNECT.equals(intent.getAction())) {
//            int _mConnectionState = mConnectionState;

            // 2015.04.15 측정종료 요청 추가 0x82 (02)
            requestStopMeasuring();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    disconnect();
                }
            }, 1000);


        }
        else if (ACTION_READ_BATTERY_LEVEL.equals(intent.getAction())) {

            // Request READ_BATTERY_LEVEL only when a connected state!
            if (mConnectionState==STATE_CONNECTED) {

                if (battery_level_characteristic != null)
                    readCharacteristic(battery_level_characteristic);
            }
            else {
//                Toast.makeText(this, "No connection available on this Battery Read Request!", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "No connection available on this Battery Read Request!");

            }
        }
        else if (ACTION_REQUEST_REALTIME_DATA.equals(intent.getAction())) {
            requestRealtime();
        }
        else if(ACTION_REQUEST_TO_DOWNLOAD_STORED_DATA.equals(intent.getAction())) {
            requestStoredData();
        }
        else  {
            //
        }

        return super.onStartCommand(intent, flags, startId);
    }

    // 2015.04.15 측정종료 요청 추가 0x82 (02)
    void requestStopMeasuring() {
        byte[] response = new byte[5];

        response[0] = (byte) 0x80; // STX
        response[1] = (byte) 0x82; // CMD
        response[2] = (byte) 0x01; // Length
        response[3] = (byte) 0x02; // Stop Measuring
        response[4] = (byte) 0xEF; // EOF

        if (health_measurement_characteristic_write != null) {
            boolean result;

            health_measurement_characteristic_write.setValue(response);

            result = mBluetoothGatt.writeCharacteristic(health_measurement_characteristic_write);
            Log.i(TAG, "requestStopMeasuring - writeCharacteristic is called... : " + result);
            printBytes(response);

        } else {
            Log.i(TAG, "health_measurement_characteristic_write is null");
        }

    }


    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "onCreate()");

        initialize();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "onDestroy()");

        disconnect();
        close();
    }

    public BluetoothLeService() {
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind is called");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind is called");
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            // for Mocking
            //mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothManager = new BluetoothManager(new HRM3200());
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                setConnectionFlagInPreference(StartButtonView.CONNECTING);
                broadcastUpdate(ACTION_GATT_CONNECTING);
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        setConnectionFlagInPreference(StartButtonView.CONNECTING);
        broadcastUpdate(ACTION_GATT_CONNECTING);
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
//        mConnectionState = STATE_DISCONNECTED;

        mBluetoothGatt.disconnect();
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;

        mConnectionState = STATE_DISCONNECTED;
        setConnectionFlagInPreference(StartButtonView.NO_DEVICE);
    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (status != BluetoothGatt.GATT_SUCCESS) { //TODO: ??
                Log.i(TAG, "onConnectionStateChange: " + status);
            }

            String intentAction;
            if (status==BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED && mBluetoothGatt != null) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);

                Log.i(TAG, "Connected to GATT server: " + status);
                // Attempts to discover services after successful connection.
                boolean result = mBluetoothGatt.discoverServices();
                Log.i(TAG, "Attempting to start service discovery:" + result);

                setConnectionFlagInPreference(StartButtonView.CONNECTED);
                setServiceDiscoveredFlagInPreference(false);
                addDeviceInPreference(gatt.getDevice().getAddress(), gatt.getDevice().getName());

                // usesr info pref
//                SharedPreferences userinfoPref = getSharedPreferences(UserSettingActivity.prefName, 0);
//                int userStride = userinfoPref.getInt(UserSettingActivity.keyStride, 0);

//                sessionData = new SessionData(userStride);  // 세션데이타 초기화 0x1C(00)수신후로 이동


                HRM3200_state = HRM3200_STATE0; // 초기 상태

                doOnceFlag = true;

            } else if (status==BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server: " + status);

                if (mConnectionState==STATE_CONNECTING) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                                Toast.makeText(BluetoothLeService.this, "Connection failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                int sessionid = -1;
                Log.i(TAG, "post Writing to DB");
                if (sessionData != null) // 데이터 받기 시작하기 전에(0x1c 전에) disconnect되면 sessionData==null!
                {
                    if (sessionData.writeToDB(BluetoothLeService.this.getApplicationContext())) {  // DB 저장 성공
                        sessionid = sessionData.getSessionId();
                    }
                }

//                if (mConnectionState==STATE_CONNECTED && stored_data_request_flag==false) {
//                    Log.i(TAG, "post Writing to DB");
//                    final SessionData _sessionData = sessionData;
//                    handler.post(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            if (_sessionData != null) {
//                                Log.i(TAG, "call writeToDB");
//                                _sessionData.writeToDB(BluetoothLeService.this.getApplicationContext());
//                            }
//                        }
//                    });
//                }

//                sessionData = null;

                mConnectionState = STATE_DISCONNECTED;

                // TODO: 연결 해제 후 연결이 다시 되어 discoverServices()를 호출하는 경우가 있어서 null로 바꾸지 않음
//                mBluetoothGatt = null;
                mBluetoothDeviceAddress = null;

                intentAction = ACTION_GATT_DISCONNECTED;
                if ( (sessionid != -1) && (stored_data_request_flag == false) )
                    intentAction = ACTION_DATA_STORE_AND_DISCONNECTED;  // 실시간 데이타인 경우 Detail 화면으로 전환하기 위해

                broadcastUpdate(intentAction, sessionid);
//                broadcastUpdate(intentAction);

                setConnectionFlagInPreference(StartButtonView.NO_DEVICE);
                setServiceDiscoveredFlagInPreference(false);

                HRM3200_state = HRM3200_STATE100; // 종료 상태

                doOnceFlag = true; // Do we need this?
                stored_data_request_flag = false;

                close();
            }
            else {
                Log.i(TAG, "onConnectionStateChange:  status: " + status + " newState: " + newState);
                if (mConnectionState != STATE_DISCONNECTED) {

                    mConnectionState = STATE_DISCONNECTED;
                    mBluetoothDeviceAddress = null;

                    intentAction = ACTION_GATT_DISCONNECTED;
                    broadcastUpdate(intentAction);

                    setConnectionFlagInPreference(StartButtonView.NO_DEVICE);
                    setServiceDiscoveredFlagInPreference(false);

                    HRM3200_state = HRM3200_STATE100; // 종료 상태

                    doOnceFlag = true; // Do we need this?
                    stored_data_request_flag = false;

                    close();
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "onServicesDiscovered received: " + status);

                List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();
                for(BluetoothGattService service : gattServices) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();

                    if (HEART_RATE_MENU && UUID_HEART_RATE_SERVICE.equals(service.getUuid())) {
//                        Log.i(TAG, UUID_HEART_RATE_SERVICE.toString() + " == " + service.getUuid().toString());

                        for (BluetoothGattCharacteristic characteristic : characteristics) {

//                            Log.i(TAG, UUID_HEART_RATE_MEASUREMENT.toString() + " == " + characteristic.getUuid().toString());

                            if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {

                                setCharacteristicNotification(characteristic, true);

                            }
                        }
                    }

                    if (BATTERY_MENU && UUID_BATTERY_SERVICE.equals(service.getUuid())) {
//                        Log.i(TAG, UUID_BATTERY_SERVICE.toString() + " == " + service.getUuid().toString());

                        for (BluetoothGattCharacteristic characteristic : characteristics) {
//                            Log.i(TAG, UUID_BATTERY_LEVEL.toString() + " == " + characteristic.getUuid().toString());

                            if (UUID_BATTERY_LEVEL.equals(characteristic.getUuid())) {

                                Log.i(TAG, "BATTERY_LEVEL (Properties): " + Integer.toHexString(characteristic.getProperties()));
                                setCharacteristicNotification(characteristic, true);

                                battery_level_characteristic = characteristic;

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (health_measurement_characteristic_read != null)
                                            setCharacteristicNotification(health_measurement_characteristic_read, true);
                                    }
                                }, 1000);
                                // Notification 순서: battery level 먼저한후, heart_rate(H3)처리

//                                readCharacteristic(characteristic);

                            }
                        }
                    }

                    if (MHEALTH_MENU && UUID_MHEALTH_SERVICE.equals(service.getUuid())) {
                        // 0xff00
//                        Log.i(TAG, UUID_MHEALTH_SERVICE.toString() + " == " + service.getUuid().toString());

                        for (BluetoothGattCharacteristic characteristic : characteristics) {
                            // 0xff01
//                            Log.i(TAG, UUID_MHEALTH_MEASUREMENT_READ.toString() + " == " + characteristic.getUuid().toString());

                            if (UUID_MHEALTH_MEASUREMENT_READ.equals(characteristic.getUuid())) {

                                Log.i(TAG, "MHEALTH_MEASUREMENT_READ (Properties): " + Integer.toHexString(characteristic.getProperties()));
//                                setCharacteristicNotification(characteristic, true);
                                health_measurement_characteristic_read = characteristic;

                            }

                            // 0xff02
//                            Log.i(TAG, UUID_MHEALTH_MEASUREMENT_WRITE.toString() + " == " + characteristic.getUuid().toString());

                            if (UUID_MHEALTH_MEASUREMENT_WRITE.equals(characteristic.getUuid())) {

                                Log.i(TAG, "MHEALTH_MEASUREMENT_WRITE (Properties): " + Integer.toHexString(characteristic.getProperties()));
                                //setCharacteristicNotification(characteristic, true);

                                health_measurement_characteristic_write = characteristic;
                            }
                        }
                    }


                }


                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);

                setServiceDiscoveredFlagInPreference(true);

            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.i(TAG, "onCharacteristicRead is called...");

            printBytes(characteristic.getValue());

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (UUID_BATTERY_LEVEL.equals(characteristic.getUuid())) {
                    byte[] data = characteristic.getValue();
                    if (data != null && data.length > 0) {
                        Intent intent = new Intent();
                        intent.setAction(MainActivity.ACTION_DATA_BATTERY_LEVEL_AVAILABLE);
                        intent.putExtra(MainActivity.BATTERY_LEVEL_KEY, (data[0] & 0xff));
                        sendBroadcast(intent);
                        Log.i(TAG, "Characteristic Read (Battery Level): ");
                    }
                }
                else {
                    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                    Log.i(TAG, "Characteristic Read: ");
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
//            Log.i(TAG, "onCharacteristicChanged is called... " + characteristic.getUuid());
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);


            printBytes(characteristic.getValue());

            if (UUID_MHEALTH_MEASUREMENT_READ.equals(characteristic.getUuid())) {
                Log.i(TAG, "UUID_MHEALTH_MEASUREMENT_READ");

                // If HRM sends a message with 0x10, send back ACK with 0x11 and OK (00) or Error (01)
                byte[] data = characteristic.getValue();

                if ((data != null && data.length>1) == false) {
                    Log.i(TAG, "data == null || data.length<=1");
                }

                // HRM3200 => 0x10
                else if (check(HRM3200_STATE0) && (data[1] & 0xff) == 0x10) {
                    Log.i(TAG, "ACK with 0x10");

                    //Toast.makeText(BluetoothLeService.this, "H == Msg: 장비 시간 전송 - 0x10 ==> S", Toast.LENGTH_SHORT).show();

                    // 0x11 => HRM3200
                    // 0x80 => HRM3200
                    synchorizeTime();
                }
                // HRM3200 => 0x81
                else if (check(HRM3200_STATE2) && (data[1] & 0xff) ==0x81) {
                    Log.i(TAG, "ACK with 0x81");

                    // 0x82 (실시간데이터 요청 또는 저장된 데이터 요청) => HRM3200
                    requestRealtimeOrStoredData(data);
                }

                // HRM3200 => 0x83 (실시간 데이터  ACK OK/Error)
                else if (check(HRM3200_STATE4) && (data[1] & 0xff) ==0x83) {
                    // Toast.makeText(BluetoothLeService.this, "H == Msg: 실시간 데이터 요청 ACK - 0x83 ==> S", Toast.LENGTH_SHORT).show();

                    Log.i(TAG, "ACK with 0x83");

                    if (data.length == 5) {
                        if ((data[3] & 0xff) == 0x0) { // ACK with OK
                            set(HRM3200_STATE5);
                        } else { // ACK with Error
                            set(HRM3200_STATE100);
                            disconnect();
                        }
                    } else {
                        Log.i(TAG, "ACK with 0x83: data.length != 5");
                    }
                }

                // HRM3200 => 0x83 (저장된 데이터 상관없이 ACK OK/Error)
                else if (check(HRM3200_STATE6) && (data[1] & 0xff) ==0x83) {
                    // Toast.makeText(BluetoothLeService.this, "H == Msg: 실시간 데이터 요청 ACK - 0x83 ==> S", Toast.LENGTH_SHORT).show();

                    Log.i(TAG, "ACK with 0x83");

                    if (data.length == 5) {
                        if ((data[3] & 0xff) == 0x0) { // ACK with OK
                            set(HRM3200_STATE7);
                        } else { // ACK with Error
                            set(HRM3200_STATE100);
                            disconnect();
                        }
                    } else {
                        Log.i(TAG, "ACK with 0x83: data.length != 5");
                    }
                }

                // HRM3200 => 0x1A  (실시간 데이터 도착)
                else if (check(HRM3200_STATE5) && (data[1] & 0xff) ==0x1A) {

                    Log.i(TAG, "Health Data with 0x1A");

                    realtimeDataArrival(data);

                    if (doOnceFlag) {
                        setConnectionFlagInPreference(StartButtonView.MEASURING);
                        doOnceFlag = false;
                    }
                }

                // HRM3200 => 0x12
                else if (check(HRM3200_STATE7) && (data[1] & 0xff) ==0x12) {

                    Log.i(TAG, "# of Session Data with 0x12");

//                    session_count = 0;
                    if (data.length == 5) {

                        session_count = data[3] & 0xff;
                        session_index = 1;

                        requestSessionInfo(session_index);

                        Log.i(TAG, "Session Count : " + session_count);
                        Log.i(TAG, "Session Index : " + session_index);
                    }
                    else {
                        Log.i(TAG, "0x12: data.length != 5");
                    }

                }

                // HRM3200 => 0x85
                else if (check(HRM3200_STATE9) && (data[1] & 0xff) == 0x85) {
                    Log.i(TAG, "ACK with 0x85");

                    if (data.length == 5) {
                        if ((data[3] & 0xff) == 0x0) { // ACK with OK
                            set(HRM3200_STATE10);
                        } else { // ACK with Error
                            set(HRM3200_STATE100);
                            disconnect();
                        }
                    } else {
                        Log.i(TAG, "ACK with 0x85: data.length != 5");
                    }
                }

                // HRM3200 => 0x14
                // Ack with 0x15 => HRM3200
                else if (check(HRM3200_STATE10) && (data[1] & 0xff) == 0x14) {
                    Log.i(TAG, "Session Data with 0x14");

                    requestSessionData(data);
                }

                // HRM3200 => 0x16 with data
                else if (check(HRM3200_STATE12) && (data[1] & 0xff) == 0x16) {
//                    Log.i(TAG, "Stored Data with 0x16");

//                    if (data.length == 13) {
//                        printBytes(data);
//                        Log.i(TAG, "Session Index: " + (data[3] & 0xff)  );
//                        Log.i(TAG, "Data Index: " + ( ((data[4] & 0xff) * 256) + (data[5] & 0xff) ) );
//                    }
                    sessionDataArrival(data);

                }

                // HRM3200 => 0x18 (개별 세션 데이터 전송 완료)
                else if (check(HRM3200_STATE12) && (data[1] & 0xff) ==0x18) {

                    Log.i(TAG, "Stored Data with 0x18");
                    session_index = session_index + 1;

                    endOfSessionDataArrival(data);
//                    Log.i(TAG, "Session Index : " + session_index);
                }

                // 2015.04.15
                // TODO: state check(프로토콜 순서 확인) 필요
                // HRM3200 => 0x1C (측정기의 측정시작/종료 알림)
                else if ((data[1] & 0xff) ==0x1C) {

                    Log.i(TAG, "Message with 0x1C");
                    responseMeasuringState(data);
                }

                // Just for Testing ...
                else if ((data[1] & 0xff) ==0x40) {

                    Log.i(TAG, "Health Data with 0x40");
                    realtimeDataArrival(data);
                }
                else {

                }

            }


            if (UUID_BATTERY_LEVEL.equals(characteristic.getUuid())) {
                byte[] data = characteristic.getValue();
                if (data != null && data.length > 0) {
                    Intent intent = new Intent();
                    intent.setAction(MainActivity.ACTION_DATA_BATTERY_LEVEL_AVAILABLE);
                    intent.putExtra(MainActivity.BATTERY_LEVEL_KEY, (data[0] & 0xff));
                    sendBroadcast(intent);
                    Log.i(TAG, "Characteristic Changed (Battery Level): ");
                }
            }

        }
    };

    private void synchorizeTime() {

        set(HRM3200_STATE1);


        if (health_measurement_characteristic_write != null) {
            // Sending with 0x11
            byte[] response = new byte[5];
            response[0] = (byte)0x80; // STX
            response[1] = (byte)0x11; // CMD
            response[2] = (byte)0x01; // Length
            response[3] = (byte)0x00; // OK
            response[4] = (byte)0xEF; // EOF

            Calendar now = Calendar.getInstance();

            health_measurement_characteristic_write.setValue(response);


            boolean result;
            result = mBluetoothGatt.writeCharacteristic(health_measurement_characteristic_write);
            Log.i(TAG, "writeCharacteristic is called... : " + result);
            printBytes(response);

//            if (result == false) {
//                result = mBluetoothGatt.writeCharacteristic(health_measurement_characteristic_write);
//                Log.i(TAG, "writeCharacteristic is called... : " + result);
//            }



            // Sending with 0x80

            class MyRunnable implements Runnable {
                boolean result;
                byte[] responseTime;

                public MyRunnable(boolean result, byte[] responseTime) {
                    this.result = result;
                    this.responseTime = responseTime;
                }

                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        health_measurement_characteristic_write.setValue(responseTime);
                        result = mBluetoothGatt.writeCharacteristic(health_measurement_characteristic_write);
                        Log.i(TAG, "writeCharacteristic is called... : " + result);


                        printBytes(responseTime);

//                    if (result == false) {
//                        result = mBluetoothGatt.writeCharacteristic(health_measurement_characteristic_write);
//                        Log.i(TAG, "writeCharacteristic is called... : " + result);
//                    }

                        if (result) {
                            set(HRM3200_STATE2);
                        } else {
                            set(HRM3200_STATE100);
                            disconnect();
                        }
                    }
                    catch (Throwable t) {
                        set(HRM3200_STATE100);
                        disconnect();
                    }

                }
            }

            if (result) { // 앞에서 writeCharacterisitc을 성공한 경우에 그 다음으로 진행
                SharedPreferences pref = getSharedPreferences(UserSettingActivity.prefName, 0);
                int period = pref.getInt(UserSettingActivity.keyPeriod, 10);
                int overwrite;
                if (pref.getBoolean(UserSettingActivity.keyOverwrite, true))
                    overwrite = 0x00;
                else
                    overwrite = 0x01;



                byte[] responseTime = new byte[12];
                responseTime[0] = (byte) 0x80; // STX
                responseTime[1] = (byte) 0x80; // CMD
                responseTime[2] = (byte) 0x08; // Length
                responseTime[3] = (byte) (now.get(Calendar.YEAR) - 2000); // 2015
                responseTime[4] = (byte) (now.get(Calendar.MONTH) + 1); // 3 + 1
                responseTime[5] = (byte) now.get(Calendar.DAY_OF_MONTH);
                responseTime[6] = (byte) now.get(Calendar.HOUR_OF_DAY);
                responseTime[7] = (byte) now.get(Calendar.MINUTE);
                responseTime[8] = (byte) now.get(Calendar.SECOND);
                responseTime[9] = (byte) period;  // period
                responseTime[10] = (byte) overwrite;  // Overwrite
                responseTime[11] = (byte) 0xEF;  // EOF

                Thread thread = new Thread(new MyRunnable(result, responseTime));
                thread.start();
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//            }
            }
            else {
                set(HRM3200_STATE100);
                disconnect();
            }
        }
        else {
            Log.i(TAG, "health_measurement_characteristic_write is null");
        }
    }


    void requestRealtimeOrStoredData(byte[] data) {
        set(HRM3200_STATE3);

        if (data != null && data.length==6) {

            // realtime data 수신만 가능 2015.4.27
            if ( (data[4]&0xff) == 0x1 ) {
                set(HRM3200_STATE5);
                // usesr info pref
                SharedPreferences userinfoPref = getSharedPreferences(UserSettingActivity.prefName, 0);
                int userStride = userinfoPref.getInt(UserSettingActivity.keyStride, 0);

//                실시간데이타는 저장주기 period 1초
                sessionData = new SessionData(userStride, 1, true); // 실시간 데이터의 경우 count값을 계산해야 함

                setConnectionFlagInPreference(StartButtonView.WAITING);
            }

            else if ( (data[3]&0xff)==0x0 && (data[4]&0xff)==0x0 ) {
              // 실시간 데이터를 요청한 상황에서 Download할 데이터가 있는 상황, 다운로드 할지 여부를 물어보는 UI
//                if (stored_data_request_flag == true ) {
                    Intent intent = new Intent();
                    intent.setAction(MainActivity.ACTION_STORED_DATA_AVAILABLE);
                    sendBroadcast(intent);

//                } else {

//                }
            } else {
                // Real-time data request
                requestRealtime();
            }
        }
        else {
            Log.i(TAG, "requestData: data != 6");
        }

    }

    void requestRealtime() {
        byte[] response = new byte[5];

        response[0] = (byte) 0x80; // STX
        response[1] = (byte) 0x82; // CMD
        response[2] = (byte) 0x01; // Length
        response[3] = (byte) 0x01; // Real-time data request
        response[4] = (byte) 0xEF; // EOF

        if (health_measurement_characteristic_write != null) {
            boolean result;

            health_measurement_characteristic_write.setValue(response);

            result = mBluetoothGatt.writeCharacteristic(health_measurement_characteristic_write);
            Log.i(TAG, "requestRealtime: writeCharacteristic is called... : " + result);
            printBytes(response);

            if (result) {
                set(HRM3200_STATE4);
            } else {
                set(HRM3200_STATE100);
                disconnect();
            }

        } else {
            Log.i(TAG, "health_measurement_characteristic_write is null");
        }

        stored_data_request_flag = false;
    }

    void requestStoredData() {
        byte[] response = new byte[5];

        response[0] = (byte) 0x80; // STX
        response[1] = (byte) 0x82; // CMD
        response[2] = (byte) 0x01; // Length
        response[3] = (byte) 0x03; // Stored data request
        response[4] = (byte) 0xEF; // EOF

        if (health_measurement_characteristic_write != null) {
            boolean result;

            health_measurement_characteristic_write.setValue(response);

            result = mBluetoothGatt.writeCharacteristic(health_measurement_characteristic_write);
            Log.i(TAG, "writeCharacteristic is called... : " + result);
            printBytes(response);

            if (result) {
                set(HRM3200_STATE6);
            } else {
                set(HRM3200_STATE100);
                disconnect();
            }

        } else {
            Log.i(TAG, "health_measurement_characteristic_write is null");
        }

        stored_data_request_flag = true;
    }

    void realtimeDataArrival(byte[] data) {
        if (data.length == 11) {

            int hr_status = data[3] & 0xff;
            int heart_rate = data[4] & 0xff;
            int act_level = data[5] & 0xff;
            int calorie = ( (data[6] & 0xff) * 256 ) + (data[7] & 0xff);
            calorie = (calorie * userWeight) / 100;

            int steps = ( (data[8] & 0xff) * 256) + (data[9] & 0xff);

            sessionData.putData(this, heart_rate, act_level, calorie, steps, hr_status, -1);

            int duration = sessionData.getIntDuration();

            Intent intent = new Intent();
            intent.setAction(MainActivity.ACTION_DATA_HEALTH_MEASUREMENT_AVAILABLE);
            intent.putExtra(MainActivity.HEALTH_MEASUREMENT_KEY, heart_rate);
            intent.putExtra(MainActivity.HEALTH_MEASUREMENT_HR_STATUS_KEY, hr_status);
            intent.putExtra(MainActivity.HEALTH_MEASUREMENT_ACT_LEVEL_KEY, act_level);
            intent.putExtra(MainActivity.HEALTH_MEASUREMENT_ENERGY_KEY, calorie);
            intent.putExtra(MainActivity.HEALTH_MEASUREMENT_STEPS_KEY, steps);
            intent.putExtra(MainActivity.HEALTH_MEASUREMENT_DURATION_KEY, duration);

            sendBroadcast(intent);
        }
        else {
            // TODO:
        }
    }

    ///// 저장된 데이터 가져오기 위한 상태

    private int session_count;
    private int session_index;
    private int number_of_data_per_session;

    void  requestSessionInfo(int no) {
        set(HRM3200_STATE8);

        byte[] response = new byte[5];

        response[0] = (byte) 0x80; // STX
        response[1] = (byte) 0x84; // CMD
        response[2] = (byte) 0x01; // Length
        response[3] = (byte) no; // 전송 요청 세션 번호
        response[4] = (byte) 0xEF; // EOF

        if (health_measurement_characteristic_write != null) {
            boolean result;

            health_measurement_characteristic_write.setValue(response);

            result = mBluetoothGatt.writeCharacteristic(health_measurement_characteristic_write);
            Log.i(TAG, "0x84 writeCharacteristic is called... : " + result);
            printBytes(response);

            if (result) {
                set(HRM3200_STATE9);

                Intent intent = new Intent();
                intent.setAction(MainActivity.ACTION_STORED_DATA_PROGRESS);
                intent.putExtra(MainActivity.SESSION_COUNT_KEY, session_count);
                intent.putExtra(MainActivity.SESSION_INDEX_KEY, no);
                sendBroadcast(intent);
            } else {
                set(HRM3200_STATE100);
                disconnect();
            }

        } else {
            Log.i(TAG, "health_measurement_characteristic_write is null");
        }
    }

    void requestSessionData(byte[] data) {
        set(HRM3200_STATE11);

//        number_of_data_per_session = 0;

        if (data.length == 15) {
            number_of_data_per_session = ((data[4] & 0xff) * 256) + (data[5] & 0xff);
            Log.i(TAG, "Session Index: " + (data[3] & 0xff));
            Log.i(TAG, "# of data per session: " + number_of_data_per_session);

            int period = data[6] & 0xff; // 저장 주기
            int year   = (data[7] & 0xff) + 2000;
            int month  = data[8] & 0xff;
            int day    = data[9] & 0xff;
            int hour   = data[10] & 0xff;
            int min    = data[11] & 0xff;
            int sec    = data[12] & 0xff;
            int time_state = data[13] & 0xff;

            // usesr info pref
            SharedPreferences userinfoPref = getSharedPreferences(UserSettingActivity.prefName, 0);
            int userStride = userinfoPref.getInt(UserSettingActivity.keyStride, 0);
//            int store_period = userinfoPref.getInt(UserSettingActivity.keyPeriod, 10);

            sessionData = new SessionData(userStride, period, number_of_data_per_session); // number_of_data_per_session

            sessionData.setYear(year);
            sessionData.setMonth(month);
            sessionData.setDay(day);
            sessionData.setHour(hour);
            sessionData.setMin(min);
            sessionData.setSec(sec);


            // Sending Ack
            byte[] response = new byte[5];

            response[0] = (byte) 0x80; // STX
            response[1] = (byte) 0x15; // CMD
            response[2] = (byte) 0x01; // Length
            response[3] = (byte) 0x00; // ACK OK
            response[4] = (byte) 0xEF; // EOF

            if (health_measurement_characteristic_write != null) {
                boolean result;

                health_measurement_characteristic_write.setValue(response);

                result = mBluetoothGatt.writeCharacteristic(health_measurement_characteristic_write);
                Log.i(TAG, "0x15 writeCharacteristic is called... : " + result);
                printBytes(response);


                if (result) {
                    set(HRM3200_STATE12);
                } else {
                    set(HRM3200_STATE100);
                    disconnect();
                }
            } else {
                Log.i(TAG, "health_measurement_characteristic_write is null");
            }
        }
        else {
            // TODO:
        }
    }

    void sessionDataArrival(byte[] data) {
        if (data.length ==13) {

            int session_number = data[3] & 0xff;
            int index = ((data[4] & 0xff) * 256) + (data[5] & 0xff);

            int heart_rate = data[6] & 0xff;
            int act_level = data[7] & 0xff;
            int calorie = ((data[8] & 0xff) * 256) + (data[9] & 0xff);
            calorie = (calorie * userWeight) / 100;

            int steps = ((data[10] & 0xff) * 256) + (data[11] & 0xff);

            sessionData.putData(this, heart_rate, act_level, calorie, steps, 0, index);

            Intent intent = new Intent();
            intent.setAction(MainActivity.ACTION_STORED_DATA_PROGRESS);
            intent.putExtra(MainActivity.SESSION_COUNT_KEY, session_count);
            intent.putExtra(MainActivity.SESSION_INDEX_KEY, session_number);
            intent.putExtra(MainActivity.DATA_COUNT_KEY, number_of_data_per_session);
            intent.putExtra(MainActivity.DATA_INDEX_KEY, index);

            sendBroadcast(intent);
        }
        else {
            // TODO:
        }
    }

    void endOfSessionDataArrival(byte[] data) {
        if (data.length == 5) {
            int session_number = data[3] & 0xff;

            Log.i(TAG, "Session Index: " + session_number);

            sessionData.writeToDB(getApplicationContext());

//            // FOR UI
//            Intent intent = new Intent();
//            intent.setAction(MainActivity.ACTION_STORED_DATA_AVAILABLE);
//            intent.putExtra(MainActivity.SESSION_COUNT_KEY, session_count);
//            intent.putExtra(MainActivity.SESSION_INDEX_KEY, session_index);
//
//            sendBroadcast(intent);


            if (session_index <= session_count) {
                requestSessionInfo(session_index);
            }
            else {
                // 모든 세션 데이터를 가져왔음
                // Time to disconnet!!!
                set(HRM3200_STATE100);
                disconnect();

                Log.i(TAG, "End of All Sessions");

                int sessionid = sessionData.getSessionId();

                Intent intent = new Intent();
                intent.setAction(MainActivity.ACTION_STORED_DATA_COMPLETED);
                intent.putExtra(SessionDataActivity.SESSION_ID_KEY, sessionid);
                sendBroadcast(intent);
            }
        }
        else {
            // TODO:
        }
    }
    //////////

    // 2015.04.15 0x1D => HRM3200
    void responseMeasuringState(byte[] data) {
        if (data.length == 5) {

            // Sending Ack
            byte[] response = new byte[5];

            response[0] = (byte) 0x80; // STX
            response[1] = (byte) 0x1D; // CMD
            response[2] = (byte) 0x01; // Length
            response[3] = (byte) 0x00; // ACK OK
            response[4] = (byte) 0xEF; // EOF

            writeCharacteristic(response);
//            if (health_measurement_characteristic_write != null) {
//                boolean result;
//
//                health_measurement_characteristic_write.setValue(response);
//
//                result = mBluetoothGatt.writeCharacteristic(health_measurement_characteristic_write);
//                Log.i(TAG, "0x1D writeCharacteristic is called... : " + result);
//                printBytes(response);
//            } else {
//                Log.i(TAG, "health_measurement_characteristic_write is null");
//            }


            if ( (data[3] & 0xff) == 0x0 ) { // Start measuring

                Intent intent = new Intent();
                intent.setAction(MainActivity.ACTION_RESPONSE_MEASURING_STATUS);
                intent.putExtra(MainActivity.MEASURING_START_OR_STOP_KEY, true);
                sendBroadcast(intent);
                Log.i(TAG, "ACTION_RESPONSE_MEASURING_STATUS: start with 0x1C");

                // usesr info pref
                SharedPreferences userinfoPref = getSharedPreferences(UserSettingActivity.prefName, 0);
                int userStride = userinfoPref.getInt(UserSettingActivity.keyStride, 0);
                // 실시간데이타는 저장주기 1초
//                int period = userinfoPref.getInt(UserSettingActivity.keyPeriod, 10);
//                sessionData = new SessionData(userStride, period);
                sessionData = new SessionData(userStride, 1, true); // 실시간 데이터로 count를 계산해야 함

                setConnectionFlagInPreference(StartButtonView.WAITING);

            }
            else if ( (data[3] & 0xff) == 0x01 ) { // Stop measuring
                Intent intent = new Intent();
                intent.setAction(MainActivity.ACTION_RESPONSE_MEASURING_STATUS);
                intent.putExtra(MainActivity.MEASURING_START_OR_STOP_KEY, false);
                sendBroadcast(intent);
                Log.i(TAG, "ACTION_RESPONSE_MEASURING_STATUS: stop with 0x1C");

                sessionData.writeToDB(BluetoothLeService.this.getApplicationContext());

                setConnectionFlagInPreference(StartButtonView.CONNECTED);
                doOnceFlag = true;
            }

        }
        else {
            // TODO:
        }
    }


    private void writeCharacteristic(byte[] response) {
//        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
//            Log.w(TAG, "BluetoothAdapter not initialized");
//            return;
//        }

        if (health_measurement_characteristic_write != null) {
            boolean result;

            health_measurement_characteristic_write.setValue(response);

            result = mBluetoothGatt.writeCharacteristic(health_measurement_characteristic_write);
//            while(result == false) {
//                result = mBluetoothGatt.writeCharacteristic(health_measurement_characteristic_write);
//                Log.i(TAG, "0x1D writeCharacteristic is called... : " + result);
//            }
            Log.i(TAG, "0x1D writeCharacteristic is called... : " + result);
            printBytes(response);
        } else {
            Log.i(TAG, "health_measurement_characteristic_write is null");
        }


    }


    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        boolean result = mBluetoothGatt.readCharacteristic(characteristic);

        Log.i(TAG, "readCharacteristic is called... : " + result);
        Log.i(TAG, "PROPERTY_READ : " + ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0));
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        Log.i(TAG, "setCharacteristicNotification is called...");
        boolean result = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);


        //2015.04.15 For Battery level

        // This is specific to Heart Rate Measurement.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }

        // BATTERY_LEVEL
        if (UUID_BATTERY_LEVEL.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                        UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }

        // MHEALTH_MEASUREMENT_READ (0xff01)
        if (UUID_MHEALTH_MEASUREMENT_READ.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            if (descriptor != null) { // TODO: Sometimes, descriptor becomes null!!! It should't be.
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }

    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, int sessionid) {
        final Intent intent = new Intent(action);
        intent.putExtra(SessionDataActivity.SESSION_ID_KEY, sessionid);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
            intent.putExtra(MainActivity.HEART_RATE_KEY, heartRate);
            intent.setAction(MainActivity.ACTION_DATA_HEART_RATE_AVAILABLE);
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            Log.i(TAG, "Other Characteristic (length): " + (data==null? -1 : data.length));

            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    /*
     *  Printing Bytes for Logging
     */
    private void printBytes(byte[] data) {
        if (data != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                sb.append(String.format("%02X ", data[i]));
            }
            Log.i(TAG, "data : " + sb.toString());
        }
        else {
            Log.i(TAG, "data is null");
        }
    }


    private void setConnectionFlagInPreference(int flag) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(MainActivity.prefName, 0);

        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(MainActivity.connFlag, flag);

        editor.commit();
    }

    private void setServiceDiscoveredFlagInPreference(boolean flag) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(MainActivity.prefName, 0);

        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(MainActivity.discoveryFlag, flag);

        editor.commit();
    }

    private void addDeviceInPreference(String uuid, String name) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(DeviceScanActivity.prefName, 0);

        SharedPreferences.Editor editor = pref.edit();

        editor.putString(uuid, name);

        int i = 0;
        while (true) {
            String address = pref.getString(Integer.toString(i), "");
            if (address == null || address == "") {
                break;
            } else {
                i = i + 1;
            }
        }

        editor.putString(Integer.toString(i), uuid);

        editor.commit();
    }

}
