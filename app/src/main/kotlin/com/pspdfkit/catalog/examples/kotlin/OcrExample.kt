/*
 *   Copyright © 2020 PSPDFKit GmbH. All rights reserved.
 *
 *   THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 *   AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE PSPDFKIT LICENSE AGREEMENT.
 *   UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 *   This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.kotlin

import android.content.Context
import android.net.Uri
import com.pspdfkit.catalog.PSPDFExample
import com.pspdfkit.catalog.R
import com.pspdfkit.catalog.examples.kotlin.activities.OcrProcessingActivity
import com.pspdfkit.catalog.tasks.ExtractAssetTask
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.ui.PdfActivityIntentBuilder

/**
 * This examples shows how to use Optical character recognition
 * to extract the text from scanned documents using the [com.pspdfkit.document.processor.PdfProcessor].
 */
class OcrExample(context: Context) : PSPDFExample(context, R.string.ocrExampleTitle, R.string.ocrExampleDescription) {

    companion object {
        const val OCR_PDF_PATH = "ocr/Remote Work.pdf"
    }

    override fun launchExample(context: Context, configuration: PdfActivityConfiguration.Builder) {
        // We use a custom utility class to extract the example document from the assets.
        ExtractAssetTask.extract(OCR_PDF_PATH, title, context) { documentFile ->
            val intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration
                    .disableDocumentInfoView()
                    .disableAnnotationList()
                    .disableBookmarkList()
                    .hideSettingsMenu()
                    .hideThumbnailGrid()
                    .disableOutline().build())
                .activityClass(OcrProcessingActivity::class)
                .build()

            // Start the OcrProcessingActivity for the extracted document.
            context.startActivity(intent)
        }
    }
}