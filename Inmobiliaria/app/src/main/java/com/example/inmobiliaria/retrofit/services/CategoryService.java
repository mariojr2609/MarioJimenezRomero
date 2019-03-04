package com.example.inmobiliaria.retrofit.services;

import com.example.inmobiliaria.responses.CategoryResponse;
import com.example.inmobiliaria.responses.ContainerResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CategoryService {
    final String BASE_URL = "/categories";

    @GET(BASE_URL)
    Call<ContainerResponse<CategoryResponse>> listCategories();

    @GET(BASE_URL+"/{id}")
    Call<CategoryResponse> getOne(@Path("id") String id);
}
