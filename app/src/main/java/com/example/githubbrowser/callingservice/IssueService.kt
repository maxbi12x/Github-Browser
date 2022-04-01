package com.example.githubbrowser.callingservice

import com.example.githubbrowser.responsemodels.IssueModel
import retrofit.Call
import retrofit.http.GET
import retrofit.http.Path

interface IssueService {
    @GET("repos/{owner}/{repo}/issues?state=open")
    fun getService(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Call<ArrayList<IssueModel>>
}