package com.vu.emapis;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi {

    @GET("todos")
    Call<List<Post>> getPosts();   //Call - what type of data
}
