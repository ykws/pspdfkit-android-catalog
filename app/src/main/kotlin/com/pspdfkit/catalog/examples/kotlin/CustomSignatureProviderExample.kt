/*
 *   Copyright © 2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.kotlin

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.catalog.PSPDFExample
import com.pspdfkit.catalog.R
import com.pspdfkit.catalog.tasks.ExtractAssetTask
import com.pspdfkit.signatures.EncryptionAlgorithm
import com.pspdfkit.signatures.HashAlgorithm
import com.pspdfkit.signatures.SignatureManager
import com.pspdfkit.signatures.provider.SignatureProvider
import com.pspdfkit.signatures.signers.Signer
import com.pspdfkit.ui.PdfActivityIntentBuilder
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.Signature
import java.security.SignatureException
import java.security.cert.X509Certificate

/**
 * An example showing how to use a custom [SignatureProvider] to sign a document.
 */
class CustomSignatureProviderExample(context: Context) : PSPDFExample(context, R.string.customSignatureProviderExampleTitle, R.string.customSignatureProviderExampleDescription) {

    override fun launchExample(context: Context, configuration: PdfActivityConfiguration.Builder) {
        // Our test certificate is self-signed, so we need to add it to trusted certificate store
        // for it to validate. Otherwise the new signature won't validate. Since PSPDFKit and other
        // readers (like Acrobat) will warn when using self-signed certificates, your app should use
        // a CA issued certificate instead.
        addJohnAppleseedCertificateToTrustedCertificates(context)

        try {
            // We use a custom Signer implementation (see code below) to sign PDF documents. Using
            // this pattern, you can implement virtually any signing provider (even those that
            // require user interaction – see InteractiveSigner interface for that).
            val customSigner: Signer = CustomSigner("John Appleseed", getPrivateKeyEntry(context))
            SignatureManager.addSigner("john_appleseed", customSigner)

            // Load and show the signature example PDF.
            ExtractAssetTask.extract("Form_example.pdf", title, context, true) { documentFile ->
                val intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                    .configuration(configuration.build())
                    .build()
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error launching example. See logcat for details.", Toast.LENGTH_SHORT).show()
            Log.e("CustomSignatureProvider", "Error while launching example.", e)
        }
    }

    /**
     * Adds the self-signed certificate of this example to the trust list, so that verification works properly.
     * In a real app, you should use a CA issued certificate or signature validation will fail on third-party readers.
     */
    private fun addJohnAppleseedCertificateToTrustedCertificates(context: Context) {
        try {
            val certificate = getCertificate(context) ?: return
            SignatureManager.addTrustedCertificate(certificate)
        } catch (e: Exception) {
            Log.e("PSPDFKit", "Couldn't load and add John Appleseed certificate to trusted certificate list!")
        }
    }

    /**
     * Loads the [KeyStore.PrivateKeyEntry] that will be used by our [CustomSigner] to sign a PDF.
     */
    @Throws(IOException::class, GeneralSecurityException::class)
    private fun getPrivateKeyEntry(context: Context): KeyStore.PrivateKeyEntry {
        val keystoreFile = context.assets.open("JohnAppleseed.p12")
        // Inside a p12 we have both the certificate and private key used for signing. We just need
        // the certificate here. Proper signatures should have a root CA approved certificate making
        // this step unnecessary.
        return SignatureManager.loadPrivateKeyPairFromStream(keystoreFile, "test", null, null)
    }

    /**
     * Load the example certificate of the signer from a p12 file.
     */
    @Throws(IOException::class, GeneralSecurityException::class)
    private fun getCertificate(context: Context): X509Certificate? {
        val key = getPrivateKeyEntry(context)
        val certificate = key.certificate
        if (certificate !is X509Certificate) {
            Toast.makeText(context, "Error while loading example certificate. It was not of type X.509.", Toast.LENGTH_LONG).show()
            return null
        }
        return certificate
    }
}

/**
 * A custom [Signer] implementation backed by Java's [Signature] class for signing content in a PDF.
 */
class CustomSigner internal constructor(
    displayName: String,
    /** Private key used for signing.  */
    private val signingKey: KeyStore.PrivateKeyEntry
) : Signer(displayName) {

    /**
     * This method has to be implemented by the custom signer, to hand back the [SignatureProvider] and X.509 certificate.
     */
    override fun prepareSigningParameters(callback: OnSigningParametersReadyCallback) {
        // Return the public certificate that will be embedded into the PDF as well as our custom signature provider.
        callback.onSigningParametersReady(CustomSignatureProvider(), (signingKey.certificate as X509Certificate))
    }

    /**
     * Example signature provider that uses Java's security APIs and the [Signature] class to perform document signing.
     */
    private inner class CustomSignatureProvider : SignatureProvider {
        override fun signData(data: ByteArray, hashAlgorithm: HashAlgorithm): ByteArray {
            return try {
                val rsa = Signature.getInstance(getSignatureAlgorithm(hashAlgorithm))
                rsa.initSign(signingKey.privateKey)
                rsa.update(data)
                rsa.sign()
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException("No appropriate signature algorithm available.", e)
            } catch (e: SignatureException) {
                throw RuntimeException("Error while signing data.", e)
            } catch (e: InvalidKeyException) {
                throw RuntimeException("Error accessing private key.", e)
            }
        }

        override fun getEncryptionAlgorithm(): EncryptionAlgorithm {
            // The private key loaded from the examples p12 file is an RSA key.
            // You can use any of the other supported EncryptionAlgorithms for your apps too.
            return EncryptionAlgorithm.RSA
        }

        /**
         * This method selects the appropriate signing algorithm based on the hashing algorithm requested by PSPDFKit.
         */
        private fun getSignatureAlgorithm(hashAlgorithm: HashAlgorithm): String {
            return when (hashAlgorithm) {
                HashAlgorithm.MD5 -> "MD5withRSA"
                HashAlgorithm.SHA160 -> "SHA1withRSA"
                HashAlgorithm.SHA224 -> {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
                        Log.w("CustomSignatureProvider", "This device does not yet support SHA224withRSA, which is only available since Android API 22")
                    }
                    "SHA224withRSA"
                }
                HashAlgorithm.SHA256 -> "SHA256withRSA"
                HashAlgorithm.SHA384 -> "SHA384withRSA"
                HashAlgorithm.SHA512 -> "SHA512withRSA"
                else -> throw IllegalStateException("No appropriate signing algorithm was found for hash algorithm: " + hashAlgorithm.name)
            }
        }
    }
}
