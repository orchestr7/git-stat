/**
 * Classes representing Github API model, ready for serialization
 */

package org.akuleshov7.api

import kotlinx.serialization.*

@Serializable
data class StargazersListJson(val list: List<StargazersJson>)

@Serializable
data class StargazersJson(val login: String, val id: String)

@Serializable
data class ReposJson(@SerialName("full_name") val fullName: String, @SerialName("fork") val isFork: Boolean)
