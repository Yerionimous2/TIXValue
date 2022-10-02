package com.example.tixattempt2;

import androidx.appcompat.app.AppCompatActivity;
import com.jjoe64.graphview.*;
import com.jjoe64.graphview.series.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences.Editor editor;
    public Random rand;
    public LineGraphSeries<DataPoint> srStonks;
    public GraphView grStonks;
    public Timer tm1;
    public TimerTask tmTk1;
    public TextView lbValue;
    private String yString;
    private float x;
    private float y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        y = sharedPref.getFloat("Value", 12);

        setContentView(R.layout.activity_main);
        rand = new Random();
        setContentView(R.layout.activity_main);
        srStonks = new LineGraphSeries<>();
        grStonks = findViewById(R.id.graph1);
        lbValue = findViewById(R.id.lbValue);
        x = 0;
        tmTk1 = new TimerTask() {


            @Override
            public void run() {
                x += 2;
                y = getY(y);
                y += (Math.random()*2-1)/200;
                yString = y + " Euro";
                runOnUiThread(() -> {
                    lbValue.setText(yString);
                });
                editor.putFloat("Value", y);
                editor.apply();
                srStonks.appendData(new DataPoint(x, y), true, 500000000);
                grStonks.addSeries(srStonks);
            }
        };
        tm1 = new Timer();
        tm1.schedule(tmTk1, 0, 2000);
    }

    public float getY(float y) {
        return (float) (y + (Math.random()*2-1)/200);
    }
}