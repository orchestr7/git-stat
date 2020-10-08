package org.akuleshov7.api

// FixMe: move to configuration file
const val PAGINATION_SIZE = 100000

/**
 * this github API provides information about stargazers - main fields are: "login" and "id"
 * pagination
 */
fun String.stargazers() =
    "https://api.github.com/repos/$this/stargazers?per_page=$PAGINATION_SIZE"
