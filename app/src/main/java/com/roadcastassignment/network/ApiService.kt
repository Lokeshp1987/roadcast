package com.roadcastassignment.network

import com.roadcastassignment.model.EntriesResponse
import com.roadcastassignment.model.Entry
import retrofit2.http.GET

interface ApiService {
    companion object{
        const val BASE_ULR = "https://api.publicapis.org/"
    }

    @GET("entries")
    suspend fun getPost(): EntriesResponse
}