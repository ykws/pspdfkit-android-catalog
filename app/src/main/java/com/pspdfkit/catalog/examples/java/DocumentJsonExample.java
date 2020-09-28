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
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.configuration.sharing.ShareFeatures;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.activities.DocumentJsonExampleActivity;
import com.pspdfkit.catalog.tasks.ExtractAssetTask;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

/**
 * This example shows how annotation changes can be exported to the Instant Document JSON format, and how they can be imported. You can launch the example
 * and then modify, remove, or add annotations to the document. When exporting to JSON, all changes to the document will be included. You can always reapply
 * the changes by tapping import, but the example works best if you close the example and reopen it before importing (so the document's original state is
 * restored).
 */
public class DocumentJsonExample extends PSPDFExample {
    public DocumentJsonExample(@NonNull final Context context) {
        super(context.getString(R.string.documentJsonExampleTitle), context.getString(R.string.documentJsonExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        // Load and show the a custom activity for importing and exporting document JSON.
        ExtractAssetTask.extract(ANNOTATIONS_EXAMPLE, title, context, true, documentFile -> {
            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .activityClass(DocumentJsonExampleActivity.class)
                .configuration(configuration
                        // We clean up the primary toolbar for the sake of simplicity in this example.
                        .disableAnnotationList()
                        .disableBookmarkList()
                        .hideThumbnailGrid()
                        .hideSettingsMenu()
                        .disableDocumentEditor()
                        .disableOutline()
                        .disablePrinting()
                        .setEnabledShareFeatures(ShareFeatures.none())
                        .disableSearch()
                        .build())
                .build();

            context.startActivity(intent);
        });
    }
}
