package com.ilhamalgojali0081.assesment_3.network

import com.ilhamalgojali0081.assesment_3.model.OpStatus
import com.ilhamalgojali0081.assesment_3.model.Resep
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

private const val BASE_URL = "https://xhplpylngwijhzoaemkq.supabase.co/rest/v1/"
private const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6" +
        "InhocGxweWxuZ3dpamh6b2FlbWtxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDk2OTg0OTAsImV4cCI6MjA2NT" +
        "I3NDQ5MH0.tIn3D-JRfHJJSZqKOj3NWoyC_sIUXafOAFzzhzXbEG8"

private fun apiKeyAsHeader(it: Interceptor.Chain) = it.proceed(
    it.request()
        .newBuilder()
        .addHeader("apikey", API_KEY)
        .addHeader("Authorization", "Bearer $API_KEY")
        .addHeader("Content-Type", "application/json")
        .build()
)

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor{ apiKeyAsHeader(it) }
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ResepApiService {
    @GET("recipes")
    suspend fun getRecipes(): List<Resep>

    @POST("recipes")
    suspend fun storeRecipes(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("recipe_writer") recipe_writer: RequestBody,
        @Part imageUrl: MultipartBody.Part,
        @Part("user_email") userEmail: RequestBody,
        @Part("created_at") createdAt: RequestBody,
        @Part("ingridient") ingridient: RequestBody,
    ): OpStatus

    @PUT("recipes/{id}")
    suspend fun editRecipes(
        @Path("id") id: Int,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part image_url: MultipartBody.Part,
        @Part("update_at") updateAt: RequestBody,
        @Part("ingridient") ingridient: RequestBody
    ):OpStatus

    @DELETE("recipes/{id}")
    suspend fun deleteRecipes(
        @Path("id")id: Int,
        @Body resep: Resep
    ): OpStatus
}

object ResepApi{
    val service: ResepApiService by lazy {
        retrofit.create(ResepApiService::class.java)
    }
}

enum class ApiStatus{ LOADING, SUCCESS, FAILED }
