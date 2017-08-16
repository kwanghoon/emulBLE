package com.h3.hrm3200;

import android.app.Activity;
import android.app.AlertDialog;
import mocking.android.bluetooth.BluetoothAdapter;
import mocking.android.bluetooth.BluetoothManager;
import mocking.android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothManager;
//import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.h3.hrm3200.emul.AutoHRM3200;
import com.h3.hrm3200.emul.HRM3200;

import java.util.ArrayList;


public class DeviceScanActivity extends Activity {
    private LeDeviceListAdapter mLeDeviceListAdapterForScannedDevices;
    private LeDeviceListAdapter mLeDeviceListAdapterForRegisteredDevices;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private final int REQUEST_ENABLE_BT = 1;

    public final static String DEVICE_NAME = "device name";
    public final static String DEVICE_ADDRESS = "device address";

    public final static String AutoScanningAction = "android.intent.action.AUTO_SCANNING";

    public final static String prefName = "DeviceScanActivityPref";


    public final static String TAG = "DeviceScanActivity";

    private ListView listViewScannedDevices;
    private ListView listViewRegisteredDevices;

    private ImageButton cancelButton;
    private ImageButton rescanButton;

    private boolean pressToConnect = false;
    private boolean autoStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);

        listViewScannedDevices = (ListView) findViewById(R.id.listView);
        listViewRegisteredDevices = (ListView) findViewById(R.id.listView2);
        cancelButton = (ImageButton) findViewById(R.id.imageButton9);
        rescanButton = (ImageButton) findViewById(R.id.imageButton10);

        mHandler = new Handler();

        // For Mocking:
//        // Initialize BluetoothAdapter
//        final BluetoothManager btmngr =
//                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothManager btmngr =
                (BluetoothManager) new BluetoothManager(new AutoHRM3200());
        mBluetoothAdapter = btmngr.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        listViewScannedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Connecting a BLE device
                mDeviceName = mLeDeviceListAdapterForScannedDevices.getDeviceName(position);
                mDeviceAddress = mLeDeviceListAdapterForScannedDevices.getDeviceAddress(position);

//                Intent gattServiceIntent = new Intent(DeviceScanActivity.this, BluetoothLeService.class);
//                gattServiceIntent.putExtra(DEVICE_NAME, mDeviceName);
//                gattServiceIntent.putExtra(DEVICE_ADDRESS, mDeviceAddress);

                SharedPreferences pref = getSharedPreferences(MainActivity.prefName, 0);
                int connectionStatus = pref.getInt(MainActivity.connFlag, StartButtonView.NO_DEVICE);

                // If not press a button to connect just before and not connected yet
                if (pressToConnect == false && (connectionStatus == StartButtonView.NO_DEVICE)) {
//                    mServiceConnection = new MyServiceConnection();
//                    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

                    Intent intent = new Intent(DeviceScanActivity.this, BluetoothLeService.class);
                    intent.setAction(BluetoothLeService.ACTION_CONNECT);
                    intent.putExtra(BluetoothLeService.CONNECT_ADDRESS_KEY, mDeviceAddress);
//                    intent.putExtra(BluetoothLeService.STORED_DATA_REQUEST_KEY, false);
                    startService(intent);

                    pressToConnect = true;

                    DeviceScanActivity.this.finish();

                    Log.i(TAG, "A bluetooth device is selected to connect...");
                }
//                } else {
//                    if(statusBLEService) { // TODO: Need to be clarified!!
//                        unbindService(mServiceConnection);
//                        pressToConnect = false;
//                    }
//
//                    Log.i(TAG, "A bluetooth device is selected to disconnect...");
//
//                }

            }
        });


        listViewRegisteredDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Connecting a BLE device
                mDeviceName = mLeDeviceListAdapterForRegisteredDevices.getDeviceName(position);
                mDeviceAddress = mLeDeviceListAdapterForRegisteredDevices.getDeviceAddress(position);

