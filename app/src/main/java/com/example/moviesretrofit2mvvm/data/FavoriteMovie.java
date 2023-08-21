package com.example.moviesretrofit2mvvm.data;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "favorite_movies")
public class FavoriteMovie extends Movie{

    public FavoriteMovie(int movieID, String title, String en_title, String releaseDate, double rating, String description, String posterPath, String bigPosterPath, String trailer) {
        super(movieID, title, en_title, releaseDate, rating, description, posterPath, bigPosterPath, trailer);
    }
    @Ignore
    public FavoriteMovie(Movie movie) {
        super(movie.getMovieID(), movie.getTitle(), movie.getEn_title(), movie.getReleaseDate(), movie.getRating(), movie.getDescription(), movie.getPosterPath(), movie.getBigPosterPath(), movie.getTrailer());
    }
}
