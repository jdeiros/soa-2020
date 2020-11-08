package com.unlam.dimequiensoy.activities;
import androidx.appcompat.app.AppCompatActivity;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.unlam.dimequiensoy.R;
import com.unlam.dimequiensoy.includes.MyToolbar;
import com.unlam.dimequiensoy.models.UserRequest;
import com.unlam.dimequiensoy.models.UserResponse;
import com.unlam.dimequiensoy.interfaces.RetrofitServiceLogin;
import com.unlam.dimequiensoy.threads.KeepLoginRunnable;

public class LoginActivity extends AppCompatActivity {

    public static final int MINUTES_KEEP_LOGIN = 25;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    Button mButtonLogin;
    SharedPreferences mPref;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mButtonLogin = findViewById(R.id.btnLogin);

        MyToolbar.show(this, "Login de usuario", true);
        mPref = getApplicationContext().getSharedPreferences("TokenPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        mDialog = new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Espere un momento").build();

        mButtonLogin.setOnClickListener(view -> {
            if(isConnected()){
                login(editor);
            }else{
                Toast.makeText(LoginActivity.this, "No hay conexion a internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login(SharedPreferences.Editor editor) {
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        UserRequest request = new UserRequest();
        request.setEnv(getString(R.string.environment));
        request.setEmail(email);
        request.setPassword(password);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getString(R.string.retrofit_server))
                .build();

        RetrofitServiceLogin retrofitServiceRegister = retrofit.create(RetrofitServiceLogin.class);
        Call<UserResponse> call = retrofitServiceRegister.login(request);


        if(!email.isEmpty() && !password.isEmpty()){

            if(password.length() >= 8){
                mDialog.show();
                call.enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Usuario Logueado.", Toast.LENGTH_SHORT).show();
                            synchronized (this) {
                                editor.putString("currentToken", response.body().getToken());
                                editor.putString("refreshToken", response.body().getToken_refresh());
                                editor.apply();
                            }
                            startKeepLoginThread();
                            synchronized (this) {
                                editor.putBoolean("userAlreadyLoggedIn", true);
                                editor.apply();
                            }
                            goToSelectOptionGame();

                        }else{
                            Toast.makeText(LoginActivity.this, response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                        }
                        mDialog.hide();
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {

                        Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }else{
                Toast.makeText(LoginActivity.this, "La longitud del password debe tener mas de 8 caracteres", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(LoginActivity.this, "La contraseña y el email son obligatorios", Toast.LENGTH_SHORT).show();
        }
    }

    private void startKeepLoginThread() {
        KeepLoginRunnable runnable = new KeepLoginRunnable(MINUTES_KEEP_LOGIN, this);
        new Thread(runnable).start();
    }

    private void goToSelectOptionGame() {
        Intent intent = new Intent(LoginActivity.this, SelectOptionGameActivity.class);
        startActivity(intent);
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(RegisterActivity.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null;
    }
}
