/*
 *   Copyright © 2018-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.pspdfkit.catalog.tasks.ExtractAssetTask;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.document.ImageDocumentLoader;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivity;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * This example shows how to open a demo image document from the assets folder.
 */
public class ImageDocumentExample extends AssetExample {

    public ImageDocumentExample(Context context) {
        super(context.getString(R.string.imageDocumentExampleTitle), context.getString(R.string.imageDocumentExampleDescription));
    }

    @Override
    protected String getAssetPath() {
        return "images/android.png";
    }

    @Override
    public void launchExample(@NonNull final Context context,
                              @NonNull final PdfActivityConfiguration.Builder configuration) {
        // We start off with the filename (or path) of an image document inside the app's assets.
        final String assetPath = getAssetPath();
        // Get the default image document configuration.
        // Default options in this configuration are specifically thought to
        // enhance the user experience for image documents (e.g. thumbnail bar and
        // page number overlay are hidden).
        final PdfActivityConfiguration imageDocumentConfiguration = ImageDocumentLoader.getDefaultImageDocumentActivityConfiguration(configuration.build());

        // Since PSPDFKit does not directly read documents from the assets, we extract them
        // to the internal device storage using a custom AsyncTask implementation.
        ExtractAssetTask.extract(assetPath, title, context, documentFile -> {
            // Now, as the image document file is sitting in the internal device storage, we can
            // start the PdfActivity activity by passing it the Uri of the file.
            PdfActivity.showImage(
                context,
                Uri.fromFile(documentFile),
                imageDocumentConfiguration);
        });
    }
}
