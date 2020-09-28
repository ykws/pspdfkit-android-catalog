/*
 *   Copyright © 2018-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.examples.java.activities.FormEditingInFragmentActivity;
import com.pspdfkit.ui.PdfFragment;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * This example shows how to use form editing UI with custom activity that uses {@link PdfFragment}.
 */
public class FormEditingInFragmentExample extends PSPDFExample {

    public FormEditingInFragmentExample(@NonNull Context context) {
        super(context.getString(R.string.formEditingInFragmentExampleTitle), context.getString(R.string.formEditingInFragmentExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        extract("Form-fields.pdf", title, context, documentFile -> {
            final Intent intent = new Intent(context, FormEditingInFragmentActivity.class);
            intent.putExtra(FormEditingInFragmentActivity.EXTRA_URI, Uri.fromFile(documentFile));
            intent.putExtra(FormEditingInFragmentActivity.EXTRA_CONFIGURATION, configuration.build().getConfiguration());
            context.startActivity(intent);
        });
    }
}
