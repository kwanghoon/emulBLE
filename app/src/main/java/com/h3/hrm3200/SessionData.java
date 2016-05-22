package com.h3.hrm3200;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by moonhyeonah on 2015. 4. 7..
 */
public class SessionData {
    private int sessionid;

    // Session Starting Time
    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private int sec;

    private int duration;
    private double distance; // step*stride
    private double stride = 0.0007;
    private int period; // Storing period
    private int count; // 세션별 데이터 전체 갯수, 실시간 데이타는 수신할때마다 증가
                        // 저장데이타는 세션정보 수신할때 받은 데이타 총갯수
    private int numOfData; // 수신받지 못하는 데이타를 고려해 수신한 데이타로만 평균 계산하기 위해 추가

    private double heart_rate_average;
    private int heart_rate_min;
    private int heart_rate_max;
    private int calories;
    private int time_red_zone;
    private int time_yellow_zone;
    private int time_green_zone;
    private int time_cyan_zone;
    private int time_blue_zone;


    private ArrayList<HeartRateMeasurement> arrayList;

    private boolean flag_to_write_db; // false => DB 기록 불필요 (이미 기록함), true => DB 기록 필요
    private boolean flag_for_realTime_data; // 실시간 데이타의 세션 시작시간은 데이타가 처음 들어온 시간이므로
                                            // 세션 생성시 시간을 데이타 처음 수신시 시간으로 변경하기 위한 플래그

    private final int DATA_THUNK_SIZE = 60; // 1초 period의 경우 1분에 한번씩 DB writing


    //
    private final String TAG = "SessionData";

    //
    public static final int NUM_OF_SESSION_ATTRIBUTES = 5;


    public void setPeriod(int period) {
        this.period = period;
    }

    public void setStride(int stride) {

        if (stride == 0) {
            this.stride = 0.0007; // default stride value
        } else {
            this.stride = (double) stride / 100000;
        }
//        Log.i(TAG, "setStride: " + this.stride);
    }

    public Object getSessionAttribute(int i) {
        switch(i) {
            case 0: return duration;
            case 1: return distance;
            case 2: return heart_rate_average;
            case 3: return heart_rate_min;
            case 4: return heart_rate_max;
            default:
                return null;
        }
    }


    public int getSessionId() { return sessionid; }
    public int getNumOfData() { return numOfData; }

    public int getYear() {        return year; }
    public int getMonth() {       return month; }
    public int getDay() {         return day; }
    public int getHour() {        return hour; }
    public int getMin() {         return min; }
    public int getSec() {         return sec; }

    public void setYear(int year) { this.year = year; }
    public void setMonth(int month) { this.month = month; }
    public void setDay(int day) { this.day = day; }
    public void setHour(int hour) { this.hour = hour; }
    public void setMin(int min) { this.min = min; }
    public void setSec(int sec) { this.sec = sec; }

    public void setCount(int count) { this.count = count;}


    // 세션 요약 정보 화면 출력시 이용
    public String getSessionAttributeTitle(int i) {
        switch(i) {
            case 0: return "Duration";
            case 1: return "Distance";
            case 2: return "HR Avg.";
            case 3: return "HR Min.";
            case 4: return "HR Max.";
            default:
                return null;
        }
    }

    public String getSessionAttributeString(int i) {
        switch(i) {
            case 0: return getDuration();
            case 1: return getDistance();
            case 2: return getHeart_rate_average();
            case 3: return getHeart_rate_min();
            case 4: return getHeart_rate_max();
            default:
                return null;
        }
    }

    public int getIntDuration() {
        return duration;
    }

    public String getDuration() {
        return getTimeString(duration);
    }

    public String getTimeString(int time) {
        int hour = time / (60 * 60);
        int min = (time - hour * 60 * 60) / 60;
        int sec = time - hour * 60 * 60 - min * 60;

        return hour + ":" + min + ":" + sec;
    }

