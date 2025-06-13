package com.ilhamalgojali0081.assesment_3.network

import android.content.Context
import com.ilhamalgojali0081.assesment_3.model.Resep
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://resep-makanan.azurewebsites.net/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

class ResepApi(appContext: Context) {
    private val applicationContext = appContext.applicationContext

    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        val userEmail = runBlocking {
            UserDataStore(applicationContext).userFlow.first().email
        }
        if (userEmail.isNotEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $userEmail")
        }
        chain.proceed(requestBuilder.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .build()

    val service: ResepApiService = retrofit.create(ResepApiService::class.java)
}

interface ResepApiService {
    @GET("recipes")
    suspend fun getRecipes(): List<Resep>

    @GET("recipes/{id}")
    suspend fun getRecipeById(@Path("id") id: String): Resep

    @Multipart
    @POST("recipes")
    suspend fun addRecipe(
        @Part("recipeData") recipeData: RequestBody,
        @Part image: MultipartBody.Part
    ): Resep

    @Multipart
    @PUT("recipes/{id}")
    suspend fun updateRecipe(
        @Path("id") id: String,
        @Part("recipeData") recipeData: RequestBody,
        @Part image: MultipartBody.Part?
    ): Resep

    @DELETE("recipes/{id}")
    suspend fun deleteRecipe(@Path("id") id: String)
}
