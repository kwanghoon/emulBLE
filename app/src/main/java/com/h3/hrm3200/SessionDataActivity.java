package com.h3.hrm3200;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
//import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.zip.Inflater;


public class SessionDataActivity extends Activity {

    // db
    private HeartRateDBHelper dbHelper;
    private SQLiteDatabase db;

    private ExpandableListView expandableListView;

    private LayoutInflater inflater;


    final static String SESSION_ID_KEY = "session id";
    final static String DURATION_KEY = "duration";
    final static String DISTANCE_KEY = "distance";
    final static String CALORIES_KEY = "calories";
    final static String HR_AVG_KEY = "hr avg";
    final static String HR_MAX_KEY = "hr max";
    final static String HR_MIN_KEY = "hr min";
    final static String RED_ZONE_KEY = "red zone";
    final static String YELLOW_ZONE_KEY = "yellow zone";
    final static String GREEN_ZONE_KEY = "green zone";
    final static String CYAN_ZONE_KEY = "cyan zone";
    final static String BLUE_ZONE_KEY = "blue zone";
    final static String SESSION_TIME_KEY = "session time";

    private ImageButton buttonDelete;
    private ImageButton buttonGoogleSync;


    public String[] check_string_array;

    // Google Sync account management
    private static final int REQUEST_OAUTH = 1001;

    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;

    private GoogleApiClient mClient = null;

    private Boolean canDeleteFlag = true; // false => Cannot Delete, true => Can Delete

    //
    private final String TAG = "SessionDataActivity";
    private static final String TIME_FORMAT = "HH:mm:ss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_data);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);


        buttonDelete = (ImageButton)findViewById(R.id.imageBtnSDDelete);
        buttonGoogleSync = (ImageButton)findViewById(R.id.imageBtnSDGooglesync);


        dbHelper = new HeartRateDBHelper(this, null, 1);
        db = dbHelper.getWritableDatabase();

