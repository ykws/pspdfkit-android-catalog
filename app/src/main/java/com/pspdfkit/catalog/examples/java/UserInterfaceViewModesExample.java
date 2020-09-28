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

import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.activities.UserInterfaceViewModesActivity;
import com.pspdfkit.catalog.tasks.ExtractAssetTask;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.configuration.activity.TabBarHidingMode;
import com.pspdfkit.configuration.activity.UserInterfaceViewMode;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * This example shows how to change user interface view modes. To do so, it subclasses the
 * {@link PdfActivity} and calls {@link PdfActivity#setUserInterfaceViewMode(UserInterfaceViewMode)} method
 * to toggle between various user interface view modes.
 */
public class UserInterfaceViewModesExample extends PSPDFExample {

    public UserInterfaceViewModesExample(Context context) {
        super(context.getString(R.string.userInterfaceViewModesExampleTitle), context.getString(R.string.userInterfaceViewModesExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        // Configure the PSPDFKitAppCompatActivity to use the examples subclass. This is
        // important if your app wants to tweak the default behavior of our implementation.
        configuration.layout(R.layout.user_interface_view_modes_activity);

        // Hide navigation buttons when unused.
        configuration.hideNavigationButtons();

        // Hide tab bar when unused.
        configuration.setTabBarHidingMode(TabBarHidingMode.HIDE);

        // We use a custom utility class to extract the example document from the assets.
        ExtractAssetTask.extract(QUICK_START_GUIDE, title, context, documentFile -> {
            // Launch the custom example activity using the document and configuration.
            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration.build())
                .activityClass(UserInterfaceViewModesActivity.class)
                .build();

            // Possible display of sending custom data.
            // Bundle dataToSend = new Bundle();
            // dataToSend.putString(CustomActionsActivity.STRING_SAMPLE_ARG, "Some string.");
            // intent.putExtras(dataToSend);

            // Start the UserInterfaceViewModesActivity for the extracted document.
            context.startActivity(intent);
        });
    }
}
