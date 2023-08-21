package com.example.moviesretrofit2mvvm.screens.favoritescreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.moviesretrofit2mvvm.screens.mainscreen.MainActivity;
import com.example.moviesretrofit2mvvm.adapters.MovieAdapter;
import com.example.moviesretrofit2mvvm.R;
import com.example.moviesretrofit2mvvm.data.FavoriteMovie;
import com.example.moviesretrofit2mvvm.data.Movie;
import com.example.moviesretrofit2mvvm.data.MovieViewModel;
import com.example.moviesretrofit2mvvm.screens.detailscreen.DetailActivity;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {
    private RecyclerView recyclerViewFavorite;
    private MovieAdapter adapter;
    private MovieViewModel viewModel;

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
        setContentView(R.layout.activity_favorite);
        recyclerViewFavorite = findViewById(R.id.recyclerViewFavorite);
        recyclerViewFavorite.setLayoutManager(new GridLayoutManager(this, getColumnCount()));
        adapter = new MovieAdapter();
        recyclerViewFavorite.setAdapter(adapter);
        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        LiveData<List<FavoriteMovie>> favoriteMovies = viewModel.getFavoriteMovies();
        if (favoriteMovies != null) {
            favoriteMovies.observe(this, new Observer<List<FavoriteMovie>>() {
                @Override
                public void onChanged(List<FavoriteMovie> favoriteMovies) {
                    List<Movie> movies = new ArrayList<>(favoriteMovies);

                    adapter.setMovies(movies);
                }
            });
        }
        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = adapter.getMovies().get(position);
                if (viewModel.getMovieByID(movie.getMovieID()) == null) {
                    viewModel.insertMovie(movie);
                }
                Intent intent = new Intent(FavoriteActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getMovieID());
                startActivity(intent);
            }
        });
    }
}