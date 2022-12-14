package com.example.tixattempt2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("SpellCheckingInspection")
public class MainActivity extends AppCompatActivity {
    private SharedPreferences.Editor editor;
    public Random rand;
    public Timer tm1;
    public TimerTask tmTk1;
    public TextView lbValue;
    private String yString;
    private static String htmlTemp = "";
    private float x;
    private float y;
    private float Xiaomi;
    private float SP500;
    private float GlobalCleanEnergy;

    public LineChart chart;
    public List<Entry> dataPoints2 = new ArrayList<>();
    public LineDataSet dataSet;
    public LineData lineData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        y = sharedPref.getFloat("Value", 0);
        Xiaomi = sharedPref.getFloat("Xiaomi", 0);
        GlobalCleanEnergy = sharedPref.getFloat("GlobalCleanEnergy", 0);
        SP500 = sharedPref.getFloat("SP500", 0);

        rand = new Random();
        setContentView(R.layout.activity_main);
        lbValue = findViewById(R.id.lbValue);

        chart = new LineChart(this);
        chart.setAutoScaleMinMaxEnabled(false);
        dataPoints2.add(new Entry(0, y));
        yString = y + " Euro";
        lbValue.setText(yString);
        dataSet = new LineDataSet(dataPoints2, "Tix");
        lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
        runOnUiThread(() -> lbValue.bringToFront());

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ConstraintLayout rl = (ConstraintLayout) findViewById(R.id.layout1);
        rl.addView(chart);
        chart.setLayoutParams(new FrameLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));

        x = 0;
        tmTk1 = new TimerTask() {
            @Override
            public void run() {
                x += 2;
                float lastY = y;
                y = getY(1000, 75, 100, 450, 264);
                yString = y + " Euro";
                runOnUiThread(() -> lbValue.setText(yString));
                if(y != lastY) {
                    editor.putFloat("Value", y);
                    editor.putFloat("Xiaomi", Xiaomi);
                    editor.putFloat("GlobalCleanEnergy", GlobalCleanEnergy);
                    editor.putFloat("SP500", SP500);
                    editor.apply();
                    addToGraph(x, y);
                }
            }
        };
        tm1 = new Timer();
        tm1.schedule(tmTk1, 0, 2000);
    }

    private void addToGraph(float x, float y) {
        runOnUiThread(() -> lbValue.bringToFront());
        chart.setVisibleXRange(0, x);
        dataPoints2.add(new Entry(x, y));
        dataSet.notifyDataSetChanged();
        lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    public static String performHTMLRequest(String sUrl) {
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
                rd.close();
                connection.disconnect();
                content = sb.toString();
                htmlTemp = content;
            } catch(Exception ex) {
                htmlTemp = "";
            }
        });
        t.start();
        //noinspection StatementWithEmptyBody
        while(t.isAlive());
        return htmlTemp;
    }


    public float getXiaomiValue() {
        String htmlTemp2 = performHTMLRequest("https://www.google.com/search?q=xiaomi+wert&oq=xiaomi+wert&aqs=chrome..69i57.2502j0j7&sourceid=chrome&ie=UTF-8");
        if(htmlTemp2.equals("")) return Xiaomi;
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
        String htmlTemp2 = performHTMLRequest("https://www.justetf.com/de/etf-profile.html?isin=IE00B1XNHC34#overview");
        if(htmlTemp2.equals("")) return GlobalCleanEnergy;
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
        String htmlTemp2 = performHTMLRequest("https://www.finanzen.net/etf/ishares-global-clean-energy-etf-ie00b1xnhc34");
        if(htmlTemp2.equals("")) return SP500;
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
                case '.':
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

    public float getY(int XiaomiPieces, int GlobalCleanEnergyPieces, int SP500Pieces, int EURValue, int TIXShares) {
        float XiaomiValue, GlobalCleanEnergyValue, SP500Value;
        Xiaomi = getXiaomiValue();
        GlobalCleanEnergy = getGlobalCleanEnergyValue();
        SP500 = getSP500Value();
        XiaomiValue            = XiaomiPieces            * Xiaomi;
        GlobalCleanEnergyValue = GlobalCleanEnergyPieces * GlobalCleanEnergy;
        SP500Value             = SP500Pieces             * SP500;
        return calcY(XiaomiValue, GlobalCleanEnergyValue, SP500Value, EURValue, TIXShares);
    }

    public float calcY(float XiaomiValue, float GlobalCleanEnergyValue, float SP500Value, float EURValue, int TIXShares) {
        float result = 0;
        result += XiaomiValue;
        result += GlobalCleanEnergyValue;
        result += SP500Value;
        result += EURValue;
        result /= TIXShares;
        return result;
    }
}