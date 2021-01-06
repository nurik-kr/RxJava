package kg.nurik.rxjava.data.remote

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import kg.nurik.rxjava.data.model.SearchModel
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInterface {

    @GET(".")
    fun searchFilm(
        @Query("apikey") api: String,
        @Query("s") query: String
    ): Flowable<SearchModel> //observable in RxJava    single потому что от rxJava //single на flowable умеет работать с переполнением запросов

    @GET(".")
    fun searchFilm2(
        @Query("apikey") api: String,
        @Query("s") query: String
    ): Observable<SearchModel> //подходит для concat
}