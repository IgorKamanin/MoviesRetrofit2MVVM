package com.example.moviesretrofit2mvvm.screens.mainscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviesretrofit2mvvm.adapters.MovieAdapter;
import com.example.moviesretrofit2mvvm.R;
import com.example.moviesretrofit2mvvm.data.Movie;
import com.example.moviesretrofit2mvvm.data.MovieViewModel;
import com.example.moviesretrofit2mvvm.screens.detailscreen.DetailActivity;
import com.example.moviesretrofit2mvvm.screens.favoritescreen.FavoriteActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SwitchCompat switchSort;
    private TextView textViewPopular;
    private TextView textViewTopRated;
    private ImageView imageViewBack;
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private LiveData<List<Movie>> movies;
    private LiveData<Throwable> errors;
    private MovieViewModel viewModel;

    private static int page;
    private static boolean isTopRated;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavorite:
                Intent intentToFavorite = new Intent(this, FavoriteActivity.class);
                startActivity(intentToFavorite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getColumnCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int)(displayMetrics.widthPixels / displayMetrics.density);
        return width/284 > 2 ? width/284 : 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        page = 0;
        isTopRated = false;

        switchSort = findViewById(R.id.switchSort);
        textViewPopular = findViewById(R.id.textViewPopular);
        textViewPopular.setEnabled(false);
        textViewPopular.setTextColor(Color.CYAN);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        textViewTopRated.setTextColor(Color.WHITE);
        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewBack.setVisibility(View.INVISIBLE);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, getColumnCount()));
        adapter = new MovieAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        viewModel.loadData("-averageRating", 0);
        movies = viewModel.getMovies();
        movies.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                adapter.setMovies(movies);
            }
        });

        errors = viewModel.getErrors();
        errors.observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(Throwable throwable) {
                if (throwable != null) {
                    viewModel.clearErrors();
                    Toast.makeText(MainActivity.this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });


        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                page = 0;
                imageViewBack.setVisibility(View.INVISIBLE);
                isTopRated = isChecked;
                setSortMethod(isChecked);
            }
        });


        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = adapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getMovieID());
                startActivity(intent);
            }
        });
    }

    private void setSortMethod(boolean isTopRated) {
        if (isTopRated) {
            viewModel.loadData("-averageRating,youtubeVideoId", page);
            textViewPopular.setTextColor(Color.WHITE);
            textViewTopRated.setTextColor(Color.CYAN);
            textViewTopRated.setEnabled(false);
            textViewPopular.setEnabled(true);

        } else {
            viewModel.loadData("popularityRank,youtubeVideoId", page);
            textViewPopular.setTextColor(Color.CYAN);
            textViewTopRated.setTextColor(Color.WHITE);
            textViewTopRated.setEnabled(true);
            textViewPopular.setEnabled(false);
        }
        recyclerView.smoothScrollToPosition(0);
    }

    public void onClickForward(View view) {
        if (page == 0) {
            imageViewBack.setVisibility(View.VISIBLE);
        }
        page+=20;
        setSortMethod(isTopRated);
    }

    public void onClickBack(View view) {
        page-=20;
        if (page < 0) {
            page =0;
        }
        if (page == 0) {
            imageViewBack.setVisibility(View.INVISIBLE);
        }
        setSortMethod(isTopRated);
    }


    public void onClickSetPopular(View view) {
        page = 0;
        imageViewBack.setVisibility(View.INVISIBLE);
        isTopRated = false;
        setSortMethod(isTopRated);
        switchSort.setChecked(false);
    }

    public void onClickSetTopRated(View view) {
        page = 0;
        imageViewBack.setVisibility(View.INVISIBLE);
        isTopRated = true;
        setSortMethod(isTopRated);
        switchSort.setChecked(true);
    }




}