package com.example.githubbrowser.callingservice

import com.example.githubbrowser.responsemodels.RepoModel
import retrofit.Call
import retrofit.http.GET
import retrofit.http.Path

interface RepoService {
    @GET("repos/{owner}/{repo}")
    fun getService(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Call<RepoModel>
}