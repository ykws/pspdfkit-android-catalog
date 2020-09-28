/*
 *   Copyright © 2014-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.activities.ExternalExampleActivity;

/**
 * This example shows how to build an activity that can select and download any document using
 * the correct intents.
 */
public class ExternalDocumentExample extends PSPDFExample {

    public ExternalDocumentExample(Context context) {
        super(context.getString(R.string.externalDocumentExampleTitle), context.getString(R.string.externalDocumentExampleDescription));
    }

    @Override
    public void launchExample(@NonNull Context context, @NonNull PdfActivityConfiguration.Builder configuration) {
        // In this example we use a custom activity to build an intent that allows the user to
        // select a document.
        Intent intent = new Intent(context, ExternalExampleActivity.class);
        intent.putExtra(ExternalExampleActivity.EXTRA_CONFIGURATION, configuration.build());
        context.startActivity(intent);
    }
}
