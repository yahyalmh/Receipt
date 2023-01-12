package com.example.mlkit

import com.google.mlkit.nl.languageid.IdentifiedLanguage
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.languageid.LanguageIdentifier
import com.google.mlkit.nl.languageid.LanguageIdentifier.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject


interface LanguageIdInteractor {
    suspend fun identifyLanguage(
        text: String,
        confidence: Float = DEFAULT_IDENTIFY_LANGUAGE_CONFIDENCE_THRESHOLD
    ): Flow<String>

    fun identifyPossibleLanguages(
        text: String,
        confidence: Float = DEFAULT_IDENTIFY_POSSIBLE_LANGUAGES_CONFIDENCE_THRESHOLD
    ): Flow<List<IdentifiedLanguage>>
}

class LanguageIdInteractorImpl @Inject constructor() : LanguageIdInteractor {

    override suspend fun identifyLanguage(
        text: String,
        confidence: Float
    ): Flow<String> =
        withContext(Dispatchers.IO) {
            val resultFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
            val languageIdentifier = getLanguageIdentifier(confidence)

            languageIdentifier.identifyLanguage(text)
                .addOnSuccessListener { languageCode ->
                    if (languageCode == UNDETERMINED_LANGUAGE_TAG) {

                        languageIdentifier.identifyPossibleLanguages(text)
                            .addOnSuccessListener { identifyLanguages ->
                                identifyLanguages.sortByDescending { it.confidence }
                                resultFlow.tryEmit(identifyLanguages.first().languageTag)
                            }
                            .addOnFailureListener { e -> throw(e) }

                    } else {
                        val t = Locale(languageCode).displayName
                        resultFlow.tryEmit(languageCode)
                    }
                }
                .addOnFailureListener { e -> throw(e) }
            resultFlow.asSharedFlow()
        }

    override fun identifyPossibleLanguages(
        text: String,
        confidence: Float
    ): Flow<List<IdentifiedLanguage>> {
        val languageIdentifier = getLanguageIdentifier(confidence)

        val resultFlow = MutableSharedFlow<List<IdentifiedLanguage>>(extraBufferCapacity = 1)
        languageIdentifier.identifyPossibleLanguages(text)
            .addOnSuccessListener { identifyLanguages -> resultFlow.tryEmit(identifyLanguages) }
            .addOnFailureListener { e -> throw(e) }
        return resultFlow.asSharedFlow()
    }

    private fun getLanguageIdentifier(confidence: Float): LanguageIdentifier {
        val identifierOptions = LanguageIdentificationOptions
            .Builder()
            .setConfidenceThreshold(confidence)
            .build()
        return LanguageIdentification.getClient(identifierOptions)
    }
}

