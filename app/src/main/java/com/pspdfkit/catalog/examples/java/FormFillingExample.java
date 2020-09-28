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
import com.pspdfkit.catalog.examples.java.activities.FormFillingActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * Showcases how to fill forms programmatically.
 */
public class FormFillingExample extends PSPDFExample {
    public FormFillingExample(@NonNull final Context context) {
        super(context.getString(R.string.formFillingExampleTitle), context.getString(R.string.formFillingExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        configuration
            // Turn off saving, so we have the clean original document every time the example is launched.
            .autosaveEnabled(false)
            .enableFormEditing()
            .build();

        // Extract the document from the assets.
        extract("Form_example.pdf", title, context, documentFile -> {
            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration.build())
                .activityClass(FormFillingActivity.class)
                .build();

            context.startActivity(intent);
        });
    }
}
