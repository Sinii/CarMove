package com.example.jobs

data class Job(
    val id: String,
    val type: String,
    val url: String?,
    val created_at: String,
    val company: String,
    val company_url: String?,
    val location: String?,
    val title: String,
    val description: String,
    val how_to_apply: String?,
    val company_logo: String?
)

//{"id":"ca4d9187-66dd-4dc4-83a6-028246007229",
// "type":"Full Time",
// "url":"https://jobs.github.com/positions/ca4d9187-66dd-4dc4-83a6-028246007229",
// "created_at":"Sat Aug 10 14:34:18 UTC 2019",
// "company":"CrowdTwist",
// "company_url":null,
// "location":"New York, NY, US",
// "title":"Software Engineer",
// "description"