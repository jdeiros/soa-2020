package com.unlam.dimequiensoy.activities;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.android.material.textfield.TextInputEditText;
import com.unlam.dimequiensoy.R;
import com.unlam.dimequiensoy.includes.MyToolbar;
import com.unlam.dimequiensoy.models.UserRequest;
import com.unlam.dimequiensoy.models.UserResponse;
import com.unlam.dimequiensoy.interfaces.RetrofitServiceRegister;
import com.unlam.dimequiensoy.threads.KeepLoginRunnable;

public class RegisterActivity extends AppCompatActivity {
    public static final int MINUTES_KEEP_LOGIN = 25;
    SharedPreferences mPref;
    TextInputEditText mTextInputName;
    TextInputEditText mTextInputLastName;
    TextInputEditText mTextInputDNI;
    TextInputEditText mTextInputEmailRegister;
    TextInputEditText mTextInputPasswordRegister;
    TextInputEditText mTextInputCommission;
    Button mButtonRegister;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mDialog = new SpotsDialog.Builder().setContext(RegisterActivity.this).setMessage("Espere un momento").build();
        MyToolbar.show(this, "Registrar usuario", true);
        mPref = getApplicationContext().getSharedPreferences("TokenPref", MODE_PRIVATE);
        Editor editor = mPref.edit();
        mTextInputName = findViewById(R.id.textInputName);
        mTextInputLastName = findViewById(R.id.txtInputLastName);
        mTextInputDNI = findViewById(R.id.textInputDNI);
        mTextInputEmailRegister = findViewById(R.id.textInputEmailRegister);
        mTextInputPasswordRegister = findViewById(R.id.textInputPasswordRegister);
        mTextInputCommission = findViewById(R.id.textInputCommission);
        mButtonRegister = findViewById(R.id.btnRegister);
        mButtonRegister.setOnClickListener(view -> {
            if(isConnected()){
                userRegister(editor);
            }else{
                Toast.makeText(RegisterActivity.this, "No hay conexion a internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void userRegister(Editor editor) {

        UserRequest request = new UserRequest();
        request.setEnv(getString(R.string.environment));
        request.setName(mTextInputName.getText().toString());
        request.setLastname(mTextInputLastName.getText().toString());
        request.setDni(Long.parseLong(mTextInputDNI.getText().toString()));
        request.setEmail(mTextInputEmailRegister.getText().toString());
        request.setPassword(mTextInputPasswordRegister.getText().toString());
        request.setCommission(Long.parseLong(mTextInputCommission.getText().toString()));

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getString(R.string.retrofit_server))
                .build();
        RetrofitServiceRegister retrofitServiceRegister = retrofit.create(RetrofitServiceRegister.class);
        Call<UserResponse> call = retrofitServiceRegister.register(request);
        mDialog.show();
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Usuario Registrado.", Toast.LENGTH_SHORT).show();
                    //guardo en sharedpreferences con los identificadores currenToken y refreshToken
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
                    Toast.makeText(RegisterActivity.this, response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                }
                mDialog.hide();
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startKeepLoginThread() {
        KeepLoginRunnable runnable = new KeepLoginRunnable(MINUTES_KEEP_LOGIN, this);
        new Thread(runnable).start();
    }

    private void goToSelectOptionGame() {
        Intent intent = new Intent(RegisterActivity.this, SelectOptionGameActivity.class);
        startActivity(intent);
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(RegisterActivity.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null;
    }
}
