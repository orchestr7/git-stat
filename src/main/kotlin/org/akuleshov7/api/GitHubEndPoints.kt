package org.akuleshov7.api

// FixMe: move to configuration file
// github maximum for pagination is 100 on github, default is 30
const val PAGINATION_SIZE = 100

/**
 * this github API provides information about stargazers - main fields are: "login" and "id"
 * pagination
 */
fun String.stargazersEndPoint() =
    "https://api.github.com/repos/$this/stargazers?per_page=$PAGINATION_SIZE"
