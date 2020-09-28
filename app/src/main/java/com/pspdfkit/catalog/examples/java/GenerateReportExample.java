/*
 *   Copyright © 2018-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.text.TextPaint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.pspdfkit.annotations.FreeTextAnnotation;
import com.pspdfkit.annotations.StampAnnotation;
import com.pspdfkit.annotations.appearance.AssetAppearanceStreamGenerator;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.document.DocumentPermissions;
import com.pspdfkit.document.DocumentSaveOptions;
import com.pspdfkit.document.DocumentSource;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.document.PdfDocumentLoader;
import com.pspdfkit.document.processor.NewPage;
import com.pspdfkit.document.processor.PageCanvas;
import com.pspdfkit.document.processor.PagePattern;
import com.pspdfkit.document.processor.PdfProcessor;
import com.pspdfkit.document.processor.PdfProcessorTask;
import com.pspdfkit.document.providers.AssetDataProvider;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.utils.Size;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Locale;

/**
 * Shows how to programmatically create a PDF report.
 */
public class GenerateReportExample extends PSPDFExample {

    @Nullable
    private Disposable generationDisposable;

    public GenerateReportExample(@NonNull final Context context) {
        super(context.getString(R.string.generateReportExampleTitle), context.getString(R.string.generateReportExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        final ProgressDialog dialog = ProgressDialog.show(context, "Generating Report", "Please wait", true, true, dialog1 -> {
            if(generationDisposable != null) generationDisposable.dispose();
        });

        generationDisposable = Single.fromCallable(() -> {
            // Open the document we are going to take the first page from.
            final PdfDocument document = PdfDocumentLoader.openDocument(context, new DocumentSource(new AssetDataProvider("JKHF-AnnualReport.pdf")));
            final Size pageSize = document.getPageSize(0);

            final PdfProcessorTask task = PdfProcessorTask.fromDocument(document);

            // Keep only the first and the last page of the original document.
            final HashSet<Integer> pagesToRemove = new HashSet<>();
            for (int i = 1; i < document.getPageCount() - 1; i++) {
                pagesToRemove.add(i);
            }
            task.removePages(pagesToRemove);

            // Add a newly created single-paged document as the second page of the report
            final PdfDocument secondPageDocument = generateSecondPage(pageSize, context);
            task.addNewPage(NewPage.fromPage(secondPageDocument, 0).build(), 1);

            // Add a new page with a pattern grid as the third page of the report.
            task.addNewPage(
                NewPage.patternPage(pageSize, PagePattern.GRID_5MM)
                    .backgroundColor(Color.WHITE)
                    .build(),
                2
            );

            // Add a page from an existing document.
            final PdfDocument guideDocument = PdfDocumentLoader.openDocument(context, new DocumentSource(new AssetDataProvider("Guide-v4.pdf")));
            task.addNewPage(NewPage.fromPage(guideDocument, 7).build(), 3);

            // Scale the recently added page to the first page size
            task.resizePage(3, pageSize);

            // Draw "Generated for John Doe. Page X" on every page
            drawWatermark("John Doe", task, pageSize, 5);

            // Flatten all annotations.
            task.changeAllAnnotations(PdfProcessorTask.AnnotationProcessingMode.FLATTEN);

            // Only allow opening by users that know the password.
            final String password = "password";
            final DocumentSaveOptions saveOptions = new DocumentSaveOptions(
                password,
                EnumSet.of(DocumentPermissions.PRINTING),
                false,
                null
            );

            // Finally create the resulting document.
            final File generatedReportFile = new File(context.getDir("documents", Context.MODE_PRIVATE), "generated-report.pdf");
            PdfProcessor.processDocument(task, generatedReportFile, saveOptions);

            return Uri.fromFile(generatedReportFile);
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(pdfDocument -> {
                dialog.cancel();
                PdfActivity.showDocument(context, pdfDocument, configuration.build());
            });
    }

    private PdfDocument generateSecondPage(Size pageSize, Context context) throws IOException {
        // Create a separate single-paged document, which will be added as the second page of the report.
        final PdfProcessorTask task = PdfProcessorTask.empty();
        task.addNewPage(NewPage.emptyPage(pageSize).backgroundColor(Color.WHITE).build(), 0);

        // Invoke processor to create new document.
        final File tempFile = File.createTempFile("second_page", null, context.getCacheDir());
        PdfProcessor.processDocument(task, tempFile);

        // Create the document which will be the report's second page
        final PdfDocument pageDocument = PdfDocumentLoader.openDocument(context, Uri.fromFile(tempFile));

        // Create a free text annotation as the title of the second page.
        final FreeTextAnnotation titleFreeTextAnnotation = new FreeTextAnnotation(
            0,
            new RectF(228, 1024, 828, 964),
            "Some Annotations"
        );
        titleFreeTextAnnotation.setTextSize(40);
        pageDocument.getAnnotationProvider().addAnnotationToPage(titleFreeTextAnnotation);

        // Create a vector stamp annotation on the second page.
        final StampAnnotation vectorStamp = new StampAnnotation(
            0,
            new RectF(50, 724, 250, 524),
            "Stamp with custom AP stream"
        );
        // Set PDF from assets containing vector logo as annotation's appearance stream generator.
        vectorStamp.setAppearanceStreamGenerator(new AssetAppearanceStreamGenerator("images/PSPDFKit_Logo.pdf"));
        pageDocument.getAnnotationProvider().addAnnotationToPage(vectorStamp);

        // Create a free text annotation which describes the vector stamp
        final FreeTextAnnotation vectorStampDescriptionFreeTextAnnotation = new FreeTextAnnotation(
            0,
            new RectF(67, 520, 667, 420),
            "The logo above is a vector stamp annotation."
        );
        vectorStampDescriptionFreeTextAnnotation.setTextSize(18);
        pageDocument.getAnnotationProvider().addAnnotationToPage(vectorStampDescriptionFreeTextAnnotation);

        // Create an image stamp annotation on the second page.
        final Bitmap image = BitmapFactory.decodeStream(context.getAssets().open("images/android.png"));

        final StampAnnotation imageStamp = new StampAnnotation(
            0,
            new RectF(60, 400, 60 + image.getWidth() / 4, 400 - image.getHeight() / 4),
            image
        );
        pageDocument.getAnnotationProvider().addAnnotationToPage(imageStamp);

        // Create a free text annotation which describes the image stamp
        final FreeTextAnnotation imageStampDescriptionFreeTextAnnotation = new FreeTextAnnotation(
            0,
            new RectF(67, 240, 667, 160),
            "The image above is an image stamp annotation."
        );
        imageStampDescriptionFreeTextAnnotation.setTextSize(18);
        pageDocument.getAnnotationProvider().addAnnotationToPage(imageStampDescriptionFreeTextAnnotation);

        // Flatten all annotations
        final File flattenedTempFile = File.createTempFile("flattened_second_page", null, context.getCacheDir());
        final PdfProcessorTask flattenTask = PdfProcessorTask.fromDocument(pageDocument);
        flattenTask.changeAllAnnotations(PdfProcessorTask.AnnotationProcessingMode.FLATTEN);
        PdfProcessor.processDocument(flattenTask, flattenedTempFile);

        return PdfDocumentLoader.openDocument(context, Uri.fromFile(flattenedTempFile));
    }

    private void drawWatermark(final String name, PdfProcessorTask task, final Size pageSize, int pageCount) {
        final TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(30);
        textPaint.setColor(Color.argb(128, 255, 0, 0));
        textPaint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < pageCount; i++) {
            final int page = i;
            PageCanvas canvas = new PageCanvas(pageSize, canvas1 -> {
                String text = String.format(Locale.getDefault(), "Generated for %s. Page %d", name, (page + 1));
                canvas1.drawText(text, pageSize.width / 2, pageSize.height - 100, textPaint);
            });
            task.addCanvasDrawingToPage(canvas, i);
        }
    }
}
