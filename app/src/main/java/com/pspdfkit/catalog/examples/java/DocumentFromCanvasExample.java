/*
 *   Copyright © 2018-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.document.processor.NewPage;
import com.pspdfkit.document.processor.PdfProcessor;
import com.pspdfkit.document.processor.PdfProcessorTask;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.ui.PdfActivityIntentBuilder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;

/**
 * Showcases how to create a document from scratch drawing on canvas.
 */
public class DocumentFromCanvasExample extends PSPDFExample {
    private static final String TAG = "DocumentFromCanvas";
    private static final String PSPDFKIT_DIRECTORY_NAME = "catalog-pspdfkit";

    public DocumentFromCanvasExample(@NonNull final Context context) {
        super(context.getString(R.string.documentFromCanvasExampleTitle), context.getString(R.string.documentFromCanvasExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        configuration
            // Turn off saving, so we have the clean original document every time the example is launched.
            .autosaveEnabled(false)
            .enableFormEditing()
            .build();

        // Create a canvas based on a A4 page.
        final NewPage pageCanvas = NewPage.fromCanvas(NewPage.PAGE_SIZE_A4, canvas -> {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            Path path = new Path();
            path.cubicTo(0f, 0f, 100f, 300f, 400f, 300f);
            canvas.drawPath(path, paint);
        }).build();
        final PdfProcessorTask task = PdfProcessorTask.newPage(pageCanvas);

        final File outputFile;
        try {
            outputFile = new File(getCatalogCacheDirectory(context), "Canvas.pdf").getCanonicalFile();
        } catch (IOException exception) {
            throw new IllegalStateException("Couldn't create Canvas.pdf file.", exception);
        }

        PdfProcessor.processDocumentAsync(task, outputFile)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(processorProgress -> {
                final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(outputFile))
                    .configuration(configuration.build())
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
