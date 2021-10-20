package com.vu.emapis;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface JsonPlaceHolderApi {

    @GET("todos")
    Call<List<Post>> getPosts();   //Call - what type of data

    @FormUrlEncoded
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("todos")
    Call<Post> createPost(@Field("task") String task, @Header("Authorization") String token);

}
