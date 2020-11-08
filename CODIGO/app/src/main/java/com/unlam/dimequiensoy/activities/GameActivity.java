package com.unlam.dimequiensoy.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.unlam.dimequiensoy.R;
import com.unlam.dimequiensoy.models.EventRequest;
import com.unlam.dimequiensoy.models.EventResponse;
import com.unlam.dimequiensoy.models.Personaje;
import com.unlam.dimequiensoy.services.RetrofitServiceEventRegister;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    public static final int MINUTE_IN_MILLI_SECONDS = 60000;
    public static final int TOTAL_GAME_PLAY_MILLI_SECONDS = 90000;
    private SensorManager mSensorManager;

    public SharedPreferences mPref;

    private TextView textCharacter;
    private TextView textSensors;
    private TextView textCountdown;
    private TextView textInfo;
    private Button btnContinue;

    private CoordinatorLayout coordinatorLayout;

    private List<Personaje> personajes = new ArrayList<>();
    private List<Personaje> personajesResultado = new ArrayList<>();
    private int personajesCounter;
    DecimalFormat dosdecimales = new DecimalFormat("###.###");

    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds = 95000;
    private boolean timerRunning=false;
    private boolean flagInGame;
    private boolean pause;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mPref = getApplicationContext().getSharedPreferences("TokenPref", MODE_PRIVATE);

        textSensors = (TextView) findViewById(R.id.textSensors);
        textCountdown = (TextView) findViewById(R.id.textCountdown);
        textCharacter  = (TextView) findViewById(R.id.textCharacter);
        textInfo  = (TextView) findViewById(R.id.textInfo);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.layoutGame);
        loadCharacters();
        // Accedemos al servicio de sensores
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameContinue(mPref);
            }
        });

    }

    private void loadCharacters() {
        personajes.add(new Personaje(1, "Sandro", false, false));
        personajes.add(new Personaje(2, "Cheff Donato", false, false));
        personajes.add(new Personaje(3, "Madonna", false, false));
        personajes.add(new Personaje(4, "Maradona", false, false));
        personajes.add(new Personaje(5, "Mesi", false, false));
        personajes.add(new Personaje(6, "Julio Boca", false, false));
        personajes.add(new Personaje(7, "Fangio", false, false));
        personajes.add(new Personaje(8, "Patouruzú", false, false));
        personajes.add(new Personaje(9, "Clemente", false, false));
        personajes.add(new Personaje(10, "Trapito", false, false));
        personajes.add(new Personaje(11, "Bruja Cachavacha", false, false));
        personajes.add(new Personaje(12, "Profesor Neurus", false, false));
        personajes.add(new Personaje(13, "Isidoro Cañones", false, false));
        personajes.add(new Personaje(14, "Ricardo Darín", false, false));
        personajes.add(new Personaje(15, "Guillermo Francella", false, false));
        personajes.add(new Personaje(16, "Susana Giménez", false, false));
        personajes.add(new Personaje(17, "Moria Casan", false, false));
        personajes.add(new Personaje(18, "Guillermo Andino", false, false));
        personajes.add(new Personaje(19, "Yoda", false, false));
        personajes.add(new Personaje(20, "Slash", false, false));
        personajes.add(new Personaje(21, "Angus Young", false, false));

        personajesCounter = personajes.size()-1;
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
                goEndGameGame();
            }
        }.start();
        //textCountdown.setText("PAUSA");
        timerRunning = true;
    }

    private void updateTimer() {
        int minutes = (int) timeLeftInMilliseconds / MINUTE_IN_MILLI_SECONDS;
        int seconds = (int) timeLeftInMilliseconds %  MINUTE_IN_MILLI_SECONDS / 1000;
        String timeLeftText;
        timeLeftText = "" + minutes;
        timeLeftText += ":";
        if (seconds < 10) timeLeftText += "0";
        timeLeftText += seconds;

        textCountdown.setText(timeLeftText);

        if (timeLeftInMilliseconds > 91000) {
            timeLeftText = "" + (seconds - 29);
            textCountdown.setText("Preparese, el juego comenzara en: " + timeLeftText);
        } else if (timeLeftInMilliseconds > TOTAL_GAME_PLAY_MILLI_SECONDS) {
            textCharacter.setText(nextCharacter(false));
        }

    }

    // Metodo que escucha el cambio de los sensores
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onSensorChanged(SensorEvent event)
    {
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

                    textSensors.setText(getStringAccelerometerData(x, y, z));

                    UpdateScreen(z);
                    break;

                case Sensor.TYPE_PROXIMITY :

                    textSensors.setText(getStringProximityData(event));

                    // Si detecta 0 lo represento
                    if(event.values[0]==0){
                        if(pause)
                            gameContinue(mPref);
                        else
                            gamePause();
                        flagInGame = false;
                    }

                    break;

            }
        }
    }

    private void gamePause() {
        btnContinue.setVisibility(View.VISIBLE);
        startStopTimer();
        Parar_Sensores();
        pause = true;
        Toast.makeText(GameActivity.this, "JUEGO PAUSADO", Toast.LENGTH_SHORT).show();
    }

    private void eventContinueRegister(SharedPreferences mPref) {

        String currentToken = mPref.getString("currentToken", null);

        EventRequest request = new EventRequest();
        request.setEnv(getString(R.string.environment));
        request.setDescription("game_continue");
        request.setType_events("game_events");

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getString(R.string.retrofit_server))
                .build();

        RetrofitServiceEventRegister retrofitServiceRegister = retrofit.create(RetrofitServiceEventRegister.class);
        Call<EventResponse> call = retrofitServiceRegister.eventRegister("Bearer "+currentToken, request);

        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(GameActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(GameActivity.this, response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                Toast.makeText(GameActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void gameContinue(SharedPreferences mPref) {
        btnContinue.setVisibility(View.INVISIBLE);
        startStopTimer();
        Ini_Sensores();
        pause = false;

        eventContinueRegister(mPref);

        Toast.makeText(GameActivity.this, "ADELANTE!", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M) //-> permite manipular el color background
    private void UpdateScreen(float z) {
        if (z > 8)
        {
            if(flagInGame){
                textInfo.setText("Sostenga el dispositivo recto (horizontal) para continuar.");
                textCharacter.setText(nextCharacter(true));
                coordinatorLayout.setBackgroundColor(getColor(R.color.colorLightGreen));
                flagInGame = false;
            }
        }
        if (z < -8)
        {
            if(flagInGame){
                textInfo.setText("Sostenga el dispositivo recto (horizontal) para continuar.");
                textCharacter.setText(nextCharacter(false));
                coordinatorLayout.setBackgroundColor(getColor(R.color.colorRed));
                flagInGame = false;
            }

        }
        if (z > -4 && z < 4)
        {
            textInfo.setText("");
            coordinatorLayout.setBackgroundColor(getColor(R.color.colorBlue));
            flagInGame = true;
        }
    }

    private String getStringProximityData(SensorEvent event) {
        String txt = "";
        txt += "Proximidad: ";
        txt += event.values[0];
        return txt;
    }

    private String getStringAccelerometerData(float x, float y, float z) {
        String txt = "";
        txt += "Acelerometro: ";
        txt += "x = " + dosdecimales.format(x) + " m/seg2 ";
        txt += ", y = " + dosdecimales.format(y) + " m/seg2 ";
        txt += ", z = " + dosdecimales.format(z) + " m/seg2 ";
        return txt;
    }

    private String nextCharacter(boolean okay) {
        Personaje personaje = null;
        if(personajesCounter > 0)
            personaje = personajes.get(personajesCounter);
        else{
            personaje.setName("FIN");
        }
        personajesCounter--;
        personaje.setAlreadyUsed(true);
        personaje.setOkay(okay);
        personajesResultado.add(personaje);

        return personaje.getName();
    }

    private void goEndGameGame() {
        stopTimer();
        Parar_Sensores();
        Intent intent = new Intent(GameActivity.this, EndGameActivity.class);
        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(personajesResultado, new TypeToken<ArrayList<Personaje>>() {}.getType());
        JsonArray jsonArray = element.getAsJsonArray();
        intent.putExtra("jsonData", jsonArray.toString());
        startActivity(intent);
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
