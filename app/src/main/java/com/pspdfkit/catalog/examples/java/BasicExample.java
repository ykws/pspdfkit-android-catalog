/*
 *   Copyright © 2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;

import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivity;

/**
 * Opens the {@link PdfActivity} for viewing a PDF stored within the app's asset
 * folder.
 */
public class BasicExample extends AssetExample {
    public BasicExample(Context context) {
        super(context.getString(R.string.assetExampleTitle), context.getString(R.string.assetExampleDescription));
    }

    @Override
    protected String getAssetPath() {
        return PSPDFExample.QUICK_START_GUIDE;
    }
}
