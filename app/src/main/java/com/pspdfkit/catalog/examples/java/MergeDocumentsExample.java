/*
 *   Copyright © 2019-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.document.DocumentSource;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.document.PdfDocumentLoader;
import com.pspdfkit.document.processor.NewPage;
import com.pspdfkit.document.processor.PdfProcessor;
import com.pspdfkit.document.processor.PdfProcessorTask;
import com.pspdfkit.document.providers.AssetDataProvider;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.ui.PdfActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MergeDocumentsExample extends PSPDFExample {

    @Nullable
    private Disposable mergingDisposable;

    public MergeDocumentsExample(@NonNull final Context context) {
        super(context.getString(R.string.mergeDocumentsExampleTitle), context.getString(R.string.mergeDocumentsExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context,
                              @NonNull final PdfActivityConfiguration.Builder configuration) {
        final ProgressDialog dialog = ProgressDialog.show(context, "Merging Documents", "Please wait", true, true, dialog1 -> {
            if (mergingDisposable != null && !mergingDisposable.isDisposed()) mergingDisposable.dispose();
        });

        mergingDisposable = Single.fromCallable(() -> {
            // Open the documents we are going to merge.
            List<PdfDocument> documents = new ArrayList<>();
            documents.add(PdfDocumentLoader.openDocument(context, new DocumentSource(new AssetDataProvider("JKHF-AnnualReport.pdf"))));
            documents.add(PdfDocumentLoader.openDocument(context, new DocumentSource(new AssetDataProvider("pdf.pdf"))));
            documents.add(PdfDocumentLoader.openDocument(context, new DocumentSource(new AssetDataProvider("Guide-v6.pdf"))));

            PdfProcessorTask task = PdfProcessorTask.empty();

            // We need to keep track of the page count so we can add pages to the end.
            int totalPageCount = 0;
            for (PdfDocument document : documents) {
                for (int i = 0; i < document.getPageCount(); i++) {
                    // We use totalPageCount here to add the pages to the end.
                    // But you are free to add them at any place in the document you'd like.
                    task.addNewPage(NewPage.fromPage(document, i).build(), totalPageCount);
                    totalPageCount++;
                }
            }

            // Finally create the resulting document.
            final File mergedDocumentsFile = new File(context.getDir("documents", Context.MODE_PRIVATE), "merged-documents.pdf");
            PdfProcessor.processDocument(task, mergedDocumentsFile);

            return Uri.fromFile(mergedDocumentsFile);
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe((uri, throwable) -> {
                dialog.cancel();
                PdfActivity.showDocument(context, uri, configuration.build());
            });
    }
}
