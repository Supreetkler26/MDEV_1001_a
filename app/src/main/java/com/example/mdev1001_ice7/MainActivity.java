package com.example.mdev1001_ice7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainActivity extends AppCompatActivity {
    private Button addButton;
    private Button logoutButton;
    private static final long UPDATE_INTERVAL = 5000; // 5 seconds
    private Handler updateHandler;
    private Runnable updateRunnable;
    private long lastUpdated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addButton = (Button) findViewById(R.id.addBT);
        logoutButton = findViewById(R.id.logoutButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                startActivity(intent);
            }
        });
        logoutButton.setOnClickListener(v->{
            logoutButtonPressed();
        });
        lastUpdated = System.currentTimeMillis();
        setupUpdateHandler();
    }

    public void logoutButtonPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUpdateHandler();
    }

    private void setupUpdateHandler() {
        updateHandler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                checkForUpdates();
                startUpdateHandler(); // Repeat after UPDATE_INTERVAL
            }
        };
    }

    private void startUpdateHandler() {
        updateHandler.postDelayed(updateRunnable, UPDATE_INTERVAL);
    }

    private void stopUpdateHandler() {
        updateHandler.removeCallbacks(updateRunnable);
    }

    private void checkForUpdates() {
        Moshi moshi = new Moshi.Builder().addLast(new KotlinJsonAdapterFactory()).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://mdev1001-m2023-api.onrender.com/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();

        MovieApi movieApi = retrofit.create(MovieApi.class);
        Call<UpdatedResponse> call = movieApi.checkForUpdates();
        call.enqueue(new Callback<UpdatedResponse>() {
            @Override
            public void onResponse(Call<UpdatedResponse> call, Response<UpdatedResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UpdatedResponse updatedResponse = response.body();
                    long lastUpdatedRemotely = updatedResponse.getLastUpdated();
                    if (lastUpdated < lastUpdatedRemotely) {
                        lastUpdated = lastUpdatedRemotely;
                        fetchMovies(); // Instead of hitting hitGetMoviesApi()
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdatedResponse> call, Throwable t) {
                Log.e("failed","failed");
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        fetchMovies();
    }

    private void fetchMovies() {
        Moshi moshi = new Moshi.Builder().addLast(new KotlinJsonAdapterFactory()).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://mdev1001-m2023-api.onrender.com/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();

        MovieApi movieApi = retrofit.create(MovieApi.class);
        movieApi.fetchMovies().enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful()) {
                    List<Movie> movies = response.body();
                    if (movies != null && !movies.isEmpty()) {
                        RecyclerView list = (RecyclerView)  findViewById(R.id.moviesRL);
                        MoviesAdapter moviesAdapter = new MoviesAdapter(movies,movieApi,MainActivity.this);
                        list.setAdapter(moviesAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
            }
        });

    }
    }
