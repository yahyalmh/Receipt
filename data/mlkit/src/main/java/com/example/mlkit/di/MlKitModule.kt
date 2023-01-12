package com.example.mlkit.di

import com.example.mlkit.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface MlKitModule {

    @Binds
    fun bindTextRecognitionInteractor(
        textRecognizerInteractorImpl: TextRecognizerInteractorImpl
    ): TextRecognitionInteractor

    @Binds
    fun bindLanguageIdInteractor(
        languageIdInteractorImpl: LanguageIdInteractorImpl
    ): LanguageIdInteractor

    @Binds
    @Singleton
    fun bindTranslateInteractor(
        translateInteractorImpl: TranslateInteractorImpl
    ): TranslateInteractor
}