/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pspdfkit.document.formatters.DocumentJsonFormatter;
import com.pspdfkit.document.providers.DataProvider;
import com.pspdfkit.document.providers.InputStreamDataProvider;
import com.pspdfkit.catalog.R;
import com.pspdfkit.example.utils.Utils;
import com.pspdfkit.ui.PdfActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * This activity allows editing of annotations and exporting and importing of changes to an Instant Document JSON file on the external storage.
 */
public class DocumentJsonExampleActivity extends PdfActivity {
    private static final String TAG = "DocumentJsonExample";
    private static final int REQUEST_ASK_FOR_PERMISSION_EXPORT = 1;
    private static final int REQUEST_ASK_FOR_PERMISSION_IMPORT = 2;

    /** Output location of the exported JSON and read location of the import. */
    private static final File documentJsonFile = new File(Environment.getExternalStorageDirectory(), "document.json");

    private static void printFileContentsToLogcat(@NonNull final File file) {
        try (InputStream is = new FileInputStream(file)) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            do {
                line = reader.readLine();
                if (line != null) Log.i(TAG, line);
            } while (line != null);
        } catch (IOException e) {
            Log.e(TAG, "Error while reading file for printing it on logcat (" + file.getName() + ")", e);
        }
    }

    /** Adds import/export action to the toolbar. */
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.document_json_example, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean handled = false;

        final int itemId = item.getItemId();
        if (itemId == R.id.import_json) {
            handled = true;
            importDocumentJson(true);
        } else if (itemId == R.id.export_json) {
            handled = true;
            exportDocumentJson(true);
        }
        return handled || super.onOptionsItemSelected(item);
    }

    private void exportDocumentJson(final boolean requestExternalStorageAccess) {
        if (Utils.hasExternalStorageRwPermission(this)) {
            if (documentJsonFile.exists() && !documentJsonFile.delete()) {
                Toast.makeText(this, "Error while removing existing document.json prior to export.", Toast.LENGTH_LONG).show();
                return;
            }

            final OutputStream outputStream;
            try {
                outputStream = new FileOutputStream(documentJsonFile);
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Error while opening file 'document.json' for export. See logcat for more info.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error while opening file 'document.json' for export", e);
                return;
            }

            DocumentJsonFormatter.exportDocumentJsonAsync(getDocument(), outputStream)
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> printFileContentsToLogcat(documentJsonFile))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    () -> Toast.makeText(DocumentJsonExampleActivity.this, "Export successful! See /sdcard/document.json or logcat.", Toast.LENGTH_LONG).show(),
                    throwable -> {
                        Toast.makeText(DocumentJsonExampleActivity.this, "Error while exporting document JSON. See logcat for more info.", Toast.LENGTH_LONG)
                            .show();
                        Log.e(TAG, "Error while exporting document JSON", throwable);
                    }
                );
        } else if (requestExternalStorageAccess) {
            Utils.requestExternalStorageRwPermission(this, REQUEST_ASK_FOR_PERMISSION_EXPORT);
        }
    }

    private void importDocumentJson(final boolean requestExternalStorageAccess) {
        if (Utils.hasExternalStorageRwPermission(this)) {
            if (!documentJsonFile.exists()) {
                Toast.makeText(this, "There's no document.json file on the external storage.", Toast.LENGTH_LONG).show();
                return;
            }

            final DataProvider inputProvider = new DocumentJsonDataProvider();
            DocumentJsonFormatter.importDocumentJsonAsync(getDocument(), inputProvider)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Toast.makeText(DocumentJsonExampleActivity.this, "Import successful!", Toast.LENGTH_LONG).show(), throwable -> {
                    Toast.makeText(DocumentJsonExampleActivity.this, "Error while importing document JSON. See logcat for more info.", Toast.LENGTH_LONG)
                        .show();
                    Log.e(TAG, "Error while importing document JSON", throwable);
                });
        } else if (requestExternalStorageAccess) {
            Utils.requestExternalStorageRwPermission(this, REQUEST_ASK_FOR_PERMISSION_IMPORT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ASK_FOR_PERMISSION_EXPORT) {
            // Reinitiate the export action, but don't trigger another access request if permissions weren't granted.
            exportDocumentJson(false);
        } else if (requestCode == REQUEST_ASK_FOR_PERMISSION_IMPORT) {
            // Reinitiate the export action, but don't trigger another access request if permissions weren't granted.
            importDocumentJson(false);
        }
    }

    /** A small in-memory data provider for loading the JSON file from the external storage. */
    private static class DocumentJsonDataProvider extends InputStreamDataProvider {
        @NonNull
        @Override
        protected InputStream openInputStream() throws Exception {
            return new FileInputStream(documentJsonFile);
        }

        @Override
        public long getSize() {
            return documentJsonFile.length();
        }

        @NonNull
        @Override
        public String getUid() {
            return "document-json";
        }

        @Nullable
        @Override
        public String getTitle() {
            return null;
        }
    }
}
