/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import com.pspdfkit.annotations.Annotation;
import com.pspdfkit.annotations.BorderEffect;
import com.pspdfkit.annotations.BorderStyle;
import com.pspdfkit.annotations.FreeTextAnnotation;
import com.pspdfkit.annotations.HighlightAnnotation;
import com.pspdfkit.annotations.InkAnnotation;
import com.pspdfkit.annotations.LineEndType;
import com.pspdfkit.annotations.NoteAnnotation;
import com.pspdfkit.annotations.SoundAnnotation;
import com.pspdfkit.annotations.SquareAnnotation;
import com.pspdfkit.annotations.StampAnnotation;
import com.pspdfkit.annotations.appearance.AssetAppearanceStreamGenerator;
import com.pspdfkit.annotations.sound.AudioExtractor;
import com.pspdfkit.catalog.examples.java.AnnotationCreationExample;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.utils.EdgeInsets;
import com.pspdfkit.utils.Size;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This activity shows how to create various annotations programmatically. Also have a look
 * at the {@link AnnotationCreationExample} class.
 */
public class AnnotationCreationActivity extends PdfActivity {

    private static final int ADD_STAMP_ITEM_ID = 1234;

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, ADD_STAMP_ITEM_ID, 0, "Add Stamp");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == ADD_STAMP_ITEM_ID) {
            createStamp();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @UiThread
    @Override
    public void onDocumentLoaded(@NonNull final PdfDocument document) {
        final int pageIndex = 0;

        createNoteAnnotation(pageIndex);
        createHighlightAnnotation(pageIndex, "PSPDFKit", Color.YELLOW);
        createHighlightAnnotation(pageIndex, "QuickStart", Color.GREEN);
        createFreeTextAnnotation(pageIndex);
        createStampAnnotationWithCustomApStream(pageIndex);
        createFreeTextCallout(pageIndex);

        // Create an ink annotation. Note that editing of ink annotations is disabled inside
        // the AnnotationCreationExample class. To edit ink annotations, add it to the list of
        // editable types.
        createInkAnnotation(pageIndex);

        createCloudySquareAnnotation(pageIndex);

        createSoundAnnotation(pageIndex);
    }

    private void createStamp() {
        final PdfDocument document = getDocument();
        if (document == null) return;
        final int pageIndex = getPageIndex();
        final Size pageSize = document.getPageSize(pageIndex);
        float halfWidth = pageSize.width / 2;
        float halfHeight = pageSize.height / 2;
        final RectF rect = new RectF(
            halfWidth - 100, halfHeight + 100,
            halfWidth + 100, halfHeight - 100
        );

        final StampAnnotation stamp = new StampAnnotation(pageIndex, rect, "STAMP_SUBJECT");
        final int color = Color.rgb(255, 0, 0);
        stamp.setColor(color);
        stamp.setFillColor(Color.rgb(255, 255, 255));

        addAnnotationToDocument(stamp);
    }

    private void createNoteAnnotation(@IntRange(from = 0) final int pageIndex) {
        final RectF pageRect = new RectF(180, 692, 212, 660);
        final String contents = "This is note annotation was created from code.";
        final String icon = NoteAnnotation.CROSS;
        final int color = Color.GREEN;

        // Create the annotation, and set the color.
        final NoteAnnotation noteAnnotation = new NoteAnnotation(pageIndex, pageRect, contents, icon);
        noteAnnotation.setColor(color);

        addAnnotationToDocument(noteAnnotation);
    }

    private void createHighlightAnnotation(@IntRange(from = 0) final int pageIndex,
                                           @NonNull final String highlightedText, @ColorInt final int color) {
        final PdfDocument document = getDocument();
        if (document == null) return;

        // Find the provided text on the current page.
        final int textPosition = document.getPageText(pageIndex).indexOf(highlightedText);

        if (textPosition >= 0) {
            // To create a text highlight, extract the rects of the text to highlight and pass them
            // to the annotation constructor.
            final List<RectF> textRects = document.getPageTextRects(pageIndex, textPosition, highlightedText.length(), true);
            final HighlightAnnotation highlightAnnotation = new HighlightAnnotation(pageIndex, textRects);

            highlightAnnotation.setColor(color);

            addAnnotationToDocument(highlightAnnotation);
        } else {
            Toast.makeText(this, "Can't find the text to highlight.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createInkAnnotation(@IntRange(from = 0) final int pageIndex) {
        final InkAnnotation inkAnnotation = new InkAnnotation(pageIndex);
        inkAnnotation.setColor(Color.rgb(255, 165, 0)); // Orange
        inkAnnotation.setLineWidth(10);

        // Create a line from a list of points.
        final List<PointF> line = new ArrayList<>();
        for (int x = 120; x < 720; x += 60) {
            final int y = ((x % 120) == 0) ? 400 : 350;
            line.add(new PointF(x, y));
        }

        // Ink annotations can hold multiple lines. This example only uses a single line.
        inkAnnotation.setLines(Collections.singletonList(line));

        addAnnotationToDocument(inkAnnotation);
    }

    private void createFreeTextAnnotation(@IntRange(from = 0) final int pageIndex) {
        final String contents = "Add text to pages using FreeTextAnnotations";
        final RectF pageRect = new RectF(100f, 980f, 320f, 930f);

        final FreeTextAnnotation freeTextAnnotation = new FreeTextAnnotation(pageIndex, pageRect, contents);
        freeTextAnnotation.setColor(Color.BLUE);
        freeTextAnnotation.setTextSize(20f);

        addAnnotationToDocument(freeTextAnnotation);
    }

    private void createFreeTextCallout(@IntRange(from = 0) final int pageIndex) {
        final String contents = "Call out things using call outs";
        final RectF pageRect = new RectF(250f, 100f, 620f, 200f);

        final FreeTextAnnotation freeTextAnnotation = new FreeTextAnnotation(pageIndex, pageRect, contents);
        freeTextAnnotation.setColor(Color.BLUE);
        freeTextAnnotation.setTextSize(20f);
        freeTextAnnotation.setTextInsets(new EdgeInsets(0, 150f, 0, 0));
        freeTextAnnotation.setIntent(FreeTextAnnotation.FreeTextAnnotationIntent.FREE_TEXT_CALLOUT);

        List<PointF> points = new ArrayList<>(3);
        points.add(new PointF(255f, 195f));
        points.add(new PointF(325f, 150f));
        points.add(new PointF(400f, 150f));
        freeTextAnnotation.setCallOutPoints(points);

        freeTextAnnotation.setBorderWidth(1.5f);
        freeTextAnnotation.setBorderStyle(BorderStyle.SOLID);
        freeTextAnnotation.setBorderColor(Color.BLACK);
        freeTextAnnotation.setLineEnd(LineEndType.CLOSED_ARROW);

        addAnnotationToDocument(freeTextAnnotation);
    }

    private void createCloudySquareAnnotation(int pageIndex) {
        final RectF pageRect = new RectF(100f, 900f, 320f, 850f);
        final SquareAnnotation squareAnnotation = new SquareAnnotation(pageIndex, pageRect);
        squareAnnotation.setColor(Color.RED);

        squareAnnotation.setBorderEffect(BorderEffect.CLOUDY);
        squareAnnotation.setBorderEffectIntensity(3);

        addAnnotationToDocument(squareAnnotation);
    }

    private void createStampAnnotationWithCustomApStream(int pageIndex) {
        // In order for rotation to work properly your stamps with custom AP streams need to match the source aspect ratio exactly.
        // This PDF is 320x360 points big.
        final RectF pageRect = new RectF(500f, 980f, 660f, 800f);

        final StampAnnotation stampAnnotation = new StampAnnotation(pageIndex, pageRect, "Stamp with custom AP stream");
        // Set PDF from assets containing vector logo as annotation's appearance stream generator.
        stampAnnotation.setAppearanceStreamGenerator(new AssetAppearanceStreamGenerator("images/PSPDFKit_Logo.pdf"));

        addAnnotationToDocument(stampAnnotation);
    }

    private void createSoundAnnotation(int pageIndex) {
        try {
            // We are going to extract first audio track from sample video in assets.
            // Audio extractor supports decoding audio tracks from all media formats that are supported by `MediaExtractor`.
            AudioExtractor audioExtractor = new AudioExtractor(this, Uri.parse("file:///android_asset/inline-media/videos/small.mp4"));
            audioExtractor.selectAudioTrack(0);
            audioExtractor.extractAudioTrackAsync().subscribe(embeddedAudioSource -> {
                // Create new sound annotation from the extracted audio track.
                SoundAnnotation soundAnnotation = new SoundAnnotation(pageIndex, new RectF(580f, 700f, 600f, 685f), embeddedAudioSource);
                addAnnotationToDocument(soundAnnotation);
            });
        } catch (IOException e) {
            // Handle possible IOException, thrown when the Uri does not point to correct file/asset.
        }
    }

    /**
     * Add the annotation to the document, and update the annotation in the UI.
     */
    private void addAnnotationToDocument(final Annotation annotation) {
        // You can add annotation to document and notify PdfFragment to refresh the UI.
        // getDocument().getAnnotationProvider()
        //     .addAnnotationToPageAsync(annotation)
        //     .subscribe(() -> getPdfFragment().notifyAnnotationHasChanged(annotation));

        // Or use the convenience method for adding annotations to page in PdfFragment:
        getPdfFragment().addAnnotationToPage(annotation, false);
    }
}
