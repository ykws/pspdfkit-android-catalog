/*
 *   Copyright © 2016-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.pspdfkit.annotations.AnnotationType;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.activities.AnnotationCreationActivity;
import com.pspdfkit.catalog.tasks.ExtractAssetTask;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import java.util.ArrayList;
import java.util.Arrays;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class AnnotationRenderingExample extends PSPDFExample {

    public AnnotationRenderingExample(@NonNull final Context context) {
        super(context.getString(R.string.annotationRenderingExampleTitle), context.getString(R.string.annotationRenderingExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        configuration
            .excludedAnnotationTypes(new ArrayList<>(Arrays.asList(AnnotationType.NOTE, AnnotationType.HIGHLIGHT)))
            .build();

        // Extract the document from the assets. The launched activity will add annotations to that document.
        ExtractAssetTask.extract(QUICK_START_GUIDE, title, context, documentFile -> {
            // To start the AnnotationRenderingExample create a launch intent using the builder.
            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration.build())
                .activityClass(AnnotationCreationActivity.class)
                .build();

            context.startActivity(intent);
        });
    }
}
