package com.unlam.dimequiensoy.activities;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.unlam.dimequiensoy.R;
import com.unlam.dimequiensoy.includes.MyToolbar;

public class SelectOptionGameActivity extends AppCompatActivity {

    Button mButtonPlay;
    Button mBtnLogOut;
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
        mPref = getApplicationContext().getSharedPreferences("TokenPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        mButtonPlay = findViewById(R.id.btnPlay);
        mBtnLogOut = findViewById(R.id.btnLogOut);
        mBtnLogOut.setOnClickListener(view -> logOut(editor));

        mButtonPlay.setOnClickListener(view -> goToGame());
    }

    private void logOut(SharedPreferences.Editor editor) {
        synchronized (this) {
            synchronized (this) {
                editor.putBoolean("userAlreadyLoggedIn", false);
                editor.commit();
            }
        }
        goToLogin();
    }

    private void goToLogin() {
        Intent intent = new Intent(SelectOptionGameActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void goToGame() {
        Intent intent = new Intent(SelectOptionGameActivity.this, GameActivity.class);
        startActivity(intent);
    }
}
