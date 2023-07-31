package com.example.mdev1001_ice7;



import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mdev1001_ice7.Movie;
import com.example.mdev1001_ice7.MovieApi;
import com.example.mdev1001_ice7.R;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddEditActivity extends AppCompatActivity {
    Movie movieDTO;
    private static final String BASE_URL = "https://mdev1001-m2023-api.onrender.com/api/";
    private MovieApi apiService;
    private TextView titleText;
    private Button addButton;
    private Button cancelButton;
    private EditText titleET;
    private EditText idET;
    private EditText StudioET;
    private EditText genresET;
    private EditText directorsET;
    private EditText writersET;
    private EditText actorsET;
    private EditText lengthET;
    private EditText yearET;
    private EditText shortDescriptionET;
    private EditText mpaRatingET;
    private EditText criticRatingET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create ApiService instance
        apiService = retrofit.create(MovieApi.class);
        titleText = (TextView) findViewById(R.id.titleTextTV);
        idET = (EditText) findViewById(R.id.idET);
        addButton = (Button) findViewById(R.id.addBT);
        cancelButton = (Button) findViewById(R.id.cancelBT);
        titleET = (EditText) findViewById(R.id.titleET);
        StudioET = (EditText) findViewById(R.id.StudioET);
        genresET = (EditText) findViewById(R.id.genresET);
        directorsET = (EditText) findViewById(R.id.directorsET);
        writersET = (EditText) findViewById(R.id.writersET);
        actorsET = (EditText) findViewById(R.id.actorsET);
        lengthET = (EditText) findViewById(R.id.lengthET);
        yearET = (EditText) findViewById(R.id.yearET);
        shortDescriptionET = (EditText) findViewById(R.id.shortDescriptionET);
        mpaRatingET = (EditText) findViewById(R.id.mpaRatingET);
        criticRatingET = (EditText) findViewById(R.id.criticRatingET);

        if (getIntent().hasExtra("movie")) {
            movieDTO = (Movie) getIntent().getSerializableExtra("movie");
            populateFieldsWithMovieData();
            addButton.setText("Update");
        } else {
            movieDTO = null;
            addButton.setText("Add");
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMovie();
            }
        });
        cancelButton.setOnClickListener(v->{
            finish();
        });
    }


    private void populateFieldsWithMovieData() {
        idET.setText(movieDTO.getMovieID().toString());
        titleET.setText(movieDTO.getTitle().toString());
        StudioET.setText(movieDTO.getStudio());
        String genres = TextUtils.join(", ", movieDTO.getGenres());
        genresET.setText(genres);
        String directors = TextUtils.join(", ", movieDTO.getDirectors());
        directorsET.setText(directors);
        String writers = TextUtils.join(", ", movieDTO.getWriters());
        writersET.setText(writers);
        String actors = TextUtils.join(", ", movieDTO.getActors());
        actorsET.setText(actors);
        lengthET.setText(String.format("%d", movieDTO.getLength()));
        yearET.setText(String.format("%d", movieDTO.getYear()));
        shortDescriptionET.setText(movieDTO.getShortDescription().toString());
        mpaRatingET.setText(movieDTO.getMpaRating().toString());
        criticRatingET.setText(String.valueOf(movieDTO.getCriticsRating()));
    }

    private void updateMovie() {
        Movie movieReq = new Movie();
        if (movieDTO == null) {
            movieReq.set_id(UUID.randomUUID().toString());
        }
        movieReq.setMovieID(idET.getText().toString().trim());
        movieReq.setTitle(titleET.getText().toString().trim());
        movieReq.setStudio(StudioET.getText().toString().trim());
        movieReq.setGenres(Collections.singletonList(genresET.getText().toString().trim()));
        movieReq.setDirectors(Collections.singletonList(directorsET.getText().toString().trim()));
        movieReq.setWriters(Collections.singletonList(writersET.getText().toString().trim()));
        movieReq.setActors(Collections.singletonList(actorsET.getText().toString().trim()));
        movieReq.setYear(Integer.valueOf(yearET.getText().toString().trim()));
        movieReq.setLength(Integer.valueOf(lengthET.getText().toString().trim()));
        movieReq.setShortDescription(shortDescriptionET.getText().toString().trim());
        movieReq.setMpaRating(mpaRatingET.getText().toString().trim());
        movieReq.setCriticsRating(Double.valueOf(criticRatingET.getText().toString().trim()));
        if (movieDTO != null) {
                // Update existing movie
                Call<Void> call = apiService.updateMovie(movieDTO.get_id(), movieReq);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            finish();
                        } else {
                            Log.e("not","successful");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("failure", t.getLocalizedMessage());
                    }
                });
        } else {
            Call<Void> call = apiService.addMovie(movieReq);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        finish();
                    } else {
                        Log.e("not","successful");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("failure", t.getLocalizedMessage());
                }
            });
        }
    }

}