/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.pspdfkit.annotations.AnnotationType;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.document.processor.NewPage;
import com.pspdfkit.document.processor.PageImage;
import com.pspdfkit.document.processor.PagePattern;
import com.pspdfkit.document.processor.PagePosition;
import com.pspdfkit.document.processor.PdfProcessor;
import com.pspdfkit.document.processor.PdfProcessorTask;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * This activity uses the {@link PdfProcessor} to split a document, removing annotations from the document, and flatten annotation
 * on a document.
 */
public class DocumentProcessingExampleActivity extends PdfActivity {

    @NonNull private final CompositeDisposable disposables = new CompositeDisposable();

    private void createDocumentFromRange() {
        // Define the output file. This example writes to the internal app directory, into a file based on the document's Uid.
        final PdfDocument document = getDocument();
        final File outputFile = new File(getFilesDir(), document.getUid() + "-range.pdf");

        // Extract pages with indexes 1, 2, 3, 5, 6, 14. All other pages won't be copied.
        final PdfProcessorTask task = PdfProcessorTask.fromDocument(document).removePages(new HashSet<>(Arrays.asList(0, 4, 7, 8, 9, 10, 11, 12, 13)));

        // Start document processing, but without annotation flattening.
        final ProcessorProgressHandler disposable = PdfProcessor.processDocumentAsync(task, outputFile)
            // Drop update events to avoid back pressure on slow devices.
            .onBackpressureDrop()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new ProcessorProgressHandler("Extracting pages.", outputFile));
        disposables.add(disposable);
    }

    private void createFlattenedDocument() {
        // Define the output file. This example writes to the internal app directory, into a file based on the document's Uid.
        final PdfDocument document = getDocument();
        final File outputFile = new File(getFilesDir(), document.getUid() + "-flattened.pdf");

        // Start document processing, requesting a flattening of all annotations.
        PdfProcessorTask task = PdfProcessorTask.fromDocument(document).changeAllAnnotations(PdfProcessorTask.AnnotationProcessingMode.FLATTEN);
        final ProcessorProgressHandler handler = new ProcessorProgressHandler("Flattening annotations.", outputFile);
        PdfProcessor.processDocumentAsync(task, outputFile)
            // Drop update events to avoid back pressure on slow devices.
            .onBackpressureDrop()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(handler);
        disposables.add(handler);
    }

    private void createDocumentWithoutLinkAnnotations() {
        // Define the output file. This example writes to the internal app directory, into a file based on the document's Uid.
        final PdfDocument document = getDocument();
        final File outputFile = new File(getFilesDir(), document.getUid() + "-without-link-annotations.pdf");

        PdfProcessorTask task = PdfProcessorTask.fromDocument(document).changeAnnotationsOfType(AnnotationType.LINK, PdfProcessorTask.AnnotationProcessingMode.DELETE);
        // Start document processing, requesting a flattening of all annotations.
        final ProcessorProgressHandler handler = new ProcessorProgressHandler("Removing link annotations.", outputFile);
        PdfProcessor.processDocumentAsync(task, outputFile)
            // Drop update events to avoid back pressure on slow devices.
            .onBackpressureDrop()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(handler);
        disposables.add(handler);
    }

    private void createDocumentWithRotatedPages() {
        // Define the output file. This example writes to the internal app directory, into a file based on the document's Uid.
        final PdfDocument document = getDocument();
        final File outputFile = new File(getFilesDir(), document.getUid() + "-rotated.pdf");

        final PdfProcessorTask task = PdfProcessorTask.fromDocument(document);

        // Rotate all pages of the document by 90°.
        for (int pageIndex = 0, pageCount = document.getPageCount(); pageIndex < pageCount; pageIndex++) {
            task.rotatePage(pageIndex, 90);
        }

        // Start document processing, but without annotation flattening.
        final ProcessorProgressHandler disposable = PdfProcessor.processDocumentAsync(task, outputFile)
            // Drop update events to avoid back pressure on slow devices.
            .onBackpressureDrop()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new ProcessorProgressHandler("Rotating pages.", outputFile));
        disposables.add(disposable);
    }

    private void createDocumentWithNewPages() {
        // Define the output file. This example writes to the internal app directory, into a file based on the document's Uid.
        final PdfDocument document = getDocument();
        final File outputFile = new File(getFilesDir(), document.getUid() + "-new-page.pdf");

        final PdfProcessorTask task = PdfProcessorTask.fromDocument(document);

        // Create a yellow A5 page with a line pattern as first page.
        task.addNewPage(NewPage.patternPage(NewPage.PAGE_SIZE_A5, PagePattern.LINES_7MM)
                            .backgroundColor(Color.rgb(241, 236, 121))
                            .build(), 0);

        // Create an A0 page with an image as second page.
        try {
            final Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open("inline-media/images/cover.jpg"));

            task.addNewPage(
                NewPage.emptyPage(NewPage.PAGE_SIZE_A0)
                    .withPageItem(new PageImage(bitmap, PagePosition.CENTER))
                    .build(),
                1
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // The third page is cloned from the last page of the document, but rotated by 90°.
        task.addNewPage(
            NewPage.fromPage(document, document.getPageCount() - 1)
                .rotation(90)
                .build(),
            2
        );

        // Start document processing.
        final ProcessorProgressHandler disposable = PdfProcessor.processDocumentAsync(task, outputFile)
            // Drop update events to avoid back pressure on slow devices.
            .onBackpressureDrop()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new ProcessorProgressHandler("Creating new pages.", outputFile));
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

        // This will add three different menu actions, for annotation flattening, page extraction, and annotation deletion.
        getMenuInflater().inflate(R.menu.processor_example, menu);

        return true;
    }

    /**
     * Triggered by selecting an action from the overflow menu in the action bar.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean handled = false;

        final int itemId = item.getItemId();
        if (itemId == R.id.item_extract_range) {
            createDocumentFromRange();
            handled = true;
        } else if (itemId == R.id.item_flatten_annotations) {
            createFlattenedDocument();
            handled = true;
        } else if (itemId == R.id.item_remove_link_annotations) {
            createDocumentWithoutLinkAnnotations();
            handled = true;
        } else if (itemId == R.id.item_rotate_pages) {
            createDocumentWithRotatedPages();
            handled = true;
        } else if (itemId == R.id.item_new_page) {
            createDocumentWithNewPages();
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
                DocumentProcessingExampleActivity.this,
                "Processing document",
                progressMessage,
                false,
                true,
                dialog -> cancel()
            );
        }

        @Override
        public void onNext(PdfProcessor.ProcessorProgress processorProgress) {
            progressDialog.setProgress((int) Math.ceil(processorProgress.getPagesProcessed() / (float) processorProgress.getTotalPages()));
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            new AlertDialog.Builder(DocumentProcessingExampleActivity.this)
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
        final DocumentProcessingExampleActivity context = DocumentProcessingExampleActivity.this;

        final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(processedDocumentFile))
            .configuration(getConfiguration())
            .build();

        startActivity(intent);
    }
}
