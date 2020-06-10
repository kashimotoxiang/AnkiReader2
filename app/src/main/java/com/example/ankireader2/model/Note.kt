package com.example.ankireader2.model

data class Note(
    val id: Long,
    val tags: String,
    val due: Long,
    val type: Int,
    val fields:HashMap<String,String>
) {}