        expandableListAdapter = new MyExpandableListAdapter();

        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        expandableListView = (ExpandableListView)findViewById(R.id.expandableListView);
        expandableListView.setAdapter(expandableListAdapter);
//        expandableListView.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);


//        boolean isModeDelete = true;

//        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//
//                parent.setItemChecked(groupPosition, isModeDelete);
//                return isModeDelete;
////                Log.i("SessionDataActivity", "onGroupClick:");
////                return false;
//            }
//        });





        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (childPosition == SessionData.NUM_OF_SESSION_ATTRIBUTES) {
                    // Details is selected! ==> Graph activity
                    SessionData sessionData = (SessionData)expandableListAdapter.getGroup(groupPosition);
                    int sessionid = sessionData.getSessionId();
//                    String duration = sessionData.getDuration();
//                    String distance = sessionData.getDistance();
//                    String hr_avg_str = sessionData.getHeart_rate_average();
//                    String calories = sessionData.getCalories();
//                    int hr_max = sessionData.getIntHeart_rate_max();
//                    int hr_min = sessionData.getIntHeart_rate_min();
//                    String sessionTime =
//                            sessionData.getYear() + "." + sessionData.getMonth() + "." + sessionData.getDay()
//                            + " "
//                            + sessionData.getHour() + ":" + sessionData.getMin() + ":" + sessionData.getSec();

                    Intent intent = new Intent(SessionDataActivity.this, GraphActivity.class);
                    intent.putExtra(SESSION_ID_KEY, sessionid);
//                    intent.putExtra(DURATION_KEY, duration);
//                    intent.putExtra(DISTANCE_KEY, distance);
//                    intent.putExtra(CALORIES_KEY, calories);
//                    intent.putExtra(HR_AVG_KEY, hr_avg_str);
//                    intent.putExtra(HR_MAX_KEY, hr_max);
//                    intent.putExtra(HR_MIN_KEY, hr_min);
//                    intent.putExtra(RED_ZONE_KEY, sessionData.getRedZoneTime());
//                    intent.putExtra(YELLOW_ZONE_KEY, sessionData.getYellowZoneTime());
//                    intent.putExtra(GREEN_ZONE_KEY, sessionData.getGreenZoneTime());
//                    intent.putExtra(CYAN_ZONE_KEY, sessionData.getCyanZoneTime());
//                    intent.putExtra(BLUE_ZONE_KEY, sessionData.getBlueZoneTime());
//                    intent.putExtra(SESSION_TIME_KEY, sessionTime);

                    startActivityForResult(intent, 0);
                    return true;
                }
                return false;
            }
        });


        // Google Sync
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (mClient!=null && mClient.isConnected()) {
//            mClient.disconnect();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        db.close();
        dbHelper.close();
    }

    private MyExpandableListAdapter expandableListAdapter;

    class MyExpandableListAdapter extends BaseExpandableListAdapter implements ExpandableListAdapter {

        private ArrayList<SessionData> arrayList;

        HashMap<Long,Boolean> checkboxMap = new HashMap<Long,Boolean>();

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



        public MyExpandableListAdapter() {

            arrayList = new ArrayList<SessionData>();

            Cursor cursor = db.rawQuery("SELECT * FROM " + HeartRateDBHelper.TABLE_NAME_SESSION
                                + " ORDER BY " + HeartRateDBHelper.COL_YEAR + " DESC, "
                                               + HeartRateDBHelper.COL_MONTH + " DESC, "
                                               + HeartRateDBHelper.COL_DAY + " DESC, "
                                               + HeartRateDBHelper.COL_HOUR + " DESC, "
                                               + HeartRateDBHelper.COL_MIN + " DESC, "
                                               + HeartRateDBHelper.COL_SEC + " DESC ", null);

            int count = cursor.getCount();
            cursor.moveToFirst();
            while(count > 0) {
                SharedPreferences userinfoPref = getSharedPreferences(UserSettingActivity.prefName, 0);
                int userStride = userinfoPref.getInt(UserSettingActivity.keyStride, 0);
                int period = userinfoPref.getInt(UserSettingActivity.keyPeriod, 10);

                SessionData sessionData = new SessionData(userStride, period); // count를 사용하지 않음 UI 용도


                sessionData.putSessionData(
                        cursor.getInt(0),
                        cursor.getInt(1),cursor.getInt(2),cursor.getInt(3),
                        cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),
                        cursor.getInt(7),cursor.getDouble(8),cursor.getInt(9),
                        cursor.getInt(10),cursor.getInt(11),cursor.getInt(12),
                        cursor.getInt(13),cursor.getInt(14),cursor.getInt(15),
                        cursor.getInt(16),cursor.getInt(17)
                );

                arrayList.add(sessionData);

                cursor.moveToNext();
                count = count - 1;
            }
            cursor.close();


            clearChoice();
        }

        public void clearChoice() {
            int data_size = arrayList.size();
//            Log.i("MyExpandableListAdapter", "clearCheck() arrayList.size:" + data_size);
            check_string_array = null;
            checkboxMap.clear();
            //checkboxMap = new HashMap<Long,Boolean>();

            check_string_array = new String[data_size];
            for(int i=0; i<data_size; i++) {
                checkboxMap.put((long) i, false);
//                Log.i("MyExpandableListAdapter", "clearCheck() checkboxMap:" + checkboxMap.get((long)i));
                check_string_array[i] = "false";
            }
        }

        public class CheckListener implements OnCheckedChangeListener {

            long pos;

            public void setPosition(long p){
                pos = p;
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
//                Log.i("checkListenerChanged", String.valueOf(pos)+":"+String.valueOf(isChecked));
                checkboxMap.put(pos, isChecked);
                if(isChecked == true)
                    check_string_array[(int)pos] = "true";
                else
                    check_string_array[(int)pos] = "false";

            }
        }

        public void clear() {
            arrayList.clear();
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            this.getDataSetObservable().registerObserver(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            this.getDataSetObservable().unregisterObserver(observer);
        }

        @Override
        public int getGroupCount() {
            return arrayList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return SessionData.NUM_OF_SESSION_ATTRIBUTES + 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return arrayList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if (childPosition < SessionData.NUM_OF_SESSION_ATTRIBUTES) {
                return arrayList.get(groupPosition).getSessionAttribute(childPosition);
            } else {
                return "DETAILS";
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return arrayList.get(groupPosition).getSessionId();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            if (convertView==null) {
                convertView = inflater.inflate(R.layout.listitem_session_data1, parent, false);
            }

            TextView tv = (TextView) convertView.findViewById(R.id.time);
            CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBoxGroup);

            SessionData sd = arrayList.get(groupPosition);
            tv.setText(sd.getYear() + "." + sd.getMonth() + "." + sd.getDay()
                        + " "
                        + sd.getHour() + ":" + sd.getMin() + ":" + sd.getSec());


            if (groupPosition <= arrayList.size()-1) {
                cb.setFocusable(false);
                CheckListener checkL = new CheckListener();
                checkL.setPosition(groupPosition);
                cb.setOnCheckedChangeListener(checkL);
//                Log.i("SessionDataActivity", "groupPosition:" + groupPosition);
                cb.setChecked(checkboxMap.get((long)groupPosition));
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            if(convertView==null) {
                if (childPosition < SessionData.NUM_OF_SESSION_ATTRIBUTES) {
                    convertView = inflater.inflate(R.layout.listitem_session_data_detail1, parent, false);
                } else {
                    convertView = inflater.inflate(R.layout.listitem_session_data_detail2, parent, false);
                }
            }

            if (childPosition < SessionData.NUM_OF_SESSION_ATTRIBUTES) {
                TextView tvTitle = (TextView) convertView.findViewById(R.id.sessionDetailTitle);
                TextView tvData = (TextView) convertView.findViewById(R.id.sessionDetailData);

                if (tvTitle == null || tvData == null) {
                    convertView = inflater.inflate(R.layout.listitem_session_data_detail1, parent, false);

                    tvTitle = (TextView) convertView.findViewById(R.id.sessionDetailTitle);
                    tvData = (TextView) convertView.findViewById(R.id.sessionDetailData);
                }

                tvTitle.setText(arrayList.get(groupPosition).getSessionAttributeTitle(childPosition));
                tvData.setText(arrayList.get(groupPosition).getSessionAttributeString(childPosition));
            } else {
                TextView tv = (TextView) convertView.findViewById(R.id.detail);

                if (tv == null) {
                    convertView = inflater.inflate(R.layout.listitem_session_data_detail2, parent, false);

                    tv = (TextView) convertView.findViewById(R.id.detail);
                }
                tv.setText("DETAILS");
            }

            // HR_avg,max,min child view의 배경색
//            if (childPosition == 2) {
//                convertView.setBackgroundColor(Color.BLUE);
//            } else if (childPosition == 3) {
//                convertView.setBackgroundColor(Color.GREEN);
//            } else if (childPosition == 4) {
//                convertView.setBackgroundColor(Color.RED);
//            } else {
//                convertView.setBackgroundColor(Color.TRANSPARENT);
//            }
            //


            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void onGroupExpanded(int groupPosition) {
        }

        @Override
        public void onGroupCollapsed(int groupPosition) {

        }

        @Override
        public long getCombinedChildId(long groupId, long childId) {
            return 0;
        }

        @Override
        public long getCombinedGroupId(long groupId) {
            return 0;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_session_data, menu);
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
            expandableListAdapter.clear();

            db.execSQL("DELETE FROM " + HeartRateDBHelper.TABLE_NAME_SESSION);
            db.execSQL("DELETE FROM " + HeartRateDBHelper.TABLE_NAME_HR);

            expandableListAdapter.notifyDataSetChanged();

            return true;
        }
        else if (id == android.R.id.home)
        {
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        else if (id == R.id.action_gen_csv) {
            generate_csv_file();
        }
        else if (id == R.id.action_merge_sessions) {
            merge_sessions();
        }
        else if (id == R.id.action_read_googlefit_upload_data) {
            Intent intent = new Intent();
            intent.setClassName("com.h3.hrm3200", "com.h3.hrm3200.GoogleFitActivity");
            startActivityForResult(intent, 0);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        if(v == buttonDelete) {

            if (canDeleteFlag) {
                String buffer = null;
                String output_String = "";
//            Log.i("onClick", "check_string_array.length:" + check_string_array.length);
                for (int i = check_string_array.length - 1; i >= 0; i--) {
//                Log.i("onClick", "check_string_array[i]:" + check_string_array[i]);
                    buffer = check_string_array[i];
                    if (buffer.equals("true")) {
                        db.execSQL("DELETE FROM " + HeartRateDBHelper.TABLE_NAME_SESSION
                                + " WHERE " + HeartRateDBHelper.COL_ID + " = "
                                + expandableListAdapter.getGroupId(i));
                        db.execSQL("DELETE FROM " + HeartRateDBHelper.TABLE_NAME_HR
                                + " WHERE " + HeartRateDBHelper.COL_SESSION_ID + " = "
                                + expandableListAdapter.getGroupId(i));

                        expandableListAdapter.arrayList.remove(i);
                        output_String += "group " + i + " ";
                    }
                }
                expandableListAdapter.clearChoice();
                expandableListAdapter.notifyDataSetChanged();

                output_String += "is checked";
//            Toast.makeText(this, output_String, Toast.LENGTH_SHORT).show();

                Log.i(TAG, "onClick() Delete: " + output_String);
            }
            else {
                Toast.makeText(this, "Cannot delete sessions on uploading to Goolge Fit", Toast.LENGTH_SHORT).show();
            }

        }
        else if (v==buttonGoogleSync) {
//            Toast.makeText(this, "Feature Disabled...(On Debugging)", Toast.LENGTH_SHORT).show();

            ArrayList<Integer> sessionIds = new ArrayList<Integer>();

            for (int i = check_string_array.length-1; i>=0; i--) {
                if ("true".equals(check_string_array[i])) {
                    sessionIds.add((int) expandableListAdapter.getGroupId(i));
                }
            }

            buildFitnessClient(sessionIds);

//            GoogleFitSync googleFitSync = new GoogleFitSync(this, sessionIds);
            //googleFitSync.uploadSessions(this, sessionIds);
        }
    }


    // Merge
    private boolean flag_do_merge = false;
    private Handler mergeHandler;
    private static final int TIME_GAP = 10; // 두 세션의 merge 가능한 시간 간격 10분


    private void merge_sessions() {

        final ProgressDialog progressDialog = new ProgressDialog(SessionDataActivity.this);

        if (flag_do_merge == false) {
            flag_do_merge = true;

            mergeHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    if (msg.what == 1) {

                        progressDialog.setTitle("Merging");
                        progressDialog.setMessage("Wait...");
                        progressDialog.setCancelable(false);
                        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                Toast.makeText(SessionDataActivity.this, "Not cancelable (On debugging)", Toast.LENGTH_SHORT).show();
                            }
                        });
                        progressDialog.show();

                    } else {

                        if ((progressDialog != null) && (progressDialog.isShowing())) {
                            progressDialog.dismiss();
                        }

                        // Merge 완료 대화상자 띄우기
                        new AlertDialog.Builder(SessionDataActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage("The two sessions have been merged.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        for (int i = check_string_array.length - 1; i >= 0; i--) {
                                            if ("true".equals(check_string_array[i])) {
                                                expandableListAdapter.arrayList.remove(i);
                                                Log.i(TAG, "Merge/Remove session index: " + i);
                                            }
                                        }

                                        expandableListAdapter.clearChoice();
                                        expandableListAdapter.notifyDataSetChanged();
                                        expandableListAdapter = new MyExpandableListAdapter();
                                        expandableListView.setAdapter(expandableListAdapter);
                                    }
                                }).show();
                    }

                }
            };


            class MyThread extends Thread {
                private Context context;
                private Handler handler;
                public MyThread(Context context, Handler handler) {
                    this.context = context;
                    this.handler = handler;
                }

                @Override
                public void run() {
                    doRun();
                    flag_do_merge = false;
                }


                public void doRun() {

                    ArrayList<Integer> sessionIds = new ArrayList<Integer>();
                    ArrayList<Integer> indexList = new ArrayList<Integer>();

                    for (int i = check_string_array.length - 1; i >= 0; i--) {
                        if ("true".equals(check_string_array[i])) {
                            sessionIds.add((int) expandableListAdapter.getGroupId(i));
                            indexList.add(i);
                        }
                    }

                    if (sessionIds.size() != 2) {
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "More than two sessions are chosen.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }

                    Cursor cursor = db.rawQuery("SELECT * FROM " + HeartRateDBHelper.TABLE_NAME_SESSION
                            + " WHERE " + HeartRateDBHelper.COL_ID + " = " + sessionIds.get(0), null);


                    cursor.moveToFirst();
                    int session1Id = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_ID));
                    int session1Year = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_YEAR));
                    int session1Month = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_MONTH));
                    int session1Day = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_DAY));
                    int session1Hour = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_HOUR));
                    int session1Min = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_MIN));
                    int session1Sec = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_SEC));
                    int duration1 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_DURATION));
                    double distance1 = cursor.getDouble(cursor.getColumnIndex(HeartRateDBHelper.COL_DISTANCE));
                    int calories1 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_CALORIES));
                    int hr_avg1 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_HR_AVG));
                    int hr_min1 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_HR_MIN));
                    int hr_max1 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_HR_MAX));
                    int red_zone1 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_TIME_RED));
                    int yellow_zone1 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_TIME_YELLOW));
                    int green_zone1 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_TIME_GREEN));
                    int cyan_zone1 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_TIME_CYAN));
                    int blue_zone1 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_TIME_BLUE));
                    int num_of_data1 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_COUNT));
                    int period1 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_PERIOD));
                    int stride1 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_STRIDE));

                    cursor.close();


                    cursor = db.rawQuery("SELECT * FROM " + HeartRateDBHelper.TABLE_NAME_SESSION
                            + " WHERE " + HeartRateDBHelper.COL_ID + " = " + sessionIds.get(1), null);

                    cursor.moveToFirst();
                    int session2Id = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_ID));
                    int session2Year = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_YEAR));
                    int session2Month = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_MONTH));
                    int session2Day = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_DAY));
                    int session2Hour = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_HOUR));
                    int session2Min = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_MIN));
                    int session2Sec = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_SEC));
                    int duration2 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_DURATION));
                    double distance2 = cursor.getDouble(cursor.getColumnIndex(HeartRateDBHelper.COL_DISTANCE));
                    int calories2 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_CALORIES));
                    int hr_avg2 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_HR_AVG));
                    int hr_min2 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_HR_MIN));
                    int hr_max2 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_HR_MAX));
                    int red_zone2 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_TIME_RED));
                    int yellow_zone2 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_TIME_YELLOW));
                    int green_zone2 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_TIME_GREEN));
                    int cyan_zone2 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_TIME_CYAN));
                    int blue_zone2 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_TIME_BLUE));
                    int num_of_data2 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_COUNT));
                    int period2 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_PERIOD));
                    int stride2 = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_STRIDE));

                    cursor.close();


                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.set(session1Year, session1Month - 1, session1Day, session1Hour, session1Min, session1Sec); // Calendar의 month는 0부터 시작하므로 month - 1
                    long session1StartTime = calendar1.getTimeInMillis();
                    long session1EndTime = session1StartTime + duration1 * 1000;

                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.set(session2Year, session2Month - 1, session2Day, session2Hour, session2Min, session2Sec);
                    long session2StartTime = calendar2.getTimeInMillis();

                    // 1. 두 세션이 시간적으로 연속적인지 검사
                    if (session1StartTime > session2StartTime
                            || session1EndTime > session2StartTime
                            || session2StartTime - session1EndTime > TIME_GAP * 60 * 1000)
                    {
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "One session does not immediately follow the other session.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }


                    // 2. 두 세션이 칼로리와 스텝이 연속적인지 검사
                    Cursor cursor1 = db.rawQuery("SELECT * FROM " + HeartRateDBHelper.TABLE_NAME_HR
                            + " WHERE " + HeartRateDBHelper.COL_SESSION_ID + " = " + session1Id
                            + " ORDER BY " + HeartRateDBHelper.COL_HR_INDEX + " ASC ", null);

                    cursor1.moveToLast();
                    int calorie1 = cursor1.getInt(cursor1.getColumnIndex(HeartRateDBHelper.COL_CALORIE));
                    int step1 = cursor1.getInt(cursor1.getColumnIndex(HeartRateDBHelper.COL_STEP));

                    Cursor cursor2 = db.rawQuery("SELECT * FROM " + HeartRateDBHelper.TABLE_NAME_HR
                            + " WHERE " + HeartRateDBHelper.COL_SESSION_ID + " = " + session2Id
                            + " ORDER BY " + HeartRateDBHelper.COL_HR_INDEX + " ASC ", null);

                    cursor2.moveToFirst();
                    int calorie2 = cursor2.getInt(cursor2.getColumnIndex(HeartRateDBHelper.COL_CALORIE));
                    int step2 = cursor2.getInt(cursor2.getColumnIndex(HeartRateDBHelper.COL_STEP));

                    if (cursor1.getCount() <= 0  || cursor2.getCount() <= 0 || calorie1 > calorie2 || step1 > step2) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "One session does not immediately follow the other session.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        cursor1.close();
                        cursor2.close();

                        return;
                    }


                    handler.sendEmptyMessage(1); // Merge Start

                    int max_period = (period1>=period2) ? period1 : period2;

                    SessionData mergedSession = new SessionData(stride1, max_period);

