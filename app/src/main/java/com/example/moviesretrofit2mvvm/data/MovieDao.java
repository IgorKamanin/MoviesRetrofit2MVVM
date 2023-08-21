package com.example.moviesretrofit2mvvm.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getAllMovies();

    @Insert
    void insertMovie (Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Query("DELETE FROM movies")
    void deleteAllMovies();

   @Query("SELECT * FROM movies WHERE movieID ==:idMovie")
    Movie getMovieByID(int idMovie);

   @Query("SELECT * FROM favorite_movies")
   LiveData<List<FavoriteMovie>> getAllFavoriteMovies();

   @Insert
    void insertFavoriteMovie(FavoriteMovie favoriteMovie);

   @Delete
    void deleteFavoriteMovie (FavoriteMovie favoriteMovie);

   //не реализовал во ViewModel
   @Query("DELETE FROM favorite_movies")
    void deleteAllFavoriteMovies();

   @Query("SELECT * FROM favorite_movies WHERE movieID ==:idMovie")
   FavoriteMovie getFavoriteMovieByID(int idMovie);

   @Query("SELECT * FROM comments")
    LiveData<List<Comment>> getAllComments();

   @Insert
    void insertComment(Comment comment);

   @Query("DELETE FROM comments")
    void deleteAllComments();


}
