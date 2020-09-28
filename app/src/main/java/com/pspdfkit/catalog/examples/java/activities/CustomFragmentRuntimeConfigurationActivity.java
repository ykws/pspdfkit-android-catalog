/*
 *   Copyright © 2014-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pspdfkit.configuration.PdfConfiguration;
import com.pspdfkit.configuration.page.PageScrollDirection;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfFragment;
import com.pspdfkit.ui.PdfThumbnailBar;

/**
 * This activity shows how to change {@link PdfFragment} configuration at runtime when used with custom activity.
 */
public class CustomFragmentRuntimeConfigurationActivity extends AppCompatActivity {

    public static final String EXTRA_URI = "CustomFragmentRuntimeConfigurationActivity.EXTRA_URI";

    private PdfFragment fragment;
    private PdfThumbnailBar thumbnailBar;
    private PdfConfiguration configuration;

    private static int getNightModeTheme(boolean isNightMode) {
        return isNightMode ? R.style.PSPDFCatalog_Theme_Dark : R.style.PSPDFCatalog_Theme;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the Uri provided when launching the activity.
        final Uri documentUri = getIntent().getParcelableExtra(EXTRA_URI);

        // Extract the existing fragment. The fragment only exist if it has been created previously (like if the activity is recreated).
        // If no fragment was found, create a new one providing it with the configuration and document Uri.
        PdfFragment fragment = (PdfFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            // Create a new configuration.
            configuration = new PdfConfiguration.Builder().build();
            // Create a new fragment.
            fragment = PdfFragment.newInstance(documentUri, configuration);
        } else {
            // Use existing fragment configuration.
            configuration = fragment.getConfiguration();
        }

        // Set theme according to invert colors property in the configuration.
        setTheme(getNightModeTheme(configuration.isInvertColors()));
        // Set activity layout. Must be called after setTheme for custom theme to work properly.
        setContentView(R.layout.activity_custom_fragment_runtime_configuration);

        findViewById(R.id.toggle_scroll_direction).setOnClickListener(v -> toggleScrollDirection());
        findViewById(R.id.toggle_night_mode_button).setOnClickListener(v -> toggleNightMode());

        // Add the fragment to the activity and register all needed listeners.
        setFragment(fragment);
    }

    private PdfFragment getFragment() {
        return fragment;
    }

    private void setFragment(@NonNull PdfFragment fragment) {
        this.fragment = fragment;

        // Replace old fragment with a new one.
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit();
    }

    /**
     * When changing fragment configuration we just need to replace fragment with a new instance with updated configuration.
     */
    private void toggleScrollDirection() {
        if (fragment == null) return;

        PageScrollDirection scrollDirection = fragment.getConfiguration().getScrollDirection();

        // Copy existing configuration to a new configuration builder.
        PdfConfiguration.Builder configurationBuilder = new PdfConfiguration.Builder(fragment.getConfiguration());

        // Toggle scroll direction.
        configurationBuilder.scrollDirection(
            scrollDirection == PageScrollDirection.HORIZONTAL ?
            PageScrollDirection.VERTICAL :
            PageScrollDirection.HORIZONTAL);

        // Create fragment based on current fragment and the new configuration.
        PdfFragment newFragment = PdfFragment.newInstance(fragment, configurationBuilder.build());
        setFragment(newFragment);
    }

    /**
     * Here we show how to implement the night mode. We replace existing fragment with
     * configuration that inverts rendering colors and set dark theme on the activity.
     */
    private void toggleNightMode() {
        if (fragment == null) return;

        // In this example, we will use invert colors property to control night mode to make things simple.
        boolean isNightModeActive = fragment.getConfiguration().isInvertColors();

        // Copy existing configuration to a new configuration builder.
        PdfConfiguration.Builder configurationBuilder = new PdfConfiguration.Builder(fragment.getConfiguration());

        // Toggle invert colors property.
        configurationBuilder.invertColors(!isNightModeActive);

        // Create fragment based on current fragment and the new configuration.
        PdfFragment newFragment = PdfFragment.newInstance(fragment, configurationBuilder.build());
        setFragment(newFragment);

        // Activity theme must be applied before setContentView. Thus we need to restart the activity.
        // When activity is restarted, we set the theme according to invertColors configuration property (see onCreate above).
        recreate();
    }
}
