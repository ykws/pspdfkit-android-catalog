/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.utils.StringUtils;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.document.download.DownloadJob;
import com.pspdfkit.document.download.DownloadRequest;
import com.pspdfkit.document.download.Progress;
import com.pspdfkit.document.providers.DataProvider;
import com.pspdfkit.document.providers.InputStreamDataProvider;
import com.pspdfkit.document.providers.ProgressDataProvider;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

/**
 * This example shows how to create a custom data provider that loads a document from the web and shows the progress in the {@link PdfActivity}.
 * Furthermore, it implements {@link Parcelable} to allow using the data provider with {@link PdfActivity}.
 */
public class ProgressProviderExample extends PSPDFExample {

    /**
     * Custom data provider for loading a PDF document from the web. This provider derives from
     * {@code InputStreamDataProvider} which handles loading data from a stream object.
     */
    public static class RemoteDataProvider extends InputStreamDataProvider implements ProgressDataProvider, Parcelable {

        public static final Creator<RemoteDataProvider> CREATOR = new Creator<RemoteDataProvider>() {
            @Override
            public RemoteDataProvider createFromParcel(Parcel in) {
                return new RemoteDataProvider(in);
            }

            @Override
            public RemoteDataProvider[] newArray(int size) {
                return new RemoteDataProvider[size];
            }
        };
        /** The url where the PDF document is located. */
        private final String url;
        /** Responsible for downloading our PDF. */
        private DownloadJob downloadJob;
        /** Used to wait until the download is done. */
        private CountDownLatch downloadLatch = new CountDownLatch(1);
        /** Used to notify the PdfFragment of download progress updates. */
        private PublishSubject<Double> progressSubject = PublishSubject.create();

        /**
         * Create a new data provider for reading the PDF document stored at the URL.
         * @param url The url of the PDF document.
         */
        public RemoteDataProvider(String url) {
            this.url = url;
        }

        protected RemoteDataProvider(Parcel in) {
            url = in.readString();
        }

        @NonNull
        @Override
        public Flowable<Double> observeProgress() {
            // We can just return our PublishSubject.
            return progressSubject.toFlowable(BackpressureStrategy.LATEST);
        }

        @NonNull
        @Override
        protected InputStream openInputStream() throws Exception {
            startDownloadIfNotRunning();
            // We need to wait until our download is finished.
            downloadLatch.await();
            return new FileInputStream(downloadJob.getOutputFile());
        }

        @Override
        public long getSize() {
            startDownloadIfNotRunning();
            // We need to wait until our download is finished.
            try {
                downloadLatch.await();
            } catch (InterruptedException ex) {
                return FILE_SIZE_UNKNOWN;
            }
            return downloadJob.getOutputFile().length();
        }

        @NonNull
        @Override
        public String getUid() {
            return StringUtils.sha1(url);
        }

        @Nullable
        @Override
        public String getTitle() {
            return url;
        }

        /**
         * Starts our download if it wasn't already started.
         */
        private void startDownloadIfNotRunning() {
            if (downloadJob == null) {
                try {
                    // We delay starting the download so the progress bar will appear, this is only required because our example file is so small.
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    // No problem.
                }

                downloadJob = DownloadJob.startDownload(new DownloadRequest.Builder(getContext())
                                                            .uri(url)
                                                            .outputFile(new File(getContext().getDir("documents", Context.MODE_PRIVATE), "temp.pdf"))
                                                            .overwriteExisting(true)
                                                            .build());
                downloadJob.setProgressListener(new DownloadJob.ProgressListener() {
                    @Override
                    public void onProgress(@NonNull Progress progress) {
                        // Notify our listeners about the download progress.
                        progressSubject.onNext((double) progress.bytesReceived / (double) progress.totalBytes);
                    }

                    @Override
                    public void onComplete(@NonNull File output) {
                        progressSubject.onComplete();
                        downloadLatch.countDown();
                    }

                    @Override
                    public void onError(@NonNull Throwable exception) {
                        progressSubject.onError(exception);
                        downloadLatch.countDown();
                    }
                });
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(url);
        }
    }

    public ProgressProviderExample(Context context) {
        super(context.getString(R.string.progressProviderExampleTitle), context.getString(R.string.progressProviderExampleDescription));
    }

    @Override
    public void launchExample(@NonNull Context context, @NonNull PdfActivityConfiguration.Builder configuration) {
        // Create an instance of the custom data provider. See the implementation details below.
        DataProvider dataProvider = new RemoteDataProvider("https://pspdfkit.com/downloads/case-study-box.pdf");

        // Start the activity using our data provider.
        Intent intent = PdfActivityIntentBuilder.fromDataProvider(context, dataProvider)
            .configuration(configuration.build())
            .build();

        context.startActivity(intent);
    }
}