    public String getRedZoneTime() {
        return getTimeString(time_red_zone);
    }
    public String getYellowZoneTime() {
        return getTimeString(time_yellow_zone);
    }
    public String getGreenZoneTime() {
        return getTimeString(time_green_zone);
    }
    public String getCyanZoneTime() {
        return getTimeString(time_cyan_zone);
    }
    public String getBlueZoneTime() {
        return getTimeString(time_blue_zone);
    }

    public String getDistance() {

//        double d = distance * stride;

        if (distance >= 0.1 ) {
            return String.format("%.1fkm", distance);
        } else {
            return String.format("%.1fm", (distance * 1000));
        }
    }
    public String getCalories() { return calories + "kcal"; }
    public String getHeart_rate_min() { return heart_rate_min + "bpm"; }
    public String getHeart_rate_max() { return heart_rate_max + "bpm"; }
    public String getHeart_rate_average() { return (int)heart_rate_average + "bpm"; }

    public int getIntHeart_rate_min() { return heart_rate_min; }
    public int getIntHeart_rate_max() { return heart_rate_max; }


    class HeartRateMeasurement {
        int heart_rate;
        int act_level;
        int calorie;
        int steps;
        int hr_status;
        int hr_index = 0;
//        int measuring_time;

        HeartRateMeasurement(int heart_rate, int act_level, int calorie, int steps, int hr_status) {
            this.heart_rate = heart_rate;
            this.act_level  = act_level;
            this.calorie    = calorie;
            this.steps      = steps;
            this.hr_status  = hr_status;
        }

        HeartRateMeasurement(int heart_rate, int act_level, int calorie, int steps, int hr_status, int index) {
            this.heart_rate = heart_rate;
            this.act_level  = act_level;
            this.calorie    = calorie;
            this.steps      = steps;
            this.hr_status  = hr_status;
            this.hr_index   = index;
        }
    }

    public SessionData(int stride) {
        Calendar now = Calendar.getInstance();
        year  = now.get(Calendar.YEAR);
        month = now.get(Calendar.MONTH) + 1;
        day   = now.get(Calendar.DAY_OF_MONTH);
        hour  = now.get(Calendar.HOUR_OF_DAY);
        min   = now.get(Calendar.MINUTE);
        sec   = now.get(Calendar.SECOND);

        arrayList = new ArrayList<HeartRateMeasurement>();

        count = 0;
        numOfData = 0;
        duration = 0;
        distance = 0;
        calories = 0;

        time_red_zone = 0;
        time_yellow_zone = 0;
        time_green_zone = 0;
        time_cyan_zone = 0;
        time_blue_zone = 0;

        heart_rate_average = 0.0;
        heart_rate_min     = Integer.MAX_VALUE;
        heart_rate_max     = Integer.MIN_VALUE;

        setStride(stride);

        flag_to_write_db = false;
//        flag_for_realTime_data = true;

        sessionid = -1;
//        session_id = -1;
        old_index = -1;
    }

    public SessionData(int stride, int period) {
        this(stride);

        setPeriod(period);
        flag_for_realTime_data = false;
    }


    public SessionData(int stride, int period, boolean real) {      // sessiondata for_realTime_data
        this(stride);

        setPeriod(period);
        flag_for_realTime_data = real;
    }

    public SessionData(int stride, int period, int count) {         // sessiondata for_stored_data
        this(stride, period);
        this.count = count;
    }

    public void putSessionData(
            int sessionid,
            int year, int month, int day, int hour, int min, int sec,
            int duration, double distance, int calories,
            int heart_rate_average, int heart_rate_min, int heart_rate_max,
            int red, int yellow, int green, int cyan, int blue ) {
        this.sessionid = sessionid;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.sec = sec;
        this.duration = duration;
        this.distance = distance;
        this.calories = calories;
        this.heart_rate_average = heart_rate_average;
        this.heart_rate_min = heart_rate_min;
        this.heart_rate_max = heart_rate_max;
        this.time_red_zone = red;
        this.time_yellow_zone = yellow;
        this.time_green_zone = green;
        this.time_cyan_zone = cyan;
        this.time_blue_zone = blue;

    }

