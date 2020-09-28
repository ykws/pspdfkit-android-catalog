/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.instant;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.instant.activities.InstantExampleConnectionActivity;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.catalog.R;

/**
 * Connects to example Instant server at <a href="https://pspdfkit.com/instant/try">pspdfkit.com/instant/try</a>
 */
public class InstantExample extends PSPDFExample {

    public InstantExample(@NonNull Context context) {
        super(context.getString(R.string.tryInstantExampleTitle), context.getString(R.string.tryInstantExampleDescription));
    }

    @Override
    public void launchExample(@NonNull Context context, @NonNull PdfActivityConfiguration.Builder configuration) {
        // Instant example starts with a simple login/connection screen.
        final Intent intent = new Intent(context, InstantExampleConnectionActivity.class);

        // Pass the configuration to the connection activity. This configuration will
        // be passed to created InstantPdfActivity with downloaded Instant document.
        intent.putExtra(InstantExampleConnectionActivity.CONFIGURATION_ARG, configuration.build());

        context.startActivity(intent);
    }
}
