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
import com.pspdfkit.annotations.AnnotationType;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.activities.AnnotationCreationActivity;
import com.pspdfkit.preferences.PSPDFKitPreferences;
import com.pspdfkit.ui.PdfActivityIntentBuilder;
import com.pspdfkit.ui.special_mode.controller.AnnotationTool;

import java.util.Arrays;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

public class AnnotationCreationExample extends PSPDFExample {

    public AnnotationCreationExample(@NonNull final Context context) {
        super(context.getString(R.string.annotationCreationExampleTitle), context.getString(R.string.annotationCreationExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        configuration
            // Turn off saving, so we have the clean original document every time the example is launched.
            .autosaveEnabled(false)
            // By default all supported annotation types are editable.
            // You can selectively enable certain types by providing them here.
            .editableAnnotationTypes(Arrays.asList(
                AnnotationType.NOTE,
                AnnotationType.HIGHLIGHT,
                AnnotationType.FREETEXT,
                AnnotationType.STAMP,
                AnnotationType.SQUARE,
                AnnotationType.SOUND))
            // You can also specify which annotations tools are enabled. Note that annotation tool will be enabled
            // only when underlying annotation type (see AnnotationTool.toAnnotationType()) is editable.
            // This will enable signature tool but won't show ink tool.
            .enabledAnnotationTools(Arrays.asList(
                AnnotationTool.NOTE,
                AnnotationTool.HIGHLIGHT,
                AnnotationTool.FREETEXT,
                AnnotationTool.SIGNATURE,
                AnnotationTool.SQUARE,
                AnnotationTool.SOUND))
            .build();

        // The annotation creator written into newly created annotations. If not set, or set to null
        // a dialog will normally be shown when creating an annotation, asking you to enter a name.
        // We are going to skip this part and set it as "John Doe" only if it was not yet set.
        if (!PSPDFKitPreferences.get(context).isAnnotationCreatorSet()) {
            PSPDFKitPreferences.get(context).setAnnotationCreator("John Doe");
        }

        // Extract the document from the assets. The launched activity will add annotations to that document.
        extract(QUICK_START_GUIDE, title, context, documentFile -> {

            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration.build())
                .activityClass(AnnotationCreationActivity.class)
                .build();
            context.startActivity(intent);
        });
    }
}
