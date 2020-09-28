/*
 *   Copyright © 2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.utils

import android.content.Context
import com.pspdfkit.catalog.PSPDFExample
import com.pspdfkit.catalog.PSPDFExample.ExampleLanguage
import com.pspdfkit.catalog.examples.java.instant.InstantExample

/**
 * Map of Java example class to Kotlin example class for examples that are not
 * in the default packages (i.e. com.pspdfkit.catalog.examples.java/kotlin)
 */
private val MANUAL_EXAMPLES_LANGUAGE_MAPPING = listOf(
    InstantExample::class.java to com.pspdfkit.catalog.examples.kotlin.instant.InstantExample::class.java
)

/**
 * Expands examples list with examples written in both supported languages.
 */
fun addExamplesForSupportedLanguagesIfNotPresent(context: Context, originalExamples: List<PSPDFExample.Section>): List<PSPDFExample.Section> {
    val updatedExamples = ArrayList<PSPDFExample.Section>(originalExamples.size)
    for (section in originalExamples) {
        val updatedSection = ArrayList<PSPDFExample>(section.size)
        for (pspdfExample in section) {
            updatedSection.add(pspdfExample)

            // Try to add example in the other language.
            val languageToAdd = pspdfExample.exampleLanguage.getOtherLanguage()
            val exampleClassToAdd = pspdfExample.getExampleClass(languageToAdd) ?: continue

            // Make sure that the example class is not already present in the section.
            if (section.find { it.javaClass == exampleClassToAdd } != null) continue

            // Crete new instance of the example.
            updatedSection.add(newExampleInstance(exampleClassToAdd, context) ?: continue)
        }
        updatedExamples.add(PSPDFExample.Section(section.name, updatedSection))
    }
    return updatedExamples
}

/**
 * Returns `true` if this example is available in both Java and Kotlin.
 */
fun PSPDFExample.isAvailableInBothLanguages(): Boolean {
    return if (exampleLanguage == ExampleLanguage.JAVA) {
        getExampleClass(ExampleLanguage.KOTLIN) != null
    } else {
        getExampleClass(ExampleLanguage.JAVA) != null
    }
}

/**
 * Return class of this example in the requested `requiredLanguage`.
 */
private fun PSPDFExample.getExampleClass(requiredLanguage: ExampleLanguage): Class<out PSPDFExample?>? {
    val language: ExampleLanguage = exampleLanguage
    return if (language == requiredLanguage) {
        this.javaClass
    } else try {
        // First look in language mappings, then into our convention packages.
        return getClassFromLanguageMapping(this.javaClass, requiredLanguage) ?: run {
            val className: String = this.javaClass.simpleName
            val exampleClass: Class<*>
            exampleClass = when (requiredLanguage) {
                ExampleLanguage.KOTLIN -> Class.forName("com.pspdfkit.catalog.examples.kotlin.$className")
                ExampleLanguage.JAVA -> Class.forName("com.pspdfkit.catalog.examples.java.$className")
            }
            @Suppress("UNCHECKED_CAST")
            exampleClass as Class<PSPDFExample?>
        }
    } catch (e: Throwable) {
        null
    }
}

private fun getClassFromLanguageMapping(javaClass: Class<PSPDFExample>, requiredLanguage: ExampleLanguage): Class<out PSPDFExample?>? {
    val mappingRule = MANUAL_EXAMPLES_LANGUAGE_MAPPING.find { it.first == javaClass || it.second == javaClass }
        ?: return null
    return if (requiredLanguage == ExampleLanguage.JAVA) mappingRule.first else mappingRule.second
}

private fun ExampleLanguage.getOtherLanguage(): ExampleLanguage {
    return if (this == ExampleLanguage.JAVA) ExampleLanguage.KOTLIN else ExampleLanguage.JAVA
}

private fun newExampleInstance(exampleClass: Class<out PSPDFExample>, context: Context): PSPDFExample? {
    return try {
        exampleClass.getConstructor(Context::class.java).newInstance(context)
    } catch (e: Throwable) {
        null
    }
}