package com.example.githubbrowser.callingservice

import com.example.githubbrowser.responsemodels.Branches
import retrofit.Call
import retrofit.http.GET
import retrofit.http.Path

interface BranchService {
    @GET("repos/{owner}/{repo}/branches")
    fun getService(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): Call<ArrayList<Branches>>
}