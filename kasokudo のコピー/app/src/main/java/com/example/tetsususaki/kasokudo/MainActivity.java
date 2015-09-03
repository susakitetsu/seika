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
    private int  i2 = 0, i4 = 0, state = 0;
    private double kari[]={0,0,0};
    private double i[] = {0, 0, 0},atai[]={0,0,0};
    private final int StateA=0,StateB=1,StateC=2,StateD=3;
    String text2 = "";
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

        if(state==StateA){//加速度センサーの初期値を取る
            for (i2 = 0; i2 < 3; i2++) {
                i[i2] = values[i2];
                atai[i2]=i[i2];
            }
            text2 = "停止";
            state = StateB;
        } else if (state == StateB) {//加速度センサーの値が変わったかどうか
            for (i2 = 0; i2 < 3; i2++) {
                atai[i2]=atai[i2]*0.95+values[i2]*0.05;
                if (i[i2] + 0.05 < atai[i2] || i[i2] - 0.05 > atai[i2]) {
                    text2 = "動作中";
                    state = StateC;
                    for(i2=0;i2<3;i2++){
                        kari[i2]=atai[i2];
                    }
                }
            }
        } else if(state==StateC){//値が連続で変わらなかった場合D
            for (i2 = 0; i2 < 3; i2++) {
                atai[i2] = atai[i2] * 0.9 + values[i2] * 0.1;
            }
            if (kari[2] + 0.03 < atai[2] || kari[2] - 0.03 > atai[2]){
                i4=0;
                kari[2]=atai[2];
            }else{
                i4++;
            }
            if (i4 > 5) {
                state = StateD;
                i4 = 0;
            }
        }else if (state == StateD) {//表示を元に戻す
            for (i2 = 0; i2 < 3; i2++) {
                i[i2] = values[i2];
                atai[i2]=i[i2];
            }
            text2 = "停止";
            state = StateB;
        }
        String text = "" +
                "values[0]:" + atai[0] + "\n" +
                "values[1]:" + atai[1] + "\n" +
                "values[2]:" + atai[2] + "\n";

        textView.setText(text2);

    }
}
