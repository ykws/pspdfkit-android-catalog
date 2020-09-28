/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.configuration.activity.UserInterfaceViewMode;
import com.pspdfkit.configuration.page.PageFitMode;
import com.pspdfkit.configuration.page.PageScrollDirection;
import com.pspdfkit.configuration.page.PageScrollMode;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivity;

/**
 * Opens the {@link PdfActivity} configured for viewing a scientific paper.
 */
public class ScientificPaperExample extends AssetExample {

    public ScientificPaperExample(Context context) {
        super(context.getString(R.string.scientificPaperExampleTitle), context.getString(R.string.scientificPaperExampleDescription));
    }

    @Override
    protected String getAssetPath() {
        return "JKHF-AnnualReport.pdf";
    }

    @Override
    protected void prepareConfiguration(PdfActivityConfiguration.Builder configuration) {
        configuration.scrollMode(PageScrollMode.CONTINUOUS);
        configuration.scrollDirection(PageScrollDirection.VERTICAL);
        configuration.fitMode(PageFitMode.FIT_TO_WIDTH);
        configuration.pagePadding(5);
        configuration.setUserInterfaceViewMode(UserInterfaceViewMode.USER_INTERFACE_VIEW_MODE_VISIBLE);
    }
}
