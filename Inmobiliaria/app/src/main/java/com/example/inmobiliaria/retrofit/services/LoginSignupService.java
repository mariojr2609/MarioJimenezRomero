package com.example.inmobiliaria.retrofit.services;

import com.example.inmobiliaria.models.register;
import com.example.inmobiliaria.responses.LoginSignupResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LoginSignupService {
    @POST("/auth")
    Call<LoginSignupResponse> login(@Header("Authorization") String authorization);

    @POST("/users")
    Call<LoginSignupResponse> register(@Body register registro);
}
