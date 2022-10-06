package com.example.tixattempt2;

import androidx.appcompat.app.AppCompatActivity;
import com.jjoe64.graphview.*;
import com.jjoe64.graphview.series.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private static String htmlTemp = "";
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
        performGetCall("https://www.google.com/search?q=s%26p+500+wert&oq=s%26p+500+wert&aqs=chrome..69i57.10719j0j7&sourceid=chrome&ie=UTF-8");
        getSP500Value();
        tmTk1 = new TimerTask() {


            @Override
            public void run() {
                x += 2;
                y = getY(y);
                y += (Math.random()*2-1)/200;
                yString = y + " Euro";
                runOnUiThread(() -> lbValue.setText(yString));
                editor.putFloat("Value", y);
                editor.apply();
                srStonks.appendData(new DataPoint(x, y), true, 500000000);
                grStonks.addSeries(srStonks);
            }
        };
        tm1 = new Timer();
        tm1.schedule(tmTk1, 0, 2000);
    }

    public static String performGetCall(String sUrl) {
        Thread t = new Thread(() -> {
            try {
                URL url = new URL(sUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.connect();
                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String content = "", line;
                while ((line = rd.readLine()) != null) {
                    content += line + "\n";
                }
                htmlTemp = content;
            } catch(Exception ex) {
                htmlTemp = "";
            }
        });
        t.start();
        while(t.isAlive());
        //System.out.println(htmlTemp);

        return htmlTemp;
    }

    public float getSP500Value() {
        String htmlTemp2 = performGetCall("https://www.google.com/search?q=s%26p+500+wert&oq=s%26p+500+wert&aqs=chrome..69i57.10719j0j7&sourceid=chrome&ie=UTF-8");
        int index = 0;
        for(int i = 14; i < htmlTemp2.length(); i++) {
            if(htmlTemp2.substring(i - 14,i).equals("jsname=\"vWLAgc")) {
                index = i;
                break;
            }
        }

        String valueString = htmlTemp2.substring(index + 38, index + 46);
        float value = getValueFromString(valueString);

        return value;
    }

    public float getValueFromString(String valueString) {
        char[] valueArray = new char[valueString.length() - 1];

        for(int i = 0; i < valueString.length() - 1; i++) {
            if(i >= 1) {
                if(valueString.toCharArray()[i+1] != ',') {
                    valueArray[i] = valueString.toCharArray()[i+1];
                } else {
                    valueArray[i] = '.';
                }
            } else {
                valueArray[i] = valueString.toCharArray()[i];
            }
        }
        valueString = new String(valueArray);

        float value = Float.parseFloat(valueString);

        return value;
    }

    public float getY(float y) {
        return getSP500Value();
    }
}