package com.example.westeros.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import com.example.westeros.model.Character
import retrofit2.http.Path

/*
 * BASE_URL is the URL of the API we are asking for resources
 */
private const val BASE_URL = "https://thronesapi.com"

/*
 * Moshi object to parse JSON into kotlin objects
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/*
 * Retrofit object, uses Moshi val to do the conversion
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

/*
 * Api interface that defines the petition
 */
interface ThronesApiService {
    @GET("api/v2/Characters")
    suspend fun getCharacters(): List<Character>

    @GET("api/v2/Characters/{id}")
    suspend fun getCharacter(
        @Path("id") characterId: Int
    ): Character
}

/*
 * Only one instance of the service
 */
object ThronesApi {
    val retrofitService : ThronesApiService by lazy {
        retrofit.create(ThronesApiService::class.java)
    }
}
