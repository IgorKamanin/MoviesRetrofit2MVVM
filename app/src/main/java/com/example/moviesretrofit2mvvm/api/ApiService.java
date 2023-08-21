package com.example.moviesretrofit2mvvm.api;

import com.example.moviesretrofit2mvvm.pojo.Example;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("anime?page[limit]=20")
    Observable<Example> getMoviesFromJSON(@Query("sort") String sortBy, @Query("page[offset]") int page);
}
