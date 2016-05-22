package com.h3.hrm3200;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
//import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private String VERSION = "2015-05-21-13-30";
    //
    // UI :
    //       2015/5/19 : 타이틀바(액션바)에 백그라운드 이미지 추가,
    //                   세션데이타 groupindicator 이미지로 변경->원래대로, 상세데이타 배경색 추가
    //       2015/5/15 : user info 화면 이미지 수정
    //       2015/5/13 : 메인화면 duration 버그 수정, 화면 출력 문자열 수정, 그래프화면 calorie 이미지 추가
    //       2015/5/11 : GoogleFit 버튼 클릭시 google fit로 올린 데이타 검색해서 출력하는 화면 추가
    //       2015/5/10 : stop클릭시, 저장데이타 다운로드 완료시 그래프화면으로 전환 추가
    //       2015/5/9  : ble끊길때 progressDialog 있으면 종료
    //       2015/5/7  : 메인화면 duration 추가하고 위치 변경
    //       2015/5/6  : 그래프 그리기 -> index로 order by 하도록 수정, 손실데이타 보정그래프 방법 수정
    //       2015/5/5  : 다운로드중 손실데이타 보정 그래프 그리기 -> 버그 수정
    //       2015/5/2  : 설치후 최초 실행시, user_info가 저장되지 않았을 경우는 UserInfo 화면으로 전환
    //       2015/5/2  : 그래프 화면 Zone Time, Calorie 추가, 다운로드중 손실데이타 보정 그래프 그리기
    //       2015/5/1  : manifest file, Application 항목 attribute중 android:largeHeap="true"로 설정
    //                   -> TODO: 이미지 로드 문제로 추가 -> 제거 후 테스트 필요
    //       2015/4/30 : Out Of Memory => 메인 화면 Start버튼 이미지 크기를 기기에 맞추는 방법 재수정
    //                   Device Scan 화면 cancel 버튼 기능 활성화
    //       2015/4/29 : UserInfo 화면 stride, weight 입력방법 변경
    //       2015/4/29 : 메인 화면 Start버튼 이미지 크기를 기기에 맞추는 방법 수정, UserInfo 생년도 입력 코드 수정
    //       2015/4/28 : Back key로 종료할건지 확인하는 대화상자 추가
    //       2015/4/28 : 메인 화면 Start버튼 이미지 크기를 기기에 맞추도록 수정
    //       2015/4/28 : SessionData 최근자료부터 읽어오도록 수정
    //       2015/4/27 : UserInfo 생년도 입력 방법 spinner 로 변경
    //                   DeviceScan 화면에서 등록 device 삭제시 확인 dialog 추가
    //       2015/4/24 : 메인 화면 Menu에 DB Writing Mode 추가 (Default: Writing All Data Once)
    //                    (이 메뉴 선택시 Writing Pieces of Data로 전환하고, 다시 선택하면 Default로 전환)
    //            => Writing Pieces of Data : 60개 데이터 들어올때마다 저장 (쓰레드 사용)
    //            => Writing All Data Once : 모든 데이터가 들어오면 저장하는 기존 방법 (쓰레드 미사용)
    //       2015/4/23 : Stored Data를 받을 때 0%~100%까지 나오도록 공식 수정
    //       2015/4/23 : Logo 이미지가 너무 커서 Galaxy4에서 메모리 부족으로 죽는 문제 해결
    //       2015/4/22 : GraphView heart_rate_zone별 색상 추가
    //       2015/4/21 : 세션데이타 선택삭제
    //       2015/4/18 : 세션데이타 선택 UI(체크박스) 추가, DB 삭제 추가 필요
    //       2015/4/18 : 로고화면 추가
    //       2015/4/17 : 측정기 테스트를 위해 최신 수신 데이타 시간 화면 출력(lastUpdated)
    //       2015/4/16 : 서비스 동작 중 다른 앱으로 전환하고 돌아오더라도 일관성있는 UI
    //                   (Start/Stop, Measuring 텍스트 등) 유지
    //       2015/4/16 : 측정주기(period) 추가, SessionDataDetail 액티비티 변경
    //       2015/4/16 : 배터리 읽기 완료
    //       2015/4/16 : 0x1c, 0x1d, 0x82 (02)에 따른 UI 변경
    //       2015/4/15 : 저장된 데이터 가져오기 UI 구현 및 테스트 완료
    //       Main, Device Scan, User Setting, Session Data
    //       Display Heart Rate (from Custom Health Measurement) in MainActivity
    //       Auto Scanning in DeviceScanActivity by an Intent action.
    //       Automatically Starting
    //       UI Image 작업
    //       Two list views in DeviceScanActivity
    //       Reset Service 메뉴 추가
    //       Read Battery 메뉴 추가
    //       텍스트를 이미지 버튼으로 모두 교체
    //       그래프 기능 추가 (GraphActivity)

    // BLE :
    //       2015/5/20 : [0x11][0x80] 송신 도중 disconnect 된 경우 exception 처리
    //       2015/5/2  : 저장데이타 수신중 손실된 데이타를 duration, Zone time에 반영
    //       2015/5/2  : 측정기가 이미 측정중이라 다운로드불가(측정요청불가)일때 실시간데이타 수신대기 상태로 진행
    //                   실시간데이타의 세션시작시간은 첫 측정자료 수신시간으로 업데이트하는 코드 추가
    //       2015/5/1  : 저장주기는 실시간 데이타는 1초 -> duration 변경,
    //                   sessionData의 평균값 계산시 데이타갯수는 실제 수신한 갯수로 변경
    //       2015/4/28 : calorie, distance 계산 방식 변경. hr_status DB에 추가
    //       2015/4/24 : DB Writing Mode에서 분리해서 저장하는 방식(Writing Pieces of Data)에서
    //                    duration, average, min, max 등이 제대로 설정되지 않는 문제 수정
    //       2015/4/24 : 실시간 데이터 또는 저장된 데이터를 60개가 들어올때마다 DB 저장하도록 수정
    //       2015/4/23 : onConnectionChange에서 GATT_SUCCESS가 아닌 status인 경우에 대한 처리
    //       2015/4/23 : onConnectionChange에서 DISCONNECT 상태이면 close()도 추가하도록 넣음
    //                    (disconnect 후에 close를 부르지 않으면 이슈가 생길 수 있음)
    //       2015/4/22 : UserSetting 면에서 overwrite 설정 추가 > 4/23 수정
    //       2015/4/22 : 저장데이타 연도문제 완료
    //       2015/4/22 : HRM3200 상태 기계 구현
    //                     (상태가 꼬이는 경우, 예를들어 writeCharacteristic fail, 바로 disconnect 되도록)
    //       2015/4/17 : DB에 두번 저장되는 버그 수정
    //       2015/4/17 : 저장 주기 DB 반영
    //       2015/4/17 : 0x82 측정중지 보내고 1초후 disconnect()
    //       2015/4/16 : 배터리 Noti와 실시간 데이터 Noti에 1초 간격을 두고 보내도록 수정
    //       2015/4/16 : 0x1c, 0x1d, 0x82 (02) 프로토콜 지원
    //       2015/4/14: 저장된 데이터 가져오기 기능 구현 및 PC 시뮬레이터를 통한 테스트 완료
    //       Scanning, Connection, Heart Rate,
    //       Battery Level
    //       H3 Custom Health Measurement
    //       실시간 데이터 DB 저장
    //       DB 추가 (HeartRateDBHelper.java 추가)
    //
    //
    // 작업한 내용
    //       2015/5/19 : bluetoothGatt disconnect callback이 비정상적으로 일어난 경우 db write하고 그래프화면
    //                   전환하는 부분 코드 수정 writeDB() write 성공/실패 리턴하도록 수정
    //       2015/5/19 : 세션 merge 할때 첫 데이타는 무조건 샘플링하게 수정, 시간간격 인덱스화 계산방법 수정
    //       2015/5/15 : progressbar 버그 수정
    //       2015/5/15 : putData 계산방법 변경, stride 추가 관련 버그 수정
    //       2015/5/15 : 세션 merge 할때 통계작업 다시 하도록 변경
    //       2015/5/13 : 메인화면 GoogleFit 버튼 클릭시 사용자 계정 선택, 권한 설정만 하도록 변경,
    //                   업로드 데이타 읽어오기는 메뉴로 전환
    //       2015/5/13 : WAIT 상태, 즉 측정값이 하나도 수신되지 않은 상태에서 측정기의 종료버튼을 클릭시 앱이 다운-> 버그 수정
    //       2015/5/13 : google fit 업로드 자료에 calorie,distance 추가
    //       2015/5/13 : 세션 merge시 period 다른 경우 처리 (10초로 샘플링을 하여 저장)
    //                 : 두 세션간 시간차도 index에 반영
    //       2015/5/10 : 두 세션 merge 추가
    //       2015/5/7  : Google Fit 연동
    //       2015/5/7  : csv 파일에 측정시간 추가(DB에는 추가하지 않음)
    //       2015/5/6  : Google Fit code 수정 및 테스트
    //       2015/5/5  : DB스키마 변경
    //       2015/5/5  : Google Fit code 추가 및 테스트중
    //       2015/4/28 : csv 파일 변경
    //       2015/4/27 : 메인 화면 Start버튼의 이미 사용완료한 bitmap을 recycle
    //       2015/4/24 : Log를 동적으로 Enable/Disable 시키는 메뉴를 메인 화면에 추가
    //       2015/4/22 : csv 파일 생성해서 이메일로 보내는 기능 추가
    //       2015/4/22 : 연결 해제 시 두 번 disconnect 부르는 버그(?) 수정
    //       2015/4/16 : SessionData내에 DB 기록 여부를 표시하는 flag를 두어 중복 writing 방지
    //       2015/4/14 : BluetoothLeSerivce의 BluetoothGattCallback 코드 정리
    //       Smartphone에서 정지했을때 죽는 문제와 DB에 2번 쓰기가 발생하는 문제 해결
    //       Trying to connect 상태와 Connection Failed 상태를 UI에 추가
    //       UserInfo에서 ""를 Int로 바꾸려다 예외 발생하는 문제 해결
    //       BluetoothLeService에서 연결 상태에 새로운 연결 요청을 무시
    //       BluetoothLeService에서 비연결 상태에서 배터리 레벨 읽기 요청을 무시
    //       distance는 최종값으로
    //       Home 버튼처리
    //       StoredData 요청 프로토콜 테스트 추가

    //
    // Issues ==> OK
    //   - 2015/4/16: 서비스 동작 중 UI를 kill하고 다시 띄웠을 때 서비스 동작과 일관성있게 UI와 맞는지 확인 필요
    //   - 2015/4/16: 프로토콜 state 관리 필요. (현재는 중간 state 부터 동작 가능)
    //   - 2015/4/14: 저장된 데이터의 연도는 15이고 실시간 데이터의 연도는 2015로 다르게 표기
    //   - 2015/4/14: BluetoothLEService의 synchronizeTime()에서 맨 처음 write 2회 하는 시도가 실패
    //
    //   - Settings에서 연결한 다음 Main으로 돌아오면 STOP 버튼이 나와야 함(START 버튼 표시)
    //   - Settings에서 ReScan해서 연결하면 연결된 디바이스가 등록되어야 하는데 UI적으로 등록되지 않음
    //   - Start/Stop 버튼과 프로토콜 연동이 꼬이는 경우 종종 발생

    // TODO:
    //   - 2015/4/22: 저장된 세션 데이터 가져오는 도중에 Cancel하는 기능
    //   -            writeToDB를 스레드로 바꾸어 많은 데이터 기록시 UI에 지장이 없도록 ==> OK
    //   -            저장 데이터 download 후 detail 버튼을 누르면 해당 그래프 보여주기 ==> OK
    //   -            그래프에서 레벨 별 색깔을 다르게 표시하기 (상 => 오픈 소스 분석 필요) ==> OK
    //   -            Google Fit 연동 (상 => 샘플 테스트 및 API 분석) ==> OK




    // Intent

    // For H3 System Defined Characteristic
    public static final String HEALTH_MEASUREMENT_KEY = "health measurement";
    public static final String HEALTH_MEASUREMENT_HR_STATUS_KEY = "hr status";
    public static final String HEALTH_MEASUREMENT_ACT_LEVEL_KEY = "act level";
    public static final String HEALTH_MEASUREMENT_ENERGY_KEY = "energy expended";
    public static final String HEALTH_MEASUREMENT_STEPS_KEY = "steps";
    public static final String HEALTH_MEASUREMENT_DURATION_KEY = "duration";

    public static final String ACTION_DATA_HEALTH_MEASUREMENT_AVAILABLE = "android.intent.action.HEALTH_MEASUREMENT";

    // For BLE Standard Characteristic
    public static final String BATTERY_LEVEL_KEY = "battery level";
    public static final String ACTION_DATA_BATTERY_LEVEL_AVAILABLE = "android.intent.action.BATTERY_LEVEL";

    public static final String HEART_RATE_KEY = "heart rate";
    public static final String ACTION_DATA_HEART_RATE_AVAILABLE = "android.intent.action.HEART_RATE";

    //
    public static final String START_KEY = "start key";
    public static final String ACTION_PRESS_START_BUTTON = "android.intent.action.PRESS_START_BUTTON";

    public static final String ACTION_STORED_DATA_AVAILABLE = "android.intent.action.STORED_DATA";

    // For progressBar
    public static final String SESSION_COUNT_KEY = "session count";
    public static final String SESSION_INDEX_KEY = "session index";
    public static final String DATA_COUNT_KEY = "data count"; // optional
    public static final String DATA_INDEX_KEY = "data index"; // optional
    public static final String ACTION_STORED_DATA_PROGRESS = "android.intent.action.STORED_DATA_PROGESS";

    public static final String ACTION_STORED_DATA_COMPLETED = "android.intent.action.STORED_DATA_COMPLETED";

    // For [0x1C]
    public static final String MEASURING_START_OR_STOP_KEY = "measuring start or stop";
    public static final String ACTION_RESPONSE_MEASURING_STATUS = "android.intent.action.RESPONSE_MEASURING_STATUS";

    // 앱 종료
    public static final String ACTION_FINISH_ACTIVITY = "android.intent.action.FINISH_ACTIVITY";


    public final static String prefName = "MainActivityPref";
    public final static String connFlag = "connection";
    public final static String discoveryFlag = "discovered";

    private ImageButton buttonUserInfo;
    private ImageButton buttonSetting;
    private ImageButton buttonSessionData;
    private ImageButton buttonRecord;

    private TextView textViewBatteryLevel;
    private TextView textViewCalorie;
    private TextView textViewSteps;
    private TextView textViewDuration;

    private StartButtonView startButtonView;

    private static final String TAG = "MainActivity";

    private boolean canPressToStart = true;

    private Handler handler = new Handler();

    // For GoogleApiClient
    private boolean authInProgress = false;
    private static final int REQUEST_OAUTH = 1001;
    private GoogleApiClient mGoogleApiClient = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);


        startButtonView = (StartButtonView)findViewById(R.id.customView1);

        buttonUserInfo = (ImageButton)findViewById(R.id.imageButton);
        buttonSetting = (ImageButton)findViewById(R.id.imageButton2);
        buttonSessionData = (ImageButton)findViewById(R.id.imageButton3);
        buttonRecord = (ImageButton)findViewById(R.id.imageButton4);

        textViewBatteryLevel = (TextView)findViewById(R.id.textView4);
        textViewCalorie = (TextView)findViewById(R.id.textView);
        textViewSteps = (TextView)findViewById(R.id.textView2);
        textViewDuration = (TextView)findViewById(R.id.textViewDuration);
        textViewDuration.setTextSize((float)((textViewDuration.getTextSize())*0.55));
