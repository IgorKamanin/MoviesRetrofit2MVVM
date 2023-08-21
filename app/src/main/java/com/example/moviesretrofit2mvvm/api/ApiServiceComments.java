package com.example.moviesretrofit2mvvm.api;

import com.example.moviesretrofit2mvvm.pojoComments.Example;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiServiceComments {
//    @GET("media-reactions?filter[animeId]=44081")
    @GET("media-reactions")
    Observable<Example> getCommentsFromJson(@Query("filter[animeId]") int movieID);
}
