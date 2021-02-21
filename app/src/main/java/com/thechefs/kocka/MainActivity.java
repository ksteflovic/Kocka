package com.thechefs.kocka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener2 {
    private final int SHAKE_THRESHOLD = 600; // konštanta pre poriadne zatrasenie
    SensorManager sManager;
    Sensor accelerometer;
    MediaPlayer mp;    // deklaracia prehravaca zvuku
    TextView tKocka;    // zapamatanie TextView-u, do ktorého sa bude vypisovať
    private long lastUpdate = 0;        // čas posledného čítania
    private float last_x, last_y, last_z;    // posledné načítané hodnoty

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tKocka = (TextView) findViewById(R.id.cislo); // nájdenie poľa pre kocku
        sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // vytvorenie mediaplayera a definícia zvuku, ktorý má prehrávať
        mp = MediaPlayer.create(this, R.raw.kocka);
    }
    protected void onResume() {
        super.onResume();
        sManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sManager.unregisterListener(this);
    }
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0]; // ziskanie zrychleni pre kazdy smer
            float y = event.values[1]; float z = event.values[2];
            long curTime = System.currentTimeMillis(); // zapamatanie aktualneho casu

            if ((curTime - lastUpdate) > 100) { // ak preslo aspon 0,1 s
                long diffTime = (curTime - lastUpdate); // zistim kolko preslo
                lastUpdate = curTime; // zapamatam aktualny cas ako posledny sledovany
                // "vypocitam" rychlost, samozrejme aj v zavislosti na case
                float speed = Math.abs(x+y+z -last_x-last_y-last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) { // ak je vacsia ako konstanta potrasenia
                    // vygenerujem nahodne cislo
                    tKocka.setText("" + (int) (Math.random() * 6 + 1));
                    mp.start(); // a prehram zvuk – kým neskončí, nový zvuk sa neprehrá
                }
                last_x = x; last_y = y; last_z = z; // zapamatam si posledne hodnoty
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }
}