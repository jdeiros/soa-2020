package com.unlam.dimequiensoy.interfaces;

import com.unlam.dimequiensoy.models.UserRequest;
import com.unlam.dimequiensoy.models.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitServiceLogin {
    @POST("api/login")
    Call<UserResponse> login(@Body UserRequest request);
}
