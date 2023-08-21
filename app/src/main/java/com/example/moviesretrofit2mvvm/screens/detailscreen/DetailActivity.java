package com.example.moviesretrofit2mvvm.screens.detailscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviesretrofit2mvvm.adapters.CommentsAdapter;
import com.example.moviesretrofit2mvvm.screens.favoritescreen.FavoriteActivity;
import com.example.moviesretrofit2mvvm.screens.mainscreen.MainActivity;
import com.example.moviesretrofit2mvvm.R;
import com.example.moviesretrofit2mvvm.data.Comment;
import com.example.moviesretrofit2mvvm.data.FavoriteMovie;
import com.example.moviesretrofit2mvvm.data.Movie;
import com.example.moviesretrofit2mvvm.data.MovieViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private ImageView imageViewBigPoster;
    private ImageView imageViewFavorite;
    private TextView textViewTitle;
    private TextView textViewEn_title;
    private TextView textViewReleaseDate;
    private TextView textViewRating;
    private TextView textViewDescription;
    private ImageView imageViewTrailer;
    private RecyclerView recyclerViewComments;
    private CommentsAdapter adapter;
    private TextView textViewShowComments;
    private MovieViewModel viewModel;
    private LiveData<List<Comment>> comments;
    private LiveData<Throwable> errors;
    private List<Comment> commentList = new ArrayList<>();
    private Movie movie;
    private int movieID;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =  new MenuInflater(this);
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent (this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavorite:
                Intent intentToFav = new Intent(this, FavoriteActivity.class);
                startActivity(intentToFav);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        imageViewFavorite = findViewById(R.id.imageViewFavorite);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewEn_title = findViewById(R.id.textViewEn_title);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewRating = findViewById(R.id.textViewRating);
        textViewDescription = findViewById(R.id.textViewDescription);
        imageViewTrailer = findViewById(R.id.imageViewTrailer); //можно не инициализировать?
        textViewShowComments  =findViewById(R.id.textViewShowComments);
        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentsAdapter();
        recyclerViewComments.setAdapter(adapter);
        adapter.setComments(commentList);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            movieID = intent.getIntExtra("id", -1);
            viewModel = new ViewModelProvider(this).get(MovieViewModel.class);
            movie = viewModel.getMovieByID(movieID);
            Picasso.get().load(movie.getBigPosterPath()).into(imageViewBigPoster);
            textViewTitle.setText(movie.getTitle());
            textViewEn_title.setText(movie.getEn_title());
            textViewReleaseDate.setText(movie.getReleaseDate());
            textViewRating.setText(Double.toString(movie.getRating()));
            textViewDescription.setText(movie.getDescription());
            if(viewModel.getFavoriteMovieByID(movieID) == null) {
                imageViewFavorite.setImageResource(R.drawable.favourite_add_to);
            } else {
                imageViewFavorite.setImageResource(R.drawable.favourite_remove);
            }
        } else {
            finish();
        }


    }

    public void onClickPlayTrailer(View view) {
        if (movie.getTrailer().equals(getString(R.string.no_trailer))) {
            Toast.makeText(this, "Видео отсутствует", Toast.LENGTH_SHORT).show();
        } else {
            Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getTrailer()));
            startActivity(trailerIntent);
        }
    }

    public void onClickShowComments(View view) {
        if (movie != null) {
            textViewShowComments.setVisibility(View.GONE);
            comments = viewModel.getComments();
            comments.observe(this, new Observer<List<Comment>>() {
                @Override
                public void onChanged(List<Comment> comments) {
                    adapter.setComments(comments);
                }
            });
            errors = viewModel.getErrors();
            errors.observe(this, new Observer<Throwable>() {
                @Override
                public void onChanged(Throwable throwable) {
                    if (throwable != null) {
                        viewModel.clearErrors();
                        Toast.makeText(DetailActivity.this, R.string.failed_comments, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            viewModel.loadComments(movieID);

        }
    }

    public void onClickFavorite(View view) {
        FavoriteMovie favoriteMovie = viewModel.getFavoriteMovieByID(movieID);
        if (favoriteMovie == null) {
            viewModel.insertFavoriteMovie(new FavoriteMovie(movie));
            imageViewFavorite.setImageResource(R.drawable.favourite_remove);
            Toast.makeText(this, R.string.added_to_fav, Toast.LENGTH_SHORT).show();
        } else {
            viewModel.deleteFavoriteMovie(favoriteMovie);
            imageViewFavorite.setImageResource(R.drawable.favourite_add_to);
            Toast.makeText(this, R.string.removed_from_fav, Toast.LENGTH_SHORT).show();
        }
    }
}