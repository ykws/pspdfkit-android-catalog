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

import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.activities.SignaturePickerDialogIntegrationActivity;
import com.pspdfkit.catalog.tasks.ExtractAssetTask;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * Showcases how to manually integrate the {@link com.pspdfkit.ui.signatures.SignaturePickerFragment} into an existing {@link com.pspdfkit.ui.PdfActivity}.
 */
public class SignaturePickerDialogIntegrationExample extends PSPDFExample {

    public SignaturePickerDialogIntegrationExample(@NonNull final Context context) {
        super(context.getString(R.string.signaturePickerDialogIntegrationExampleTitle), context.getString(R.string.signaturePickerDialogIntegrationExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        // Extract the document from the assets.
        ExtractAssetTask.extract(QUICK_START_GUIDE, title, context, documentFile -> {
            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration
                    // The form field for signing is on page with index 12.
                    .page(12)
                    .build())
                .activityClass(SignaturePickerDialogIntegrationActivity.class)
                .build();

            // Start the SignaturePickerDialogIntegrationActivity showing the demo document.
            context.startActivity(intent);
        });
    }
}
