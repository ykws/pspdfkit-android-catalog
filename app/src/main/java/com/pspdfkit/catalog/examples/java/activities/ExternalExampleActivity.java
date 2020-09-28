/*
 *   Copyright © 2014-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.pspdfkit.PSPDFKit;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.document.ImageDocumentLoader;
import com.pspdfkit.document.ImageDocumentUtils;
import com.pspdfkit.document.download.DownloadJob;
import com.pspdfkit.document.download.DownloadProgressFragment;
import com.pspdfkit.document.download.DownloadRequest;
import com.pspdfkit.example.utils.Utils;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import java.io.File;

public class ExternalExampleActivity extends FragmentActivity {

    public static final String EXTRA_CONFIGURATION = "PSPDFKit.ExternalExampleActivity.configuration";
    private static final int REQUEST_OPEN_DOCUMENT = 1;
    private static final int REQUEST_ASK_FOR_PERMISSION = 2;
    private static final String IS_WAITING_FOR_RESULT = "PSPDFKit.ExternalExampleActivity.waitingForResult";

    private static final String DOWNLOAD_PROGRESS_FRAGMENT = "DownloadProgressFragment";

    private PdfActivityConfiguration configuration;
    private boolean waitingForResult = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OPEN_DOCUMENT) {
            waitingForResult = false;

            if (resultCode == Activity.RESULT_OK && data != null) {
                final Uri uri = data.getData();
                if (uri == null) return;
                final boolean isImageFile = ImageDocumentUtils.isImageUri(this, uri);

                // Some URIs can be opened directly, including local filesystem, app assets, and content provider URIs.
                if (PSPDFKit.isOpenableUri(ExternalExampleActivity.this, uri)) {
                    startActivity(createActivityIntent(uri, configuration, isImageFile));
                    finish();
                }
                // The Uri cannot be directly opened. Download the PDF document from the uri, for local access.
                else {
                    // Find the DownloadProgressFragment for showing download progress, or create a new one.
                    DownloadProgressFragment downloadFragment = (DownloadProgressFragment) getSupportFragmentManager().findFragmentByTag(
                        DOWNLOAD_PROGRESS_FRAGMENT
                    );
                    if (downloadFragment == null) {
                        final DownloadJob job = DownloadJob.startDownload(new DownloadRequest.Builder(this).uri(uri).build());
                        downloadFragment = new DownloadProgressFragment();
                        downloadFragment.show(getSupportFragmentManager(), DOWNLOAD_PROGRESS_FRAGMENT);
                        downloadFragment.setJob(job);
                    }

                    // Once the download is complete we launch the PdfActivity from the downloaded file.
                    downloadFragment.getJob().setProgressListener(new DownloadJob.ProgressListenerAdapter() {
                        @Override
                        public void onComplete(@NonNull File output) {
                            startActivity(createActivityIntent(Uri.fromFile(output), configuration, isImageFile));
                            finish();
                        }
                    });
                }

            } else if (resultCode == RESULT_CANCELED) {
                // If the user cancelled document selection, we just close the example.
                finish();
            }
        }
    }

    @NonNull
    private Intent createActivityIntent(@NonNull Uri uri, @NonNull PdfActivityConfiguration configuration, boolean isImageFile) {
        PdfActivityIntentBuilder pdfActivityIntentBuilder;
        if (isImageFile) {
            pdfActivityIntentBuilder = PdfActivityIntentBuilder.fromImageUri(this, uri);
            // Get the default image document configuration.
            // Default options in this configuration are specifically thought to enhance the user
            // experience for image documents (e.g. thumbnail bar and page number overlay are hidden).
            configuration = ImageDocumentLoader.getDefaultImageDocumentActivityConfiguration(configuration);
        } else {
            pdfActivityIntentBuilder = PdfActivityIntentBuilder.fromUri(this, uri);
        }
        return pdfActivityIntentBuilder.configuration(configuration).build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure the example activity is launched with the required extras.
        if (!getIntent().hasExtra(EXTRA_CONFIGURATION)) {
            throw new ExceptionInInitializerError(ExternalExampleActivity.class.getSimpleName() +
                                                      " was started without a PdfActivityConfiguration.");
        }

        // Extract the configuration for displaying the viewer activity.
        configuration = getIntent().getParcelableExtra(EXTRA_CONFIGURATION);

        // Check if the activity was recreated, and see if the user already started document picking.
        if (savedInstanceState != null) {
            waitingForResult = savedInstanceState.getBoolean(IS_WAITING_FOR_RESULT, false);
        }

        // Prevent the example from requesting multiple documents at the same time.
        if (!waitingForResult) {
            waitingForResult = true;
            // On Android 6.0+ we ask for SD card access permission. This isn't strictly necessary, but PSPDFKit
            // being able to access file directly will significantly improve performance.
            // Since documents can be annotated we ask for write permission as well.
            if (Utils.requestExternalStorageRwPermission(this, REQUEST_ASK_FOR_PERMISSION)) {
                showOpenFileDialog();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Retain if we are currently waiting for an intent to return, so we don't set it off
        // twice by accident.
        outState.putBoolean(IS_WAITING_FOR_RESULT, waitingForResult);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ASK_FOR_PERMISSION) {
            // We attempt to open document after permissions have been requested.
            // PSPDFKit can open documents without the permissions when SAF is used, however the access
            // without permissions will be significantly slower.
            showOpenFileDialog();
        }
    }

    private void showOpenFileDialog() {
        // Prepare an implicit intent which allows the user to select any document.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        // We accept PDF files and images (for image documents).
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"application/pdf", "image/*"});

        // Set of the intent for result, so we can retrieve the Uri of the selected document.
        startActivityForResult(intent, REQUEST_OPEN_DOCUMENT);
    }
}
