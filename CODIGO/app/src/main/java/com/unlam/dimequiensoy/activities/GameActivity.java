package com.unlam.dimequiensoy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import com.unlam.dimequiensoy.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private TextView textAccelerometer;
    private TextView textCharacter;
    private TextView textProximity;
    private TextView textCountdown;
    private List<String> characters = new ArrayList<String>();
    private int charactersCounter;
    DecimalFormat dosdecimales = new DecimalFormat("###.###");

    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds = 95000;
    private boolean timerRunning=false;
    private boolean flagInGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        textAccelerometer  = (TextView) findViewById(R.id.textAccelerometer);
        textProximity  = (TextView) findViewById(R.id.textProximity);
        textCountdown = (TextView) findViewById(R.id.textCountdown);
        textCharacter  = (TextView) findViewById(R.id.textCharacter);

        loadCharacters();
        // Accedemos al servicio de sensores
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


    }

    private void loadCharacters() {
        characters.add("Sandro");
        characters.add("Cheff Donato");
        characters.add("Madonna");
        characters.add("Maradona");
        characters.add("Mesi");
        characters.add("Julio Boca");
        characters.add("Fangio");
        characters.add("Patouruzú");
        characters.add("Clemente");
        characters.add("Trapito");
        characters.add("Profesor Neurus");
        characters.add("Bruja Cachavacha");
        characters.add("Isidoro Cañones");
        characters.add("Ricardo Darín");
        characters.add("Guillermo Francella");
        characters.add("Susana Giménez");
        charactersCounter = characters.size()-1;
    }

    public void startStopTimer(){
        if(timerRunning){
            stopTimer();
        }else{
            startTimer();
        }
    }

    private void stopTimer() {
        countDownTimer.cancel();
        //textCountdown.setText("START");
        timerRunning = false;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMilliseconds = l;
                updateTimer();
            }

            @Override
            public void onFinish() {

            }
        }.start();
        //textCountdown.setText("PAUSA");
        timerRunning = true;
    }

    private void updateTimer() {
        int minutes = (int) timeLeftInMilliseconds / 60000;
        int seconds = (int) timeLeftInMilliseconds % 60000 / 1000;
        String timeLeftText;
        timeLeftText = "" + minutes;
        timeLeftText += ":";
        if (seconds < 10) timeLeftText += "0";
        timeLeftText += seconds;

        if (timeLeftInMilliseconds > 91000) {
            timeLeftText = "" + (seconds - 29);
        } else if (timeLeftInMilliseconds > 90000) {
            timeLeftText = "YA";
            textCharacter.setText(nextCharacter());
        }

        textCountdown.setText(timeLeftText);
    }

    // Metodo que escucha el cambio de los sensores
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        String txt = "";

        // Cada sensor puede lanzar un thread que pase por aqui
        // Para asegurarnos ante los accesos simultaneos sincronizamos esto

        synchronized (this)
        {
            Log.d("sensor", event.sensor.getName());

            switch(event.sensor.getType())
            {
                case Sensor.TYPE_ACCELEROMETER :

                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    txt += "Acelerometro:\n";
                    txt += "x: " + dosdecimales.format(x) + " m/seg2 \n";
                    txt += "y: " + dosdecimales.format(y) + " m/seg2 \n";
                    txt += "z: " + dosdecimales.format(z) + " m/seg2 \n";
                    textAccelerometer.setText(txt);

                    if (z > 7)
                    {
                        textAccelerometer.setText("GANO -> SIGUIENTE PERSONAJE");
                        if(flagInGame){
                            textCharacter.setText(nextCharacter());
                            flagInGame = false;
                        }
                    }
                    if (z < -7)
                    {
                        textAccelerometer.setText("PERDIÓ -> SIGUIENTE PERSONAJE");
                        if(flagInGame){
                            textCharacter.setText(nextCharacter());
                            flagInGame = false;
                        }

                    }
                    if (z > -7 && z < 7)
                    {
                        textAccelerometer.setText("EN JUEGO");
                        flagInGame = true;
                    }
                    break;

                case Sensor.TYPE_PROXIMITY :
                    txt += "Proximidad: ";
                    txt += event.values[0] + "\n";

                    textProximity.setText(txt);

                    // Si detecta 0 lo represento
                    if(event.values[0]==0){
                        textProximity.setText("SIGUIENTE PERSONAJE");
                        textCharacter.setText(nextCharacter());
                        flagInGame = false;
                    }

                    break;

            }
        }
    }

    private String nextCharacter() {
        String character;
        if(charactersCounter > 0)
            character = characters.get(charactersCounter);
        else
            character = "NO HAY MAS PERSONAJES";
        charactersCounter--;
        return character;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onStop()
    {

        Parar_Sensores();

        super.onStop();
    }

    @Override
    protected void onRestart()
    {
        Ini_Sensores();

        super.onRestart();
    }

    @Override
    protected void onDestroy()
    {
        Parar_Sensores();

        super.onDestroy();
    }

    // Metodo para iniciar el acceso a los sensores
    protected void Ini_Sensores()
    {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),   SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),       SensorManager.SENSOR_DELAY_NORMAL);

    }

    // Metodo para parar la escucha de los sensores
    private void Parar_Sensores()
    {
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
    }


    @Override
    protected void onPause()
    {
        Parar_Sensores();
        startStopTimer();
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        startStopTimer();
        Ini_Sensores();
    }
}
