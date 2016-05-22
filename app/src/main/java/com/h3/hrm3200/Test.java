package com.h3.hrm3200;

import android.content.Context;
//import android.util.Log;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by moonhyeonah on 2015. 4. 23..
 */
public class Test {
    private static final String TAG = "Test";

    public static void dbStressTest(Context context) {

        SessionData.DB_DEBUG = true;

        class DBTestThread extends Thread {
            private Context context;
            public DBTestThread(Context context) {
                this.context = context;
            }

            public void run() {

                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH) + 1;
                int day = now.get(Calendar.DAY_OF_MONTH);
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int min = now.get(Calendar.MINUTE);
                int sec = now.get(Calendar.SECOND);

                Log.i(TAG, "DB Test (START) : " + year + "/" + month + "/" + day + "/" + hour + "/" + min + "/" + sec);

                SessionData sessionData = new SessionData(70);
                for (
                        int i = 0;
                        i < 2 * 60 + 45; i++)

                { // 21600개
                    sessionData.putData(context, 80, 10, 20, 30, 0, -1);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }

                now = Calendar.getInstance();
                year = now.get(Calendar.YEAR);
                month = now.get(Calendar.MONTH) + 1;
                day = now.get(Calendar.DAY_OF_MONTH);
                hour = now.get(Calendar.HOUR_OF_DAY);
                min = now.get(Calendar.MINUTE);
                sec = now.get(Calendar.SECOND);

                Log.i(TAG, "DB Test (All Added to ArrayList) : " + year + "/" + month + "/" + day + "/" + hour + "/" + min + "/" + sec);

                sessionData.writeToDB(context);

                now = Calendar.getInstance();
                year = now.get(Calendar.YEAR);
                month = now.get(Calendar.MONTH) + 1;
                day = now.get(Calendar.DAY_OF_MONTH);
                hour = now.get(Calendar.HOUR_OF_DAY);
                min = now.get(Calendar.MINUTE);
                sec = now.get(Calendar.SECOND);

                Log.i(TAG, "DB Test (END)" + year + "/" + month + "/" + day + "/" + hour + "/" + min + "/" + sec);

                SessionData.DB_DEBUG = false;

            }
        }

