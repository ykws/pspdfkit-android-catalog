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
import com.pspdfkit.catalog.examples.java.activities.CustomAnnotationNoteHinterProviderActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;
import com.pspdfkit.ui.drawable.PdfDrawableProvider;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * Shows how to create a custom annotation note hinter extending {@link PdfDrawableProvider}.
 */
public class CustomAnnotationNoteHinterProviderExample extends PSPDFExample {

    public CustomAnnotationNoteHinterProviderExample(Context context) {
        super(
            context.getString(R.string.customAnnotationNoteHinterProviderExampleTitle),
            context.getString(R.string.customAnnotationNoteHinterProviderExampleDescription)
        );
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        // Disable default annotation hinter provider.
        configuration.setAnnotationNoteHintingEnabled(false);

        // We use a custom utility class to extract the example document from the assets.
        extract(QUICK_START_GUIDE, title, context, documentFile -> {
            // To start the CustomAnnotationNoteHinterProviderActivity create a launch intent using the builder.
            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration.build())
                .activityClass(CustomAnnotationNoteHinterProviderActivity.class)
                .build();

            // Start the CustomAnnotationNoteHinterProviderActivity for the extracted document.
            context.startActivity(intent);
        });
    }
}
