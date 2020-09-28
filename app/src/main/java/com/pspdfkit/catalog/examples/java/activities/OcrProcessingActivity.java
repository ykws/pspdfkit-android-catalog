/*
 *   Copyright © 2020 PSPDFKit GmbH. All rights reserved.
 *
 *   THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 *   AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE PSPDFKIT LICENSE AGREEMENT.
 *   UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 *   This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.pspdfkit.catalog.R;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.document.processor.PdfProcessor;
import com.pspdfkit.document.processor.PdfProcessorTask;
import com.pspdfkit.document.processor.ocr.OcrLanguage;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * This activity uses the {@link PdfProcessor} to split a document, removing annotations from the document, and flatten annotation
 * on a document.
 */
public class OcrProcessingActivity extends PdfActivity {

    @NonNull private final CompositeDisposable disposables = new CompositeDisposable();

    private void performOcr() {
        // Define the output file. This example writes to the internal app directory, into a file based on the document's title.
        // You might want to use uuid if not sure that the title will be set on the document.
        final PdfDocument document = getDocument();
        final File outputFile = new File(getFilesDir(), document.getTitle() + "-ocr-processed.pdf");

        // Perform OCR processing for english language.
        final PdfProcessorTask task = PdfProcessorTask.fromDocument(document)
            .performOcrOnPages(new HashSet<>(Collections.singletonList(0)), OcrLanguage.ENGLISH);

        final ProcessorProgressHandler handler = new ProcessorProgressHandler("Performing OCR on the document.", outputFile);
        // Start document processing, but without annotation flattening.
        final ProcessorProgressHandler disposable = PdfProcessor.processDocumentAsync(task, outputFile)
            // Drop update events to avoid back pressure on slow devices.
            .onBackpressureDrop()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(new Action() {
                @Override
                public void run() throws Exception {
                    handler.onCancel();
                }
            })
            .subscribeWith(handler);
        disposables.add(disposable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Dispose all active disposables when activity goes to background.
        disposables.clear();
    }

    /**
     * Creates menu items that will trigger document processing.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.ocr_example, menu);
        return true;
    }

    /**
     * Triggered by selecting an action from the menu in the action bar.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean handled = false;

        final int itemId = item.getItemId();
        if (itemId == R.id.item_perform_ocr) {
            performOcr();
            handled = true;
        }
        return handled || super.onOptionsItemSelected(item);
    }

    /**
     * Helper class for showing a progress dialog and opening the processed document.
     */
    private class ProcessorProgressHandler extends DisposableSubscriber<PdfProcessor.ProcessorProgress> {

        private final File outputFile;
        private ProgressDialog progressDialog;

        ProcessorProgressHandler(@NonNull String progressMessage, @NonNull File outputFile) {
            this.outputFile = outputFile;
            this.progressDialog = ProgressDialog.show(
                OcrProcessingActivity.this,
                "Processing document",
                progressMessage,
                false,
                true,
                dialog -> cancel()
            );
        }

        public void onCancel() {
            progressDialog.dismiss();
        }

        @Override
        public void onNext(PdfProcessor.ProcessorProgress processorProgress) {
            progressDialog.setProgress((int) Math.ceil(processorProgress.getPagesProcessed() / (float) processorProgress.getTotalPages()));
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            new AlertDialog.Builder(OcrProcessingActivity.this)
                .setMessage("Error while processing file: " + e.getLocalizedMessage())
                .show();
        }

        @Override
        public void onComplete() {
            showProcessedDocument(outputFile);
            progressDialog.dismiss();
        }
    }

    private void showProcessedDocument(@NonNull final File processedDocumentFile) {
        final OcrProcessingActivity context = OcrProcessingActivity.this;
        final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(processedDocumentFile))
            .configuration(getConfiguration())
            .build();

        startActivity(intent);
    }
}
