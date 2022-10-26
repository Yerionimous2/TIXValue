package com.example.tixattempt2;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class DrawView extends View{
    private Canvas canvas = null;
    private Path path;
    private float[] start = new float[2];
    private float[] end = new float[2];
    Paint paint = new Paint();

    private void init() {
        start[0] = 0;
        start[1] = 0;
        end[0] = 0;
        end[1] = 0;
        path = new Path();
        paint.setColor(Color.BLACK);
    }

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void onDraw(Canvas canvas) {
        this.canvas = canvas;
        canvas.drawPath(path, paint);
    }

    public void draw(float[] end) {
        path.lineTo(end[0], end[1]);
    }

    public void reset(float[] start) {
        path.reset();
        path.moveTo(start[0], start[1]);
        this.start = start;
    }
}
