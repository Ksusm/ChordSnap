package cz.mendelu.examtemplate2025.api

import cz.mendelu.examtemplate2025.models.ParksResponse
import retrofit2.Response
import retrofit2.http.GET

interface ParksApi {

    @GET("parks.json")
    suspend fun getParks(): Response<ParksResponse>

//    @GET("tips.json")
   // suspend fun getTips(): Response<TipsResponse>
}