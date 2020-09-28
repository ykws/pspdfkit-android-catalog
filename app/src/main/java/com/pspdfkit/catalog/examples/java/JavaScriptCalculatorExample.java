/*
 *   Copyright © 2018-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;


import android.content.Context;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.configuration.activity.ThumbnailBarMode;
import com.pspdfkit.configuration.activity.UserInterfaceViewMode;
import com.pspdfkit.configuration.page.PageFitMode;
import com.pspdfkit.configuration.sharing.ShareFeatures;
import com.pspdfkit.catalog.R;

/**
 * Opens the JavaScript Calculator example from assets.
 */
public class JavaScriptCalculatorExample extends AssetExample {
    public JavaScriptCalculatorExample(Context context) {
        super(context.getString(R.string.javaScriptCalculatorExampleTitle), context.getString(R.string.javaScriptCalculatorExampleDescription));
    }

    @Override
    protected String getAssetPath() {
        return "Calculator.pdf";
    }

    @Override
    protected void prepareConfiguration(PdfActivityConfiguration.Builder configuration) {
        configuration
                .fitMode(PageFitMode.FIT_TO_WIDTH)
                // Disable all PSPDFKit views.
                .disableAnnotationEditing()
                .disableDocumentEditor()
                .disableSearch()
                .disableOutline()
                .disableBookmarkList()
                .disableAnnotationList()
                .setThumbnailBarMode(ThumbnailBarMode.THUMBNAIL_BAR_MODE_NONE)
                .hideDocumentTitleOverlay()
                .hidePageNumberOverlay()
                .hideThumbnailGrid()
                .setEnabledShareFeatures(ShareFeatures.none())
                .disablePrinting()
                .hideSettingsMenu()
                // Force toolbar visibility.
                .setUserInterfaceViewMode(UserInterfaceViewMode.USER_INTERFACE_VIEW_MODE_VISIBLE)
                // Disable text selection.
                .textSelectionEnabled(false)
                // Disable zoom.
                .maxZoomScale(1.0f);
    }
}