//        Log.i(TAG, "textViewDuration.getTextSize: " + textViewDuration.getTextSize());


        // usesr info pref
        SharedPreferences userinfoPref = getSharedPreferences(UserSettingActivity.prefName, 0);
        boolean userInfoSetting = userinfoPref.getBoolean(UserSettingActivity.keyStored, false);
        if (userInfoSetting == false) {
            Intent intent = new Intent();
            intent.setClassName("com.h3.hrm3200", "com.h3.hrm3200.UserSettingActivity");
            startActivityForResult(intent, 0);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(MainActivity.ACTION_PRESS_START_BUTTON);

        intentFilter.addAction(ACTION_STORED_DATA_AVAILABLE);
        intentFilter.addAction(ACTION_STORED_DATA_PROGRESS);
        intentFilter.addAction(ACTION_STORED_DATA_COMPLETED);
        intentFilter.addAction(ACTION_RESPONSE_MEASURING_STATUS);

        intentFilter.addAction(ACTION_DATA_HEALTH_MEASUREMENT_AVAILABLE);
        intentFilter.addAction(ACTION_DATA_HEART_RATE_AVAILABLE);
        intentFilter.addAction(ACTION_DATA_BATTERY_LEVEL_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTING);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE); // Standard Heart Rate

        intentFilter.addAction(BluetoothLeService.ACTION_DATA_STORE_AND_DISCONNECTED);

        registerReceiver(mainActivityReceiver, intentFilter);


