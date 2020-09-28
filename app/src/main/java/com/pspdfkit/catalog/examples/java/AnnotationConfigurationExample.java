/*
 *   Copyright © 2019-2020 PSPDFKit GmbH. All rights reserved.
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
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.examples.java.activities.AnnotationConfigurationExampleActivity;
import com.pspdfkit.catalog.tasks.ExtractAssetTask;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

/**
 * Showcases how to change annotation configuration in {@link com.pspdfkit.ui.PdfFragment}.
 */
public class AnnotationConfigurationExample extends PSPDFExample {

    public AnnotationConfigurationExample(@NonNull final Context context) {
        super(context.getString(R.string.annotationConfigurationExampleTitle), context.getString(R.string.annotationConfigurationExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context,
                              @NonNull final PdfActivityConfiguration.Builder configuration) {
        // Extract the document from the assets.
        ExtractAssetTask.extract(QUICK_START_GUIDE, title, context, documentFile -> {
            // To start the AnnotationConfigurationExampleActivity create a launch intent using the builder.
            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration.build())
                .activityClass(AnnotationConfigurationExampleActivity.class)
                .build();

            context.startActivity(intent);
        });
    }
}
