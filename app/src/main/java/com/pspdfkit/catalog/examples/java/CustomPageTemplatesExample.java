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
import com.pspdfkit.catalog.examples.java.activities.CustomPageTemplateActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

public class CustomPageTemplatesExample extends PSPDFExample {
    public CustomPageTemplatesExample(Context context) {
        super(context.getString(R.string.customPageTemplateExampleTitle), context.getString(R.string.customPageTemplateExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        // Extract the document from the assets.
        extract(ANNOTATIONS_EXAMPLE, title, context, documentFile -> {
            // To start the example create a launch intent using the builder.
            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration.build())
                .activityClass(CustomPageTemplateActivity.class)
                .build();

            context.startActivity(intent);
        });
    }
}
