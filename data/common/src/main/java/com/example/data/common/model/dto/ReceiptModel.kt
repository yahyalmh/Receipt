package com.example.data.common.model.dto


data class ReceiptModel(
    var id: String = "",
    val languageId: String = "",
    val text: String = "",
    val translate: String = "",
    val photo: Photo? = null,
)
