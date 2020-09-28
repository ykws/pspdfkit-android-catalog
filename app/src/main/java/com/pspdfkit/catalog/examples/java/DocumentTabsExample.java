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
import com.pspdfkit.configuration.activity.TabBarHidingMode;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.activities.DocumentTabsActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * This example shows how to add document tabs in the default `PdfActivity`.
 */
public class DocumentTabsExample extends PSPDFExample {

    public DocumentTabsExample(Context context) {
        super(context.getString(R.string.documentTabsExampleTitle), context.getString(R.string.documentTabsExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        configuration
                // Make the tab bar always visible.
                .setTabBarHidingMode(TabBarHidingMode.SHOW);

        // First, extract the initial document from the app's assets and place it in the device's internal storage.
        extract(QUICK_START_GUIDE, title, context, documentFile -> {
            // Launch the custom example activity using the document and configuration.
            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                    .configuration(configuration.build())
                    .activityClass(DocumentTabsActivity.class)
                    .build();

            // Start the activity for the extracted document.
            context.startActivity(intent);
        });
    }
}
