package com.example.githubbrowser.responsemodels

import java.io.Serializable

data class UserModel(
    val login: String,
    val avatar_url: String
) : Serializable