/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.graphics.RectF;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import com.pspdfkit.annotations.Annotation;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.PdfFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * This example shows how to zoom/animate between annotations of a document using the
 * {@link PdfFragment#zoomTo(RectF, int, long)} method.
 */
public class ZoomExampleActivity extends PdfActivity {

    /**
     * This list will holds all annotations of the loaded document. It is populated in onDocumentLoaded().
     */
    private List<Annotation> pageAnnotations = new ArrayList<>();

    /**
     * This holds reference to the currently zoomed annotation.
     */
    private Annotation currentAnnotation;

    /**
     * Padding around annotation bounding box used when zooming.
     */
    private int ANNOTATION_BOUNDING_BOX_PADDING_PX = 16;

    /**
     * Creates our custom navigation menu.
     */
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // It's important to call super before inflating the custom menu, or the custom menu won't be shown.
        super.onCreateOptionsMenu(menu);
        // Inflate our custom menu items, for navigation between annotations.
        getMenuInflater().inflate(R.menu.activity_zoom_example, menu);
        return true;
    }

    /**
     * Handles clicks on the navigation option menu items.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.nextAnnotation) {
            zoomToNextAnnotation();
            return true;
        } else if (itemId == R.id.previousAnnotation) {
            zoomToPreviousAnnotation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Once the document is loaded, we extract all the annotations and put them into our list.
     * That way we can easily move forth and back between the annotations.
     * This has to be called on the main thread.
     */
    @UiThread
    @Override
    public void onDocumentLoaded(@NonNull PdfDocument document) {
        for (int i = 0, n = document.getPageCount(); i < n; i++) {
            final List<Annotation> pageAnnotations = document.getAnnotationProvider().getAnnotations(i);
            if (pageAnnotations != null) {
                this.pageAnnotations.addAll(pageAnnotations);
            }
        }
    }

    /**
     * Called when pressing the "next annotation" button. Finds the next annotation and issues a call
     * to {@link PdfFragment#zoomTo(RectF, int, long)}.
     */
    private void zoomToNextAnnotation() {
        if (pageAnnotations == null || pageAnnotations.isEmpty()) return;
        final int currentAnnotationIndex = (currentAnnotation == null) ? -1 : pageAnnotations.indexOf(currentAnnotation);
        final int nextAnnotationIndex = Math.min(currentAnnotationIndex + 1, pageAnnotations.size() - 1);
        if (nextAnnotationIndex != currentAnnotationIndex) {
            currentAnnotation = pageAnnotations.get(nextAnnotationIndex);
            final RectF boundingBox = currentAnnotation.getBoundingBox();
            boundingBox.inset(-ANNOTATION_BOUNDING_BOX_PADDING_PX, -ANNOTATION_BOUNDING_BOX_PADDING_PX);
            getPSPDFKitViews().getFragment().zoomTo(boundingBox, currentAnnotation.getPageIndex(), 300);
        }
    }

    /**
     * Called when pressing the "previous annotation" button. Finds the previous annotation and issues a call
     * to {@link PdfFragment#zoomTo(RectF, int, long)}.
     */
    private void zoomToPreviousAnnotation() {
        if (currentAnnotation == null || pageAnnotations == null || pageAnnotations.isEmpty()) return;
        final int currentAnnotationIndex = pageAnnotations.indexOf(currentAnnotation);
        final int nextAnnotationIndex = Math.max(currentAnnotationIndex - 1, 0);
        if (nextAnnotationIndex != currentAnnotationIndex) {
            currentAnnotation = pageAnnotations.get(nextAnnotationIndex);
            final RectF boundingBox = currentAnnotation.getBoundingBox();
            boundingBox.inset(-ANNOTATION_BOUNDING_BOX_PADDING_PX, -ANNOTATION_BOUNDING_BOX_PADDING_PX);
            getPSPDFKitViews().getFragment().zoomTo(boundingBox, currentAnnotation.getPageIndex(), 300);
        }
    }
}