//        // usesr info pref
//        SharedPreferences userinfoPref = getSharedPreferences(UserSettingActivity.prefName, 0);
//        userWeight = userinfoPref.getInt(UserSettingActivity.keyWeight, 0);
//

        // If connected
        SharedPreferences pref = getSharedPreferences(MainActivity.prefName, 0);
        int connectionStatus = pref.getInt(MainActivity.connFlag, StartButtonView.NO_DEVICE);
        boolean discoveredStatus = pref.getBoolean(MainActivity.discoveryFlag, false);

        startButtonView.setConnectionStatus(connectionStatus);

//        if (discoveredStatus) {
////            startPolling(INTERVAL);
//            requestToReadBatteryLevel();
//        }

        if (connectionStatus == StartButtonView.NO_DEVICE) {
            startButtonView.setStart(true);
            startButtonView.setBPM(0);

            setBattery(0);
            setCalorie(0);
            setSteps(0);
            setDuration(0);

            canPressToStart = true;
        }
        else {
            switch(connectionStatus) {
                case StartButtonView.CONNECTING:
                    startButtonView.setStart(false);
                    canPressToStart = false;
                    break;
                case StartButtonView.CONNECTED:
                    startButtonView.setStart(false);
                    canPressToStart = false;
                    break;
                case StartButtonView.WAITING:
                    startButtonView.setStart(false);
                    canPressToStart = false;
                    break;
                case StartButtonView.MEASURING:
                    startButtonView.setStart(false);
                    canPressToStart = true;
                    break;
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mainActivityReceiver);
//        stopPolling();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if ( (mGoogleApiClient!= null) && (mGoogleApiClient.isConnected()) ) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        startButtonView.cleanUp();
//        buttonUserInfo = null;
//        buttonSetting = null;
//        buttonSessionData = null;
//        buttonRecord = null;
//
//        textViewBatteryLevel = null;
//        textViewCalorie = null;
//        textViewSteps = null;
//
//        startButtonView = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //For Test
        if (id == R.id.action_settings) {
            if (INTERVAL==1000) { // If 1sec
                INTERVAL = 60 * 1000; // 1 min
                Toast.makeText(getApplicationContext(), "1 min period for battery read request", Toast.LENGTH_SHORT).show();
            }
            else {
                INTERVAL = 1000; // 1 sec
                Toast.makeText(getApplicationContext(), "1 sec. period for battery read request", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        else if (id == R.id.action_read_battery) {
            requestToReadBatteryLevel();
        }
        else if (id == R.id.action_reset_service) {
            Intent intent = new Intent(MainActivity.this, BluetoothLeService.class);
            stopService(intent);

            startButtonView.setStart(true);
            startButtonView.setBPM(0);
            startButtonView.setConnectionStatus(StartButtonView.NO_DEVICE);

            setBattery(0);
            setCalorie(0);
            setSteps(0);
            setDuration(0);

            canPressToStart = true;

            SharedPreferences pref = getApplicationContext().getSharedPreferences(MainActivity.prefName, 0);
            SharedPreferences.Editor editor = pref.edit();

            editor.putInt(MainActivity.connFlag, StartButtonView.NO_DEVICE);
            editor.putBoolean(MainActivity.discoveryFlag, false);

            editor.commit();
        }
        else if (id == R.id.action_db_writing_mode) {

            //For protocol test
//            canPressToStart = false;
//            requestToReadStoredData();

//            Test.dbStressTest(this);

            if (SessionData.DB_DEBUG == true) {
                SessionData.DB_DEBUG = false;
                Toast.makeText(this, "Writing all data once ", Toast.LENGTH_SHORT).show();
            } else {
                SessionData.DB_DEBUG = true;
                Toast.makeText(this, "Writing pieces of data", Toast.LENGTH_SHORT).show();
            }
        }

        else if (id == R.id.action_log_mode) {
            if (Log.DEBUG == true) {
                Log.DEBUG = false;
                Toast.makeText(this, "Log disabled", Toast.LENGTH_SHORT).show();
            } else {
                Log.DEBUG = true;
                Toast.makeText(this, "Log enabled", Toast.LENGTH_SHORT).show();
            }
        }
//        else if ( id == R.id.action_gen_test_data_for_merge) {
//            Test.writeDBForMergeTest(this);
//        }

        return super.onOptionsItemSelected(item);
    }



    public void onClick(View v) {
        if(v == buttonUserInfo) {
            Intent intent = new Intent();
            intent.setClassName("com.h3.hrm3200", "com.h3.hrm3200.UserSettingActivity");
            startActivityForResult(intent, 0);
        }
        else if (v==buttonSetting) {
            Intent intent = new Intent();
            intent.setClassName("com.h3.hrm3200", "com.h3.hrm3200.DeviceScanActivity");
            startActivityForResult(intent, 0);
        }
        else if (v==buttonSessionData) {
            Intent intent = new Intent();
            intent.setClassName("com.h3.hrm3200", "com.h3.hrm3200.SessionDataActivity");
            startActivityForResult(intent, 0);
        }
        else if (v==buttonRecord) {

//            Intent intent = new Intent();
//            intent.setClassName("com.h3.hrm3200", "com.h3.hrm3200.GoogleFitActivity");
//            startActivityForResult(intent, 0);

            buildFitnessClient();
        }
    }


    // Google Sync
    private void buildFitnessClient() {

        // Create the Google API Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SESSIONS_API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "Connected to Google Fit.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.i(TAG, "Connection failed. Cause: " + result.toString());
                                if (!result.hasResolution()) {
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            MainActivity.this, 0).show();
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
                                if (!authInProgress) {
                                    try {
                                        Log.i(TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(MainActivity.this,
                                                REQUEST_OAUTH);
                                    } catch (IntentSender.SendIntentException e) {
                                        Log.e(TAG,
                                                "Exception while starting resolution activity " + e.toString());
                                    }
                                }
                            }
                        }
                )
                .build();

        mGoogleApiClient.connect();
    }


    private long INTERVAL = 1 * 60 * 1000; // Every 1 minute
    Thread thread;

//    private void startPolling(long interval) {
//        requestToReadBatteryLevel();
//
//        if (thread == null) {
//            thread = new Thread(new PollingBatteryLevel(this, interval));
//            thread.start();
//        }
//    }

//    private void stopPolling() {
//        if (thread != null) {
//            thread.interrupt();
//            thread = null;
//        }
//    }

    class PollingBatteryLevel implements Runnable {
        private Context context;
        private long interval;
        public PollingBatteryLevel(Context context, long interval) {
            this.context = context;
            this.interval = interval;
        }

        @Override
        public void run() {
            Log.i(TAG, "PollingBatteryLevel.run() starts.(Request to read Battery Level)");

            boolean flag = true;
            while(flag) {
                try {
                    requestToReadBatteryLevel();

                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    flag = false;
                }
            }

            Log.i(TAG, "PollingBatteryLevel.run() ends.");
        }
    }

    public void setBattery(int battery_level) {
        if (battery_level<0) battery_level = 0;
        else if (battery_level>100) battery_level = 100;

        textViewBatteryLevel.setText(battery_level + "");
    }

    public void setCalorie(int calorie) {
        if (calorie < 0 ) calorie = 0;

        textViewCalorie.setText(calorie + "");
    }

    public void setSteps(int steps) {
        if (steps < 0) steps = 0;

        textViewSteps.setText(steps + "");
    }

    public void setDuration(int duration) {
        if (duration < 0) duration = 0;
        int hour = duration / (60 * 60);
        int min = (duration - hour * 60 * 60) / 60;
        int sec = duration - hour * 60 * 60 - min * 60;

        textViewDuration.setText(hour + ":" + min + ":" + sec);
    }

    // Receiver
    MainActivityReceiver mainActivityReceiver = new MainActivityReceiver();

    class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {

            if (ACTION_DATA_HEALTH_MEASUREMENT_AVAILABLE.equals(intent.getAction())) {
                int heart_rate = intent.getIntExtra(HEALTH_MEASUREMENT_KEY, 0);
                int hr_status = intent.getIntExtra(HEALTH_MEASUREMENT_HR_STATUS_KEY, 0);
                int act_level = intent.getIntExtra(HEALTH_MEASUREMENT_ACT_LEVEL_KEY, 0);
                int calorie = intent.getIntExtra(HEALTH_MEASUREMENT_ENERGY_KEY, 0);
                int steps = intent.getIntExtra(HEALTH_MEASUREMENT_STEPS_KEY, 0);
                int duration = intent.getIntExtra(HEALTH_MEASUREMENT_DURATION_KEY, 0);

                startButtonView.setConnectionStatus(StartButtonView.MEASURING);

                startButtonView.setBPM(heart_rate);
                startButtonView.chkHearRateZone(heart_rate);
                startButtonView.setLastUpdate(StartButtonView.HEART_RATE);
                setCalorie(calorie);
                setSteps(steps);
                setDuration(duration);

                startButtonView.setStart(false);

                canPressToStart = true;

            } else if (ACTION_DATA_HEART_RATE_AVAILABLE.equals(intent.getAction())) {

                int heart_rate = intent.getIntExtra(HEART_RATE_KEY, 0);
                startButtonView.setBPM(heart_rate);
                startButtonView.chkHearRateZone(heart_rate);
                startButtonView.setLastUpdate(StartButtonView.HEART_RATE);

            } else if (ACTION_DATA_BATTERY_LEVEL_AVAILABLE.equals(intent.getAction())) {

                int battery_level = intent.getIntExtra(BATTERY_LEVEL_KEY, 0);
                startButtonView.setLastUpdate(StartButtonView.BATTERY);
                setBattery(battery_level);

            } else if (BluetoothLeService.ACTION_GATT_CONNECTING.equals(intent.getAction())) {

                startButtonView.setConnectionStatus(StartButtonView.CONNECTING);

                canPressToStart = false;

            } else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(intent.getAction())) {

                startButtonView.setConnectionStatus(StartButtonView.CONNECTED);

                canPressToStart = false;

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(intent.getAction())) {

//                startPolling(INTERVAL);

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(intent.getAction())) {

//                stopPolling();
//                requestToUnbindService();
//                requestToDisconnect();

                startButtonView.setConnectionStatus(StartButtonView.NO_DEVICE);

                startButtonView.setStart(true);
                startButtonView.setBPM(0);

                setBattery(0);
                setCalorie(0);
                setSteps(0);
                setDuration(0);

                canPressToStart = true;

                if ( (progressDialog!=null) && (progressDialog.isShowing()) ) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }


            } else if (BluetoothLeService.ACTION_DATA_STORE_AND_DISCONNECTED.equals(intent.getAction())) {

                startButtonView.setConnectionStatus(StartButtonView.NO_DEVICE);

                startButtonView.setStart(true);
                startButtonView.setBPM(0);

                setBattery(0);
                setCalorie(0);
                setSteps(0);
                setDuration(0);

                canPressToStart = true;
                if ( (progressDialog!=null) && (progressDialog.isShowing()) ) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }


                int sessionid = intent.getIntExtra(SessionDataActivity.SESSION_ID_KEY, -1);
                Intent i = new Intent();
                i.setClassName("com.h3.hrm3200", "com.h3.hrm3200.GraphActivity");
                i.putExtra(SessionDataActivity.SESSION_ID_KEY, sessionid);
                startActivity(i);

            } else if (BluetoothLeService.ACTION_GATT_ABNORMAL_TERMINATION.equals(intent.getAction())) {
//                stopPolling();
                startButtonView.setConnectionStatus(StartButtonView.NO_DEVICE);

                startButtonView.setStart(true);
                startButtonView.setBPM(0);

                setBattery(0);
                setCalorie(0);
                setSteps(0);
                setDuration(0);

                canPressToStart = true;

                if ( (progressDialog!=null) && (progressDialog.isShowing()) ) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }

                Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(intent.getAction())) {


            } else if (MainActivity.ACTION_PRESS_START_BUTTON.equals(intent.getAction())) {
                boolean start = intent.getBooleanExtra(MainActivity.START_KEY, false);
                if (canPressToStart) {
                    if (start) { // Time to "START"

                        Log.i(TAG, "cnaPressToStart==true && start==true");

                        // If a registered device is available
                        DeviceScanActivity.MyDevice device = isRegisteredDeviceAvailable();

                        if (device == null) {
                            Intent i = new Intent();
                            i.setClassName("com.h3.hrm3200", "com.h3.hrm3200.DeviceScanActivity");
                            i.setAction(DeviceScanActivity.AutoScanningAction);
                            startActivityForResult(i, 0);
                        } else {
                            Intent i = new Intent(MainActivity.this, BluetoothLeService.class);
                            i.setAction(BluetoothLeService.ACTION_CONNECT);
                            i.putExtra(BluetoothLeService.CONNECT_ADDRESS_KEY, device.getAddress());
//                            i.putExtra(BluetoothLeService.STORED_DATA_REQUEST_KEY, false);
                            startService(i);
//                        makeConnection(device.getAddress(), device.getName());
                        }


                    } else { // Time to "STOP"
//                    makeDisconnection();
//                    requestToUnbindService();
                        Log.i(TAG, "cnaPressToStart==true && start==false");

                        requestToDisconnect();
                    }
                    canPressToStart = false;
                }

            }
            else if (ACTION_STORED_DATA_AVAILABLE.equals(intent.getAction())) {

//                int session_count = intent.getIntExtra(SESSION_COUNT_KEY, 0);
//                int session_index = intent.getIntExtra(SESSION_INDEX_KEY, 0);
//
//                Toast.makeText(MainActivity.this,
//                        "Current Session/Total Stored Session : " + session_index + "/"
//                                + session_count, Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Data Download")
                       .setMessage("Do you want to download data?")
                       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               Intent intent = new Intent();
                               intent.setClassName("com.h3.hrm3200", "com.h3.hrm3200.BluetoothLeService");
                               intent.setAction(BluetoothLeService.ACTION_REQUEST_TO_DOWNLOAD_STORED_DATA);
                               startService(intent);
                               Log.i(TAG, "Request to download");
                           }
                       })
                       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               Intent intent = new Intent();
                               intent.setClassName("com.h3.hrm3200", "com.h3.hrm3200.BluetoothLeService");
                               intent.setAction(BluetoothLeService.ACTION_REQUEST_REALTIME_DATA);
                               startService(intent);
                               Log.i(TAG, "Request to realtime data");
                           }
                       })
                       .setCancelable(false)
                       .show();

                canPressToStart = false;

            }
            else if (ACTION_STORED_DATA_PROGRESS.equals(intent.getAction())) {

                int session_count = intent.getIntExtra(SESSION_COUNT_KEY, 0);
                int session_number = intent.getIntExtra(SESSION_INDEX_KEY, 0);
                int data_count = intent.getIntExtra(DATA_COUNT_KEY, 0);
                int data_number = intent.getIntExtra(DATA_INDEX_KEY, 0);

                double progress = (100.0/session_count) * (session_number-1);

                if (data_count != 0) {
                    progress = progress + (100.0/session_count/data_count) * (data_number+1);
                }

                Log.i(TAG, "[session_count,session_number,data_count,data_number:progress]"
                        + "[" + session_count + "," + session_number + "," + data_count + "," + data_number + ":" + (int)progress + "]");

                if (progressDialog == null || progressDialog.isShowing() == false) {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setMessage("Data Download");
                    progressDialog.setCancelable(false);
                    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Toast.makeText(MainActivity.this, "Not cancelable (On debugging)", Toast.LENGTH_SHORT).show();
                        }
                    });
                    progressDialog.show();
                }

                progressDialog.setProgress((int) progress);
            }
            else if (ACTION_STORED_DATA_COMPLETED.equals(intent.getAction())) {
                progressDialog.dismiss();
                progressDialog = null;

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Data Download")
                        .setMessage("Download completed.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Details", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int sessionid = intent.getIntExtra(SessionDataActivity.SESSION_ID_KEY, -1);
                                Intent i = new Intent();
//                                intent.setClassName("com.h3.hrm3200", "com.h3.hrm3200.SessionDataActivity");
                                i.setClassName("com.h3.hrm3200", "com.h3.hrm3200.GraphActivity");
                                i.putExtra(SessionDataActivity.SESSION_ID_KEY, sessionid);
                                startActivity(i);
                            }
                        })
                        .show();
            }
            else if (ACTION_RESPONSE_MEASURING_STATUS.equals(intent.getAction())) {
                boolean measuring_start_or_stop = intent.getBooleanExtra(MEASURING_START_OR_STOP_KEY, false);

                if (measuring_start_or_stop==true) {
                    startButtonView.setConnectionStatus(StartButtonView.WAITING);
                } else {
                    startButtonView.setConnectionStatus(StartButtonView.CONNECTED);
                }
            }
            else {
                Log.i(TAG, "MainActivityReceiver.onReceive : no action is matched.");
            }
        }

        private DeviceScanActivity.MyDevice isRegisteredDeviceAvailable() {
            SharedPreferences pref = getSharedPreferences(DeviceScanActivity.prefName, 0);

            DeviceScanActivity.MyDevice device = null;

            String address = pref.getString(Integer.toString(0), "");
            String name = pref.getString(address, "");

            if ( ! (address == null || address == "") ) {
                device = new DeviceScanActivity.MyDevice(address, name);
            }

            return device;
        }

