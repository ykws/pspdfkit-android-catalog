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

import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.activities.KioskActivity;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.catalog.R;

/**
 * This example lists all documents found in the assets.
 */
public class KioskExample extends PSPDFExample {

    public KioskExample(@NonNull final Context context) {
        super(context.getString(R.string.kioskExampleTitle), context.getString(R.string.kioskExampleDescription));
    }

    @Override
    public void launchExample(@NonNull Context context, @NonNull PdfActivityConfiguration.Builder configuration) {
        final Intent intent = new Intent(context, KioskActivity.class);
        // Pass the configuration to our activity.
        intent.putExtra(KioskActivity.CONFIGURATION_ARG, configuration.build());
        context.startActivity(intent);
    }
}
