package com.example.reto12.data

import com.google.gson.annotations.SerializedName

data class GitHubUser(
    @SerializedName("login")
    val login: String,

    @SerializedName("id")
    val id: Long,

    @SerializedName("avatar_url")
    val avatarUrl: String,

    @SerializedName("name")
    val name: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("bio")
    val bio: String?,

    @SerializedName("location")
    val location: String?,

    @SerializedName("public_repos")
    val publicRepos: Int,

    @SerializedName("followers")
    val followers: Int,

    @SerializedName("following")
    val following: Int
)

