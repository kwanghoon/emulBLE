package com.h3.hrm3200;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Session;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.SessionInsertRequest;


/**
 * Created by moonhyeonah on 2015. 5. 5..
 */
public class GoogleFitSync extends AsyncTask<Void,Void,Void> {
    private Context context;
    private GoogleApiClient mClient;
    private ArrayList<Integer> sessionIds;
    private Boolean canDeleteFlag;      // 참조형 변수이므로 Boolean

    // db
    private HeartRateDBHelper dbHelper;
    private SQLiteDatabase db;

    private static String SESSION_NAME = "HRM3200";

    private static String TAG = "GoogleFitSync";
    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";


    public GoogleFitSync (Context context, GoogleApiClient mClient, ArrayList<Integer> sessionIds, Boolean canDeleteFlag) {
        this.context = context;
        this.mClient = mClient;
        this.sessionIds = sessionIds;
        this.canDeleteFlag = canDeleteFlag;

        dbHelper = new HeartRateDBHelper(context, null, 1);
        db = dbHelper.getWritableDatabase();

        // check 버튼을 변경할 수 없도록 flag 설정
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (sessionIds.size() > 0) {
            Toast.makeText(context, "Google Fit: Uploading Started.", Toast.LENGTH_SHORT).show();
        }
    }

    private int DEBUG_INTERVAL = 1; // default = 1

