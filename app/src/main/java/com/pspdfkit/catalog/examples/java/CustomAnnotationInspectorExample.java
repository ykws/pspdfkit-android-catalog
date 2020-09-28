/*
 *   Copyright © 2014-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.activities.CustomAnnotationInspectorActivity;
import com.pspdfkit.catalog.tasks.ExtractAssetTask;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * Showcases how to create custom annotation inspector from scratch.
 */
public class CustomAnnotationInspectorExample extends PSPDFExample {

    public CustomAnnotationInspectorExample(@NonNull final Context context) {
        super(context.getString(R.string.annotationCustomInspectorExampleTitle), context.getString(R.string.annotationCustomInspectorExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        // Extract the document from the assets. The launched activity will add annotations to that document.
        ExtractAssetTask.extract(QUICK_START_GUIDE, title, context, documentFile -> {
            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration.build())
                .activityClass(CustomAnnotationInspectorActivity.class)
                .build();

            // Start the CustomAnnotationInspectorActivity for the extracted document.
            context.startActivity(intent);
        });
    }
}
