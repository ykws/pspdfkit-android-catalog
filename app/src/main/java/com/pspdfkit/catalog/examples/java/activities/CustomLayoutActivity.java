/*
 *   Copyright © 2014-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.catalog.R;
import com.pspdfkit.example.utils.Utils;
import com.pspdfkit.listeners.OnVisibilityChangedListener;
import com.pspdfkit.ui.PSPDFKitViews;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.PdfThumbnailGrid;


public class CustomLayoutActivity extends PdfActivity {

    /**
     * Gravity of the thumbnail grid drawer.
     */
    private static final int DRAWER_GRAVITY = GravityCompat.END;

    /**
     * Total number of pages in the current document.
     */
    private int documentPageCount = 0;

    private DrawerLayout drawerLayout;
    private PdfThumbnailGrid thumbnailGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get all required views for customization.
        drawerLayout = findViewById(R.id.drawerLayout);

        // Dynamically set the correct width of the thumbnail grid drawer.
        View thumbnailGridDrawer = findViewById(R.id.thumbnailGridDrawer);
        Utils.setProperNavigationDrawerWidth(thumbnailGridDrawer);

        thumbnailGridView = findViewById(R.id.pspdf__activity_thumbnail_grid);

        // Register the thumbnail grid with the fragment, so it is notified of page changes.
        getPdfFragment().addDocumentListener(thumbnailGridView);

        thumbnailGridView.addOnVisibilityChangedListener(new OnVisibilityChangedListener() {
            @Override
            public void onShow(@NonNull View view) {
                drawerLayout.openDrawer(DRAWER_GRAVITY);
            }

            @Override
            public void onHide(@NonNull View view) {
                drawerLayout.closeDrawer(DRAWER_GRAVITY);
            }
        });

        // Ensure action bar and grid are visible when drawer is opened.
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                thumbnailGridView.hide();
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_DRAGGING) {
                    thumbnailGridView.show();
                }
            }
        });

        // Go to the tapped page, and close the thumbnail drawer after selecting a page.
        thumbnailGridView.setOnPageClickListener((view, pageIndex) -> {
            setPageIndex(pageIndex);
            toggleThumbnailGrid();
        });

        // Flip to the next page.
        findViewById(R.id.nextPageButton).setOnClickListener(v -> {
            final int currentPage = getPageIndex();
            if (currentPage < documentPageCount - 1) setPageIndex(currentPage + 1);
        });

        // Flip to the previous page.
        findViewById(R.id.previousPageButton).setOnClickListener(v -> {
            final int currentPage = getPageIndex();
            if (currentPage > 0) setPageIndex(currentPage - 1);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == MENU_OPTION_THUMBNAIL_GRID) {
            toggleThumbnailGrid();
            hidePSPDFViews();

            // Consume the event, preventing the default behavior.
            return true;
        }

        // Close the drawer if outline or search are opened.
        else if (itemId == MENU_OPTION_OUTLINE || itemId == MENU_OPTION_SEARCH) {
            hideThumbnailGrid();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideThumbnailGrid();
    }

    /**
     * Called as soon as the PDF document has been loaded.
     * This has to be called on the main thread.
     */
    @UiThread
    @Override
    public void onDocumentLoaded(@NonNull PdfDocument document) {
        super.onDocumentLoaded(document);

        // Retrieve the total number of pages in the document.
        documentPageCount = document.getPageCount();
    }

    @SuppressWarnings("ConstantConditions")
    private void hidePSPDFViews() {
        final PSPDFKitViews pspdfKitViews = getPSPDFKitViews();
        if (pspdfKitViews.getOutlineView() != null) pspdfKitViews.getOutlineView().hide();
        if (pspdfKitViews.getSearchView() != null) pspdfKitViews.getSearchView().hide();
    }

    private void hideThumbnailGrid() {
        if (drawerLayout.isDrawerVisible(DRAWER_GRAVITY)) {
            drawerLayout.closeDrawer(DRAWER_GRAVITY);
        }
    }

    private void toggleThumbnailGrid() {
        if (drawerLayout.isDrawerOpen(DRAWER_GRAVITY)) {
            thumbnailGridView.hide();
            drawerLayout.closeDrawer(DRAWER_GRAVITY);
        } else {
            thumbnailGridView.show();
            drawerLayout.openDrawer(DRAWER_GRAVITY);
        }
    }
}
