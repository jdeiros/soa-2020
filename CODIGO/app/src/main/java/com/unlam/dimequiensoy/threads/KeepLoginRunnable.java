package com.unlam.dimequiensoy.threads;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.unlam.dimequiensoy.interfaces.RetrofitServiceTokenRefresh;
import com.unlam.dimequiensoy.models.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class KeepLoginRunnable implements Runnable{
    private Context context;

    private static final String TAG = "KEEP_LOGIN_RUNNABLE";
    int seconds;
    public SharedPreferences mPref;
    SharedPreferences.Editor editor;

    public KeepLoginRunnable(int minutes,Context context){
        this.seconds = minutes * 60;
        this.context = context;
        this.mPref = context.getSharedPreferences("TokenPref", MODE_PRIVATE);
        this.editor = mPref.edit();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public void run() {
        int secondsElapsed = 0;
        Boolean loggedIn;
        while (true){

            synchronized (this) {
                loggedIn = mPref.getBoolean("userAlreadyLoggedIn", false);
            }
            Log.d(TAG, "startService: " + secondsElapsed);
            Log.d(TAG, "startService: " + loggedIn);
            if(loggedIn){
                if(secondsElapsed == seconds){
                    refreshToken();
                    secondsElapsed = 0;
                }
            }else{
                break;
            }

            try {
                Thread.sleep(1000);
                secondsElapsed++;

            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void refreshToken() {

        String currentToken = this.mPref.getString("refreshToken", null);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://so-unlam.net.ar/api/")
                .build();

        RetrofitServiceTokenRefresh retrofitServiceRegister = retrofit.create(RetrofitServiceTokenRefresh.class);
        Call<UserResponse> call = retrofitServiceRegister.tokenRefresh("Bearer " + currentToken);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful()){
                    synchronized (this) {
                        editor.putString("currentToken", response.body().getToken());
                        editor.putString("refreshToken", response.body().getToken_refresh());
                        editor.putBoolean("userAlreadyLoggedIn", true);
                        editor.apply();
                    }
                    Log.d(TAG, response.body().toString());

                }else{
                    Log.d(TAG, response.body().toString());
                    Log.d(TAG, response.errorBody().toString());
                    synchronized (this) {
                        editor.putBoolean("userAlreadyLoggedIn", false);
                        editor.apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }
}
