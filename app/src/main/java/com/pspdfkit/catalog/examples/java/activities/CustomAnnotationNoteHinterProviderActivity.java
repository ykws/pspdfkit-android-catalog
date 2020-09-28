/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import com.pspdfkit.annotations.Annotation;
import com.pspdfkit.annotations.AnnotationProvider.OnAnnotationUpdatedListener;
import com.pspdfkit.annotations.AnnotationType;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.drawable.PdfDrawable;
import com.pspdfkit.ui.drawable.PdfDrawableProvider;

import java.util.ArrayList;
import java.util.List;

import static com.pspdfkit.example.utils.Utils.dpToPx;

/**
 * This example shows how to create a custom annotation note hinter extending {@link PdfDrawableProvider}.
 */
public class CustomAnnotationNoteHinterProviderActivity extends PdfActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomAnnotationNoteHinter customAnnotationNoteHinter = new CustomAnnotationNoteHinter(this);
        // Add new custom annotation note.
        getPdfFragment().addDrawableProvider(customAnnotationNoteHinter);
        getPdfFragment().addOnAnnotationUpdatedListener(customAnnotationNoteHinter);

    }

    /**
     * A custom annotation note hinter provider that works only for ink annotations.
     */
    private static class CustomAnnotationNoteHinter extends PdfDrawableProvider implements OnAnnotationUpdatedListener {

        @NonNull private final PdfActivity pdfActivity;
        private Drawable noteIcon;

        CustomAnnotationNoteHinter(@NonNull PdfActivity pdfActivity) {
            this.pdfActivity = pdfActivity;
            noteIcon = ContextCompat.getDrawable(pdfActivity, R.drawable.ic_bookmark);
        }

        @Override
        public void onAnnotationCreated(@NonNull Annotation annotation) {
            notifyDrawablesChangedIfSupported(annotation);
        }

        @Override
        public void onAnnotationUpdated(@NonNull Annotation annotation) {
            notifyDrawablesChangedIfSupported(annotation);
        }

        @Override
        public void onAnnotationRemoved(@NonNull Annotation annotation) {
            notifyDrawablesChangedIfSupported(annotation);
        }

        @Override
        public void onAnnotationZOrderChanged(int pageIndex,
                                              @NonNull List<Annotation> oldOrder,
                                              @NonNull List<Annotation> newOrder) {
        }

        @Nullable
        @Override
        public List<? extends PdfDrawable> getDrawablesForPage(@NonNull Context context,
                                                               @NonNull PdfDocument document,
                                                               @IntRange(from = 0) int pageIndex) {
            ArrayList<NoteInkHinterDrawable> drawables = new ArrayList<>();
            if (noteIcon != null) {
                List<Annotation> annotations = document.getAnnotationProvider().getAnnotations(pageIndex);
                for (Annotation annotation : annotations) {
                    if (annotation.getType() == AnnotationType.INK) {
                        drawables.add(new NoteInkHinterDrawable(pdfActivity, noteIcon, annotation));
                    }
                }
            }
            return drawables;
        }

        private void notifyDrawablesChangedIfSupported(@NonNull Annotation annotation) {
            if (annotation.getType() == AnnotationType.INK) {
                notifyDrawablesChanged();
            }
        }
    }

    private static class NoteInkHinterDrawable extends PdfDrawable {

        private static final int ALPHA = 255;

        @NonNull private final PdfActivity pdfActivity;
        @NonNull private final Drawable noteIcon;
        @NonNull private final Annotation annotation;
        @NonNull private final Rect viewBoundingBoxRounded;
        @NonNull private final RectF pdfBoundingBox;
        @NonNull private final PointF viewPoint;
        @NonNull private final RectF viewBoundingBox;
        private final int widthPx;
        private final int heightPx;
        private final int halfWidthPx;
        private final int halfHeightPx;

        NoteInkHinterDrawable(@NonNull PdfActivity pdfActivity,
                              @NonNull Drawable noteIcon,
                              @NonNull Annotation annotation) {
            this.pdfActivity = pdfActivity;
            this.noteIcon = noteIcon;
            this.annotation = annotation;
            this.viewBoundingBoxRounded = new Rect();
            this.pdfBoundingBox = new RectF();
            this.viewBoundingBox = new RectF();
            this.viewPoint = new PointF();
            annotation.getBoundingBox(pdfBoundingBox);

            this.widthPx = dpToPx(pdfActivity, 24);
            this.heightPx = dpToPx(pdfActivity, 24);

            this.halfWidthPx = widthPx / 2;
            this.halfHeightPx = heightPx / 2;
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            invalidateSelf();
            if (TextUtils.isEmpty(annotation.getContents())) {
                return;
            }
            DrawableCompat.setTint(noteIcon, annotation.getColor());
            noteIcon.setBounds(viewBoundingBoxRounded);
            noteIcon.draw(canvas);
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
            noteIcon.setAlpha(ALPHA);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            noteIcon.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public void updatePDFToViewTransformation(@NonNull Matrix matrix) {
            super.updatePDFToViewTransformation(matrix);

            annotation.getBoundingBox(pdfBoundingBox);
            viewPoint.set(pdfBoundingBox.centerX(), pdfBoundingBox.centerY());
            pdfActivity.getPdfFragment().getViewProjection().toViewPoint(viewPoint, annotation.getPageIndex());
            viewBoundingBox.top = (viewPoint.y - halfHeightPx);
            viewBoundingBox.bottom = (viewPoint.y + halfHeightPx);
            viewBoundingBox.left = viewPoint.x - halfWidthPx;
            viewBoundingBox.right = viewPoint.x + halfWidthPx;

            viewBoundingBox.round(viewBoundingBoxRounded);
        }
    }
}
