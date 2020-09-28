/*
 *   Copyright © 2014-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivity;

/**
 * Shows the {@link PdfActivity} with a password protected document loaded.
 */
public class PasswordExample extends AssetExample {

    public PasswordExample(Context context) {
        super(context.getString(R.string.passwordExampleTitle), context.getString(R.string.passwordExampleDescription));
    }

    @Override
    protected String getAssetPath() {
        return "password.pdf";
    }
}
