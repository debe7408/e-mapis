package com.vu.emapis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StatisticsActivity extends AppCompatActivity {

    private TextView textViewResult;

    private JsonPlaceHolderApi jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        textViewResult = findViewById(R.id.text_view_result);

        // Retrofit builder
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://193.219.91.103:3906/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Interface instance
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        getPosts();
        //createPost();
    }

    private void getPosts() {
        Call<List<Post>> call = jsonPlaceHolderApi.getPosts();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                //Checking for the response
                if (response.code() != 200) {
                    textViewResult.setText("Check connection");
                }

                //Put data into textview
                List<Post> posts = response.body();

                for (Post post: posts) {
                    String content = "";
                    content += "ID: " + post.getUserId() + "\n";
                    content += "Is done? " + post.isDone() + "\n";
                    content += "Text: " + post.getText() + "\n";
                    content += "Due: " + post.getDue() + "\n\n";

                    textViewResult.append(content);
                }

            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }

        });
    }

    private void createPost() {
        Post post = new Post("psi task", "October 24th", true);

        Call<Post> call = jsonPlaceHolderApi.createPost(post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if (response.code() != 200) {
                    textViewResult.setText("Check connection");
                }

                Post postResponce = response.body();

                String content = "";
                content += "Code: " + response.code() + "\n";
                content += "ID: " + postResponce.getUserId() + "\n";
                content += "Is done? " + postResponce.isDone() + "\n";
                content += "Text: " + postResponce.getText() + "\n";
                content += "Due: " + postResponce.getDue() + "\n\n";

                textViewResult.setText(content);

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });

    }
}