
package com.scriptsquad.unitalk.network

import com.scriptsquad.unitalk.Ai_Page.AI_Activity
import com.scriptsquad.unitalk.Ai_Page.Ai_Prompt_Response_Pair
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("/generate")
    suspend fun generateResponse(@Body request: AI_Activity.RequestBody): Ai_Prompt_Response_Pair // Make sure this matches your API response


    @GET("/responses")
    suspend fun getResponses(): List<Ai_Prompt_Response_Pair>
}
