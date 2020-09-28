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
import com.pspdfkit.catalog.examples.java.activities.AnnotationWithAlphaCreationActivity;
import com.pspdfkit.preferences.PSPDFKitPreferences;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class AnnotationWithAlphaCreationExample extends PSPDFExample {

    public AnnotationWithAlphaCreationExample(@NonNull final Context context) {
        super(context.getString(R.string.annotationWithAlphaCreationExampleTitle), context.getString(R.string.annotationWithAlphaCreationExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        configuration
            // Turn off saving, so we have the clean original document every time the example is launched.
            .autosaveEnabled(false)
            .build();

        // The annotation creator written into newly created annotations. If not set, or set to null
        // a dialog will be shown when creating an annotation, asking you to enter a name.
        PSPDFKitPreferences.get(context).setAnnotationCreator("John Doe");

        // Extract the document from the assets. The launched activity will add annotations to that document.
        extract(QUICK_START_GUIDE, title, context, documentFile -> {

            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration.build())
                .activityClass(AnnotationWithAlphaCreationActivity.class)
                .build();
            context.startActivity(intent);
        });
    }
}