//                    int time_elapsed = 0;
                    int time_elapsed = max_period; // 최초 데이타는 무조건 추가하기 위해


                    // HeartRate DB1 update
                    int new_index = -1;

                    int count1 = cursor1.getCount();
                    cursor1.moveToFirst();

                    int old_hr_index = -1;
                    int hr_index = 0;

                    while (count1 > 0) {
                        int hr_id = cursor1.getInt(cursor1.getColumnIndex(HeartRateDBHelper.COL_ID));
                        int heart_rate = cursor1.getInt(cursor1.getColumnIndex(HeartRateDBHelper.COL_HEARTRATE));
                        int act_level = cursor1.getInt(cursor1.getColumnIndex(HeartRateDBHelper.COL_ACTLEVEL));
                        int calorie = cursor1.getInt(cursor1.getColumnIndex(HeartRateDBHelper.COL_CALORIE));
                        int step = cursor1.getInt(cursor1.getColumnIndex(HeartRateDBHelper.COL_STEP));
                        int hrstatus = cursor1.getInt(cursor1.getColumnIndex(HeartRateDBHelper.COL_HRSTATUS));
                        hr_index = cursor1.getInt(cursor1.getColumnIndex(HeartRateDBHelper.COL_HR_INDEX));

                        int delta_index = hr_index - old_hr_index;
                        time_elapsed = time_elapsed + period1 * delta_index;
                        old_hr_index = hr_index;

                        if (time_elapsed >= max_period) {
                            new_index = new_index + delta_index;

                            mergedSession.putData(context, heart_rate, act_level, calorie, step, hrstatus, new_index);

                            time_elapsed = 0;
                        } else {
                            // do nothing
                        }

                        cursor1.moveToNext();
                        count1 = count1 - 1;
                    }

                    cursor1.close();

                    long gap = (session2StartTime - session1EndTime) / 1000; // gap은 초단위