    @Override
    protected Void doInBackground(Void... params) {

        if (sessionIds.size() > 0) {
            for (int sessionId : sessionIds) {

                Cursor cursorSession = db.rawQuery("SELECT * "
                        + " FROM " + HeartRateDBHelper.TABLE_NAME_SESSION
                        + " WHERE  " + HeartRateDBHelper.COL_ID + " = " + sessionId, null);

                cursorSession.moveToFirst();

                // 시작 시간
                int year = cursorSession.getInt(cursorSession.getColumnIndex(HeartRateDBHelper.COL_YEAR));
                int month = cursorSession.getInt(cursorSession.getColumnIndex(HeartRateDBHelper.COL_MONTH));
                int day = cursorSession.getInt(cursorSession.getColumnIndex(HeartRateDBHelper.COL_DAY));
                int hour = cursorSession.getInt(cursorSession.getColumnIndex(HeartRateDBHelper.COL_HOUR));
                int min = cursorSession.getInt(cursorSession.getColumnIndex(HeartRateDBHelper.COL_MIN));
                int sec = cursorSession.getInt(cursorSession.getColumnIndex(HeartRateDBHelper.COL_SEC));
                int period = cursorSession.getInt(cursorSession.getColumnIndex(HeartRateDBHelper.COL_PERIOD));
                int calories = cursorSession.getInt(cursorSession.getColumnIndex(HeartRateDBHelper.COL_CALORIES));
                double distance = cursorSession.getDouble(cursorSession.getColumnIndex(HeartRateDBHelper.COL_DISTANCE));

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month-1, day, hour, min, sec); // Calendar의 month는 0부터 시작하므로 month - 1
                long startTime = calendar.getTimeInMillis();

                // 끝 시간에 대한 정보
                int duration = cursorSession.getInt(cursorSession.getColumnIndex(HeartRateDBHelper.COL_DURATION));
                long endTime = startTime + duration * 1000 * DEBUG_INTERVAL;

//                Log.i(TAG, "startTime: " + startTime);
//                Log.i(TAG, "endTime: " + (startTime + duration * 1000 * DEBUG_INTERVAL));

//                Calendar endCalendar = Calendar.getInstance();
//                endCalendar.setTimeInMillis(startTime + duration * 1000 * DEBUG_INTERVAL);
//                Log.i(TAG, "endTime: " + endCalendar.get(Calendar.YEAR) + "/" +
//                    endCalendar.get(Calendar.MONTH) + "/" +
//                    endCalendar.get(Calendar.DAY_OF_MONTH) + "/" +
//                    endCalendar.get(Calendar.HOUR_OF_DAY) + "/" +
//                    endCalendar.get(Calendar.MINUTE) + "/" +
//                    endCalendar.get(Calendar.SECOND));

                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                Log.i(TAG, "startTime: " + dateFormat.format(startTime) );
                Log.i(TAG, "endTime: " + dateFormat.format(endTime) );

                // 세션 내 데이터 전체 개수
//                int count = cursorSession.getInt(cursorSession.getColumnIndex(HeartRateDBHelper.COL_COUNT));

                cursorSession.close();


                // 3) Heart Rate 정보
                DataSource heartRateDataSource = new DataSource.Builder()
                        .setAppPackageName(context.getPackageName())
                        .setDataType(DataType.TYPE_HEART_RATE_BPM)
                        .setName(SESSION_NAME + " Heart Rate " + year + "/" + month + "/" + day + "/" + hour + "/" + min + "/" + sec)
                        .setType(DataSource.TYPE_RAW)
                        .build();

                DataSet heartRateDataSet = DataSet.create(heartRateDataSource);


                Cursor cursorData = db.rawQuery("SELECT * "
                        + " FROM " + HeartRateDBHelper.TABLE_NAME_HR
                        + " WHERE " + HeartRateDBHelper.COL_SESSION_ID + " = " + sessionId
                        + " ORDER BY " + HeartRateDBHelper.COL_HR_INDEX + " ASC ", null);


                int i = cursorData.getCount();

                int steps = 0;

                cursorData.moveToFirst();
                while (i > 0) {

                    int bpm = cursorData.getInt(cursorData.getColumnIndex(HeartRateDBHelper.COL_HEARTRATE));
                    int index = cursorData.getInt(cursorData.getColumnIndex(HeartRateDBHelper.COL_HR_INDEX));
                    steps = cursorData.getInt(cursorData.getColumnIndex(HeartRateDBHelper.COL_STEP));

                    if (( (index * period * DEBUG_INTERVAL) % 60) == 0) {         // 1분마다 한번씩 DataPoint생성하기 위해
                        ///
                        long s = startTime + index * period * 1000 * DEBUG_INTERVAL;
//                    long s = startTime + index * duration * 1000 / count;
//                        long s = startTime + index * duration * 1000 * DEBUG_INTERVAL / count;
//                    long e = s + duration * 1000 * DEBUG_INTERVAL / count;

                        DataPoint heartRateDP = heartRateDataSet.createDataPoint()
                                .setTimestamp(s, TimeUnit.MILLISECONDS);
//                            .setTimeInterval(s, e, TimeUnit.MILLISECONDS);
                        heartRateDP.getValue(Field.FIELD_BPM).setFloat(bpm);
                        heartRateDataSet.add(heartRateDP);

                        Log.i(TAG, "s: " + dateFormat.format(heartRateDP.getTimestamp(TimeUnit.MILLISECONDS)) + " bpm: " + bpm);
                    }
                        ///
                        cursorData.moveToNext();
                        i--;

                }
                Log.i(TAG, "Steps: " + steps);

                cursorData.close();

                // 1) 걸음 수

//                DataSource stepDataSource = new DataSource.Builder()
//                        .setAppPackageName(context.getPackageName())
//                        .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
//                        .setName(SESSION_NAME + " Steps " + year + "/" + month + "/" + day + "/" + hour + "/" + min + "/" + sec)
//                        .setType(DataSource.TYPE_RAW)
//                        .build();
//
//                DataSet stepDataSet = DataSet.create(stepDataSource);
//
//                DataPoint stepDataPoint = stepDataSet.createDataPoint()
//                        .setTimeInterval(startTime, startTime + duration * 1000 * DEBUG_INTERVAL, TimeUnit.MILLISECONDS);
//
//                stepDataPoint.getValue(Field.FIELD_STEPS).setInt(steps);
//
//                stepDataSet.add(stepDataPoint);

                DataSource stepDataSource = new DataSource.Builder()
                        .setAppPackageName(context.getPackageName())
                        .setDataType(DataType.AGGREGATE_STEP_COUNT_DELTA)
                        .setName(SESSION_NAME + " Steps " + year + "/" + month + "/" + day + "/" + hour + "/" + min + "/" + sec)
                        .setType(DataSource.TYPE_RAW)
                        .build();

                DataSet stepDataSet = DataSet.create(stepDataSource);

                DataPoint stepDataPoint = stepDataSet.createDataPoint()
                        .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);

                stepDataPoint.getValue(Field.FIELD_STEPS).setInt(steps);

                stepDataSet.add(stepDataPoint);

                // 2) distance
                DataSource distanceDataSource = new DataSource.Builder()
                        .setAppPackageName(context.getPackageName())
                        .setDataType(DataType.AGGREGATE_DISTANCE_DELTA)
                        .setName(SESSION_NAME + " Distance " + year + "/" + month + "/" + day + "/" + hour + "/" + min + "/" + sec)
                        .setType(DataSource.TYPE_RAW)
                        .build();

                DataSet distanceDataSet = DataSet.create(distanceDataSource);

                DataPoint distanceDataPoint = distanceDataSet.createDataPoint()
                        .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);