    private int old_index = -1; // 저장데이타의 손실 데이타 처리

    // 세션에 heart rate data 추
    public void putData(Context context, int heart_rate, int act_level,
                        int calorie, int steps, int hr_status, int index) {

        int index_for_both_cases =
                (index==-1) ? numOfData : index;

//        count = count + 1;  // 세션 별 데이터 전체 갯수, 실시간 데이타는 수신할때마다 증가
//                            // 저장데이타는 세션정보 수신할때 받은 데이타 총갯수

        if (flag_for_realTime_data) {               // 실시간 데이타 세션의 시작시간을 첫 데이타 수신시간으로 변경
            flag_for_realTime_data = false;
            Calendar now = Calendar.getInstance();
            year  = now.get(Calendar.YEAR);
            month = now.get(Calendar.MONTH) + 1;
            day   = now.get(Calendar.DAY_OF_MONTH);
            hour  = now.get(Calendar.HOUR_OF_DAY);
            min   = now.get(Calendar.MINUTE);
            sec   = now.get(Calendar.SECOND);
        }

        calories = calorie;

        heart_rate_average = (heart_rate_average * numOfData + heart_rate) / (numOfData + 1);

        if (heart_rate_min > heart_rate) {
            heart_rate_min = heart_rate;
        }

        if (heart_rate_max < heart_rate) {
            heart_rate_max = heart_rate;
        }

        numOfData++;

        if (index == -1) {                                  // 실시간데이타
            addTimeHearRateZone(context, heart_rate, period);
            duration = duration + period;                   // TODO: 실제 시간 측정해서 구해야 함
            count = numOfData;                              // 실시간인 경우 전체 데이타갯수 증가
        }
        else {                                              // 저장데이타
            for (int i=old_index+1; i<index; i++) {         // 데이타 손실 보정
                Log.i(TAG, "old_index: " + old_index);

                addTimeHearRateZone(context, heart_rate, period);
                duration = duration + period;
            }

            addTimeHearRateZone(context, heart_rate, period);
            duration = duration + period;

            old_index = index;
        }

        distance = steps * stride;

        arrayList.add(new HeartRateMeasurement(heart_rate, act_level, calorie, steps, hr_status, index_for_both_cases));

        flag_to_write_db = true;

        if (DB_DEBUG) {     // 60개 수신하면 저장 모드인 경우
            if (arrayList.size() >= DATA_THUNK_SIZE) {
                writeToDB(context);
            }
        }
    }

    public void addTimeHearRateZone(Context context, int heart_rate, int period) {

        SharedPreferences pref = context.getSharedPreferences(UserSettingActivity.prefName, 0);
//        int birth = pref.getInt(UserSettingActivity.keyBirth, 0);

        int temp = pref.getInt(UserSettingActivity.keyAge, 0);
        int age=40;
        if (temp>0 && temp<200) age = temp;  //TODO: 초기값이 항상 존재하면 문제 없으므로 삭제해야함

        int max_heart_rate = 220 - age;
        double rate = (heart_rate*100)/max_heart_rate;


        if (rate>=50 && rate<60)
            time_blue_zone = time_blue_zone + period;
        else if (rate>=60 && rate<70)
            time_cyan_zone = time_cyan_zone + period;
        else if (rate>=70 && rate<80)
            time_green_zone = time_green_zone + period;
        else if (rate>=80 && rate<90)
            time_yellow_zone = time_yellow_zone + period;
        else if (rate>=90 && rate<100)
            time_red_zone = time_red_zone + period;
        else if (rate>=100)
            time_red_zone = time_red_zone + period;
    }

    public static boolean DB_DEBUG = false;

