/*
 *   Copyright © 2019-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.kotlin

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.document.PdfDocument
import com.pspdfkit.catalog.PSPDFExample
import com.pspdfkit.catalog.R
import com.pspdfkit.catalog.tasks.ExtractAssetTask
import com.pspdfkit.forms.SignatureFormField
import com.pspdfkit.signatures.HashAlgorithm
import com.pspdfkit.signatures.SignatureManager
import com.pspdfkit.signatures.contents.PKCS7SignatureContents
import com.pspdfkit.signatures.contents.SignatureContents
import com.pspdfkit.signatures.signers.ContainedSignaturesSigner
import com.pspdfkit.signatures.signers.SignerOptions
import com.pspdfkit.ui.PdfActivityIntentBuilder
import com.pspdfkit.utils.PdfLog
import java.io.File
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.X509Certificate

/**
 * An example showing how to use PKCS7 container to sign a document (contained digital signatures).
 */
class ContainedSignaturesExample(context: Context) : PSPDFExample(context, R.string.containedSignaturesExampleTitle, R.string.containedSignaturesExampleDescription) {

    override fun launchExample(context: Context, configuration: PdfActivityConfiguration.Builder) {
        // Our test certificate is self-signed, so we need to add it to trusted certificate store for it to validate.
        // Otherwise the new signature won't validate. Since PSPDFKit and other readers (like Acrobat) will warn when
        // using self-signed certificates your app should use a CA issued certificate instead.
        addJohnAppleseedCertificateToTrustedCertificates(context)

        try {
            // We use a custom Signer implementation (see code below) to sign PDF documents. Using this pattern, you can implement
            // virtually any signing provider (even those that require user interaction – see InteractiveSigner interface for that).
            val customSigner = CustomContainedSignatureSigner(context, "John Appleseed", getPrivateKeyEntry(context))
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
            PdfLog.e("ContainedSignatures", e, "Error while launching example.")
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
     * Loads the [KeyStore.PrivateKeyEntry] that will be used by our [CustomContainedSignatureSigner].
     */
    @Throws(IOException::class, GeneralSecurityException::class)
    private fun getPrivateKeyEntry(context: Context): KeyStore.PrivateKeyEntry {
        val keystoreFile = context.assets.open("JohnAppleseed.p12")
        // Inside a p12 we have both the certificate and private key used for signing. We just need the certificate here.
        // Proper signatures should have a root CA approved certificate making this step unnecessary.
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
 * A custom [ContainedSignaturesSigner] implementation showcasing the contained signatures signing.
 */
class CustomContainedSignatureSigner(
    context: Context,
    displayName: String,
    private val signingKey: KeyStore.PrivateKeyEntry
) : ContainedSignaturesSigner(context, displayName) {

    /**
     * All you need to do to use contained signatures is to provide [SignatureContents] with a valid
     * PKCS#7 signature of the document that was prepared for signing during the signing flow.
     */
    override fun prepareSignatureContents(signerOptions: SignerOptions,
                                          preparedDocumentFile: File,
                                          preparedDocument: PdfDocument,
                                          preparedFormField: SignatureFormField): SignatureContents {
        // Retrieve the byte range of the document covered by the prepared signature.
        val signatureByteRange = preparedFormField.signatureInfo.byteRange
            ?: throw IllegalStateException("Can't retrieve prepared signature byte range.")

        // Calculate hash of the signature byte range.
        val documentDigest = preparedDocument.getHashForDocumentRange(signatureByteRange, HashAlgorithm.SHA256)

        // Create the signature contents from the document digest, this can be passed to a remote server or built manually.
        // PSPDFKit ships with PKCS7 signature container and we use it here as an example.
        return PKCS7SignatureContents(documentDigest, signingKey, HashAlgorithm.SHA256)

        // Alternative, use the simplified secondary constructor of PKCS7SignatureContents to replace the manual digest calculation.
        // return PKCS7SignatureContents(signatureFormField, signingKey, HashAlgorithm.SHA256)
    }
}
