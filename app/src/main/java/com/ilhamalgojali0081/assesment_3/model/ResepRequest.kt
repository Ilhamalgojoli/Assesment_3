package com.ilhamalgojali0081.assesment_3.model

data class ResepRequest(
    val title: String,
    val description: String,
    val ingredients: String,
    val image_url: String?,
    val user_email: String,
    val user_name: String
)
