package com.example.mlkit

import android.util.LruCache
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

interface TranslateInteractor {
    fun getAllLanguages(): Flow<List<Language>>
    fun translate(text: String, sourceLang: String, targetLang: String): Flow<String>
    suspend fun evictLanguages()
}

class TranslateInteractorImpl @Inject constructor() : TranslateInteractor {
    companion object {
        private const val TRANSLATORS_COUNT = 3
    }

    private val translators =
        object : LruCache<TranslatorOptions, Translator>(TRANSLATORS_COUNT) {
            override fun create(options: TranslatorOptions): Translator {
                return Translation.getClient(options)
            }

            override fun entryRemoved(
                evicted: Boolean,
                key: TranslatorOptions,
                oldValue: Translator,
                newValue: Translator?,
            ) {
                oldValue.close()
            }
        }

    override fun getAllLanguages(): Flow<List<Language>> = flow {
        TranslateLanguage
            .getAllLanguages()
            .map { Language(it) }
            .also { emit(it) }
    }

    override fun translate(
        text: String,
        sourceLang: String,
        targetLang: String
    ): Flow<String> {
        val resultFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
        if (text.isEmpty()) {
            resultFlow.tryEmit("")
        }

        val sourceLangCode = TranslateLanguage.fromLanguageTag(sourceLang) ?: "pl"
        val targetLangCode = TranslateLanguage.fromLanguageTag(targetLang)
        if (sourceLangCode.isNullOrEmpty() || targetLangCode.isNullOrEmpty()) {
            throw Exception("language code are not detectable for $sourceLang or $targetLang")
        }
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLangCode)
            .setTargetLanguage(targetLangCode)
            .build()

        translators[options].downloadModelIfNeeded().continueWithTask { task ->
            if (task.isSuccessful) {
                translators[options].translate(text)
            } else {
                Tasks.forException(task.exception ?: Exception("Error while translating"))
            }
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                resultFlow.tryEmit(task.result)
            } else {
                throw task.exception ?: Exception("Error while translating")
            }
        }

        return resultFlow.asSharedFlow()
    }

    override suspend fun evictLanguages() = withContext(Dispatchers.Default) {
        translators.evictAll()
    }
}

class Language(private val code: String) : Comparable<Language> {
    private val displayName: String
        get() = Locale(code).displayName

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is Language) {
            return false
        }
        val otherLang = other as Language?
        return otherLang!!.code == code
    }

    override fun toString(): String {
        return "$code - $displayName"
    }

    override fun compareTo(other: Language): Int {
        return this.displayName.compareTo(other.displayName)
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}