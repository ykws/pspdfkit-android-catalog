/*
 *   Copyright © 2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.kotlin

import android.content.Context
import android.net.Uri
import android.util.Log
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.catalog.PSPDFExample
import com.pspdfkit.catalog.R
import com.pspdfkit.catalog.tasks.ExtractAssetTask
import com.pspdfkit.signatures.SignatureManager
import com.pspdfkit.signatures.signers.Pkcs12Signer
import com.pspdfkit.signatures.signers.Signer
import com.pspdfkit.ui.PdfActivityIntentBuilder
import java.security.cert.X509Certificate

/**
 * An example that shows how to register and use a digital certificate for signing a PDF document.
 */
class DigitalSignatureExample(context: Context) : PSPDFExample(context, R.string.digitalSignatureExampleTitle, R.string.digitalSignatureExampleDescription) {
    
    override fun launchExample(context: Context, configuration: PdfActivityConfiguration.Builder) {
        // Our test certificate is self-signed, so we need to add it to trusted certificate store
        // for it to validate. Otherwise the new signature won't validate. Since PSPDFKit and other
        // readers (like Acrobat) will warn when using self-signed certificates your app should use
        // a CA issued certificate instead.
        addJohnAppleseedCertificateToTrustedCertificates(context)

        // The signer is a named entity holding a certificate (usually a person) and has a display
        // name shown in the app. Registration of the Signer instance has to happen using a unique
        // string identifier. The signer can be associated with a signature for signing the document.
        val johnAppleseedSigner: Signer = Pkcs12Signer(
            "John Appleseed",
            Uri.parse("file:///android_asset/JohnAppleseed.p12"))
        val signerIdentifier = "john_appleseed"
        SignatureManager.addSigner(signerIdentifier, johnAppleseedSigner)

        // Use the signer as the default signer in the UI.
        configuration.defaultSigner(signerIdentifier)

        // Load and show the signature example PDF.
        ExtractAssetTask.extract("Form_example.pdf", title, context, true) { documentFile ->
            val intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration.build())
                .build()
            context.startActivity(intent)
        }
    }

    private fun addJohnAppleseedCertificateToTrustedCertificates(context: Context) {
        try {
            val keystoreFile = context.assets.open("JohnAppleseed.p12")
            // Inside a p12 we have both the certificate and private key used for signing. We just
            // need the certificate here. Proper signatures should have a root CA approved
            // certificate making this step unnecessary.
            val key = SignatureManager.loadPrivateKeyPairFromStream(keystoreFile, "test", null, null)
            if (key.certificate.type == "X.509") {
                SignatureManager.addTrustedCertificate((key.certificate as X509Certificate))
            }
        } catch (e: Exception) {
            Log.e("PSPDFKit", "Couldn't load and add John Appleseed certificate to trusted certificate list!")
        }
    }
}