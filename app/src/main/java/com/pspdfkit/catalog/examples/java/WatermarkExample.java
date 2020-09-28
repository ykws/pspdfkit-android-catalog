/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.document.providers.AssetDataProvider;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.activities.WatermarkExampleActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

/**
 * Shows how to create watermarks using Drawable API.
 */
public class WatermarkExample extends PSPDFExample {

    public WatermarkExample(Context context) {
        super(
            context.getString(R.string.watermarkExampleTitle),
            context.getString(R.string.watermarkExampleDescription)
        );
    }

    @Override
    public void launchExample(@NonNull Context context, @NonNull PdfActivityConfiguration.Builder configuration) {
        final Intent intent = PdfActivityIntentBuilder.fromDataProvider(context, new AssetDataProvider("Guide-v6.pdf"))
            .configuration(
                configuration.build()
            )
            .activityClass(WatermarkExampleActivity.class)
            .build();

        context.startActivity(intent);
    }
}
