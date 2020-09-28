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
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.tasks.ExtractAssetTask;
import com.pspdfkit.signatures.EncryptionAlgorithm;
import com.pspdfkit.signatures.HashAlgorithm;
import com.pspdfkit.signatures.SignatureManager;
import com.pspdfkit.signatures.provider.SignatureProvider;
import com.pspdfkit.signatures.signers.Signer;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * An example showing how to use a custom {@link SignatureProvider} to sign a document.
 */
public class CustomSignatureProviderExample extends PSPDFExample {

    public CustomSignatureProviderExample(Context context) {
        super(context.getString(R.string.customSignatureProviderExampleTitle), context.getString(R.string.customSignatureProviderExampleDescription));
    }

    @Override
    public void launchExample(@NonNull final Context context, @NonNull final PdfActivityConfiguration.Builder configuration) {
        // Our test certificate is self-signed, so we need to add it to trusted certificate store for it to validate. Otherwise
        // the new signature won't validate. Since PSPDFKit and other readers (like Acrobat) will warn when using self-signed certificates
        // your app should use a CA issued certificate instead.
        addJohnAppleseedCertificateToTrustedCertificates(context);

        try {
            // We use a custom Signer implementation (see code below) to sign PDF documents. Using this pattern, you can implement
            // virtually any signing provider (even those that require user interaction – see InteractiveSigner interface for that).
            final Signer customSigner = new CustomSigner("John Appleseed", getPrivateKeyEntry(context));
            SignatureManager.addSigner("john_appleseed", customSigner);

            // Load and show the signature example PDF.
            ExtractAssetTask.extract("Form_example.pdf", title, context, true, documentFile -> {
                final Intent intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                    .configuration(configuration.build())
                    .build();
                context.startActivity(intent);
            });
        } catch (Exception e) {
            Toast.makeText(context, "Error launching example. See logcat for details.", Toast.LENGTH_SHORT).show();
            Log.e("CustomSignatureProvider", "Error while launching example.", e);
        }
    }

    /**
     * Adds the self-signed certificate of this example to the trust list, so that verification works properly.
     * In a real app, you should use a CA issued certificate or signature validation will fail on third-party readers.
     */
    private void addJohnAppleseedCertificateToTrustedCertificates(@NonNull Context context) {
        try {
            final X509Certificate certificate = getCertificate(context);
            if (certificate == null) return;
            SignatureManager.addTrustedCertificate(certificate);
        } catch (IOException | GeneralSecurityException e) {
            Log.e("PSPDFKit", "Couldn't load and add John Appleseed certificate to trusted certificate list!");
        }
    }

    /**
     * Loads the {@link KeyStore.PrivateKeyEntry} that will be used by our {@link CustomSigner} to sign a PDF.
     */
    @NonNull
    private KeyStore.PrivateKeyEntry getPrivateKeyEntry(@NonNull Context context) throws IOException, GeneralSecurityException {
        final InputStream keystoreFile = context.getAssets().open("JohnAppleseed.p12");
        // Inside a p12 we have both the certificate and private key used for signing. We just need the certificate here.
        // Proper signatures should have a root CA approved certificate making this step unnecessary.
        return SignatureManager.loadPrivateKeyPairFromStream(keystoreFile, "test", null, null);
    }

    /**
     * Load the example certificate of the signer from a p12 file.
     */
    private X509Certificate getCertificate(@NonNull Context context) throws IOException, GeneralSecurityException {
        final KeyStore.PrivateKeyEntry key = getPrivateKeyEntry(context);
        final Certificate certificate = key.getCertificate();
        if (!(certificate instanceof X509Certificate)) {
            Toast.makeText(context, "Error while loading example certificate. It was not of type X.509.", Toast.LENGTH_LONG).show();
            return null;
        }
        return (X509Certificate) certificate;
    }

    /**
     * A custom {@link Signer} implementation backed by Java's {@link Signature} class for signing content in a PDF.
     */
    public static class CustomSigner extends Signer {

        /** Private key used for signing. */
        private final KeyStore.PrivateKeyEntry signingKey;

        CustomSigner(@NonNull String displayName, KeyStore.PrivateKeyEntry signingKey) {
            super(displayName);
            this.signingKey = signingKey;
        }

        /**
         * This method has to be implemented by the custom signer, to hand back the {@link SignatureProvider} and X.509 certificate.
         */
        @Override
        protected void prepareSigningParameters(@NonNull OnSigningParametersReadyCallback callback) {
            // Return the public certificate that will be embedded into the PDF as well as our custom signature provider.
            callback.onSigningParametersReady(new CustomSignatureProvider(), (X509Certificate) signingKey.getCertificate());
        }

        /**
         * Example signature provider that uses Java's security APIs and the {@link Signature} class to perform document signing.
         */
        private class CustomSignatureProvider implements SignatureProvider {
            @NonNull
            @Override
            public byte[] signData(@NonNull byte[] data, @NonNull HashAlgorithm hashAlgorithm) {
                try {
                    final Signature rsa = Signature.getInstance(getSignatureAlgorithm(hashAlgorithm));
                    rsa.initSign(signingKey.getPrivateKey());
                    rsa.update(data);
                    return rsa.sign();
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException("No appropriate signature algorithm available.", e);
                } catch (SignatureException e) {
                    throw new RuntimeException("Error while signing data.", e);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException("Error accessing private key.", e);
                }
            }

            @NonNull
            @Override
            public EncryptionAlgorithm getEncryptionAlgorithm() {
                // The private key loaded from the examples p12 file is an RSA key.
                // You can use any of the other supported EncryptionAlgorithms for your apps too.
                return EncryptionAlgorithm.RSA;
            }

            /**
             * This method selects the appropriate signing algorithm based on the hashing algorithm requested by PSPDFKit.
             */
            private String getSignatureAlgorithm(@NonNull final HashAlgorithm hashAlgorithm) {
                switch (hashAlgorithm) {
                    case MD5:
                        return "MD5withRSA";
                    case SHA160:
                        return "SHA1withRSA";
                    case SHA224:
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
                            Log.w("CustomSignatureProvider", "This device does not yet support SHA224withRSA, which is only available since Android API 22");
                        }
                        return "SHA224withRSA";
                    case SHA256:
                        return "SHA256withRSA";
                    case SHA384:
                        return "SHA384withRSA";
                    case SHA512:
                        return "SHA512withRSA";
                    default:
                        throw new IllegalStateException("No appropriate signing algorithm was found for hash algorithm: " + hashAlgorithm.name());
                }
            }
        }
    }
}
