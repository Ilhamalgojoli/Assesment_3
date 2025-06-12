package com.ilhamalgojali0081.assesment_3.model

data class Resep(
    val id: Int,
    val title: String,
    val description: String,
    val recipe_writer: String,
    val image_url: String,
    val user_email: String,
    val created_at: String,
    val update_at: String,
    val ingridient: String
)
