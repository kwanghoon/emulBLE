package com.h3.hrm3200;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
//import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;


public class GraphActivity extends Activity {

    private GraphView graphView;


    private HeartRateDBHelper dbHelper;
    private SQLiteDatabase db;


    private static String TAG = "GraphActivity";

    private TextView textViewSessionTime;
    private TextView textViewDuration;
    private TextView textViewDistance;
    private TextView textViewCalories;
    private TextView textViewHRAvg;
    private TextView textViewHRMax;
    private TextView textViewHRMin;

    private TextView textViewRed;
    private TextView textViewYellow;
    private TextView textViewGreen;
    private TextView textViewCyan;
    private TextView textViewBlue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);


        graphView = (GraphView)findViewById(R.id.graph);
        textViewSessionTime = (TextView)findViewById(R.id.textViewSessionKey);
        textViewDuration = (TextView)findViewById(R.id.textViewDuration);
        textViewDistance = (TextView)findViewById(R.id.textViewDistance);
        textViewCalories = (TextView)findViewById(R.id.textViewCalories);
        textViewHRAvg = (TextView)findViewById(R.id.textViewHRAvg);
        textViewHRMax = (TextView)findViewById(R.id.textViewHRMax);
        textViewHRMin = (TextView)findViewById(R.id.textViewHRMin);

        textViewRed = (TextView)findViewById(R.id.textViewRed);
        textViewYellow = (TextView)findViewById(R.id.textViewYellow);
        textViewGreen = (TextView)findViewById(R.id.textViewGreen);
        textViewCyan = (TextView)findViewById(R.id.textViewCyan);
        textViewBlue = (TextView)findViewById(R.id.textViewBlue);

        dbHelper = new HeartRateDBHelper(this, null, 1);
        db = dbHelper.getWritableDatabase();
    }

    public String getTimeString(int time) {
        int hour = time / (60 * 60);
        int min = (time - hour * 60 * 60) / 60;
        int sec = time - hour * 60 * 60 - min * 60;

        return hour + ":" + min + ":" + sec;
    }

    public String getDistance(double distance) {

        if (distance >= 0.1 ) {
            return String.format("%.1fkm", distance);
        } else {
            return String.format("%.1fm", (distance * 1000));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        int sessionid = getIntent().getIntExtra(SessionDataActivity.SESSION_ID_KEY, -1);
//        int hr_max = getIntent().getIntExtra(SessionDataActivity.HR_MAX_KEY, -1);
//        int hr_min = getIntent().getIntExtra(SessionDataActivity.HR_MIN_KEY, -1);
//        String sessiontime = getIntent().getStringExtra(SessionDataActivity.SESSION_TIME_KEY);
//
//        String duration = getIntent().getStringExtra(SessionDataActivity.DURATION_KEY);
//        String distance = getIntent().getStringExtra(SessionDataActivity.DISTANCE_KEY);
//        String calories = getIntent().getStringExtra(SessionDataActivity.CALORIES_KEY);
//
//        String redZone = getIntent().getStringExtra(SessionDataActivity.RED_ZONE_KEY);
//        String yellowZone = getIntent().getStringExtra(SessionDataActivity.YELLOW_ZONE_KEY);
//        String greenZone = getIntent().getStringExtra(SessionDataActivity.GREEN_ZONE_KEY);
//        String cyanZone = getIntent().getStringExtra(SessionDataActivity.CYAN_ZONE_KEY);
//        String blueZone = getIntent().getStringExtra(SessionDataActivity.BLUE_ZONE_KEY);
//
//        String hr_avg = getIntent().getStringExtra(SessionDataActivity.HR_AVG_KEY);

        Log.i(TAG, "Graph View session id : " + sessionid);


        if (sessionid != -1) {

            Cursor session_cursor = db.rawQuery("SELECT * FROM " + HeartRateDBHelper.TABLE_NAME_SESSION
                    + " WHERE " + HeartRateDBHelper.COL_ID + " = " + sessionid, null);

            if (session_cursor.getCount() > 0) {
                session_cursor.moveToFirst();

                int hr_max = session_cursor.getInt(session_cursor.getColumnIndex(HeartRateDBHelper.COL_HR_MAX));
                int hr_min = session_cursor.getInt(session_cursor.getColumnIndex(HeartRateDBHelper.COL_HR_MIN));
                String sessiontime = session_cursor.getInt(session_cursor.getColumnIndex(HeartRateDBHelper.COL_YEAR)) + "."
                        + session_cursor.getInt(session_cursor.getColumnIndex(HeartRateDBHelper.COL_MONTH)) + "."
                        + session_cursor.getInt(session_cursor.getColumnIndex(HeartRateDBHelper.COL_DAY)) + " "
                        + session_cursor.getInt(session_cursor.getColumnIndex(HeartRateDBHelper.COL_HOUR)) + ":"
                        + session_cursor.getInt(session_cursor.getColumnIndex(HeartRateDBHelper.COL_MIN)) + ":"
                        + session_cursor.getInt(session_cursor.getColumnIndex(HeartRateDBHelper.COL_SEC));

                String duration = getTimeString(session_cursor.getInt(session_cursor.getColumnIndex(HeartRateDBHelper.COL_DURATION)));
                String distance = getDistance(session_cursor.getDouble(session_cursor.getColumnIndex(HeartRateDBHelper.COL_DISTANCE)));
                String calories = session_cursor.getInt(session_cursor.getColumnIndex(HeartRateDBHelper.COL_CALORIES)) + "kcal";

                String redZone = getTimeString(session_cursor.getInt(session_cursor.getColumnIndex(HeartRateDBHelper.COL_TIME_RED)));
                String yellowZone = getTimeString(session_cursor.getInt(session_cursor.getColumnIndex(HeartRateDBHelper.COL_TIME_YELLOW)));
                String greenZone = getTimeString(session_cursor.getInt(session_cursor.getColumnIndex(HeartRateDBHelper.COL_TIME_GREEN)));
                String cyanZone = getTimeString(session_cursor.getInt(session_cursor.getColumnIndex(HeartRateDBHelper.COL_TIME_CYAN)));
                String blueZone = getTimeString(session_cursor.getInt(session_cursor.getColumnIndex(HeartRateDBHelper.COL_TIME_BLUE)));

                String hr_avg = session_cursor.getInt(session_cursor.getColumnIndex(HeartRateDBHelper.COL_HR_AVG)) + "bpm";

                session_cursor.close();


                Cursor cursor = db.rawQuery("SELECT " + HeartRateDBHelper.COL_HEARTRATE
                        + ", " + HeartRateDBHelper.COL_HR_INDEX
                        + " FROM " + HeartRateDBHelper.TABLE_NAME_HR
                        + " WHERE  " + HeartRateDBHelper.COL_SESSION_ID + " = " + sessionid
                        + " ORDER BY " + HeartRateDBHelper.COL_HR_INDEX + " ASC ", null);

                int count = cursor.getCount();


//            Log.i(TAG, "Graph View data count : " + count);

                ArrayList<DataPoint> arrayDataPoint = new ArrayList<DataPoint>();

                int old_index = -1;              // 손실 데이타 처리
                int x = 0;

                cursor.moveToFirst();
                while (count > 0) {
                    int heart_rate = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_HEARTRATE));
                    int index = cursor.getInt(cursor.getColumnIndex(HeartRateDBHelper.COL_HR_INDEX));

                    for (int i = old_index + 1; i < index; i++) {
//                    arrayDataPoint.add(new DataPoint(x, heart_rate));
                        x++;
                    }

                    arrayDataPoint.add(new DataPoint(x, heart_rate));
                    x++;

                    old_index = index;

//                Log.i(TAG, "XY = " + x + ", " + heart_rate);

                    cursor.moveToNext();
                    count = count - 1;
                }
                cursor.close();


                // set manual X bounds
                graphView.getViewport().setXAxisBoundsManual(true);
                graphView.getViewport().setMinX(0);
                graphView.getViewport().setMaxX(x - 1);

                SharedPreferences pref = getSharedPreferences(UserSettingActivity.prefName, 0);
                int temp = pref.getInt(UserSettingActivity.keyAge, 0);
                int age = 40;                           // age default --> 삭제 필요
                if (temp > 0 && temp < 200) age = temp;

                // set manual Y bounds
                graphView.getViewport().setYAxisBoundsManual(true);
                graphView.getViewport().setMinY(0);
                graphView.getViewport().setMaxY(220 - age);

                textViewSessionTime.setText(sessiontime);
                textViewDuration.setText(duration);
                textViewDistance.setText(distance);
                textViewCalories.setText(calories);
                textViewHRAvg.setText(hr_avg);
                textViewHRMax.setText(hr_max + "bpm");
                textViewHRMin.setText(hr_min + "bpm");

                textViewRed.setText(redZone);
                textViewYellow.setText(yellowZone);
                textViewGreen.setText(greenZone);
                textViewCyan.setText(cyanZone);
                textViewBlue.setText(blueZone);

                graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if (isValueX) {
                            // show normal x values
                            return super.formatLabel(value, isValueX);
                        } else {
                            // show currency for y values
                            return super.formatLabel(value, isValueX) + "bpm";
                        }
                    }
                });

                graphView.getGridLabelRenderer().setNumVerticalLabels(11);


                StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
                staticLabelsFormatter.setHorizontalLabels(new String[]{"0", duration});

//            graphView.getLegendRenderer().setMargin(10);

//            staticLabelsFormatter.setVerticalLabels(new String[] {"","","HeartRate"});
                graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
//            graphView.getGridLabelRenderer().setVerticalAxisTitle("Heart Rate");
//            graphView.getGridLabelRenderer().setHorizontalAxisTitle(duration);


//            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
//            staticLabelsFormatter.setHorizontalLabels(new String[] {"old", "middle", "new"});
//            staticLabelsFormatter.setVerticalLabels(new String[] {"low", "middle", "high"});
//            graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

//            graphView.setTitle(sessiontime);
                DataPoint[] arr = new DataPoint[arrayDataPoint.size()];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = arrayDataPoint.get(i);
//                Log.i(TAG, "<X,Y> = " + arr[i].getX() + ", " + arr[i].getY());
                }

                LineGraphSeries<DataPoint> lgs = new LineGraphSeries<DataPoint>(arr);
                lgs.setColor(Color.WHITE);

//            lgs.setDrawDataPoints(true);
                graphView.addSeries(lgs);
            }
            session_cursor.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        db.close();
        dbHelper.close();
    }

    public void onClick(View v) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph, menu);
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
