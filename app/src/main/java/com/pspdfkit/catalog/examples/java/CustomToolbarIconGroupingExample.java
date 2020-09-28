/*
 *   Copyright © 2016-2020 PSPDFKit GmbH. All rights reserved.
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
import com.pspdfkit.catalog.examples.java.activities.CustomToolbarIconGroupingActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * Example of how to alter contextual toolbar menu items structure. Displayed on AnnotationCreationToolbar.
 */
public class CustomToolbarIconGroupingExample extends PSPDFExample {

    public CustomToolbarIconGroupingExample(@NonNull Context context) {
        super(context.getString(R.string.customToolbarItemGroupingExampleTitle), context.getString(R.string.customToolbarItemGroupingExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        // We use a custom utility class to extract the example document from the assets.
        extract(QUICK_START_GUIDE, title, context, documentFile -> {
            // To start the DarkThemeActivity create a launch intent using the builder.
            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration.build())
                .activityClass(CustomToolbarIconGroupingActivity.class)
                .build();

            // Start the DarkThemeActivity for the extracted document.
            context.startActivity(intent);
        });
    }
}
