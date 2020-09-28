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
import android.util.Log;
import androidx.annotation.NonNull;

import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.activities.FormCreationActivity;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.document.processor.NewPage;
import com.pspdfkit.document.processor.PdfProcessor;
import com.pspdfkit.document.processor.PdfProcessorTask;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivityIntentBuilder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;

/**
 * Showcases how to create forms programmatically.
 */
public class FormCreationExample extends PSPDFExample {
    private static final String TAG = "FormCreationExample";
    private static final String PSPDFKIT_DIRECTORY_NAME = "catalog-pspdfkit";

    public FormCreationExample(@NonNull final Context context) {
        super(context.getString(R.string.formCreationExampleTitle), context.getString(R.string.formCreationExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        configuration
            // Turn off saving, so we have the clean original document every time the example is launched.
            .autosaveEnabled(false)
            .enableFormEditing()
            .build();

        // Create an A4 page document from scratch.
        final NewPage newPage = NewPage.emptyPage(NewPage.PAGE_SIZE_A4).build();
        final PdfProcessorTask task = PdfProcessorTask.newPage(newPage);

        final File outputFile;
        try {
            outputFile = new File(getCatalogCacheDirectory(context), "Blank.pdf").getCanonicalFile();
        } catch (IOException exception) {
            throw new IllegalStateException("Couldn't create Blank.pdf file.", exception);
        }

        PdfProcessor.processDocumentAsync(task, outputFile)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(processorProgress -> {
                final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(outputFile))
                    .configuration(configuration.build())
                    .activityClass(FormCreationActivity.class)
                    .build();

                context.startActivity(intent);
            }, throwable -> Log.e(TAG, "Error while trying to create PDF document.", throwable));
    }

    @NonNull
    private static File getCatalogCacheDirectory(@NonNull Context ctx) throws IOException {
        File dir = new File(ctx.getCacheDir(), PSPDFKIT_DIRECTORY_NAME);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Failed to create Catalog cache directory.");
            }
        }
        return dir;
    }
}
