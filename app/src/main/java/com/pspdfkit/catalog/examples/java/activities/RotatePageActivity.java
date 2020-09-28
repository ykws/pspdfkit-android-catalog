/*
 *   Copyright © 2018-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivity;

/**
 * This example shows how to rotate pages using {@link PdfDocument#setRotationOffset(int, int)}.
 */
public class RotatePageActivity extends PdfActivity {

    /**
     * Creates our custom navigation menu.
     */
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // It's important to call super before inflating the custom menu, or the custom menu won't be shown.
        super.onCreateOptionsMenu(menu);
        // Inflate our custom menu items, for rotating pages.
        getMenuInflater().inflate(R.menu.activity_rotate_example, menu);
        return true;
    }

    /**
     * Handles clicks on the navigation option menu items.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.rotateClockwise) {
            rotatePage(getPageIndex(), 90);
            return true;
        } else if (itemId == R.id.rotateCounterClockwise) {
            rotatePage(getPageIndex(), -90);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void rotatePage(int pageIndex, int rotationToApply) {
        int currentRotationOffset = getDocument().getRotationOffset(pageIndex);
        // Add the desired rotation to the current offset.
        int newRotation = currentRotationOffset + rotationToApply;

        // Make sure that the new rotation offset is in bounds.
        if (newRotation < 0) {
            newRotation += 360;
        } else if (newRotation >= 360) {
            newRotation -= 360;
        }
        if (getPdfFragment().getDocument() != null) {
            getPdfFragment().getDocument().setRotationOffset(newRotation, pageIndex);
        }
    }
}
