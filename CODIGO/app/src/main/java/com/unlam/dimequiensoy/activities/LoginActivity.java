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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.unlam.dimequiensoy.R;
import com.unlam.dimequiensoy.includes.MyToolbar;
import com.unlam.dimequiensoy.models.UserRequest;
import com.unlam.dimequiensoy.models.UserResponse;
import com.unlam.dimequiensoy.services.RetrofitServiceLogin;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    Button mButtonLogin;

//    FirebaseAuth mAuth;
//    DatabaseReference mDatabase;

    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mButtonLogin = findViewById(R.id.btnLogin);

        MyToolbar.show(this, "Login de usuario", true);

//        mAuth = FirebaseAuth.getInstance();
//        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDialog = new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Espere un momento").build();

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void login() {
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        UserRequest request = new UserRequest();
        request.setEmail(email);
        request.setPassword(password);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getString(R.string.retrofit_server))
                .build();

        RetrofitServiceLogin retrofitServiceRegister = retrofit.create(RetrofitServiceLogin.class);
        Call<UserResponse> call = retrofitServiceRegister.register(request);


        if(!email.isEmpty() && !password.isEmpty()){

            if(password.length() >= 8){
                mDialog.show();
                call.enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Usuario Registrado.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, response.body().getToken(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, response.body().getToken_refresh(), Toast.LENGTH_SHORT).show();
                            goToSelectOptionGame();

                        }else{
                            Toast.makeText(LoginActivity.this, response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                        }
                        //fin mensaje
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

    private void goToSelectOptionGame() {
        Intent intent = new Intent(LoginActivity.this, SelectOptionGameActivity.class);
        startActivity(intent);
    }
}
