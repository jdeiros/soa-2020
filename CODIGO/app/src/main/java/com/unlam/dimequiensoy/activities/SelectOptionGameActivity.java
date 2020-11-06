package com.unlam.dimequiensoy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unlam.dimequiensoy.R;
import com.unlam.dimequiensoy.includes.MyToolbar;

public class SelectOptionGameActivity extends AppCompatActivity {

    Button mButtonPlay;
    Button mButtonConfig;

    private TextView textBatteryLevelGameOpt;
    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);


    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_game);

        MyToolbar.show(this, "Seleccione una opción", true);
        textBatteryLevelGameOpt = (TextView) findViewById(R.id.textBatteryLevelGameOpt);

        Intent batteryStatus = this.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level * 100 / (float)scale;
        textBatteryLevelGameOpt.setText("Estado de la bateria: "+ batteryPct + " % de carga.");

        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPref.edit();

        mButtonPlay = findViewById(R.id.btnPlay);
        mButtonConfig = findViewById(R.id.btnConfig);

        mButtonConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("user","owner");
                editor.apply();
                goToConfig();
            }
        });

        mButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("user","walker");
                editor.apply();
                goToGame();
            }
        });
    }

    private void goToConfig() {
        Intent intent = new Intent(SelectOptionGameActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void goToGame() {
        Intent intent = new Intent(SelectOptionGameActivity.this, GameActivity.class);
        startActivity(intent);
    }
}
