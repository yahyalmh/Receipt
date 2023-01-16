package com.example.scan.result

import com.example.data.common.model.dto.Photo
import com.example.data.common.model.dto.ReceiptModel
import com.example.scan.util.SuperclassExclusionStrategy
import com.google.gson.GsonBuilder
import com.google.mlkit.vision.text.Text

data class MLResult(val text: Text, val languageId: String, val translate: String)

fun MLResult.toReceiptModel(photoLocalUri: String): ReceiptModel {
    val gsonBuilder = GsonBuilder().apply {
        addDeserializationExclusionStrategy(SuperclassExclusionStrategy())
        addSerializationExclusionStrategy(SuperclassExclusionStrategy())
    }
    return ReceiptModel(
        languageId = languageId,
        text = gsonBuilder.create().toJson(text),
        translate = translate,
        photo = Photo(
            localUri = photoLocalUri, timestamp = System.currentTimeMillis().toString()
        )
    )
}