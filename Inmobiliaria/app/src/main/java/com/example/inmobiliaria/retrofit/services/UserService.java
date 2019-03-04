package com.example.inmobiliaria.retrofit.services;

import com.example.inmobiliaria.responses.UserResponse;
import retrofit2.Call;
import retrofit2.http.GET;
public interface UserService {
    @GET("/users/me")
    Call<UserResponse> getMe();
}
