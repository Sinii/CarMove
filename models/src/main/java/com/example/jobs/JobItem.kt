package com.example.jobs

data class JobItem(
    val jobArticle: String,
    val company: String,
    val location: String?,
    val companyLogoUrl: String?
)