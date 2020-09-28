/*
 *   Copyright © 2016-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import com.pspdfkit.PSPDFKit;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.configuration.policy.ApplicationPolicy;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * Example of how to customize application policy preventing copy/paste action.
 */
public class CustomApplicationPolicyExample extends PSPDFExample {

    /**
     * Custom Application policy.
     */
    public static class CustomApplicationPolicy extends ApplicationPolicy {

        private static final String LOG_TAG = "CustomApplicationPolicy";

        @Override
        public boolean hasPermissionForEvent(@NonNull PolicyEvent event) {
            switch (event) {
                case TEXT_COPY_PASTE:
                    // Disable TEXT_COPY_PASTE policy.
                    return false;
                default:
                    // It's usually a good practise logging unhandled events.
                    Log.i(LOG_TAG, String.format("event %s not included in current policy: %s", event,
                                                 getClass().getSimpleName()
                    ));
                    return false;
            }
        }
    }

    public CustomApplicationPolicyExample(@NonNull Context context) {
        super(context.getString(R.string.customApplicationPolicyExampleTitle), context.getString(R.string.customApplicationPolicyExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        // We use a custom utility class to extract the example document from the assets.
        extract(QUICK_START_GUIDE, title, context, documentFile -> {
            // To customize the application policy we extend ApplicationPolicy.
            CustomApplicationPolicy customApplicationPolicy = new CustomApplicationPolicy();
            PSPDFKit.setApplicationPolicy(customApplicationPolicy);

            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration.build())
                .build();

            context.startActivity(intent);
        });
    }
}
