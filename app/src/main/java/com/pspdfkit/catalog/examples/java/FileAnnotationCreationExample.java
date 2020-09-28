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
import com.pspdfkit.catalog.examples.java.activities.FileAnnotationCreationActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * This example shows how to create file annotations programmatically.
 */
public class FileAnnotationCreationExample extends PSPDFExample {

    public FileAnnotationCreationExample(@NonNull final Context context) {
        super(context.getString(R.string.fileAnnotationCreationExampleTitle), context.getString(R.string.fileAnnotationCreationExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        // Turn off saving, so we have the clean original document every time the example is launched.
        configuration.autosaveEnabled(false).build();

        // Extract the document from the assets. The launched activity will add file annotations to that document.
        extract(QUICK_START_GUIDE, title, context, documentFile -> {
            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                    .configuration(configuration.build())
                    .activityClass(FileAnnotationCreationActivity.class)
                    .build();
            context.startActivity(intent);
        });
    }
}