//                Intent gattServiceIntent = new Intent(DeviceScanActivity.this, BluetoothLeService.class);
//                gattServiceIntent.putExtra(DEVICE_NAME, mDeviceName);
//                gattServiceIntent.putExtra(DEVICE_ADDRESS, mDeviceAddress);

                SharedPreferences pref = getSharedPreferences(MainActivity.prefName, 0);
                int connectionStatus = pref.getInt(MainActivity.connFlag, StartButtonView.NO_DEVICE);

                // If we have no connection now
                if (pressToConnect==false && connectionStatus == StartButtonView.NO_DEVICE) {
//                    mServiceConnection = new MyServiceConnection();
//                    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

                    Intent intent = new Intent(DeviceScanActivity.this, BluetoothLeService.class);
                    intent.setAction(BluetoothLeService.ACTION_CONNECT);
                    intent.putExtra(BluetoothLeService.CONNECT_ADDRESS_KEY, mDeviceAddress);
//                    intent.putExtra(BluetoothLeService.STORED_DATA_REQUEST_KEY, false);
                    startService(intent);

                    DeviceScanActivity.this.finish();

                    Log.i(TAG, "A bluetooth device is selected to connect (from registered list)...");
                }

            }
        });

        listViewRegisteredDevices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                final long index = id;
                AlertDialog.Builder builder = new AlertDialog.Builder(DeviceScanActivity.this);
                builder.setTitle("Device unregistration")
                       .setMessage("Do you want to delete this device?")
                       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                               SharedPreferences pref = getSharedPreferences(DeviceScanActivity.prefName, 0);

                               ArrayList<MyDevice> list = new ArrayList<MyDevice>();

                               SharedPreferences.Editor editor = pref.edit();
                               editor.clear();

                               for (int i = 0; i < mLeDeviceListAdapterForRegisteredDevices.getCount(); i++) {
                                   if (i != (int) index) {
                                       list.add(mLeDeviceListAdapterForRegisteredDevices.getDevice(i));
                                   }
                               }

                               mLeDeviceListAdapterForRegisteredDevices.clear();

                               int i = 0;
                               for (MyDevice device : list) {
                                   editor.putString(device.getAddress(), device.getName());
                                   editor.putString(Integer.toString(i), device.getAddress());

                                   mLeDeviceListAdapterForRegisteredDevices.addDevice(device);
                                   i = i + 1;
                               }

                               editor.commit();
                               mLeDeviceListAdapterForRegisteredDevices.notifyDataSetChanged();

                               Log.i(TAG, "Request to delete this device");
                           }
                       })
                       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                           }
                       })
                       .setCancelable(false)
                       .show();


