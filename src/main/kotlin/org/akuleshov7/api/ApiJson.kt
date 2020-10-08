package org.akuleshov7.api

import kotlinx.serialization.*

@Serializable
data class StargazersListJson(val list:List<StargazersJson>)

@Serializable
data class StargazersJson(val login: String, val id: String)

@Serializable
data class ReposJson(val full_name: String, val fork: Boolean)



