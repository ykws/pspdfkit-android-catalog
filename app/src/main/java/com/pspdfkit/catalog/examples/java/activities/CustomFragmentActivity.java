/*
 *   Copyright © 2014-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.pspdfkit.annotations.Annotation;
import com.pspdfkit.annotations.LinkAnnotation;
import com.pspdfkit.annotations.NoteAnnotation;
import com.pspdfkit.annotations.actions.Action;
import com.pspdfkit.annotations.actions.ActionType;
import com.pspdfkit.annotations.actions.UriAction;
import com.pspdfkit.catalog.R;
import com.pspdfkit.configuration.PdfConfiguration;
import com.pspdfkit.document.DocumentSaveOptions;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.document.search.SearchResult;
import com.pspdfkit.listeners.DocumentListener;
import com.pspdfkit.listeners.OnDocumentLongPressListener;
import com.pspdfkit.ui.PdfFragment;
import com.pspdfkit.ui.PdfOutlineView;
import com.pspdfkit.ui.PdfThumbnailBar;
import com.pspdfkit.ui.PdfThumbnailGrid;
import com.pspdfkit.ui.outline.DefaultBookmarkAdapter;
import com.pspdfkit.ui.outline.DefaultOutlineViewListener;
import com.pspdfkit.ui.search.PdfSearchViewModular;
import com.pspdfkit.ui.search.SearchResultHighlighter;
import com.pspdfkit.ui.search.SimpleSearchResultListener;
import com.pspdfkit.utils.PdfUtils;

import java.util.List;

/**
 * This activity shows how to build a custom activity using the {@link PdfFragment} together
 * with some of the PSPDFKit views.
 */
public class CustomFragmentActivity extends AppCompatActivity implements DocumentListener, OnDocumentLongPressListener {

    public static final String EXTRA_URI = "CustomFragmentActivity.EXTRA_URI";

