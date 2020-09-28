/*
 *   Copyright © 2014-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.ui.PdfActivity;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * Opens the {@link PdfActivity} for viewing a PDF stored within the app's asset
 * folder.
 */
abstract class AssetExample extends PSPDFExample {

    AssetExample(@NonNull String title, @NonNull String description) {
        super(title, description);
    }

    /**
     * Gets the path to the asset that we want to display.
     * @return The path to the asset
     */
    protected abstract String getAssetPath();

    /**
     * Allows subclasses to adjust the configuration before displaying the document.
     * @param configuration The configuration that will be used to display the document
     */
    protected void prepareConfiguration(PdfActivityConfiguration.Builder configuration) {
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        // We start off with the filename (or path) of a PDF document inside the app's assets.
        final String assetPath = getAssetPath();

        prepareConfiguration(configuration);

        // Since PSPDFKit does not directly read documents from the assets, we extract them
        // to the internal device storage using a custom AsyncTask implementation.
        extract(assetPath, title, context, documentFile -> {
            // Now, as the documentFile is sitting in the internal device storage, we can
            // start the PSPDFKitAppCompat activity by passing it the Uri of the file.
            PdfActivity.showDocument(context, Uri.fromFile(documentFile), configuration.build());
        });
    }
}