//        private void makeConnection(String mDeviceAddress, String mDeviceName) {
//            // Connecting a BLE device
//
//            Intent gattServiceIntent = new Intent(MainActivity.this, BluetoothLeService.class);
//            gattServiceIntent.putExtra(DeviceScanActivity.DEVICE_NAME, mDeviceName);
//            gattServiceIntent.putExtra(DeviceScanActivity.DEVICE_ADDRESS, mDeviceAddress);
//
//            ServiceConnection mServiceConnection = new BLEServiceConnection(MainActivity.this);
//
//            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
////            startButtonView.setStart(false);
//
//            Log.i(TAG, "A bluetooth device is automatically selected from registered list to connect...");
//        }

//        private void makeDisconnection() {
//            if (mServiceConnection != null) {
//                unbindService(mServiceConnection);
//            }
//
////            startButtonView.setStart(true);
//
//            Log.i(TAG, "A bluetooth device is selected automatically from registered list to disconnect...");
//        }

    }

    private ProgressDialog progressDialog;

    // Requests to BluetoothLeService
    private void requestToReadBatteryLevel() {
        Intent intent = new Intent();
        intent.setClassName("com.h3.hrm3200", "com.h3.hrm3200.BluetoothLeService");
        intent.setAction(BluetoothLeService.ACTION_READ_BATTERY_LEVEL);
        startService(intent);
        Log.i(TAG, "Request to read Battery Level");
    }

    private void requestToDisconnect() {
        Intent intent = new Intent(MainActivity.this, BluetoothLeService.class);
        intent.setAction(BluetoothLeService.ACTION_DISCONNECT);
        startService(intent);
        Log.i(TAG, "Request to disconnect BluetoothLeService");

        canPressToStart = false;
    }

