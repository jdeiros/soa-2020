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
import com.unlam.dimequiensoy.models.Personage;
import com.unlam.dimequiensoy.interfaces.RetrofitServiceEventRegister;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    public static final int MINUTE_IN_MILLI_SECONDS = 60000;
    public static final int TOTAL_GAME_PLAY_MILLI_SECONDS = 90000;
    public static final int COUNT_DOWN_INTERVAL = 1000;
    public static final int ONE_THOUSAND_SECONDS = 1000;
    public static final int TOTAL_GAME_PLUS_ONE_SECOND = 91000;
    private SensorManager mSensorManager;
    public SharedPreferences mPref;
    private TextView textCharacter;
    private TextView textSensors;
    private TextView textCountdown;
    private TextView textInfo;
    private Button btnContinue;
    private CoordinatorLayout coordinatorLayout;
    private List<Personage> personages = new ArrayList<>();
    private List<Personage> personagesResult = new ArrayList<>();
    private int personageCounter;
    DecimalFormat twoDecimals = new DecimalFormat("###.###");
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
        textSensors = findViewById(R.id.textSensors);
        textCountdown = findViewById(R.id.textCountdown);
        textCharacter  = findViewById(R.id.textCharacter);
        textInfo  = findViewById(R.id.textInfo);
        coordinatorLayout = findViewById(R.id.layoutGame);
        loadCharacters();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(view -> gameContinue(mPref));
    }

    private void loadCharacters() {
        personages.add(new Personage(1, "Sandro", false, false));
        personages.add(new Personage(2, "Cheff Donato", false, false));
        personages.add(new Personage(3, "Madonna", false, false));
        personages.add(new Personage(4, "Maradona", false, false));
        personages.add(new Personage(5, "Mesi", false, false));
        personages.add(new Personage(6, "Julio Boca", false, false));
        personages.add(new Personage(7, "Fangio", false, false));
        personages.add(new Personage(8, "Patouruzú", false, false));
        personages.add(new Personage(9, "Clemente", false, false));
        personages.add(new Personage(10, "Trapito", false, false));
        personages.add(new Personage(11, "Bruja Cachavacha", false, false));
        personages.add(new Personage(12, "Profesor Neurus", false, false));
        personages.add(new Personage(13, "Isidoro Cañones", false, false));
        personages.add(new Personage(14, "Ricardo Darín", false, false));
        personages.add(new Personage(15, "Guillermo Francella", false, false));
        personages.add(new Personage(16, "Susana Giménez", false, false));
        personages.add(new Personage(17, "Moria Casan", false, false));
        personages.add(new Personage(18, "Guillermo Andino", false, false));
        personages.add(new Personage(19, "Yoda", false, false));
        personages.add(new Personage(20, "Slash", false, false));
        personages.add(new Personage(21, "Angus Young", false, false));

        personageCounter = personages.size()-1;
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
        timerRunning = false;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, COUNT_DOWN_INTERVAL) {
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

        timerRunning = true;
    }

    private void updateTimer() {
        int minutes = (int) timeLeftInMilliseconds / MINUTE_IN_MILLI_SECONDS;
        int seconds = (int) timeLeftInMilliseconds %  MINUTE_IN_MILLI_SECONDS / ONE_THOUSAND_SECONDS;
        String timeLeftText;
        timeLeftText = "" + minutes;
        timeLeftText += ":";
        if (seconds < 10) timeLeftText += "0";
        timeLeftText += seconds;

        textCountdown.setText(timeLeftText);

        if (timeLeftInMilliseconds > TOTAL_GAME_PLUS_ONE_SECOND) {
            int prevCountdown = seconds - 30; //al inicio son 34 segundos (la parte extraida de los segundos)
            timeLeftText = "" + prevCountdown;
            if(prevCountdown >= 4) Stop_Sensors(); //parar sensores en conteo previo
            textCountdown.setText("Preparese, el juego comenzara en: " + timeLeftText);
        } else if (timeLeftInMilliseconds > TOTAL_GAME_PLAY_MILLI_SECONDS) {
            textCharacter.setText(nextCharacter(false));
            Start_Sensors();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onSensorChanged(SensorEvent event)
    {
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
                    // Si detecta 0 -> acercó la mano -> pausa
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
        Stop_Sensors();
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
                    Toast.makeText(GameActivity.this, "Evento Registrado", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(GameActivity.this, "Error registrando evento", Toast.LENGTH_SHORT).show();
                    Toast.makeText(GameActivity.this, response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                Toast.makeText(GameActivity.this, "Error registrando evento", Toast.LENGTH_SHORT).show();
                Toast.makeText(GameActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void gameContinue(SharedPreferences mPref) {
        btnContinue.setVisibility(View.INVISIBLE);
        startStopTimer();
        Start_Sensors();
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
        txt += "x = " + twoDecimals.format(x) + " m/seg2 ";
        txt += ", y = " + twoDecimals.format(y) + " m/seg2 ";
        txt += ", z = " + twoDecimals.format(z) + " m/seg2 ";
        return txt;
    }

    private String nextCharacter(boolean okay) {
        Personage personage = null;
        String nameToShow;
        if(personageCounter > 0) {
            personage = personages.get(personageCounter);
            personageCounter--;
            personage.setAlreadyUsed(true);
            personage.setOkay(okay);
            personagesResult.add(personage);
            nameToShow = personage.getName();
        }
        else
            nameToShow="NO HAY MAS PERSONAJES";

        return nameToShow;
    }

    private void goEndGameGame() {
        stopTimer();
        Stop_Sensors();
        Intent intent = new Intent(GameActivity.this, EndGameActivity.class);
        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(personagesResult, new TypeToken<ArrayList<Personage>>() {}.getType());
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
        Stop_Sensors();
        super.onStop();
    }

    @Override
    protected void onRestart()
    {
        Start_Sensors();
        super.onRestart();
    }

    @Override
    protected void onDestroy()
    {
        Stop_Sensors();
        super.onDestroy();
    }

    protected void Start_Sensors()
    {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),   SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),       SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void Stop_Sensors()
    {
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
    }


    @Override
    protected void onPause()
    {
        Stop_Sensors();
        startStopTimer();
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        startStopTimer();
        Start_Sensors();
    }
}
