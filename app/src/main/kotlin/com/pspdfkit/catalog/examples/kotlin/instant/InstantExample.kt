/*
 *   Copyright © 2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.kotlin.instant

import android.content.Context
import android.content.Intent
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.catalog.PSPDFExample
import com.pspdfkit.catalog.R
import com.pspdfkit.catalog.examples.kotlin.instant.activities.InstantExampleConnectionActivity

/**
 * Connects to example Instant server at [pspdfkit.com/instant/try](https://pspdfkit.com/instant/try)
 */
class InstantExample(context: Context) : PSPDFExample(context, R.string.tryInstantExampleTitle, R.string.tryInstantExampleDescription) {

    override fun launchExample(context: Context, configuration: PdfActivityConfiguration.Builder) {
        // Instant example starts with a simple login/connection screen.
        val intent = Intent(context, InstantExampleConnectionActivity::class.java)
        // Pass the configuration to the connection activity. This configuration will
        // be passed to created InstantPdfActivity with downloaded Instant document.
        intent.putExtra(InstantExampleConnectionActivity.CONFIGURATION_ARG, configuration.build())
        context.startActivity(intent)
    }
}