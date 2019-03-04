package com.example.inmobiliaria.retrofit.services;

import com.example.inmobiliaria.models.AddProperty;
import com.example.inmobiliaria.models.EditProperty;
import com.example.inmobiliaria.responses.ContainerOneRowResponse;
import com.example.inmobiliaria.responses.ContainerResponse;
import com.example.inmobiliaria.responses.PropertyFavsResponse;
import com.example.inmobiliaria.responses.PropertyResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import java.util.Map;

public interface PropertyService {
    final String BASE_URL = "/properties";

    @GET(BASE_URL)
    Call<ContainerResponse<PropertyResponse>> listProperties(@QueryMap Map<String, String> options);

    @GET(BASE_URL)
    Call<ContainerResponse<PropertyResponse>> listGeo(@Query("near") String near);

    @GET(BASE_URL + "/mine")
    Call<ContainerResponse<PropertyFavsResponse>> getMine();

    @GET(BASE_URL + "/fav")
    Call<ContainerResponse<PropertyResponse>> getFavs();

    @GET(BASE_URL + "/{id}")
    Call<ContainerOneRowResponse<PropertyResponse>>getOne(@Path("id") String id);

    @POST(BASE_URL)
    Call<AddProperty> create (@Body AddProperty property);

    @POST(BASE_URL+"/fav/{id}")
    Call<PropertyResponse> addFav (@Path("id") String id);

    @PUT(BASE_URL + "/{id}")
    Call<EditProperty> edit(@Path("id") String id, @Body EditProperty edited);

    @DELETE(BASE_URL + "/{id}")
    Call<PropertyFavsResponse> delete(@Path("id") String id);

    @DELETE(BASE_URL + "/fav/{id}")
    Call<PropertyResponse> deleteFav(@Path("id") String id);
}
