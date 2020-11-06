package com.unlam.dimequiensoy.services;

import com.unlam.dimequiensoy.models.UserRequest;
import com.unlam.dimequiensoy.models.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitServiceRegister {
    @POST("api/register")
    Call<UserResponse> register(@Body UserRequest request);
}
