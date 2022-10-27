package com.tulinova.olgaforecast.data

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.readystatesoftware.chuck.ChuckInterceptor
import com.tulinova.olgaforecast.data.response.CurrentWeatherResponse
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "84b26273242b3de0fc7e008873f14fe5"

interface ApixuWeatherApiService {

    @GET("weather")
    fun getCurrentWeather(
        @Query("q") location: String,
        @Query("lang") language: String = "en"
    ): Deferred<CurrentWeatherResponse>

    companion object{
        operator fun invoke(context:Context?):ApixuWeatherApiService{
            val requestInterceptor = Interceptor { chain ->
                val url = chain.request()
                    .url().newBuilder().addQueryParameter("appid", API_KEY).build()
                val request = chain.request().newBuilder().url(url).build()
                return@Interceptor chain.proceed(request)
            }
            val okHttpClient = OkHttpClient.Builder().addInterceptor(requestInterceptor)
            if(context!= null) {
                okHttpClient.addInterceptor(ChuckInterceptor(context))
            }

            return Retrofit.Builder().client(okHttpClient.build()).baseUrl("https://api.openweathermap.org/data/2.5/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApixuWeatherApiService::class.java)
        }

    }


}