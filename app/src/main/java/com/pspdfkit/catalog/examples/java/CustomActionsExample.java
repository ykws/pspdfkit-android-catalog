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
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.activities.CustomActionsActivity;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * This example shows how to add custom actions to the ActionBar. To do so, it subclasses the
 * {@link PdfActivity} and overrides the menu logic within {@link CustomActionsActivity}.
 */
public class CustomActionsExample extends PSPDFExample {

    public CustomActionsExample(Context context) {
        super(context.getString(R.string.customActionsExampleTitle), context.getString(R.string.customActionsExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        // We use a custom utility class to extract the example document from the assets.
        extract(QUICK_START_GUIDE, title, context, documentFile -> {
            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration.build())
                .activityClass(CustomActionsActivity.class)
                .build();

            // You can add your own intent extras to the activity too.
            intent.putExtra(CustomActionsActivity.STRING_SAMPLE_ARG, "This toast message is passed via intent extras.");

            // Start the CustomActionsActivity for the extracted document.
            context.startActivity(intent);
        });

    }
}