    public boolean writeToDB(Context context) {
        Log.i(TAG, "writeDB is called: " + arrayList.size() + " flag=" + flag_to_write_db);

        if (flag_to_write_db == true) {

            flag_to_write_db = false;  // 측정종료 완료후 측정기에서 연결 끊었을때 두번 기록되는 것 방지

            if (arrayList.size() > 0) {

                if (DB_DEBUG) {
                    class DBWriteThread extends Thread {
                        private Context context;
                        private ArrayList<HeartRateMeasurement> arrayList;

                        public DBWriteThread(Context context, ArrayList<HeartRateMeasurement> arrayList) {
                            this.context = context;
                            this.arrayList = arrayList;
                        }

                        public void run() {
                            Log.i(TAG, "DBWriteThread (BEGIN) " + this.getName() + " " + ": " + arrayList.size());

                            HeartRateDBHelper heartrateDBHelper = new HeartRateDBHelper(context, null, 1);
                            SQLiteDatabase db = heartrateDBHelper.getWritableDatabase();

                            // Session Info
                            {
                                ContentValues cv = new ContentValues();
                                cv.put(HeartRateDBHelper.COL_YEAR, year);
                                cv.put(HeartRateDBHelper.COL_MONTH, month);
                                cv.put(HeartRateDBHelper.COL_DAY, day);
                                cv.put(HeartRateDBHelper.COL_HOUR, hour);
                                cv.put(HeartRateDBHelper.COL_MIN, min);
                                cv.put(HeartRateDBHelper.COL_SEC, sec);
                                cv.put(HeartRateDBHelper.COL_DURATION, duration);
                                cv.put(HeartRateDBHelper.COL_DISTANCE, distance);
                                cv.put(HeartRateDBHelper.COL_CALORIES, calories);
                                cv.put(HeartRateDBHelper.COL_HR_AVG, heart_rate_average);
                                cv.put(HeartRateDBHelper.COL_HR_MIN, heart_rate_min);
                                cv.put(HeartRateDBHelper.COL_HR_MAX, heart_rate_max);
                                cv.put(HeartRateDBHelper.COL_TIME_RED, time_red_zone);
                                cv.put(HeartRateDBHelper.COL_TIME_YELLOW, time_yellow_zone);
                                cv.put(HeartRateDBHelper.COL_TIME_GREEN, time_green_zone);
                                cv.put(HeartRateDBHelper.COL_TIME_CYAN, time_cyan_zone);
                                cv.put(HeartRateDBHelper.COL_TIME_BLUE, time_blue_zone);
                                cv.put(HeartRateDBHelper.COL_COUNT, count);
                                cv.put(HeartRateDBHelper.COL_PERIOD, period);
                                cv.put(HeartRateDBHelper.COL_STRIDE, (int)(stride*100000));

                                long session_id = -1;
                                if (sessionid == -1) {
                                    session_id = db.insert(HeartRateDBHelper.TABLE_NAME_SESSION, null, cv);
                                    sessionid = (int)session_id;

                                    Log.i(TAG, "session id: " + sessionid);
                                } else {
                                    db.update(HeartRateDBHelper.TABLE_NAME_SESSION, cv,
                                            HeartRateDBHelper.COL_ID + " = " + sessionid, null);
                                }
                            }

                            // Session Data
                            for (HeartRateMeasurement hrm : arrayList) {
                                ContentValues cv = new ContentValues();

                                cv.put(HeartRateDBHelper.COL_HEARTRATE, hrm.heart_rate);
                                cv.put(HeartRateDBHelper.COL_ACTLEVEL, hrm.act_level);
                                cv.put(HeartRateDBHelper.COL_CALORIE, hrm.calorie);
                                cv.put(HeartRateDBHelper.COL_STEP, hrm.steps);
                                cv.put(HeartRateDBHelper.COL_HRSTATUS, hrm.hr_status);
                                cv.put(HeartRateDBHelper.COL_HR_INDEX, hrm.hr_index);
                                cv.put(HeartRateDBHelper.COL_SESSION_ID, sessionid);
//                                cv.put(HeartRateDBHelper.COL_MEASURING_TIME, hrm.measuring_time);

                                db.insert(HeartRateDBHelper.TABLE_NAME_HR, null, cv);
                            }

                            db.close();
                            heartrateDBHelper.close();

                            Log.i(TAG, "DBWriteThread (END) " + this.getName());
                        }
                    }

                    new DBWriteThread(context, arrayList).start();

                    arrayList = new ArrayList<HeartRateMeasurement>();
                }

                else {

                    // Original Code
                    HeartRateDBHelper heartrateDBHelper = new HeartRateDBHelper(context, null, 1);
                    SQLiteDatabase db = heartrateDBHelper.getWritableDatabase();

                    ContentValues cv = new ContentValues();
                    cv.put(HeartRateDBHelper.COL_YEAR, year);
                    cv.put(HeartRateDBHelper.COL_MONTH, month);
                    cv.put(HeartRateDBHelper.COL_DAY, day);
                    cv.put(HeartRateDBHelper.COL_HOUR, hour);
                    cv.put(HeartRateDBHelper.COL_MIN, min);
                    cv.put(HeartRateDBHelper.COL_SEC, sec);
                    cv.put(HeartRateDBHelper.COL_DURATION, duration);
                    cv.put(HeartRateDBHelper.COL_DISTANCE, distance);
                    cv.put(HeartRateDBHelper.COL_CALORIES, calories);
                    cv.put(HeartRateDBHelper.COL_HR_AVG, heart_rate_average);
                    cv.put(HeartRateDBHelper.COL_HR_MIN, heart_rate_min);
                    cv.put(HeartRateDBHelper.COL_HR_MAX, heart_rate_max);
                    cv.put(HeartRateDBHelper.COL_TIME_RED, time_red_zone);
                    cv.put(HeartRateDBHelper.COL_TIME_YELLOW, time_yellow_zone);
                    cv.put(HeartRateDBHelper.COL_TIME_GREEN, time_green_zone);
                    cv.put(HeartRateDBHelper.COL_TIME_CYAN, time_cyan_zone);
                    cv.put(HeartRateDBHelper.COL_TIME_BLUE, time_blue_zone);
                    cv.put(HeartRateDBHelper.COL_COUNT, count);
                    cv.put(HeartRateDBHelper.COL_PERIOD, period);
                    cv.put(HeartRateDBHelper.COL_STRIDE, (int)(stride*100000));
//                    Log.i(TAG, "stride: " + stride);

                    long session_id = db.insert(HeartRateDBHelper.TABLE_NAME_SESSION, null, cv);
                    sessionid = (int)session_id;

                    Log.i(TAG, "session id: " + session_id);


                    for (HeartRateMeasurement hrm : arrayList) {
                        cv.clear();

                        cv.put(HeartRateDBHelper.COL_HEARTRATE, hrm.heart_rate);
                        cv.put(HeartRateDBHelper.COL_ACTLEVEL, hrm.act_level);
                        cv.put(HeartRateDBHelper.COL_CALORIE, hrm.calorie);
                        cv.put(HeartRateDBHelper.COL_STEP, hrm.steps);
                        cv.put(HeartRateDBHelper.COL_HRSTATUS, hrm.hr_status);
                        cv.put(HeartRateDBHelper.COL_HR_INDEX, hrm.hr_index);
                        cv.put(HeartRateDBHelper.COL_SESSION_ID, session_id);
//                        cv.put(HeartRateDBHelper.COL_MEASURING_TIME, hrm.measuring_time);

                        db.insert(HeartRateDBHelper.TABLE_NAME_HR, null, cv);
                    }

                    db.close();
                    heartrateDBHelper.close();
                }
            }
        } else {
            return false;  // 실제로 db write한 경우만 그래프화면으로 가기 위해, db write 실패 리턴
        }

        return true;
    }
}
