package com.jeromedusanter.restorik.core.model

data class RecentSearch(
    val id: Int,
    val query: String,
    val timestamp: Long,
)
