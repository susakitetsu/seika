package com.example.tetsususaki.kasokudo;

import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import android.widget.Button;
import android.view.View;

public class MainActivity extends Activity implements SensorEventListener {
    /**
     * Called when the activity is first created.
     */
    private int i = 0, startcount=0,stopcount = 0, state = 0, startflg=0,stopflg = 0;
    private final double stability=0.99;
    private double oldvalue[] = {0, 0, 0};
    private double d[] = {0, 0, 0}, newvalue[] = {0, 0, 0};
    private final int StateA = 0, StateB = 1, StateC = 2, StateD = 3;
    String text = "";
    private SensorManager manager;
    private Sensor sensor;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView1);

        // センサーマネージャの取得
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // センサーの取得
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0)
            sensor = sensors.get(0);

        //((Button) find)

    }

    @Override
    protected void onResume() {
        super.onResume();

        // センサーの処理の開始
        if (sensor != null) {
            manager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // センサーの処理の停止
        manager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        showValues(event.values);
    }

    private void showValues(float[] values) {

        if (state == StateA ||state==StateD) {//加速度センサーの初期値を取る
            for (i = 0; i < 3; i++) {
                d[i] = values[i];
                newvalue[i] = d[i];
            }
            text = "停止";
            state = StateB;
        } else if (state == StateB) {//加速度センサーの値が変わったかどうか
            for (i = 0; i < 3; i++) {
                newvalue[i] = newvalue[i] * stability + values[i] * (1-stability);
                if (d[i] + 0.005 < newvalue[i] || d[i] - 0.005 > newvalue[i]) {//+-0.01の範囲を超えたかどうかで判断
                    newvalue[i]=values[i];
                    d[i]=newvalue[i];
                    startflg=1;
                }
            }
            if (startflg == 1) {
                startcount++;
                startflg = 0;
            } else {
                if(startcount>0) {
                    startcount --;
                }
            }
            if (startcount > 30) {
                text = "動作中";
                state = StateC;
                for (i = 0; i < 3; i++) {
                    oldvalue[i] = newvalue[i];
                }
                startcount = 0;
            }
        } else if (state == StateC) {//値が連続で変わらなかった場合D
            for (i = 0; i < 3; i++) {
                newvalue[i] = newvalue[i] * stability + values[i] * (1-stability);

                if (oldvalue[i] + 0.01 < newvalue[i] || oldvalue[i] - 0.01 > newvalue[i]) {//+-0.01の範囲を超えたかどうかで判断
                    stopflg=1;
                }
            }
            if (stopflg == 1) {
                for(i=0;i<3;i++){
                    oldvalue[i]=values[i];
                    newvalue[i]=values[i];
                }
                if(stopcount>0) {
                    stopcount =0;
                }
                stopflg = 0;
            } else {
                stopcount ++;
            }
            if (stopcount > 30) {
                state = StateD;
                stopcount = 0;
            }
        }
        String text2 = "" +
                "[0]:" + newvalue[0] + "\n" +
                "[1]:" + newvalue[1] + "\n" +
                "[2]:" + newvalue[2] + "\n";

        textView.setText("センサーの値\n"+text2 +"\n"+ text+"\n\n"+"変化がなかった回数(30で停止)　"+stopcount+"\n\n"+"連続で変化した回数（30で動作中）\n"+startcount);

    }
}
