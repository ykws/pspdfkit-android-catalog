/*
 *   Copyright © 2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.kotlin

import android.content.Context
import android.net.Uri
import com.pspdfkit.catalog.PSPDFExample
import com.pspdfkit.catalog.R
import com.pspdfkit.catalog.tasks.ExtractAssetTask
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.ui.PdfActivityIntentBuilder

/**
 * This example shows how to customize the annotation selection layout.
 */
class AnnotationSelectionViewStylingExample(context: Context) : PSPDFExample(context, R.string.annotationSelectionViewStylingExampleTitle, R.string.annotationSelectionViewStylingExampleDescription) {

    override fun launchExample(context: Context, configuration: PdfActivityConfiguration.Builder) {
        // Set the new theme that overrides annotation selection color,
        // scale handle drawables and background drawable.
        configuration.theme(R.style.AnnotationSelectionExample_Theme)

        // Extract the example document from the app's assets.
        ExtractAssetTask.extract("Annotation-Selection.pdf", title, context) { documentFile ->
            val intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration.build())
                .build()

            context.startActivity(intent)
        }
    }
}