//    private void requestToUnbindService() {
//        Intent intent = new Intent();
//        intent.setClassName("com.h3.hrm3200", "com.h3.hrm3200.BluetoothLeService");
//        intent.setAction(BluetoothLeService.ACTION_UNBIND_SERVICE);
//        startService(intent);
//        Log.i(TAG, "Request to unbind BluetoothLeService");
//    }



    // For protocol test, request to Read Stored Data
    private void requestToReadStoredData() {

        SharedPreferences pref = getSharedPreferences(DeviceScanActivity.prefName, 0);

        DeviceScanActivity.MyDevice device = null;

        String address = pref.getString(Integer.toString(0), "");
        String name = pref.getString(address, "");

        if ( ! (address == null || address == "") ) {
            device = new DeviceScanActivity.MyDevice(address, name);
        }

        if (device == null) {
            Toast.makeText(this, "Please retry after registering a device", Toast.LENGTH_SHORT).show();
        } else {
            Intent i = new Intent(MainActivity.this, BluetoothLeService.class);
            i.setAction(BluetoothLeService.ACTION_CONNECT);
            i.putExtra(BluetoothLeService.CONNECT_ADDRESS_KEY, device.getAddress());
//            i.putExtra(BluetoothLeService.STORED_DATA_REQUEST_KEY, true);
            startService(i);
//                        makeConnection(device.getAddress(), device.getName());

            canPressToStart = false;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Quit")
                            .setMessage("Do you want to quit?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
//                                    finishAffinity();
                                }
                            }).setNegativeButton( "No", null ).show();
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
        else if (resultCode==RESULT_OK) {
            String action = data.getAction();
            if (action != null && ACTION_FINISH_ACTIVITY.equals(action)) {
                finish();
            }
        }
    }
}