//                    int index_gap = hr_index + (int)(gap / max_period) + 1; // 앞 세션 인덱스 다음 인덱스 + 세션 간 시간 간격에 의한 index_gap
                    new_index = new_index + (int)(gap / max_period + 0.5);      // 다음 세션 인덱스의 시작(앞 세션 인덱스 다음 인덱스 + 세션 간 시간 간격)
                    int index_gap = new_index;                                // index_gap


                    time_elapsed = 0; // 초기값

                    // HeartRate DB2 update
                    int count2 = cursor2.getCount();
                    cursor2.moveToFirst();

                    old_hr_index = -1 + index_gap;

                    while (count2 > 0) {
                        int hr_id = cursor2.getInt(cursor2.getColumnIndex(HeartRateDBHelper.COL_ID));
                        int heart_rate = cursor2.getInt(cursor2.getColumnIndex(HeartRateDBHelper.COL_HEARTRATE));
                        int act_level = cursor2.getInt(cursor2.getColumnIndex(HeartRateDBHelper.COL_ACTLEVEL));
                        int calorie = cursor2.getInt(cursor2.getColumnIndex(HeartRateDBHelper.COL_CALORIE));
                        int step = cursor2.getInt(cursor2.getColumnIndex(HeartRateDBHelper.COL_STEP));
                        int hrstatus = cursor2.getInt(cursor2.getColumnIndex(HeartRateDBHelper.COL_HRSTATUS));
                        hr_index = cursor2.getInt(cursor2.getColumnIndex(HeartRateDBHelper.COL_HR_INDEX));

                        hr_index = hr_index + index_gap; // 앞 세션과의 gap으로 인한 index를 반영

                        int delta_index = hr_index - old_hr_index;
                        time_elapsed = time_elapsed + period2 * delta_index;
                        old_hr_index = hr_index;

                        if (time_elapsed >= max_period) {
                            new_index = new_index + delta_index;

                            mergedSession.putData(context, heart_rate, act_level, calorie, step, hrstatus, new_index);

                            time_elapsed = 0;
                        } else {
                            // do nothing
                        }

                        cursor2.moveToNext();
                        count2 = count2 - 1;
                    }

                    cursor2.close();

                    // 시작 시간을 session1의 시간으로 설정
                    mergedSession.setYear(session1Year);
                    mergedSession.setMonth(session1Month);
                    mergedSession.setDay(session1Day);
                    mergedSession.setHour(session1Hour);
                    mergedSession.setMin(session1Min);
                    mergedSession.setSec(session1Sec);
                    mergedSession.setCount(mergedSession.getNumOfData()); // merge한 경우는 전체 데이타갯수가 변경됨

                    mergedSession.writeToDB(context);


                    // 세션 info와 해당 데이터를 모두 삭제
                    db.delete(HeartRateDBHelper.TABLE_NAME_SESSION, HeartRateDBHelper.COL_ID + " = " + session1Id, null);
                    db.delete(HeartRateDBHelper.TABLE_NAME_SESSION, HeartRateDBHelper.COL_ID + " = " + session2Id, null);

                    db.delete(HeartRateDBHelper.TABLE_NAME_HR, HeartRateDBHelper.COL_SESSION_ID + " = " + session1Id, null);
                    db.delete(HeartRateDBHelper.TABLE_NAME_HR, HeartRateDBHelper.COL_SESSION_ID + " = " + session2Id, null);

                    handler.sendEmptyMessage(0); // Merge End

                }
            }


            new MyThread(this, mergeHandler).start();

        }
    }


    private void generate_csv_file() {

        FileWriter fout = null;
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hrdata.csv";

        File file = new File(path);

        try {
            fout = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Uri fileUri = Uri.fromFile(file);


        for(int i=0; i<expandableListAdapter.getGroupCount(); i++) {
            if (check_string_array[i].equals("true")) {
                long id = expandableListAdapter.getGroupId(i);

                Cursor cursor = db.rawQuery("SELECT * FROM " + HeartRateDBHelper.TABLE_NAME_SESSION
                        + " WHERE " + HeartRateDBHelper.COL_ID + " = " + id, null);

                try {
                    fout.write("year,month,day,hour,min,sec,duration,distance,calories," +
                            "hr_avg,hr_min,hr_max," +
                            "red_zone,yellow_zone,green_zone,cyan_zone,blue_zone," +
                            "num_of_data,period,stride,session_id" + "\n");
                    fout.flush();
                } catch (IOException e) {
//                        e.printStackTrace();
                }

                long startTime = 0;
                int period = 1;

                int count = cursor.getCount();
                cursor.moveToFirst();
                while(count > 0) {
                    int sessionid   = cursor.getInt(0);
                    int year        = cursor.getInt(1);
                    int month       = cursor.getInt(2);
                    int day         = cursor.getInt(3);
                    int hour        = cursor.getInt(4);
                    int min         = cursor.getInt(5);
                    int sec         = cursor.getInt(6);
                    int duration    = cursor.getInt(7);
                    double distance = cursor.getDouble(8);
                    int calories    = cursor.getInt(9);
                    int hr_avg      = cursor.getInt(10);
                    int hr_min      = cursor.getInt(11);
                    int hr_max      = cursor.getInt(12);
                    int red_zone    = cursor.getInt(13);
                    int yellow_zone = cursor.getInt(14);
                    int green_zone  = cursor.getInt(15);
                    int cyan_zone   = cursor.getInt(16);
                    int blue_zone   = cursor.getInt(17);
                    int num_of_data = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_COUNT));
                    period      = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_PERIOD));
                    int stride      = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_STRIDE));

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month-1, day, hour, min, sec); // month - 1
                    startTime = calendar.getTimeInMillis();

                    String s = year + "," + month + "," + day + ","
                                + hour + "," + min + "," + sec + ","
                                + duration + "," + distance + "," + calories + ","
                                + hr_avg + "," + hr_min + "," + hr_max + ","
                                + red_zone + "," + yellow_zone + "," + green_zone + ","
                                + cyan_zone + "," + blue_zone + ","
                                + num_of_data + "," + period + "," + stride + ","
                                + sessionid + "\n";
                    try {
                        fout.write(s);
                        fout.flush();
                    } catch (IOException e) {
//                        e.printStackTrace();
                    }

                    cursor.moveToNext();
                    count = count - 1;
                }
                cursor.close();

                try {
                    fout.write("measuring_time,hr_index,heart_rate,act_level,calorie,step,hr_status,hr_id,session_id" + "\n");
                    fout.flush();
                } catch (IOException e) {
//                        e.printStackTrace();
                }

                cursor = db.rawQuery("SELECT * FROM " + HeartRateDBHelper.TABLE_NAME_HR
                        + " WHERE " + HeartRateDBHelper.COL_SESSION_ID + " = " + id
                        + " ORDER BY " + HeartRateDBHelper.COL_HR_INDEX + " ASC ", null);

                count = cursor.getCount();
                cursor.moveToFirst();
                while(count > 0) {
                    int hr_id       = cursor.getInt(0);
                    int heart_rate  = cursor.getInt(1);
                    int act_level   = cursor.getInt(2);
                    int calorie     = cursor.getInt(3);
                    int step        = cursor.getInt(4);
                    int hr_status   = cursor.getInt(5);
                    int hr_index    = cursor.getInt(6);
                    int session_id  = cursor.getInt(7);

                    long measuring_time = startTime + hr_index * period * 1000;
                    SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);

                    String s = timeFormat.format(measuring_time) + "," + hr_index + ","
                            + heart_rate + "," + act_level + "," + calorie + ","
                            + step + "," + hr_status + "," + hr_id + "," + session_id + "\n";

                    try {
                        fout.write(s);
                        fout.flush();
                    } catch (IOException e) {
//                        e.printStackTrace();
                    }

                    cursor.moveToNext();
                    count = count - 1;
                }
                cursor.close();
            }

        }
        try {
            fout.close();
        } catch (IOException e) {

        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");

        intent.putExtra(Intent.EXTRA_SUBJECT, "Session Data");
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        startActivity(intent);
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

    // Google Sync
    private void buildFitnessClient(ArrayList<Integer> sessionIds) {
        final ArrayList<Integer> _sessionIds = sessionIds;

        canDeleteFlag = false;
//        canGoogleSync = false;

        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
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
                                // Now you can make calls to the Fitness APIs.  What to do?
                                // Play with some sessions!!
                                new GoogleFitSync(SessionDataActivity.this, mClient, _sessionIds, canDeleteFlag).execute();
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
                                            SessionDataActivity.this, 0).show();
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
                                if (!authInProgress) {
                                    try {
                                        Log.i(TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(SessionDataActivity.this,
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

        mClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    mClient.connect();
                }
            }
        }
        else if (resultCode==RESULT_OK) {
            String action = data.getAction();
            if (action != null && MainActivity.ACTION_FINISH_ACTIVITY.equals(action)) {
                Intent intent = new Intent();
                intent.setAction(MainActivity.ACTION_FINISH_ACTIVITY);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}


