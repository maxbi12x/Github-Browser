package com.example.githubbrowser.callingservice

import com.example.githubbrowser.responsemodels.CommitModel
import retrofit.Call
import retrofit.http.GET
import retrofit.http.Path
import retrofit.http.Query

interface CommitService {
    @GET("repos/{owner}/{repo}/commits")
    fun getService(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("sha") sha: String
    ): Call<ArrayList<CommitModel>>
}