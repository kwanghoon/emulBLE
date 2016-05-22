package com.h3.hrm3200;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


public class SplashActivity extends Activity {

    Bitmap bitmapLogo;

    private String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        ImageView imageView = (ImageView)findViewById(R.id.imageView);

//
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int dstWidth = metrics.widthPixels;
        int dstHeight = metrics.heightPixels;


        bitmapLogo = StartButtonView.decodeSampledBitmapFromResource(getResources(),
                    R.drawable.h3system_logo, dstWidth, dstHeight);

        imageView.setImageBitmap(bitmapLogo);

        Log.i(TAG, "width=" + dstWidth + ", height=" + dstHeight);
        Log.v(TAG, "Allocation byte count: " + bitmapLogo.getByteCount() + " for " + R.drawable.h3system_logo);



//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        bitmapLogo = BitmapFactory.decodeResource(getResources(), R.drawable.h3system_logo);
//        imageView.setImageBitmap(bitmapLogo);


        Handler handler = new Handler ();

        final Intent intent = new Intent(this, MainActivity.class);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 2000);

    }

    @Override
    protected void onPause() {
        super.onPause();

        setContentView(new View(this)); // Galaxy4에서 App에 할당된 메모리가 적어 Start/Stop 버튼 이미지를 띄울때
                                        // OutOfMemory 에러가 발생하는 문제를 해결하기 위해 로고 이미지를 갖고 있는
                                        // ImageView를 더이상 가리키지 않도록 코딩

        if (bitmapLogo!=null) {
            bitmapLogo.recycle();
            bitmapLogo = null;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
