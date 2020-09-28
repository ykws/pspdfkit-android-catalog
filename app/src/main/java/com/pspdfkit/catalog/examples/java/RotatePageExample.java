/*
 *   Copyright © 2018-2020 PSPDFKit GmbH. All rights reserved.
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
import com.pspdfkit.catalog.examples.java.activities.RotatePageActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * Simple example showing how to apply rotation to pages.
 */
public class RotatePageExample extends PSPDFExample {

    public RotatePageExample(@NonNull Context context) {
        super(context.getString(R.string.rotatePageExampleTitle), context.getString(R.string.rotatePageExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        // This example uses a custom activity which adds some option menu items. The default
        // menu items are deactivated for simplicity.
        configuration.disableSearch()
            .disableOutline();

        // Start the activity once the example document has been extracted from the app's assets.
        extract(QUICK_START_GUIDE, title, context, documentFile -> {
            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration.build())
                .activityClass(RotatePageActivity.class)
                .build();

            // Start the RotatePageActivity for the extracted document.
            context.startActivity(intent);
        });
    }
}
