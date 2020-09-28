/*
 *   Copyright © 2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog;

import androidx.multidex.MultiDexApplication;

import com.pspdfkit.example.PSPDFKitReporting;

public class PSPDFCatalog extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        PSPDFKitReporting.initializeBugReporting(this);
    }

}
