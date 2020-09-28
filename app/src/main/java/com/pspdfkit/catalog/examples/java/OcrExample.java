/*
 *   Copyright © 2020 PSPDFKit GmbH. All rights reserved.
 *
 *   THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 *   AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE PSPDFKIT LICENSE AGREEMENT.
 *   UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 *   This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.examples.java.activities.OcrProcessingActivity;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * This examples shows how to use Optical character recognition
 * to extract the text from scanned documents using the {@link com.pspdfkit.document.processor.PdfProcessor}.
 */
public class OcrExample extends PSPDFExample {
    private static final String OCR_PDF_PATH = "ocr/Remote Work.pdf";

    public OcrExample(Context context) {
        super(context.getString(R.string.ocrExampleTitle), context.getString(R.string.ocrExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        extract(OCR_PDF_PATH, title, context, documentFile -> {
            // We use a custom utility class to extract the example document from the assets.
            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration
                    .disableDocumentInfoView()
                    .disableAnnotationList()
                    .disableBookmarkList()
                    .hideSettingsMenu()
                    .hideThumbnailGrid()
                    .disableOutline()
                    .build())
                .activityClass(OcrProcessingActivity.class)
                .build();

            // Start the OcrProcessingActivity for the extracted document.
            context.startActivity(intent);
        });
    }
}
