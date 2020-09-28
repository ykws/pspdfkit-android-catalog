/*
 *   Copyright © 2014-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.catalog.R;

/**
 * This example showcases the inline multimedia extension of PSPDFKit. The multimedia-capable document
 * as well as the media files themselves are located within the asset folder. An even more detailed
 * description of this feature can be found in the README.html.
 */
public class InlineMediaExample extends AssetExample {

    public InlineMediaExample(Context context) {
        super(context.getString(R.string.inlineMultimediaExampleTitle), context.getString(R.string.inlineMultimediaExampleDescription));
    }

    @Override
    protected String getAssetPath() {
        return "inline-media/multimedia_android_v2.pdf";
    }

    @Override
    protected void prepareConfiguration(PdfActivityConfiguration.Builder configuration) {
        configuration.videoPlaybackEnabled(true);
    }
}
