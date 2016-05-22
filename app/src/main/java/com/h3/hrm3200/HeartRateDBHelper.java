package com.h3.hrm3200;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

/**
 * Created by moonhyeonah on 2015. 4. 7..
 */
public class HeartRateDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "database_heartrate";

    public static final String TABLE_NAME_HR = "table_heartrate";
    public static final String COL_ID = "_id";
    public static final String COL_HEARTRATE = "heartrate";
    public static final String COL_ACTLEVEL = "actlevel";
    public static final String COL_CALORIE = "calorie";
    public static final String COL_STEP = "step";
    public static final String COL_HRSTATUS = "hrstatus";
    public static final String COL_HR_INDEX = "hrindex";
    public static final String COL_SESSION_ID = "sessionid";
//    public static final String COL_MEASURING_TIME = "measuringtime";


    public static final String TABLE_NAME_SESSION = "table_session";
    public static final String COL_YEAR = "year";
    public static final String COL_MONTH = "month";
    public static final String COL_DAY = "day";
    public static final String COL_HOUR = "hour";
    public static final String COL_MIN = "min";
    public static final String COL_SEC = "sec";
    public static final String COL_DURATION = "duration";
    public static final String COL_DISTANCE = "distance";
    public static final String COL_CALORIES = "calories";
    public static final String COL_HR_AVG = "hr_avg";
    public static final String COL_HR_MIN = "hr_min";
    public static final String COL_HR_MAX = "hr_max";
    public static final String COL_TIME_RED = "red_zone";
    public static final String COL_TIME_YELLOW = "yellow_zone";
    public static final String COL_TIME_GREEN = "green_zone";
    public static final String COL_TIME_CYAN = "cyan_zone";
    public static final String COL_TIME_BLUE = "blue_zone";
    public static final String COL_COUNT = "count";             // 세션별 전체 데이타 갯수
    public static final String COL_PERIOD = "period";
    public static final String COL_STRIDE = "stride";


    public HeartRateDBHelper(Context context, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME + ".db", factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME_HR + " ( "
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "   // 0
                + COL_HEARTRATE + " INTEGER, "                      // 1
                + COL_ACTLEVEL + " INTEGER, "                       // 2
                + COL_CALORIE + " INTEGER, "                        // 3
                + COL_STEP + " INTEGER, "                           // 4
                + COL_HRSTATUS + " INTEGER, "                       // 5
                + COL_HR_INDEX + " INTEGER, "                       // 6
                + COL_SESSION_ID + " INTEGER ) ");                     // 7
//                + COL_SESSION_ID + " INTEGER, "                     // 7
//                + COL_MEASURING_TIME + " INTEGER ) ");               // 8



        db.execSQL("CREATE TABLE " + TABLE_NAME_SESSION + " ( "
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "   // 0
                + COL_YEAR + " INTEGER, "                           // 1
                + COL_MONTH + " INTEGER, "                          // 2
                + COL_DAY + " INTEGER, "                            // 3
                + COL_HOUR + " INTEGER, "                           // 4
                + COL_MIN + " INTEGER, "                            // 5
                + COL_SEC + " INTEGER, "                            // 6
                + COL_DURATION + " INTEGER, "                       // 7
                + COL_DISTANCE + " DOUBLE, "                        // 8
                + COL_CALORIES + " INTEGER, "                       // 9
                + COL_HR_AVG + " INTEGER, "                         // 10
                + COL_HR_MIN + " INTEGER, "                         // 11
                + COL_HR_MAX + " INTEGER, "                         // 12
                + COL_TIME_RED + " INTEGER, "                       // 13
                + COL_TIME_YELLOW + " INTEGER, "                    // 14
                + COL_TIME_GREEN + " INTEGER, "                     // 15
                + COL_TIME_CYAN + " INTEGER, "                      // 16
                + COL_TIME_BLUE + " INTEGER, "                      // 17
                + COL_COUNT + " INTEGER, "                          // 18
                + COL_PERIOD + " INTEGER, "                         // 19
                + COL_STRIDE + " INTEGER ) ");                      // 20
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_HR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SESSION);
        onCreate(db);
    }
}


