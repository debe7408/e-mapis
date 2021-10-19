package com.vu.emapis;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface JsonPlaceHolderApi {

    @GET("todos")
    Call<List<Post>> getPosts();   //Call - what type of data

    @POST("todos")
    Call<Post> createPost(@Body Post post);
}
