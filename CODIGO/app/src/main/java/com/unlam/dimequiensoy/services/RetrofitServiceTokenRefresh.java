package com.unlam.dimequiensoy.services;
import com.unlam.dimequiensoy.models.UserResponse;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.PUT;

public interface RetrofitServiceTokenRefresh {
    @PUT("api/refresh")
    Call<UserResponse> tokenRefresh(@Header("Authorization") String token);
}