        new DBTestThread(context).start();

    }

    // Merge 테스트를 위한 가상의 DB 데이터를 쓰는 메소드
    public static void writeDBForMergeTest(Context context) {

        class MyThread extends Thread {
            private Context context;
            public MyThread(Context context) {
                this.context = context;
            }
            public void run() {
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH) + 1;
                int day = now.get(Calendar.DAY_OF_MONTH);
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int min = now.get(Calendar.MINUTE);
                int sec = now.get(Calendar.SECOND);

                Log.i(TAG, "writeDBForMergeTest (START) : " + year + "/" + month + "/" + day + "/" + hour + "/" + min + "/" + sec);

                SessionData sessionData = new SessionData(75, 1); // 실시간 데이터

                Random random = new Random();
                int heartrate = 80;
                int calorie = -1;
                int steps = 0;

                for (
                        int i = 0;
                        i < 1000; i++)

                {
                    // Heart Rate
                    int r = random.nextInt() % 3;
                    switch (r) {
                        case 0:
                            heartrate--;
                            break;
                        case 1:
                            heartrate++;
                            break;
                        default:
                            break;
                    }
                    if (heartrate < 50)
                        heartrate = 50;
                    else if (heartrate > 150)
                        heartrate = 150;
                    else
                        ;

                    // Calorie & steps
                    calorie = calorie + 2;

                    steps++;

                    sessionData.putData(context, heartrate, 10, calorie, steps, 0, -1);

                    // public void putData(Context context, int heart_rate, int act_level,
                    //                                int calorie, int steps, int hr_status, int index)
                }


                now = Calendar.getInstance();
                year = now.get(Calendar.YEAR);
                month = now.get(Calendar.MONTH) + 1;
                day = now.get(Calendar.DAY_OF_MONTH);
                hour = now.get(Calendar.HOUR_OF_DAY);
                min = now.get(Calendar.MINUTE);
                sec = now.get(Calendar.SECOND);

                Log.i(TAG, "writeDBForMergeTest [real-time data] : " + year + "/" + month + "/" + day + "/" + hour + "/" + min + "/" + sec);

                sessionData.writeToDB(context);

                now = Calendar.getInstance();
                year = now.get(Calendar.YEAR);
                month = now.get(Calendar.MONTH) + 1;
                day = now.get(Calendar.DAY_OF_MONTH);
                hour = now.get(Calendar.HOUR_OF_DAY);
                min = now.get(Calendar.MINUTE);
                sec = now.get(Calendar.SECOND);

                Log.i(TAG, "writeDBForMergeTest (END)" + year + "/" + month + "/" + day + "/" + hour + "/" + min + "/" + sec);


                // 저장된 데이터를 DB에 저장

//                sessionData = new SessionData(75, 10); // 저장된 데이터

                Calendar calendar = Calendar.getInstance();
                calendar.set(sessionData.getYear(), sessionData.getMonth()-1, sessionData.getDay(),
                        sessionData.getHour(), sessionData.getMin(), sessionData.getSec());
                long millsec = calendar.getTimeInMillis();

                millsec = millsec + (1000+30) * 1000;  // 앞 세션: 1000초

                calendar.setTimeInMillis(millsec);

                sessionData = new SessionData(75, 10); // 저장된 데이터

                sessionData.setYear(calendar.get(Calendar.YEAR));
                sessionData.setMonth(calendar.get(Calendar.MONTH)+1);
                sessionData.setDay(calendar.get(Calendar.DAY_OF_MONTH));
                sessionData.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                sessionData.setMin(calendar.get(Calendar.MINUTE));
                sessionData.setSec(calendar.get(Calendar.SECOND));

                for (
                        int i = 0;
                        i < 500; i++)

                {
                    // Heart Rate
                    int r = random.nextInt() % 3;
                    switch (r) {
                        case 0:
                            heartrate--;
                            break;
                        case 1:
                            heartrate++;
                            break;
                        default:
                            break;
                    }
                    if (heartrate < 50)
                        heartrate = 50;
                    else if (heartrate > 150)
                        heartrate = 150;
                    else
                        ;

                    // Calorie & steps
                    calorie = calorie + 2;

                    steps++;

                    if (random.nextInt() % 100 != 50) {
                        sessionData.putData(context, heartrate, 10, calorie, steps, 0, i);
                    }

                    // public void putData(Context context, int heart_rate, int act_level,
                    //                                int calorie, int steps, int hr_status, int index)
                }

                now = Calendar.getInstance();
                year = now.get(Calendar.YEAR);
                month = now.get(Calendar.MONTH) + 1;
                day = now.get(Calendar.DAY_OF_MONTH);
                hour = now.get(Calendar.HOUR_OF_DAY);
                min = now.get(Calendar.MINUTE);
                sec = now.get(Calendar.SECOND);

                Log.i(TAG, "writeDBForMergeTest [stored data] : " + year + "/" + month + "/" + day + "/" + hour + "/" + min + "/" + sec);

                sessionData.writeToDB(context);

                now = Calendar.getInstance();
                year = now.get(Calendar.YEAR);
                month = now.get(Calendar.MONTH) + 1;
                day = now.get(Calendar.DAY_OF_MONTH);
                hour = now.get(Calendar.HOUR_OF_DAY);
                min = now.get(Calendar.MINUTE);
                sec = now.get(Calendar.SECOND);

                Log.i(TAG, "writeDBForMergeTest (END)" + year + "/" + month + "/" + day + "/" + hour + "/" + min + "/" + sec);
            }
        }

        new MyThread(context).start();

    }

//    public static DataSet insertFitnessData(Context context) {
//        Log.i(TAG, "Creating a new data insert request");
//
//        // [START build_insert_data_request]
//        // Set a start and end time for our data, using a start time of 1 hour before this moment.
//        Calendar cal = Calendar.getInstance();
//        Date now = new Date();
//        cal.setTime(now);
//        long endTime = cal.getTimeInMillis();
//        cal.add(Calendar.HOUR_OF_DAY, -1);
//        long startTime = cal.getTimeInMillis();
//
//        // Create a data source
//        DataSource dataSource = new DataSource.Builder()
//                .setAppPackageName(context)
//                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
//                .setName(TAG + " - step count")
//                .setType(DataSource.TYPE_RAW)
//                .build();
//
//        // Create a data set
//        int stepCountDelta = 1234;
//        DataSet dataSet = DataSet.create(dataSource);
//        // For each data point, specify a start time, end time, and the data value -- in this case,
//        // the number of new steps.
//        DataPoint dataPoint = dataSet.createDataPoint()
//                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
//        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
//        dataSet.add(dataPoint);
//        // [END build_insert_data_request]
//
//        return dataSet;
//    }
}
