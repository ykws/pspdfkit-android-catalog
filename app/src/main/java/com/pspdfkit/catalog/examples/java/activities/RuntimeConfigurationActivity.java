/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.configuration.page.PageScrollDirection;
import com.pspdfkit.configuration.page.PageScrollMode;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivity;

/**
 * Shows how to apply {@link PdfActivityConfiguration} to the activity at runtime.
 */
public class RuntimeConfigurationActivity extends PdfActivity {

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.runtime_configuration_example_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean handled = false;
        PdfActivityConfiguration configuration = getConfiguration();
        final int itemId = item.getItemId();
        // Create configuration builder with configuration pre-filled from the current configuration.
        // Set configuration on the activity. This will recreate the activity similar to changing orientation or language.
        if (itemId == R.id.toggle_night_mode) {
            handled = true;
            boolean isNightMode = configuration.getConfiguration().isInvertColors();
            int themeId = !isNightMode ? R.style.PSPDFCatalog_Theme_Dark : R.style.PSPDFCatalog_Theme;
            PdfActivityConfiguration newConfiguration = new PdfActivityConfiguration.Builder(getConfiguration(), themeId)
                .invertColors(!isNightMode)
                .build();
            setConfiguration(newConfiguration);
        } else if (itemId == R.id.toggle_scroll_direction) {
            handled = true;
            PdfActivityConfiguration newConfiguration = new PdfActivityConfiguration.Builder(getConfiguration())
                .scrollDirection(
                    configuration.getConfiguration().getScrollDirection() == PageScrollDirection.HORIZONTAL ?
                        PageScrollDirection.VERTICAL :
                        PageScrollDirection.HORIZONTAL)
                .build();
            setConfiguration(newConfiguration);
        } else if (itemId == R.id.toggle_scroll_mode) {
            handled = true;
            PdfActivityConfiguration newConfiguration = new PdfActivityConfiguration.Builder(getConfiguration())
                .scrollMode(configuration.getConfiguration().getScrollMode() == PageScrollMode.PER_PAGE ?
                    PageScrollMode.CONTINUOUS :
                    PageScrollMode.PER_PAGE)
                .build();
            setConfiguration(newConfiguration);
        }
        return handled || super.onOptionsItemSelected(item);
    }
}
