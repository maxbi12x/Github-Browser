package com.example.githubbrowser.responsemodels

import java.io.Serializable

data class IssueModel(
    val title: String,
    val user: UserModel
) : Serializable