/*
 *   Copyright © 2018-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

@file:JvmName("PSPDFKitKotlin")

package com.pspdfkit.catalog.utils

import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.ui.PdfActivity
import com.pspdfkit.ui.PdfFragment
import com.pspdfkit.utils.Size

//
// Extensions and helpers for using PSPDFKit with Kotlin.
//

/**
 * The [PdfFragment] used by this activity. This is an alias for [PdfActivity.getPdfFragment].
 * @return The [PdfFragment] used by this activity to show PDF documents.
 */
val PdfActivity.fragment: PdfFragment? get() = pdfFragment

/**
 * Creates a [PdfActivityConfiguration.Builder] from the existing configuration.
 */
fun PdfActivityConfiguration.buildUpon(): PdfActivityConfiguration.Builder = PdfActivityConfiguration.Builder(this)

/**
 * Returns a new [Size] from proportionally fitting `this` into [targetWidth] and [targetHeight].
 */
fun Size.fitInside(targetWidth: Float?, targetHeight: Float?): Size =
    when {
        width == 0f || height == 0f -> Size(0f, 0f)
        targetWidth == null && targetHeight == null -> this
        else -> {
            val maxWidth = targetWidth ?: Float.MAX_VALUE
            val maxHeight = targetHeight ?: Float.MAX_VALUE
            val ratio = Math.min(maxWidth / width, maxHeight / height)
            Size(width * ratio, height * ratio)
        }
    }

/**
 * Returns a new [Size] from proportionally scaling `this` until it is >= both [targetWidth] and [targetHeight].
 */
fun Size.fill(targetWidth: Float?, targetHeight: Float?): Size =
    when {
        width == 0f || height == 0f -> Size(0f, 0f)
        targetWidth == null && targetHeight == null -> this
        else -> {
            val minWidth = targetWidth ?: 0f
            val minHeight = targetHeight ?: 0f
            val ratio = Math.max(minWidth / width, minHeight / height)
            Size(width * ratio, height * ratio)
        }
    }
