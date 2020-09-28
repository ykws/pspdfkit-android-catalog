/*
 *   Copyright © 2014-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.pspdfkit.configuration.activity.UserInterfaceViewMode;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivity;

/**
 * This subclass of {@link PdfActivity} adds a set of actions to change user interface view modes.
 */
public class UserInterfaceViewModesActivity extends PdfActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.show_user_interface_button).setOnClickListener(v -> {
            // This method overrides all restraints on showing user interface.
            // Shows user interface even when using USER_INTERFACE_VIEW_MODE_HIDDEN.
            setUserInterfaceVisible(true, true);
        });

        findViewById(R.id.hide_user_interface_button).setOnClickListener(v -> {
            // This method overrides all restraints on hiding user interface.
            // Hides user interface even when using USER_INTERFACE_MODE_VISIBLE.
            setUserInterfaceVisible(false, true);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.user_interface_view_modes_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean handled = false;
        final int itemId = item.getItemId();
        if (itemId == R.id.user_interface_view_mode_automatic) {
            handled = true;
            setUserInterfaceViewMode(UserInterfaceViewMode.USER_INTERFACE_VIEW_MODE_AUTOMATIC);
        } else if (itemId == R.id.user_interface_view_mode_automatic_border_pages) {
            handled = true;
            setUserInterfaceViewMode(UserInterfaceViewMode.USER_INTERFACE_VIEW_MODE_AUTOMATIC_BORDER_PAGES);
        } else if (itemId == R.id.user_interface_view_mode_visible) {
            handled = true;
            setUserInterfaceViewMode(UserInterfaceViewMode.USER_INTERFACE_VIEW_MODE_VISIBLE);
        } else if (itemId == R.id.user_interface_view_mode_hidden) {
            handled = true;
            setUserInterfaceViewMode(UserInterfaceViewMode.USER_INTERFACE_VIEW_MODE_HIDDEN);
        } else if (itemId == R.id.user_interface_view_mode_manual) {
            handled = true;
            setUserInterfaceViewMode(UserInterfaceViewMode.USER_INTERFACE_VIEW_MODE_MANUAL);
        }
        return handled || super.onOptionsItemSelected(item);
    }

    @Override
    public void onUserInterfaceVisibilityChanged(boolean visible) {
        super.onUserInterfaceVisibilityChanged(visible);
        // You can monitor UI visibility changes by overriding this method.
    }
}