                distanceDataPoint.getValue(Field.FIELD_DISTANCE).setFloat((float)(distance*1000));

                distanceDataSet.add(distanceDataPoint);


                // 3) calories
                DataSource caloriesDataSource = new DataSource.Builder()
                        .setAppPackageName(context.getPackageName())
                        .setDataType(DataType.AGGREGATE_CALORIES_EXPENDED)
                        .setName(SESSION_NAME + " Calories " + year + "/" + month + "/" + day + "/" + hour + "/" + min + "/" + sec)
                        .setType(DataSource.TYPE_RAW)
                        .build();

                DataSet caloriesDataSet = DataSet.create(caloriesDataSource);

                DataPoint caloriesDataPoint = caloriesDataSet.createDataPoint()
                        .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);

                caloriesDataPoint.getValue(Field.FIELD_CALORIES).setFloat(calories);

                caloriesDataSet.add(caloriesDataPoint);


                // 4) 액티비티

                DataSource activityDataSource = new DataSource.Builder()
                        .setAppPackageName(context.getPackageName())
                        .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                        .setName(SESSION_NAME + " Activity " + year + "/" + month + "/" + day + "/" + hour + "/" + min + "/" + sec)
                        .setType(DataSource.TYPE_RAW)
                        .build();

                DataSet activityDataSet = DataSet.create(activityDataSource);

                DataPoint activityDataPoint = activityDataSet.createDataPoint()
                        .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);

                activityDataPoint.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.WALKING);
                activityDataSet.add(activityDataPoint);


                Session googleFitSession = new Session.Builder()
                        .setName(SESSION_NAME + " " + year + "/" + month + "/" + day + "/" + hour + "/" + min + "/" + sec)
                        .setDescription("HRM3200 Session")
                        .setIdentifier("HRM3200 - H3 System")
//                        .setActivity(FitnessActivities.WALKING)
                        .setStartTime(startTime, TimeUnit.MILLISECONDS)
                        .setEndTime(endTime, TimeUnit.MILLISECONDS)
                        .build();

                SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
                        .setSession(googleFitSession)
                        .addDataSet(stepDataSet)
                        .addDataSet(distanceDataSet)
                        .addDataSet(caloriesDataSet)
                        .addDataSet(activityDataSet)
                        .addDataSet(heartRateDataSet)
                        .build();

                com.google.android.gms.common.api.Status insertStatus =
                        Fitness.SessionsApi.insertSession(mClient, insertRequest)
                                .await(1, TimeUnit.MINUTES);

                if (!insertStatus.isSuccess()) {
                    Toast.makeText(context, "There was a problem inserting the session: " +
                            insertStatus.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.i(TAG, "insertStatus is susccessful...");
                }

            }
        }

        mClient.disconnect();

        dbHelper.close();
        db.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (sessionIds.size() > 0) {
            Toast.makeText(context, "Google Fit: Upload completed.", Toast.LENGTH_SHORT).show();
        }
        /// delete할 수 있도록 flag 설정
        canDeleteFlag = true;
    }
}
