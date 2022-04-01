package com.example.githubbrowser.responsemodels

import java.io.Serializable

data class CommitModel(
    val commit: Commit,
    val author: Author,
    val committer: Author
) : Serializable