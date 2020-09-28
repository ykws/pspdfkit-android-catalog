/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
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
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.activities.CustomFragmentRuntimeConfigurationActivity;
import com.pspdfkit.ui.PdfFragment;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * Shows how to change {@link PdfFragment} configuration at runtime when used with custom activity.
 */
public class CustomFragmentRuntimeConfigurationExample extends PSPDFExample {
    public CustomFragmentRuntimeConfigurationExample(@NonNull Context context) {
        super(context.getString(R.string.runtimeConfigurationFragmentExampleTitle), context.getString(R.string.runtimeConfigurationFragmentExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        extract(QUICK_START_GUIDE, title, context, documentFile -> {
            final Intent intent = new Intent(context, CustomFragmentRuntimeConfigurationActivity.class);
            intent.putExtra(CustomFragmentRuntimeConfigurationActivity.EXTRA_URI, Uri.fromFile(documentFile));
            context.startActivity(intent);
        });
    }
}
