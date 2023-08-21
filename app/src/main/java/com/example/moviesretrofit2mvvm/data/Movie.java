package com.example.moviesretrofit2mvvm.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies")
public class Movie {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int movieID;
    private String title;
    private String en_title;
    private String releaseDate;
    private double rating;
    private String description;
    private String posterPath;
    private String bigPosterPath;
    private String trailer;

    public Movie(int id, int movieID, String title, String en_title, String releaseDate, double rating, String description, String posterPath, String bigPosterPath, String trailer) {
        this.id = id;
        this.movieID = movieID;
        this.title = title;
        this.en_title = en_title;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.description = description;
        this.posterPath = posterPath;
        this.bigPosterPath = bigPosterPath;
        this.trailer = trailer;
    }

    @Ignore
    public Movie(int movieID, String title, String en_title, String releaseDate, double rating, String description, String posterPath, String bigPosterPath, String trailer) {
        this.movieID = movieID;
        this.title = title;
        this.en_title = en_title;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.description = description;
        this.posterPath = posterPath;
        this.bigPosterPath = bigPosterPath;
        this.trailer = trailer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEn_title() {
        return en_title;
    }

    public void setEn_title(String en_title) {
        this.en_title = en_title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBigPosterPath() {
        return bigPosterPath;
    }

    public void setBigPosterPath(String bigPosterPath) {
        this.bigPosterPath = bigPosterPath;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }
}
