/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.activities.DocumentProcessingExampleActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

public class DocumentProcessingExample extends PSPDFExample {
    public DocumentProcessingExample(Context context) {
        super(context.getString(R.string.documentProcessingExampleTitle), context.getString(R.string.documentProcessingExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        // This example uses a custom activity which showcases several document processing features.
        // For sake of simplicity, deactivate actions in the processing activity.
        configuration.disableAnnotationList()
            .disableSearch()
            .disableOutline()
            .hideThumbnailGrid();

        // First extract the document from the assets.
        extract(ANNOTATIONS_EXAMPLE, title, context, documentFile -> {
            // This example opens up a combined document, by providing the extracted document twice.
            // This is just an example of how you can merge two, or more documents.
            final Uri documentUri1 = Uri.fromFile(documentFile);
            final Uri documentUri2 = Uri.fromFile(documentFile);

            // To start the DocumentProcessingExampleActivity create a launch intent using the builder.
            final Intent intent = PdfActivityIntentBuilder.fromUri(context, documentUri1, documentUri2)
                .configuration(configuration.build())
                .activityClass(DocumentProcessingExampleActivity.class)
                .build();

            // Start the DocumentProcessingExampleActivity for the extracted document.
            context.startActivity(intent);
        });

    }
}
