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
import com.pspdfkit.forms.FormType
import com.pspdfkit.forms.SignatureFormElement
import com.pspdfkit.listeners.DocumentSigningListener
import com.pspdfkit.signatures.SignatureManager
import com.pspdfkit.signatures.signers.InteractiveSigner
import com.pspdfkit.signatures.signers.Pkcs12Signer
import com.pspdfkit.signatures.signers.Signer
import com.pspdfkit.ui.PdfActivity
import com.pspdfkit.ui.PdfActivityIntentBuilder
import com.pspdfkit.ui.signatures.SignatureSignerDialog
import com.pspdfkit.ui.special_mode.controller.AnnotationTool
import java.security.cert.X509Certificate
import java.util.ArrayList

/**
 * An example showing how to digitally sign document after clicking on [com.pspdfkit.forms.SignatureFormElement].
 */
class ManualSigningExample(context: Context) : PSPDFExample(context, R.string.manualSigningExampleTitle, R.string.manualSigningExampleDescription) {

    override fun launchExample(context: Context, configuration: PdfActivityConfiguration.Builder) {
        // Our test certificate is self-signed, so we need to add it to trusted certificate store
        // for it to validate. Otherwise the new signature won't validate. Since PSPDFKit and other
        // readers (like Acrobat) will warn when using self-signed certificates your app should use
        // a CA issued certificate instead.
        addJohnAppleseedCertificateToTrustedCertificates(context)

        // The signer is a named entity holding a certificate (usually a person) and has a display
        // name shown in the app. Registration of the Signer instance has to happen using a unique
        // string identifier. The signer can be associated with a signature for signing the document.
        val johnAppleseed: Signer = Pkcs12Signer("John Appleseed", Uri.parse("file:///android_asset/JohnAppleseed.p12"))
        SignatureManager.addSigner(SIGNER_IDENTIFIER, johnAppleseed)

        // We'll disable ink signature tool in annotation creation toolbar to prevent creation of ink signatures.
        val annotationTools: MutableList<AnnotationTool> = ArrayList(listOf(*AnnotationTool.values()))
        annotationTools.remove(AnnotationTool.SIGNATURE)
        configuration.enabledAnnotationTools(annotationTools)

        // Load and show the signature example PDF.
        ExtractAssetTask.extract("Form_example.pdf", title, context, true) { documentFile ->
            val intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .activityClass(ManualSigningActivity::class)
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

class ManualSigningActivity : PdfActivity(), DocumentSigningListener {
    override fun onStart() {
        super.onStart()
        // When tapping a form element, PSPDFKit will normally handle showing the signature picker
        // itself. You can override this by registering a form element click listener.
        requirePdfFragment().addOnFormElementClickedListener { formElement ->
            if (formElement.type == FormType.SIGNATURE) {
                onSignatureFormElementClicked(formElement as SignatureFormElement)
                // By returning true, you intercept the event and prevent
                // PSPDFKit from showing the signature picker itself.
                return@addOnFormElementClickedListener true
            }
            false
        }
    }

    /**
     * This callback handles digital signing whenever a signature form element is clicked by the user.
     */
    private fun onSignatureFormElementClicked(formElement: SignatureFormElement) {
        val document = document ?: return

        // Retrieve the signer we've created before launching the example in ManualSigningExample.
        val signer = SignatureManager.getSigners()[SIGNER_IDENTIFIER] ?: return

        // Provide a password to the signer, which will be used to unlock its private key.
        if (signer is InteractiveSigner) {
            (signer as InteractiveSigner).unlockPrivateKeyWithPassword("test")
        }

        // Show the signer dialog that handles the signing process.
        SignatureSignerDialog.show(
            supportFragmentManager,
            SignatureSignerDialog.Options.Builder(
                document,
                formElement.formField,
                signer
            ).build(),
            this
        )
    }

    override fun onDocumentSigned(signedDocumentUri: Uri) {
        // Replace loaded document with signed document.
        setDocumentFromUri(signedDocumentUri, null)
    }

    override fun onDocumentSigningError(error: Throwable?) {
        // Handle signing errors.
    }

    override fun onSigningCancelled() {}
}

private const val SIGNER_IDENTIFIER = "john_appleseed"