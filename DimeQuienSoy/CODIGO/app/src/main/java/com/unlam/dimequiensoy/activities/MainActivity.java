package com.unlam.dimequiensoy.activities;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.unlam.dimequiensoy.R;

public class MainActivity extends AppCompatActivity {

    Button mButtonGoToLogin;
    Button mButtonGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonGoToLogin = findViewById(R.id.btnGoToLogin);
        mButtonGoToLogin.setOnClickListener(view -> {
            if(isConnected()){
                goToLogin();
            }else{
                Toast.makeText(MainActivity.this, "No hay conexion a internet", Toast.LENGTH_SHORT).show();
            }
        });
        mButtonGoToRegister = findViewById(R.id.btnGoToRegister);
        mButtonGoToRegister.setOnClickListener(view -> {
            if(isConnected()){
                goToRegister();
            }else{
                Toast.makeText(MainActivity.this, "No hay conexion a internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToRegister() {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void goToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(RegisterActivity.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null;
    }
}
