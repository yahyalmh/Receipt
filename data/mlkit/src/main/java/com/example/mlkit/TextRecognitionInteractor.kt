package com.example.mlkit

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.Text.Element
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

interface TextRecognitionInteractor {
    fun recognizeText(image: InputImage): Flow<Text>
}

class TextRecognizerInteractorImpl @Inject constructor() : TextRecognitionInteractor {

    override fun recognizeText(image: InputImage): Flow<Text> {
        val resultFlow = MutableSharedFlow<Text>(extraBufferCapacity = 1)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
            .addOnSuccessListener {
                it.textBlocks.map { t -> t.cornerPoints?.asList() }
                resultFlow.tryEmit(it)
            }
            .addOnFailureListener { throw(it) }

        return resultFlow.asSharedFlow()
    }


    private fun processTextBlock(result: Text) {
        val resultText = result.text
        for (block in result.textBlocks) {
            val blockText = block.text
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox
            for (line in block.lines) {
                val lineText = line.text
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                for (element in line.elements) {
                    val elementText = element.text
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                }
            }
        }
    }
}



