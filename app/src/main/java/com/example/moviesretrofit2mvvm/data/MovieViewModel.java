package com.example.moviesretrofit2mvvm.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moviesretrofit2mvvm.api.ApiFactory;
import com.example.moviesretrofit2mvvm.api.ApiService;
import com.example.moviesretrofit2mvvm.api.ApiServiceComments;
import com.example.moviesretrofit2mvvm.pojo.Datum;
import com.example.moviesretrofit2mvvm.pojo.Example;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MovieViewModel extends AndroidViewModel {
    private MovieDatabase database;
    private LiveData<List<Movie>> movies;
    private LiveData<List<FavoriteMovie>> favoriteMovies;
    private LiveData<List<Comment>> comments;



    private MutableLiveData<Throwable> errors;


    private ExecutorService executor;
    private Disposable disposable;
    private Disposable disposableComments;
    ApiFactory apiFactory;



    public MovieViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        movies = database.movieDao().getAllMovies();
        favoriteMovies = database.movieDao().getAllFavoriteMovies();
        comments = database.movieDao().getAllComments();
        executor = Executors.newFixedThreadPool(11);
        apiFactory = ApiFactory.getInstance();
        errors = new MutableLiveData<>();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }



    public void insertMovie (Movie movie){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                database.movieDao().insertMovie(movie);
            }
        });
    }

    public void deleteMovie(Movie movie) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                database.movieDao().deleteMovie(movie);
            }
        });
    }

    public void deleteAllMovies() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                database.movieDao().deleteAllMovies();
            }
        });
    }

    public Movie getMovieByID(int idMovie) {
        Movie movie = null;
        Future<Movie> future = executor.submit(new Callable<Movie>() {
            @Override
            public Movie call() throws Exception {
                return database.movieDao().getMovieByID(idMovie);
            }
        });
        try {
            movie = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    return movie;
    }

    public LiveData<List<FavoriteMovie>> getFavoriteMovies() {
        return favoriteMovies;
    }

    public void insertFavoriteMovie(FavoriteMovie favoriteMovie) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                database.movieDao().insertFavoriteMovie(favoriteMovie);
            }
        });
    }

    public void  deleteFavoriteMovie(FavoriteMovie favoriteMovie) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                database.movieDao().deleteFavoriteMovie(favoriteMovie);
            }
        });
    }

    public FavoriteMovie getFavoriteMovieByID(int movieID) {
        Future<FavoriteMovie> future = executor.submit(new Callable<FavoriteMovie>() {
            @Override
            public FavoriteMovie call() throws Exception {
                return database.movieDao().getFavoriteMovieByID(movieID);
            }
        });
        try {
            return future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    public LiveData<List<Comment>> getComments() {
        return comments;
    }

    private void insertComment(Comment comment) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                database.movieDao().insertComment(comment);
            }
        });
    }

    private void deleteAllComments() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                database.movieDao().deleteAllComments();
            }
        });
    }

    public void loadData(String sortBy, int page) {
        ApiService apiService = apiFactory.getApiService();
        disposable = apiService.getMoviesFromJSON(sortBy, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Example>() {
                    @Override
                    public void accept(Example example) throws Exception {
                        deleteAllMovies();
                        List<Datum> datumArrayList = example.getData();
                        for (Datum datum : datumArrayList){
                            int movieID = Integer.parseInt(datum.getId());
                            String title = datum.getAttributes().getSlug();
                            String en_title = datum.getAttributes().getTitles().getEn();
                            if (en_title == null) {
                                en_title = "отсутствует";
                            }
                            String releaseDate = datum.getAttributes().getCreatedAt();
                            double rating = Double.parseDouble(datum.getAttributes().getAverageRating());
                            String description = datum.getAttributes().getDescription();
                            String posterPath = datum.getAttributes().getPosterImage().getMedium();
                            String bigPosterPath = datum.getAttributes().getPosterImage().getLarge();
                            String trailer = datum.getAttributes().getYoutubeVideoId();
                            if (trailer == null) {
                                trailer = "Видео отсутствует";
                            } else {
                                trailer = "https://www.youtube.com/watch?v="+trailer;
                            }

                            Movie movie = new Movie(movieID, title, en_title, releaseDate, rating, description, posterPath, bigPosterPath, trailer);
                            insertMovie(movie);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        errors.setValue(throwable);
                    }
                });
    }

    public void clearErrors() {
        errors.setValue(null);
    }

    public void loadComments(int movieID) {
        ApiServiceComments apiServiceComments = apiFactory.getApiServiceComments();
        disposableComments = apiServiceComments.getCommentsFromJson(movieID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<com.example.moviesretrofit2mvvm.pojoComments.Example>() {
                    @Override
                    public void accept(com.example.moviesretrofit2mvvm.pojoComments.Example example) throws Exception {
                        deleteAllComments();
                        List<com.example.moviesretrofit2mvvm.pojoComments.Datum> datumList = example.getData();
                        for (com.example.moviesretrofit2mvvm.pojoComments.Datum d : datumList) {
                            String authorLink = d.getRelationships().getUser().getLinks().getRelated();
                            String author = loadAuthor(authorLink);
                            String reaction = d.getAttributes().getReaction();
                            Comment comment = new Comment(author, reaction);
                            insertComment(comment);
                        }


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        errors.setValue(throwable);
                    }
                });
    }

    private String loadAuthor(String authorLink) {
        if (authorLink != null) {
            Future<String> future = executor.submit(new Callable<String>() {
                @Override
                public String call() {
                    URL url = null;
                    HttpURLConnection connection = null;
                    StringBuilder builder = new StringBuilder();
                    String author = "---";
                    try {
                        url = new URL(authorLink);
                        connection = (HttpURLConnection) url.openConnection();
                        InputStream stream = connection.getInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(stream);
                        BufferedReader reader = new BufferedReader(inputStreamReader);
                        String line = reader.readLine();
                        while (line != null) {
                            builder.append(line);
                            line = reader.readLine();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(builder.toString());
                        author = jsonObject.getJSONObject("data").getJSONObject("attributes").getString("name");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return author;
                }
            });
            try {
                return future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "---";
    }



    @Override
    protected void onCleared() {
        if (disposable != null) {
            disposable.dispose();
        }
        if(disposableComments != null) {
            disposableComments.dispose();
        }
        executor.shutdown();
        super.onCleared();
    }

    public LiveData<Throwable> getErrors() {
        return errors;
    }

}
