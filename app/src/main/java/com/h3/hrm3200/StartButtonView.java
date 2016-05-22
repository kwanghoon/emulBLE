package com.h3.hrm3200;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLES20;
import android.util.AttributeSet;
//import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by moonhyeonah on 2015. 4. 2..
 */
public class StartButtonView extends View {
    public final static int PEAK_PERFORMANCE       = 1;
    public final static int ANAEROBIC_INTENSIVE    = 2;
    public final static int AEROBIC_FITNESS        = 3;
    public final static int FAT_BURN_AND_ENDURANCE = 4;
    public final static int WARM_UP_AND_RECOVERY   = 5;
    public final static int DEFAULT_HEART_RATE_ZONE= 6;
    public final static int START_AVAILABLE        = 7; //moon


    // For connectionStatus
    public final static int NO_DEVICE  = 1;   // 연결 끊김
    public final static int CONNECTING = 2;   // 연결 시도
    public final static int CONNECTED  = 3;   // 연결됨
    public final static int WAITING    = 4;   // 연결되고 데이터 전송 대기 : 0x1c (00)받음
    public final static int MEASURING  = 5;   // 연결되고 데이터 받고,
                                              // 0x1c (01)을 받으면 CONNECTED로 바뀜

    // For Testing : LastUpdate
    public final static int NOT_EXIST   = 1;
    public final static int BATTERY     = 2;
    public final static int HEART_RATE  = 3;
    private int lastUpdate;

    // States
    private int heart_rate_zone;
    private int bpm;
    private int battery_level;
    private int calorie;
    private int steps;
    private boolean start; // start==true => "Start" 표시,  start==false => "Stop" 표시
    private int connectionStatus; // BLE Device Connection Status

    private Context context;

    private static final String TAG = "StartButtonView";

    public StartButtonView(Context context) {
        super(context);
        initialize(context);
    }

    public StartButtonView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context);
    }

    private void initialize(Context context) {
        this.context = context;

        start = true;
        connectionStatus = NO_DEVICE;

        SharedPreferences pref = context.getSharedPreferences(MainActivity.prefName, 0);

        if (pref != null)
            connectionStatus = pref.getInt(MainActivity.connFlag, NO_DEVICE);


        heart_rate_zone = START_AVAILABLE;
        bpm = 0;
        battery_level = 0;
        calorie = 0;
        steps = 0;

        centerX = 0;
        centerY = 0;
        radius = 0;


        lastUpdate = NOT_EXIST;
    }

    //
    public void setLastUpdate(int lastUpdate) {
        this.lastUpdate = lastUpdate;
        invalidate();
    }

    public void setHeartRateZone(int heart_rate_zone) {
        this.heart_rate_zone = heart_rate_zone;
        invalidate();
    }

    // moon set HeartRateZone for heart rate
    public void chkHearRateZone(int heart_rate) {

        SharedPreferences pref = context.getSharedPreferences(UserSettingActivity.prefName, 0);
//        int birth = pref.getInt(UserSettingActivity.keyBirth, 0);

        int temp = pref.getInt(UserSettingActivity.keyAge, 0);
        int age=40;
        if (temp>0 && temp<200) age = temp;  //TODO: 초기값이 항상 존재하면 문제 없으므로 삭제해야함

        int max_heart_rate = 220 - age;
        double rate = (heart_rate*100)/max_heart_rate;


        if (rate>=50 && rate<60)
            heart_rate_zone = WARM_UP_AND_RECOVERY;
        else if (rate>=60 && rate<70)
            heart_rate_zone = FAT_BURN_AND_ENDURANCE;
        else if (rate>=70 && rate<80)
            heart_rate_zone = AEROBIC_FITNESS;
        else if (rate>=80 && rate<90)
            heart_rate_zone = ANAEROBIC_INTENSIVE;
        else if (rate>=90 && rate<100)
            heart_rate_zone = PEAK_PERFORMANCE;
        else if (rate>=100)
            heart_rate_zone = PEAK_PERFORMANCE;
        else
            heart_rate_zone = DEFAULT_HEART_RATE_ZONE;

        setHeartRateZone(heart_rate_zone);

    }

    public void setBPM(int bpm) {
        if (bpm < 0) bpm = 0;
        else if (bpm > 500) bpm = 500;

        this.bpm = bpm;
//        lastUpdate = HEART_RATE;

        invalidate();
    }

    public void setStart(boolean start) {
        this.start = start;
        if (start) {
            heart_rate_zone = START_AVAILABLE;
        } else {
        }

        invalidate();
    }

    public void setConnectionStatus(int connectionStatus) {
        this.connectionStatus = connectionStatus;

        invalidate();
    }

