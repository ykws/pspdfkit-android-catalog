/*
 *   Copyright © 2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */
package com.pspdfkit.catalog.examples.kotlin

import android.content.Context
import com.pspdfkit.catalog.PSPDFExample
import com.pspdfkit.catalog.R
import com.pspdfkit.ui.PdfActivity

/**
 * Opens the [PdfActivity] for viewing a PDF stored within the app's asset folder.
 */
class BasicExample(context: Context) : AssetExample(context, R.string.assetExampleTitle, R.string.assetExampleDescription) {

    override val assetPath: String
        get() = PSPDFExample.QUICK_START_GUIDE
}