/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.instant.api;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

/**
 * Describes single Instant document on the web preview server.
 */
public class InstantExampleDocumentDescriptor implements Parcelable {
    /** Instant server url. */
    @NonNull public final String serverUrl;

    /** Instant document id. */
    @NonNull public final String documentId;

    /** Authentication token (JWT) used to authenticate access to the document. */
    @NonNull public final String jwt;

    /** Document code identifying document and editing group. */
    @SerializedName("encodedDocumentId")
    @NonNull public final String documentCode;

    /** Web preview url. */
    @SerializedName("url")
    @NonNull public final String webUrl;

    public InstantExampleDocumentDescriptor(@NonNull String serverUrl,
                                            @NonNull String documentId,
                                            @NonNull String jwt,
                                            @NonNull String documentCode,
                                            @NonNull String webUrl) {
        this.serverUrl = serverUrl;
        this.documentId = documentId;
        this.jwt = jwt;
        this.documentCode = documentCode;
        this.webUrl = webUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.serverUrl);
        dest.writeString(this.documentId);
        dest.writeString(this.jwt);
        dest.writeString(this.documentCode);
        dest.writeString(this.webUrl);
    }

    private InstantExampleDocumentDescriptor(Parcel in) {
        this.serverUrl = in.readString();
        this.documentId = in.readString();
        this.jwt = in.readString();
        this.documentCode = in.readString();
        this.webUrl = in.readString();
    }

    public static final Creator<InstantExampleDocumentDescriptor> CREATOR = new Creator<InstantExampleDocumentDescriptor>() {
        @Override
        public InstantExampleDocumentDescriptor createFromParcel(Parcel source) {
            return new InstantExampleDocumentDescriptor(source);
        }

        @Override
        public InstantExampleDocumentDescriptor[] newArray(int size) {
            return new InstantExampleDocumentDescriptor[size];
        }
    };
}