//    public int getColorOfHeartRateZone() {
//        switch(heart_rate_zone) {
//            case PEAK_PERFORMANCE:
//                return Color.RED;
//            case ANAEROBIC_INTENSIVE:
//                return Color.YELLOW;
//            case AEROBIC_FITNESS:
//                return Color.GREEN;
//            case FAT_BURN_AND_ENDURANCE:
//                return Color.CYAN;
//            case WARM_UP_AND_RECOVERY:
//                return Color.BLUE;
//            default:
//                return Color.BLUE; // Should reach here.
//        }
//    }

    //Select stop image moon
    public int getImageNameOfHeartRateZone() {
        switch(heart_rate_zone) {
            case PEAK_PERFORMANCE:
                return R.drawable.stop_06;
            case ANAEROBIC_INTENSIVE:
                return R.drawable.stop_05;
            case AEROBIC_FITNESS:
                return R.drawable.stop_04;
            case FAT_BURN_AND_ENDURANCE:
                return R.drawable.stop_03;
            case WARM_UP_AND_RECOVERY:
                return R.drawable.stop_02;
            case DEFAULT_HEART_RATE_ZONE:
                return R.drawable.stop_01;
            case START_AVAILABLE:
                return R.drawable.start_bt;
            default:
                return R.drawable.start_bt;
        }
    }

    private float centerX;
    private float centerY;
    private float radius;
    private float radiusdelta=10;

    private Bitmap oldBit;


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        Log.v(TAG, "Raw Height and Width: " + options.outHeight + ", " + options.outWidth);

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height ; // TODO: height / 2 ==> height
            final int halfWidth = width ; // TODO: width / 2 ==> width

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        if (inSampleSize == 1) inSampleSize = 2; // TODO:

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        Log.v(TAG, "inSampleSize=" + options.inSampleSize);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    @Override
    public void onDraw(Canvas canvas) {
        Rect rect = canvas.getClipBounds();

        Paint paint = new Paint();

//        paint.setStrokeWidth(5);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(getColorOfHeartRateZone());

        // Draw a Circle
        final float ratio = 2.5f;

        float width = rect.right - rect.left;
        float height = rect.bottom - rect.top;

        float centerX = rect.left + width/2;
        float centerY = rect.top + height/2;

        float radius = (width>height ? height : width)/ratio;

        this.centerX = centerX;
        this.centerY = centerY;

        this.radius = radius;

        RectF rectF = new RectF(centerX-radius, centerY-radius,
                                centerX+radius, centerY+radius);

//        BitmapFactory.Options options = new BitmapFactory.Options();
//
        int dstWidth = (int)(radius*2);

//        Log.i(TAG, "src(Height,Width): " +  src.getHeight() + "," + src.getWidth()  + ", dstWidth: " + dstWidth);


        Bitmap src = decodeSampledBitmapFromResource(getResources(), getImageNameOfHeartRateZone(), dstWidth, dstWidth);

        Log.v(TAG, "Allocation byte count: " + src.getByteCount() + " for " + getImageNameOfHeartRateZone());

        canvas.drawBitmap(src, null, rectF, null);
        if ( (oldBit!=null) && !(oldBit.equals(src)) )
            oldBit.recycle();
        oldBit = src;

        float diameterHorizontal = rect.right - width / ratio - rect.left + width / ratio;
        float diameterVertical = rect.bottom - height / ratio - rect.top + height / ratio;


        // Draw BPM Text
        paint = new Paint();
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);            //TODO 기기 크기에 따른 처리

        canvas.drawText(bpm + " bpm", rect.left+width/2, rect.top+height/2+diameterVertical/4, paint);


        // 가장 최근 데이타 업데이트 시간
        Calendar now = Calendar.getInstance();
        int hour  = now.get(Calendar.HOUR_OF_DAY);
        int min   = now.get(Calendar.MINUTE);
        int sec   = now.get(Calendar.SECOND);

        paint = new Paint();
        paint.setTextSize(25);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);

        if (lastUpdate == BATTERY)
            canvas.drawText("Last updated (Battery) < " + hour + ":" + min + ":" + sec + " >", rect.left+200, rect.bottom-30, paint);
        else if (lastUpdate == HEART_RATE)
            canvas.drawText("Last updated (HR) < " + hour + ":" + min + ":" + sec + " >", rect.left+200, rect.bottom-30, paint);

        lastUpdate = NOT_EXIST;




        // Draw Connection Status Text
        paint = new Paint();
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);

        if (connectionStatus == NO_DEVICE) {
            canvas.drawText("Not Connected",
                    centerX, rect.top+50, paint);
        } else if (connectionStatus == CONNECTING) {
            canvas.drawText("Connecting ...",
                    centerX, rect.top+50, paint);
        } else if (connectionStatus == WAITING) {
            canvas.drawText("Waiting",
                    centerX, rect.top+50, paint);
        } else if (connectionStatus == CONNECTED) {
            canvas.drawText("Connected",
                    centerX, rect.top+50, paint);
        }
        else if (connectionStatus == MEASURING) {
            canvas.drawText("Measuring",
                    centerX, rect.top+50, paint);
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float radius2 = (event.getX() - centerX) * (event.getX() - centerX)
                + (event.getY() - centerY) * (event.getY() - centerY);

        if (radius2 < radius * radius + radiusdelta) {
            if (event.getAction()==MotionEvent.ACTION_DOWN) {
                // 버튼을 누른 효과 그리기
                return true;
            }
            else if (event.getAction()==MotionEvent.ACTION_UP) {

//                Toast.makeText(context, "Feature Disabled...(On Debugging)", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.setAction(MainActivity.ACTION_PRESS_START_BUTTON);
                intent.putExtra(MainActivity.START_KEY, start);
                context.sendBroadcast(intent);

//                setStart(!start);
                return true;
            }
        }


        return super.onTouchEvent(event);
    }

    public void cleanUp() {
        if (oldBit != null) {
            oldBit.recycle();
        }
    }
}