//                SharedPreferences pref = getSharedPreferences(DeviceScanActivity.prefName, 0);
//
//                ArrayList<MyDevice> list = new ArrayList<MyDevice>();
//
//                SharedPreferences.Editor editor = pref.edit();
//                editor.clear();
//
//                for (int i = 0; i < mLeDeviceListAdapterForRegisteredDevices.getCount(); i++) {
//                    if (i != (int) id) {
//                        list.add(mLeDeviceListAdapterForRegisteredDevices.getDevice(i));
//                    }
//                }
//
//                mLeDeviceListAdapterForRegisteredDevices.clear();
//
//                int i = 0;
//                for (MyDevice device : list) {
//                    editor.putString(device.getAddress(), device.getName());
//                    editor.putString(Integer.toString(i), device.getAddress());
//
//                    mLeDeviceListAdapterForRegisteredDevices.addDevice(device);
//                    i = i + 1;
//                }
//
//                editor.commit();
//                mLeDeviceListAdapterForRegisteredDevices.notifyDataSetChanged();

                return true;
            }
        });

        Intent intent = getIntent();

        if (intent != null && AutoScanningAction.equals(intent.getAction())) {
            autoStart = true;
            scanLeDevice(true);
        }
    }

    public void onClick(View view) {
        if (view == rescanButton) {
            scanLeDevice(true);
        } else if (view==cancelButton) {
            finish();
        } else {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initializes list view adapter.
        // For Scanned Devices (Empty)
        mLeDeviceListAdapterForScannedDevices = new LeDeviceListAdapter();
        listViewScannedDevices.setAdapter(mLeDeviceListAdapterForScannedDevices);


        // For Registered Devices
        setRegisteredDevicesFromPreference();
        listViewRegisteredDevices.setAdapter(mLeDeviceListAdapterForRegisteredDevices);

        IntentFilter intentFilter = new IntentFilter();

//        intentFilter.addAction(MainActivity.ACTION_PRESS_START_BUTTON);

        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);

        registerReceiver(deviceScanActivityReceiver, intentFilter);

        if(autoStart) {
            scanLeDevice(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(deviceScanActivityReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        if (statusBLEService)
//            (mServiceConnection);
//
//        mBluetoothLeService = null;
    }

    private static final long SCAN_PERIOD = 5000;

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    //mBluetoothLeScanner.stopScan(mScanCallback);
                    Toast.makeText(DeviceScanActivity.this, "Scan completed", Toast.LENGTH_SHORT).show();
                    Log.i("DeviceScanActivity", "Scanning ends...");
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            //mBluetoothLeScanner.startScan(mScanCallback);
            Toast.makeText(DeviceScanActivity.this, "Scanning...", Toast.LENGTH_SHORT).show();
            Log.i("DeviceScanActivity", "Scanning starts...");


        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            //mBluetoothLeScanner.stopScan(mScanCallback);
            Toast.makeText(DeviceScanActivity.this, "Scan completed", Toast.LENGTH_SHORT).show();
            Log.i("DeviceScanActivity", "Scanning ends...");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == android.R.id.home)
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class MyDevice {
        private String address;
        private String name;

        public MyDevice(String address, String name) {
            this.address = address;
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public String getName() {
            return name;
        }
    }



    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<MyDevice> mLeDevices;
        private LayoutInflater mInflator;


        private DataSetObservable dataSetObservable = new DataSetObservable();

        private DataSetObservable getDataSetObservable() {
            return dataSetObservable;
        }

        public void notifyDataSetChanged() {
            this.getDataSetObservable().notifyChanged();
        }
        public void notifyDataSetInvalidated() {
            this.getDataSetObservable().notifyInvalidated();
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            this.getDataSetObservable().registerObserver(observer);

        }
        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            this.getDataSetObservable().unregisterObserver(observer);

        }


        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<MyDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(MyDevice device) {
            boolean flag = true;

            for (MyDevice d : mLeDevices) {
                if (d.getAddress().equals(device.getAddress())) {
                    flag = false;
                }
            }
            if (flag) {
                mLeDevices.add(device);
            }
        }

        public MyDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public String getDeviceName(int position) {
            return mLeDevices.get(position).getName();
        }

        public String getDeviceAddress(int position) {
            return mLeDevices.get(position).getAddress();
        }


        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            MyDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }

    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    Log.i("DeviceScanActivity", "onLeScan is called...");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLeDeviceListAdapterForScannedDevices.addDevice(new MyDevice(device.getAddress(), device.getName()));
                            mLeDeviceListAdapterForScannedDevices.notifyDataSetChanged();
                        }
                    });
                }
            };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }


    // Connection

    private String mDeviceAddress;
    private String mDeviceName;


    // Receiver
    DeviceScanActivityReceiver deviceScanActivityReceiver = new DeviceScanActivityReceiver();

    class DeviceScanActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive : " + intent.getAction());

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(intent.getAction())) {
                Log.i(TAG, "ACTION_GATT_CONNECTTED");

                if (autoStart) {
                    // Finish with no UI job
                    finish();
                }
                else {
                    // UI Job to update the registered device list
                    mLeDeviceListAdapterForRegisteredDevices.clear();
                    setRegisteredDevicesFromPreference();
                    mLeDeviceListAdapterForRegisteredDevices.notifyDataSetChanged();
                }

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(intent.getAction())) {

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(intent.getAction())) {
                pressToConnect = false;

            } else {
                Log.i(TAG, "DeviceScanActivityReceiver.onReceive : no action is matched.");
            }
        }

    }

    //
    private void setRegisteredDevicesFromPreference() {
        SharedPreferences pref = getSharedPreferences(DeviceScanActivity.prefName, 0);
        mLeDeviceListAdapterForRegisteredDevices = new LeDeviceListAdapter();

        int i = 0;
        while (true) {
            String address = pref.getString(Integer.toString(i), "");
            String name = pref.getString(address, "");
            if (address == null || address == "") {
                break;
            } else {
                mLeDeviceListAdapterForRegisteredDevices.addDevice(new MyDevice(address, name));
            }
            i = i + 1;
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
                                    Intent intent = new Intent();
                                    intent.setAction(MainActivity.ACTION_FINISH_ACTIVITY);
                                    setResult(RESULT_OK, intent);
                                    finish();
//                                    finishAffinity();
                                }
                            }).setNegativeButton( "No", null ).show();
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
