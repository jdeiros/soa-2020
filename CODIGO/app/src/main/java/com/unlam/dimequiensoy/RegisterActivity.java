package com.unlam.dimequiensoy;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.android.material.textfield.TextInputEditText;

//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.unlam.dimequiensoy.includes.MyToolbar;
import com.unlam.dimequiensoy.models.UserRequest;
import com.unlam.dimequiensoy.models.UserResponse;
import com.unlam.dimequiensoy.services.RetrofitService;

public class RegisterActivity extends AppCompatActivity {

    SharedPreferences mPref;
    //DatabaseReference mDatabase;

    TextInputEditText mTextInputName;
    TextInputEditText mTextInputLastName;
    TextInputEditText mTextInputDNI;
    TextInputEditText mTextInputEmailRegister;
    TextInputEditText mTextInputPasswordRegister;
    TextInputEditText mTextInputCommission;
    Button mButtonRegister;


    AlertDialog mDialog;
    //public IntentFilter filter;
    //private ReceiverOperation receiver = new ReceiverOperation();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDialog = new SpotsDialog.Builder().setContext(RegisterActivity.this).setMessage("Espere un momento").build();

        MyToolbar.show(this, "Registrar usuario", true);

        //mDatabase = FirebaseDatabase.getInstance().getReference();

        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);


        mTextInputName = findViewById(R.id.textInputName);
        mTextInputLastName = findViewById(R.id.txtInputLastName);
        mTextInputDNI = findViewById(R.id.textInputDNI);
        mTextInputEmailRegister = findViewById(R.id.textInputEmailRegister);
        mTextInputPasswordRegister = findViewById(R.id.textInputPasswordRegister);
        mTextInputCommission = findViewById(R.id.textInputCommission);
        mButtonRegister = findViewById(R.id.btnRegister);

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRegister();
                //registerUserFirebase();
            }
        });

        //cofigureBroadcastReceiver();
    }

    private void userRegister() {

        UserRequest request = new UserRequest();
        request.setEnv("TEST");
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
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<UserResponse> call = retrofitService.register(request);
        mDialog.show();
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Usuario Registrado.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(RegisterActivity.this, response.body().getToken(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(RegisterActivity.this, response.body().getToken_refresh(), Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(RegisterActivity.this, response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                }
                //fin mensaje
                mDialog.hide();
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


//    private void registerUserFirebase() {
//        final String env = getString(R.string.environment);
//        final String name = mTextInputName.getText().toString();
//        final String lastname = mTextInputLastName.getText().toString();
//        final Long dni = Long.parseLong(mTextInputDNI.getText().toString());
//        final String email = mTextInputEmailRegister.getText().toString();
//        final String password = mTextInputPasswordRegister.getText().toString();
//        final Long commission = Long.parseLong(mTextInputCommission.getText().toString());
//
//        if(!name.isEmpty() && !lastname.isEmpty() && !email.isEmpty() && !password.isEmpty()){
//            if(password.length() >= 8){
//                mDialog.show();
//                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        mDialog.hide();
//                        if(task.isSuccessful()){
//                            String id = mAuth.getCurrentUser().getUid();
//                            saveUserFirebase(id, name, lastname, email);
//
//                        }else{
//                            Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }else{
//                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
//            }
//        }else{
//            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void saveUserFirebase(String id, String name, String lastname, String email) {
//        String selectedUser = mPref.getString("user", "");
//
//        UserRequest user = new UserRequest();
//        user.setEmail(email);
//        user.setLastname(lastname);
//        user.setName(name);
//
//        if(selectedUser.equals("walker")){
//            mDatabase.child("Users").child("Walkers").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful()){
//                        Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
//                    }else {
//                        Toast.makeText(RegisterActivity.this, "Falló el registro", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }else if(selectedUser.equals("owner")){
//            mDatabase.child("Users").child("Owners").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful()){
//                        Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
//                    }else {
//                        Toast.makeText(RegisterActivity.this, "Falló el registro", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }
//    }
}
