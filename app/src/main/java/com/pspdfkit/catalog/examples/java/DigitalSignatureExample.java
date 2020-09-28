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

import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.configuration.signatures.SignatureCertificateSelectionMode;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.tasks.ExtractAssetTask;
import com.pspdfkit.signatures.SignatureManager;
import com.pspdfkit.signatures.signers.Pkcs12Signer;
import com.pspdfkit.signatures.signers.Signer;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * An example showing how to register and use a digital certificate for signing a PDF document.
 */
public class DigitalSignatureExample extends PSPDFExample {

    public DigitalSignatureExample(Context context) {
        super(context.getString(R.string.digitalSignatureExampleTitle), context.getString(R.string.digitalSignatureExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        // Our test certificate is self-signed, so we need to add it to trusted certificate store for it to validate. Otherwise
        // the new signature won't validate. Since PSPDFKit and other readers (like Acrobat) will warn when using self-signed certificates
        // your app should use a CA issued certificate instead.
        addJohnAppleseedCertificateToTrustedCertificates(context);

        final String signerIdentifier = "john_appleseed";

        // The signer is a named entity holding a certificate (usually a person) and has a display name shown in the app. Registration of the Signer instance
        // has to happen using a unique string identifier. The signer can be associated with a signature for signing the document.
        final Signer johnAppleseed = new Pkcs12Signer("John Appleseed", Uri.parse("file:///android_asset/JohnAppleseed.p12"));
        SignatureManager.addSigner(signerIdentifier, johnAppleseed);

        // Load and show the signature example PDF.
        ExtractAssetTask.extract("Form_example.pdf", title, context, true, documentFile -> {
            final PdfActivityConfiguration pdfActivityConfiguration = configuration.defaultSigner(signerIdentifier)
                .signatureCertificateSelectionMode(SignatureCertificateSelectionMode.IF_AVAILABLE)
                .build();

            final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(pdfActivityConfiguration)
                .build();

            context.startActivity(intent);
        });
    }

    private void addJohnAppleseedCertificateToTrustedCertificates(@NonNull Context context) {
        try {
            final InputStream keystoreFile = context.getAssets().open("JohnAppleseed.p12");
            // Inside a p12 we have both the certificate and private key used for signing. We just need the certificate here.
            // Proper signatures should have a root CA approved certificate making this step unnecessary.
            KeyStore.PrivateKeyEntry key = SignatureManager.loadPrivateKeyPairFromStream(keystoreFile, "test", null, null);
            if (key.getCertificate().getType().equals("X.509")) {
                SignatureManager.addTrustedCertificate((X509Certificate) key.getCertificate());
            }
        } catch (IOException | GeneralSecurityException e) {
            Log.e("PSPDFKit", "Couldn't load and add John Appleseed certificate to trusted certificate list!");
        }
    }
}
