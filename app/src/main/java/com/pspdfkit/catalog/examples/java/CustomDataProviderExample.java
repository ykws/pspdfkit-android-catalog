/*
 *   Copyright © 2014-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.document.providers.DataProvider;
import com.pspdfkit.document.providers.InputStreamDataProvider;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * This example shows how to create a custom data provider that reads a document from the {@code raw} resources
 * of the app. Furthermore, it implements {@link Parcelable} to allow using the data provider with  {@link PdfActivity}.
 */
public class CustomDataProviderExample extends PSPDFExample {

    /**
     * Custom data provider for loading a PDF document from the app's raw resources. Since
     * {@link Resources#openRawResource(int)} returns an {@code InputStream}, this provider derives from
     * {@code InputStreamDataProvider} which handles loading data from a stream object.
     */
    public static class RawResourceDataProvider extends InputStreamDataProvider implements Parcelable {

        /**
         * Static CREATOR object for creating instances from a Parcel.
         */
        public static final Creator<RawResourceDataProvider> CREATOR = new Creator<RawResourceDataProvider>() {
            public RawResourceDataProvider createFromParcel(Parcel source) {
                return new RawResourceDataProvider(source);
            }

            public RawResourceDataProvider[] newArray(int size) {
                return new RawResourceDataProvider[size];
            }
        };
        @RawRes private int resId;
        /**
         * The size of the raw resource. This will be cached after the first call to {@link #getSize()}.
         */
        private long size = FILE_SIZE_UNKNOWN;

        /**
         * Create a new data provider for reading the PDF document stored within the {@code res/raw/} folder of
         * the application.
         * @param resId The id of the PDF document inside the resources.
         */
        public RawResourceDataProvider(@RawRes int resId) {
            this.resId = resId;
        }

        /**
         * Constructor required for unparcelation, takes a {@code in} Parcel and reads the raw resource id from it.
         */
        protected RawResourceDataProvider(Parcel in) {
            this.resId = in.readInt();
        }

        /**
         * We return the InputStream for the referenced raw resource. Since InputStreamDataProvider may call this
         * method multiple times we have to make sure that it always returns a fresh input stream object.
         */
        @NonNull
        @Override
        protected InputStream openInputStream() throws IOException {
            return getContext().getResources().openRawResource(resId);
        }

        /**
         * This method returns the size of our resource.  Android only gives us an {@link InputStream} for
         * accessing the resources we have to
         */
        @Override
        public long getSize() {
            // If the file size is already known, return it immediately.
            if (size != FILE_SIZE_UNKNOWN) return size;

            try {
                // Since we can only get size of the available data in the input stream we need to
                // reopen it here if the stream position is not 0.
                if (getInputStreamPosition() != 0) {
                    reopenInputStream();
                }

                size = openInputStream().available();

                return size;
            } catch (Exception e) {
                return FILE_SIZE_UNKNOWN;
            }
        }

        // The code below is standard Android parcelation code. If you don't know how to implement the Parcelable
        // interface start looking at {@link http://developer.android.com/reference/android/os/Parcelable.html}.
        // This code was generated using the "Android Parcelable code generator" Android Studio plugin.
        // Link: https://plugins.jetbrains.com/plugin/7332?pr=idea

        @NonNull
        @Override
        public String getUid() {
            return getContext().getResources().getResourceName(resId);
        }

        @Nullable
        @Override
        public String getTitle() {
            // Since we don't know the file name we just return null
            return null;
        }

        /**
         * Default parcelable implementation. The object is always parceled the same way. Thus, we return 0.
         */
        @Override
        public int describeContents() {
            return 0;
        }

        /**
         * We simply write the id of the PDF resource to the parcel.
         */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.resId);
        }
    }

    public CustomDataProviderExample(Context context) {
        super(context.getString(R.string.customDataProviderExampleTitle), context.getString(R.string.customDataProviderExampleDescription));
    }

    @Override
    public void launchExample(@NonNull Context context, @NonNull PdfActivityConfiguration.Builder configuration) {
        // Create an instance of the custom data provider. See the implementation details below.
        DataProvider dataProvider = new RawResourceDataProvider(R.raw.guide);

        // Start the activity using our data provider.
        Intent intent = PdfActivityIntentBuilder.fromDataProvider(context, dataProvider)
            .configuration(configuration.build())
            .build();

        context.startActivity(intent);
    }
}
