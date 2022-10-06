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

@SuppressWarnings("SpellCheckingInspection")
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
        tmTk1 = new TimerTask() {


            @Override
            public void run() {
                x += 2;
                y = getY();
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
                String content, line;
                StringBuilder sb = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                content = sb.toString();
                htmlTemp = content;
            } catch(Exception ex) {
                htmlTemp = "";
            }
        });
        t.start();
        while(t.isAlive()) {
            wait2();
        }

        return htmlTemp;
    }

    public static void wait2() {

    }

    public float getXiaomiValue() {
        String htmlTemp2 = performGetCall("https://www.google.com/search?q=xiaomi+wert&oq=xiaomi+wert&aqs=chrome..69i57.2502j0j7&sourceid=chrome&ie=UTF-8");
        int index = 0;
        for(int i = 14; i < htmlTemp2.length(); i++) {
            if(htmlTemp2.startsWith("jsname=\"vWLAgc", i - 14)) {
                index = i;
                break;
            }
        }

        String valueString = getNumberFrom(htmlTemp2.substring(index + 36, index + 48));

        return getValueFromString(valueString);
    }

    public float getGlobalCleanEnergyValue() {
        String htmlTemp2 = performGetCall("https://www.justetf.com/de/etf-profile.html?isin=IE00B1XNHC34#overview");
        int index = 0;
        for(int i = 5; i < htmlTemp2.length(); i++) {
            if(htmlTemp2.startsWith("<span>EUR</span>", i - 5)) {
                index = i;
                break;
            }
        }

        String valueString = getNumberFrom(htmlTemp2.substring(index + 47, index + 57));

        return getValueFromString(valueString);
    }

    public float getSP500Value() {
        String htmlTemp2 = performGetCall("https://www.finanzen.net/etf/ishares-global-clean-energy-etf-ie00b1xnhc34");
        int index = 0;
        for(int i = 14; i < htmlTemp2.length(); i++) {
            if(htmlTemp2.startsWith("<span>EUR</span>", i - 14)) {
                index = i;
                break;
            }
        }

        String valueString = getNumberFrom(htmlTemp2.substring(index - 20, index - 10));

        return getValueFromString(valueString);
    }

    public String getNumberFrom(String input) {
        char[] inputArray = input.toCharArray();
        char[] outputArray = new char[input.length()];
        int j = 0;
        for(int i = 0; i < input.length(); i++) {
            switch(inputArray[i]){
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    outputArray[j] = inputArray[i];
                    j++;
                    break;
                case ',':
                    outputArray[j] = '.';
                    j++;
                    break;
            }
        }
        return new String(outputArray);
    }

    public float getValueFromString(String valueString) {
        return Float.parseFloat(valueString);
    }

    public float getY() {
        float result = 0;
        result += 1000 * getXiaomiValue();
        result += 75   * getGlobalCleanEnergyValue();
        result += 100  * getSP500Value();
        result /= 274;
        return result;
    }
}