/*
 *   Copyright © 2014-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.activities.CustomFragmentActivity;
import com.pspdfkit.catalog.tasks.ExtractAssetTask;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.catalog.R;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

public class FragmentExample extends PSPDFExample {

    public FragmentExample(Context context) {
        super(context.getString(R.string.fragmentExampleTitle), context.getString(R.string.fragmentExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull PdfActivityConfiguration.Builder configuration) {
        ExtractAssetTask.extract(QUICK_START_GUIDE, title, context, documentFile -> {
            final Intent intent = new Intent(context, CustomFragmentActivity.class);
            intent.putExtra(CustomFragmentActivity.EXTRA_URI, Uri.fromFile(documentFile));
            context.startActivity(intent);
        });
    }
}
