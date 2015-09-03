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

public class MainActivity extends Activity implements SensorEventListener {
    /**
     * Called when the activity is first created.
     */
    private int i = 0, count = 0, state = 0,flg=0;
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

        if (state == StateA) {//加速度センサーの初期値を取る
            for (i = 0; i < 3; i++) {
                d[i] = values[i];
                newvalue[i] = d[i];
            }
            text = "停止";
            state = StateB;
        } else if (state == StateB) {//加速度センサーの値が変わったかどうか
            for (i = 0; i < 3; i++) {
                newvalue[i] = newvalue[i] * 0.95 + values[i] * 0.05;
                if (d[i] + 0.02 < newvalue[i] || d[i] - 0.02 > newvalue[i]) {
                    text = "動作中";
                    state = StateC;
                    for (i = 0; i < 3; i++) {
                        oldvalue[i] = newvalue[i];
                    }
                }
            }
        } else if (state == StateC) {//値が連続で変わらなかった場合D
            for (i = 0; i < 3; i++) {
                newvalue[i] = newvalue[i] * 0.9 + values[i] * 0.1;

                if (oldvalue[i] + 0.02 < newvalue[i] || oldvalue[i] - 0.02 > newvalue[i]) {
                    oldvalue[i] = newvalue[i];
                }else{
                    flg=1;
                }
            }
            if(flg==1){
                count++;
                flg=0;
            }else{
                count=0;
            }
            if (count > 30) {
                state = StateD;
                count = 0;
            }
        } else if (state == StateD) {//初期値を取り直して表示を元に戻す
            for (i = 0; i < 3; i++) {
                d[i] = values[i];
                newvalue[i] = d[i];
            }
            text = "停止";
            state = StateB;
        }

        textView.setText(text);

    }
}
