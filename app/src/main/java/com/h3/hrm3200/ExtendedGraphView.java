package com.h3.hrm3200;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.jjoe64.graphview.GraphView;

/**
 * Created by moonhyeonah on 2015. 4. 22..
 */
public class ExtendedGraphView extends GraphView {

    public ExtendedGraphView(Context context) {
        super(context);
    }

    public ExtendedGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedGraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Paint paint;
        int left = getGraphContentLeft();
        int top = getGraphContentTop();
        int width = getGraphContentWidth();
        float height = getGraphContentHeight();
        float part = getGraphContentHeight()/10;

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(paint.getTextSize()*2);
        canvas.drawText("bpm",left,top,paint);

        paint.setStyle(Paint.Style.FILL);

        paint.setAlpha(Color.TRANSPARENT);

        paint.setColor(Color.RED);
        canvas.drawRect(left, top, left+width, top+part, paint);

        paint.setColor(0xffFFFF00);
        canvas.drawRect(left, top+part, left+width, top+part*2, paint);

        paint.setColor(0xff00FF00);
        canvas.drawRect(left, top+part*2, left+width, top+part*3, paint);

        paint.setColor(0xff00FFFF);
        canvas.drawRect(left, top+part*3, left+width, top+part*4, paint);

        paint.setColor(0xff0000FF);
        canvas.drawRect(left, top+part*4, left+width, top+part*5, paint);

        paint.setColor(Color.GRAY);
        canvas.drawRect(left, top+part*5, left+width, top+height, paint);

        super.onDraw(canvas);
    }
}