    private PdfFragment fragment;
    private PdfThumbnailBar thumbnailBar;
    private PdfConfiguration configuration;
    private PdfSearchViewModular modularSearchView;
    private PdfThumbnailGrid thumbnailGrid;
    private SearchResultHighlighter highlighter;
    private PdfOutlineView pdfOutlineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_fragment);

        // Get the Uri provided when launching the activity.
        final Uri documentUri = getIntent().getParcelableExtra(EXTRA_URI);

        // Create a new (plain) configuration.
        configuration = new PdfConfiguration.Builder().build();

        // Extract the existing fragment from the layout. The fragment only exist if it has been created
        // previously (like if the activity is recreated). If no fragment was found, create a new one
        // providing it with the configuration and document Uri.
        fragment = (PdfFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = PdfFragment.newInstance(documentUri, configuration);
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
        }

        initModularSearchViewAndButton();
        initOutlineViewAndButton();
        initThumbnailBar();
        initThumbnailGridAndButton();

        // Register the activity to be notified when the document is loaded.
        fragment.addDocumentListener(this);
        fragment.addDocumentListener(modularSearchView);
        fragment.addDocumentListener(thumbnailBar.getDocumentListener());
        fragment.addDocumentListener(thumbnailGrid);
        fragment.setOnDocumentLongPressListener(this);
    }

    private void initThumbnailGridAndButton() {
        thumbnailGrid = findViewById(R.id.thumbnailGrid);
        if (thumbnailGrid == null) {
            throw new IllegalStateException("Error while loading CustomFragmentActivity. The example layout was missing the thumbnail grid view.");
        }

        thumbnailGrid.setOnPageClickListener((view, pageIndex) -> {
            fragment.setPageIndex(pageIndex);
            view.hide();
        });

        // The thumbnail grid is hidden by default. Set up a click listener to show it.
        ImageView openThumbnailGridButton = findViewById(R.id.openThumbnailGridButton);
        openThumbnailGridButton.setOnClickListener(v -> thumbnailGrid.show());
        openThumbnailGridButton.setImageDrawable(tintDrawable(
            openThumbnailGridButton.getDrawable(),
            ContextCompat.getColor(this, R.color.white)
        ));
    }

    private void initThumbnailBar() {
        thumbnailBar = findViewById(R.id.thumbnailBar);
        if (thumbnailBar == null) {
            throw new IllegalStateException("Error while loading CustomFragmentActivity. The example layout was missing thumbnail bar view.");
        }
        thumbnailBar.setOnPageChangedListener((controller, pageIndex) -> fragment.setPageIndex(pageIndex));
    }

    private void initOutlineViewAndButton() {
        // Extract the remaining views from our custom layout.
        pdfOutlineView = findViewById(R.id.outlineView);
        if (pdfOutlineView == null) {
            throw new IllegalStateException("Error while loading CustomFragmentActivity. The example layout was missing the outline view.");
        }

        final DefaultOutlineViewListener outlineViewListener = new DefaultOutlineViewListener(fragment);
        pdfOutlineView.setOnAnnotationTapListener(outlineViewListener);
        pdfOutlineView.setOnOutlineElementTapListener(outlineViewListener);
        pdfOutlineView.setBookmarkAdapter(new DefaultBookmarkAdapter(fragment));

        ImageView openOutlineButton = findViewById(R.id.openOutlineButton);
        openOutlineButton.setOnClickListener(v -> pdfOutlineView.show());
        openOutlineButton.setImageDrawable(tintDrawable(
            openOutlineButton.getDrawable(),
            ContextCompat.getColor(this, R.color.white)
        ));
    }

    private void initModularSearchViewAndButton() {
        // The search result highlighter will highlight any selected result.
        highlighter = new SearchResultHighlighter(this);
        fragment.addDrawableProvider(highlighter);

        modularSearchView = findViewById(R.id.modularSearchView);
        if (modularSearchView == null) {
            throw new IllegalStateException("Error while loading CustomFragmentActivity. The example layout was missing the search view.");
        }

        modularSearchView.setSearchViewListener(new SimpleSearchResultListener() {
            @Override
            public void onMoreSearchResults(@NonNull List<SearchResult> results) {
                highlighter.addSearchResults(results);
            }

            @Override
            public void onSearchCleared() {
                highlighter.clearSearchResults();
            }

            @Override
            public void onSearchResultSelected(@Nullable SearchResult result) {
                // Pass on the search result to the highlighter. If 'null' the highlighter will clear any selection.
                highlighter.setSelectedSearchResult(result);

                if (result != null) {
                    fragment.scrollTo(PdfUtils.createPdfRectUnion(result.textBlock.pageRects), result.pageIndex, 250, false);
                }
            }
        });

        // The search view is hidden by default (see layout). Set up a click listener that will show the view once pressed.
        ImageView openSearchButton = findViewById(R.id.openSearchButton);
        openSearchButton.setImageDrawable(tintDrawable(
            openSearchButton.getDrawable(),
            ContextCompat.getColor(this, R.color.white)
        ));

        openSearchButton.setOnClickListener(v -> modularSearchView.show());
    }

    private void createNoteAnnotation(@IntRange(from = 0) final int pageIndex) {
        final RectF pageRect = new RectF(180, 692, 212, 660);
        final String contents = "This is note annotation was created from code.";
        final String icon = NoteAnnotation.CROSS;
        final int color = Color.GREEN;

        // Create the annotation, and set the color.
        final NoteAnnotation noteAnnotation = new NoteAnnotation(pageIndex, pageRect, contents, icon);
        noteAnnotation.setColor(color);

        fragment.addAnnotationToPage(noteAnnotation, false);
    }

    @Override
    public void onBackPressed() {
        if (modularSearchView.isDisplayed()) {
            modularSearchView.hide();
            return;
        } else if (thumbnailGrid.isDisplayed()) {
            thumbnailGrid.hide();
            return;
        } else if (pdfOutlineView.isDisplayed()) {
            pdfOutlineView.hide();
            return;
        }

        super.onBackPressed();
    }

    /**
     * This method binds the thumbnail bar and the search view to the fragment, once the document is loaded.
     * This has to be called on the main thread.
     */
    @UiThread
    @Override // DocumentListener
    public void onDocumentLoaded(@NonNull PdfDocument document) {
        fragment.addDocumentListener(modularSearchView);
        thumbnailBar.setDocument(document, configuration);
        modularSearchView.setDocument(document, configuration);
        pdfOutlineView.setDocument(document, configuration);
        thumbnailGrid.setDocument(document, configuration);

        // Adding note annotation to populate Annotation section in PdfOutlineView
        createNoteAnnotation(1);
    }

    @Override // DocumentListener
    public void onDocumentLoadFailed(@NonNull Throwable exception) {
        // Not used.
    }

    @Override // DocumentListener
    public boolean onDocumentSave(@NonNull PdfDocument document, @NonNull DocumentSaveOptions saveOptions) {
        // Not used.
        return true;
    }

    @Override // DocumentListener
    public void onDocumentSaved(@NonNull PdfDocument document) {
        // Not used.
    }

    @Override // DocumentListener
    public void onDocumentSaveFailed(@NonNull PdfDocument document, @NonNull Throwable exception) {
        // Not used.
    }

    @Override // DocumentListener
    public void onDocumentSaveCancelled(PdfDocument document) {
        // Not used.
    }

    @Override // DocumentListener
    public boolean onPageClick(@NonNull PdfDocument document,
                               @IntRange(from = 0) int pageIndex,
                               @Nullable MotionEvent event,
                               @Nullable PointF pagePosition,
                               @Nullable Annotation clickedAnnotation) {
        // Not used.
        return false;
    }

    @Override // DocumentListener
    public boolean onDocumentClick() {
        // Not used
        return false;
    }

    @Override // DocumentListener
    public void onPageChanged(@NonNull PdfDocument document, @IntRange(from = 0) int pageIndex) {
        // Not used
    }

    @Override
    public void onDocumentZoomed(@NonNull PdfDocument document, @IntRange(from = 0) int pageIndex, float scaleFactor) {
        // Not used
    }

    @Override
    public void onPageUpdated(@NonNull PdfDocument document, @IntRange(from = 0) int pageIndex) {
        // Not used
    }

    @Override
    public boolean onDocumentLongPress(@NonNull PdfDocument document,
                                       @IntRange(from = 0) int pageIndex,
                                       @Nullable MotionEvent event,
                                       @Nullable PointF pagePosition,
                                       @Nullable Annotation longPressedAnnotation) {
        if (fragment.getView() != null) {
            fragment.getView().performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
        if (longPressedAnnotation instanceof LinkAnnotation) {
            final Action action = ((LinkAnnotation) longPressedAnnotation).getAction();
            if (action != null && action.getType() == ActionType.URI) {
                String uri = ((UriAction) action).getUri();
                Toast.makeText(CustomFragmentActivity.this, uri, Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return false;
    }

    /**
     * Applies the {@code tint} color to the given {@code drawable}.
     */
    @NonNull
    private Drawable tintDrawable(@NonNull Drawable drawable, int tint) {
        final Drawable tintedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(tintedDrawable, tint);
        return tintedDrawable;
    }
}
