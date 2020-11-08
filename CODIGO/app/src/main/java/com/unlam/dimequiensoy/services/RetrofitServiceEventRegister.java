package com.unlam.dimequiensoy.services;
import com.unlam.dimequiensoy.models.EventRequest;
import com.unlam.dimequiensoy.models.EventResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RetrofitServiceEventRegister {
    @POST("api/event")
    Call<EventResponse> eventRegister(@Header("Authorization") String token, @Body EventRequest request);
}
