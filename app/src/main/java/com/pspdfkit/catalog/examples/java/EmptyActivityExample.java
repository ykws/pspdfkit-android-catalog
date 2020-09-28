/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
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
import com.pspdfkit.ui.PdfActivityIntentBuilder;

/**
 * This example shows how to use {@link PdfActivityIntentBuilder#emptyActivity(Context)} to open the
 * {@link com.pspdfkit.ui.PdfActivity} without any document loaded.
 * This is most useful when also providing a custom activity subclass with options for the user to open a document.
 */
public class EmptyActivityExample extends PSPDFExample {

    public EmptyActivityExample(@NonNull Context context) {
        super(context.getString(R.string.emptyActivityExampleTitle), context.getString(R.string.emptyActivityExampleDescription));
    }

    @Override
    public void launchExample(@NonNull Context context, @NonNull PdfActivityConfiguration.Builder configuration) {
        // We use emptyActivity() to start the PdfActivity without any document loaded.
        Intent intent = PdfActivityIntentBuilder.emptyActivity(context)
            .configuration(configuration.build())
            .build();
        context.startActivity(intent);
    }
}