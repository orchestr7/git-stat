/**
 * Utilities to interact with Github API
 */

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

/**
 * this github API provides information about the repositories in the organization and user account
 * pagination
 */
fun String.reposEndPoint() =
    "https://api.github.com/users/$this/repos?per_page=$PAGINATION_SIZE"
