package com.samyeung.wherever.util.helper

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.v7.preference.PreferenceManager
import java.util.*


object LanguageManager {
    enum class Language(val language: String, val apiLanguage: String) {
        LANGUAGE_ENGLISH("en", "en"),
        LANGUAGE_ZH("zh_rTW", "tc")
    }


    const val LANGUAGE_KEY = "lang"
    @TargetApi(Build.VERSION_CODES.N)
    fun updateResources(context: Context, language: Language = Language.LANGUAGE_ENGLISH): Context {
        var locale = Locale(language.language)
        if(language.language == "zh_rTW"){
            locale = Locale.TAIWAN
        }
        Locale.setDefault(locale)

        val conf = context.resources.configuration
        conf.setLocale(locale)
        conf.setLayoutDirection(locale)
        return context.createConfigurationContext(conf)
    }
    @SuppressWarnings("deprecation")
    private fun updateResourcesLegacy(context: Context, language: Language): Context {
        val locale = Locale(language.language)
        Locale.setDefault(locale)

        val resources = context.resources

        val configuration = resources.configuration
        configuration.locale = locale
        configuration.setLayoutDirection(locale)

        resources.updateConfiguration(configuration, resources.displayMetrics)

        return context
    }
    fun setNewLocale(
        c: Context,
        language: Language = LanguageManager.Language.LANGUAGE_ENGLISH
    ): Context {
        persistLanguage(c,language)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(c, language)
        }
        return updateResourcesLegacy(c, language)
    }
    fun setLocale(c: Context): Context {
        return updateResources(c, findLanguageByKey(getLanguage(c))!!)
    }
    fun getLanguage(c: Context): String {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(c)
        return sharedPreferences.getString(LANGUAGE_KEY, Language.LANGUAGE_ENGLISH.language)!!
    }

    fun getLanguageAPI(c: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c)
        return sharedPreferences.getString(LANGUAGE_KEY, Language.LANGUAGE_ENGLISH.language)!!
    }

    fun findLanguageByKey(key: String): Language? {
        return when (key) {
            Language.LANGUAGE_ENGLISH.language -> {
                LanguageManager.Language.LANGUAGE_ENGLISH
            }
            Language.LANGUAGE_ZH.language -> {
                LanguageManager.Language.LANGUAGE_ZH
            }
            else -> {
                null
            }
        }
    }

    private fun persistLanguage(c: Context, language: Language) {
        val edit = PreferenceManager
            .getDefaultSharedPreferences(c).edit()
        edit.putString(LANGUAGE_KEY, language.language)
        edit.apply()
    }
}