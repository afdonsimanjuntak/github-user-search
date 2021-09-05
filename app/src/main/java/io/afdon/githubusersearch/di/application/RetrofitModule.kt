package io.afdon.githubusersearch.di.application

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import io.afdon.githubusersearch.BuildConfig
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object RetrofitModule {

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, converter: Converter.Factory): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(converter)
            .build()

    @Singleton
    @Provides
    fun provideOkHttpClient(cache: Cache, interceptor: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(interceptor)
            .build()

    @Singleton
    @Provides
    fun provideCache(context: Context): Cache {
        val cacheSize = (5 * 1024 * 1024).toLong()
        return Cache(context.cacheDir, cacheSize)
    }

    @Singleton
    @Provides
    fun provideInterceptor(): Interceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Singleton
    @Provides
    fun provideGsonConverter(): Converter.Factory = GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideGson() : Gson = Gson()
}