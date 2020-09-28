/*
 *   Copyright © 2014-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.configuration.activity.TabBarHidingMode;
import com.pspdfkit.configuration.activity.ThumbnailBarMode;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.activities.CustomLayoutActivity;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * This example shows how to use a custom Activity and layout. In detail:
 * <p>
 * - It subclasses the {@link PdfActivity} and uses a custom layout resource.
 * - It removes the thumbnail bar and adds two navigation buttons to the layouts ("Next" and "Previous").
 * - It puts the thumbnail grid into the right navigation drawer.
 */
public class CustomLayoutExample extends PSPDFExample {

    public CustomLayoutExample(Context context) {
        super(context.getString(R.string.customLayoutExampleTitle), context.getString(R.string.customLayoutExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        // Define the custom layout of our activity inside the configuration.
        configuration.layout(R.layout.custom_pdf_activity);

        // The custom layout has no thumbnail bar. In order to prevent the activity from accessing it
        // we have to deactivate it in the configuration.
        configuration.setThumbnailBarMode(ThumbnailBarMode.THUMBNAIL_BAR_MODE_NONE);

        // The custom layout has no document editor. In order to prevent the activity from accessing it
        // we have to deactivate it in the configuration.
        configuration.disableDocumentEditor();

        // The custom layout has no document title overlay. In order to prevent the activity from accessing it
        // we have to deactivate it in the configuration.
        configuration.hideDocumentTitleOverlay();

        // The custom layout has no navigation buttons. In order to prevent the activity from accessing it
        // we have to deactivate it in the configuration.
        configuration.hideNavigationButtons();

        // This example shows the thumbnail grid in a custom drawer layout.
        configuration.showThumbnailGrid();

        // Disable forms editing.
        configuration.disableFormEditing();

        // Hide tab bar as it's not used by the custom layout.
        configuration.setTabBarHidingMode(TabBarHidingMode.HIDE);

        // We keep things simple, and use inline search and deactivate immersive mode for this example.
        configuration.setSearchType(PdfActivityConfiguration.SEARCH_INLINE);
        configuration.useImmersiveMode(false);

        // We use a custom utility class to extract the example document from the assets.
        extract(QUICK_START_GUIDE, title, context, documentFile -> {
            // To start the CustomLayoutActivity create a launch intent using the builder.
            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration.build())
                .activityClass(CustomLayoutActivity.class)
                .build();

            context.startActivity(intent);
        });
    }
}
