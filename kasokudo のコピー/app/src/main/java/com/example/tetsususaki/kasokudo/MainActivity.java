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
    /** Called when the activity is first created. */
    int i[]={0,0,0},i2=0,i4=0,j=0;
    String text2="";
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

        String text = "" +
                "values[0]:" + values[0] + "\n" +
                "values[1]:" + values[1] + "\n" +
                "values[2]:" + values[2] + "\n";

        if(j==0){
            for(i2=0;i2<3;i2++) {
                i[i2] = (int) values[i2];
            }
            text2="停止";
            j=1;
        }else if(j==1){
            for(i2=0;i2<3;i2++) {
                if (i[i2] + 3 < (int) values[i2] || i[i2] - 3 > (int) values[i2]) {
                    text2 = "動作中";
                    j = 2;
                }
            }
        }else{
            i4++;
            if(i4>10){
                j=0;
                i4=0;
            }
        }

        textView.setText(text+text2);

    }
}
