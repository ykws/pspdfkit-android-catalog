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
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.pspdfkit.PSPDFKit;
import com.pspdfkit.catalog.tasks.ExtractAssetTask;
import com.pspdfkit.document.ImageDocumentUtils;
import com.pspdfkit.document.download.DownloadJob;
import com.pspdfkit.document.download.DownloadProgressFragment;
import com.pspdfkit.document.download.DownloadRequest;
import com.pspdfkit.catalog.R;
import com.pspdfkit.example.utils.Utils;
import com.pspdfkit.ui.DocumentCoordinator;
import com.pspdfkit.ui.DocumentDescriptor;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.tabs.PdfTabBar;

import java.io.File;

import static com.pspdfkit.catalog.tasks.ExtractAssetTask.extract;

/**
 * This example activity show how to use tabs in {@link com.pspdfkit.ui.PdfActivity}.
 */
public class DocumentTabsActivity extends PdfActivity {

    private static final String[] assetFiles = new String[]{"Guide-v5.pdf", "Guide-v4.pdf", "Annotations.pdf"};

    private static final int REQUEST_OPEN_DOCUMENT = 1;
    private static final int REQUEST_ASK_FOR_PERMISSION = 2;

    private static final String DOWNLOAD_PROGRESS_FRAGMENT = "DownloadProgressFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add a few documents to document coordinator when first creating the activity.
        // Tab bar will pick these documents and display tabs for them.
        final DocumentCoordinator documentCoordinator = getDocumentCoordinator();
        if (getDocument() == null && savedInstanceState == null) {
            for (final String assetName : assetFiles) {
                // Extract document from the app's assets and place it in the device's internal storage.
                ExtractAssetTask.extract(assetName, assetName, this, documentFile -> documentCoordinator.addDocument(DocumentDescriptor.fromUri(Uri.fromFile(documentFile))));
            }

            // Extract image from the app's assets and place it in the device's internal storage.
            ExtractAssetTask.extract("images/android.png", "images/android.png", this,
                    documentFile -> {
                        DocumentDescriptor documentDescriptor = DocumentDescriptor.imageDocumentFromUri(Uri.fromFile(documentFile));
                        documentDescriptor.setTitle("Android Image Document");
                        documentCoordinator.addDocument(documentDescriptor);
                    }
            );
        }

        // Insert add tab button at the start of the tab bar.
        PdfTabBar tabsBar = getPSPDFKitViews().getTabBar();
        if (tabsBar != null) {
            ImageView addTabButton = (ImageView) getLayoutInflater().inflate(R.layout.item_add_button, tabsBar, false);
            addTabButton.setOnClickListener(v -> addNewTab());
            tabsBar.addView(addTabButton, 0);
        }
    }

    private void addNewTab() {
        // On Android 6.0+ we ask for SD card access permission. This isn't strictly necessary, but PSPDFKit
        // being able to access file directly will significantly improve performance.
        // Since documents can be annotated we ask for write permission as well.
        if (Utils.requestExternalStorageRwPermission(this, REQUEST_ASK_FOR_PERMISSION)) {
            showOpenFileDialog();
        }
    }

    private void showOpenFileDialog() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        // We accept PDF files and images (for image documents).
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/pdf", "image/*"});

        // Set of the intent for result, so we can retrieve the Uri of the selected document.
        startActivityForResult(intent, REQUEST_OPEN_DOCUMENT);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OPEN_DOCUMENT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                final Uri uri = data.getData();
                if (uri == null) return;
                final boolean isImageFile = ImageDocumentUtils.isImageUri(this, uri);

                // Some URIs can be opened directly, including local filesystem, app assets, and content provider URIs.
                if (PSPDFKit.isOpenableUri(this, uri)) {
                    showDocumentInNewTab(uri, isImageFile);
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

                    // Once the download is complete we show the downloaded document in a new tab.
                    downloadFragment.getJob().setProgressListener(new DownloadJob.ProgressListenerAdapter() {
                        @Override
                        public void onComplete(@NonNull File output) {
                            showDocumentInNewTab(Uri.fromFile(output), isImageFile);
                        }
                    });
                }

            }
        }
    }

    /**
     * Adds document from Uri to the {@link DocumentCoordinator} and makes it visible immediately.
     */
    private void showDocumentInNewTab(@NonNull Uri uri, boolean isImageDocument) {
        DocumentDescriptor documentDescriptor = isImageDocument ?
                DocumentDescriptor.imageDocumentFromUri(uri) :
                DocumentDescriptor.fromUri(uri);
        getDocumentCoordinator().addDocument(documentDescriptor);
        getDocumentCoordinator().setVisibleDocument(documentDescriptor);
    }
}
