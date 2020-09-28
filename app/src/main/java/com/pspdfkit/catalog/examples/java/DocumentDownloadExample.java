/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.document.download.DownloadJob;
import com.pspdfkit.document.download.DownloadProgressFragment;
import com.pspdfkit.document.download.DownloadRequest;
import com.pspdfkit.document.download.source.DownloadSource;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * This is an example showing how to use the {@link DownloadJob} to download a PDF document from the web.
 */
public class DocumentDownloadExample extends PSPDFExample {
    private static final String TAG = "DocumentDownloadExample";

    public DocumentDownloadExample(@NonNull final Context context) {
        super(context.getString(R.string.documentDownloadExampleTitle), context.getString(R.string.documentDownloadExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        // The web download source is a custom DownloadSource implemented below.
        final WebDownloadSource source;

        // Try to parse the URL pointing to the PDF document. If an error occurs, log it and leave the example.
        try {
            source = new WebDownloadSource(new URL("https://pspdfkit.com/downloads/case-study-box.pdf"));
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error while trying to parse the PDF Download URL.", e);
            return;
        }


        // Build a download request based on various input parameters. Provide the web source pointing to the document.
        final DownloadRequest request = new DownloadRequest.Builder(context)
            .source(source)
            .outputFile(new File(context.getDir("documents", Context.MODE_PRIVATE), "case-study-box.pdf"))
            .overwriteExisting(true)
            .build();

        // This will initiate the download.
        final DownloadJob job = DownloadJob.startDownload(request);
        final DownloadProgressFragment fragment = new DownloadProgressFragment();
        fragment.show(((FragmentActivity) context).getSupportFragmentManager(), "download-fragment");
        fragment.setJob(job);

        job.setProgressListener(new DownloadJob.ProgressListenerAdapter() {
            @Override
            public void onComplete(@NonNull File output) {
                final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(output))
                    .configuration(configuration.build())
                    .build();

                context.startActivity(intent);
            }

            @Override
            public void onError(@NonNull Throwable exception) {
                new AlertDialog.Builder(context)
                    .setMessage("There was an error downloading the example PDF file. For further information see Logcat.")
                    .show();
            }
        });
    }

    /**
     * This download source can be used to download a PDF document from the web.
     */
    private static class WebDownloadSource implements DownloadSource {

        @NonNull private final URL documentURL;

        private WebDownloadSource(@NonNull URL documentURL) {
            this.documentURL = documentURL;
        }

        /**
         * The open method needs to return an {@link InputStream} that will provide the complete document.
         */
        @Override
        public InputStream open() throws IOException {
            final HttpURLConnection connection = (HttpURLConnection) documentURL.openConnection();
            connection.connect();
            return connection.getInputStream();
        }

        /**
         * If the length is available it can be returned here. This is optional, and can improve the reported download progress, since it will then contain
         * a percentage of download.
         */
        @Override
        public long getLength() {
            long length = DownloadSource.UNKNOWN_DOWNLOAD_SIZE;

            // We try to estimate the download size using the content length header.
            URLConnection urlConnection = null;
            try {
                urlConnection = documentURL.openConnection();
                final int contentLength = urlConnection.getContentLength();
                if (contentLength != -1) {
                    length = contentLength;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null && urlConnection instanceof HttpURLConnection) {
                    ((HttpURLConnection) urlConnection).disconnect();
                }
            }

            return length;
        }

        @Override
        public String toString() {
            return "WebDownloadSource{" +
                "documentURL=" + documentURL +
                '}';
        }
    }
}
