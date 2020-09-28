/*
 *   Copyright © 2018-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.kotlin

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import com.pspdfkit.annotations.InkAnnotation
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.catalog.PSPDFExample
import com.pspdfkit.catalog.R
import com.pspdfkit.catalog.tasks.ExtractAssetTask.extract
import com.pspdfkit.forms.FormType
import com.pspdfkit.forms.SignatureFormElement
import com.pspdfkit.signatures.Signature
import com.pspdfkit.ui.PdfActivity
import com.pspdfkit.ui.PdfActivityIntentBuilder
import com.pspdfkit.ui.signatures.SignaturePickerFragment
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Showcases how to manually integrate the [com.pspdfkit.ui.signatures.SignaturePickerFragment] into an existing [com.pspdfkit.ui.PdfActivity].
 */
class SignaturePickerDialogIntegrationExample(context: Context)
    : PSPDFExample(context, R.string.signaturePickerDialogIntegrationExampleTitle, R.string.signaturePickerDialogIntegrationExampleDescription) {

    override fun launchExample(context: Context, configuration: PdfActivityConfiguration.Builder) {

        // The form field for signing is on page with index 11.
        configuration.page(11)

        // Extract the document from the assets.
        extract(PSPDFExample.QUICK_START_GUIDE, title, context) { documentFile ->
            val intent = PdfActivityIntentBuilder.fromUri(context, Uri.fromFile(documentFile))
                .configuration(configuration.build())
                .activityClass(SignaturePickerDialogIntegrationActivity::class)
                .build()

            // Start the SignaturePickerDialogIntegrationActivity showing the demo document.
            context.startActivity(intent)
        }
    }
}

/**
 * Shows how to manually handle the [com.pspdfkit.ui.signatures.SignaturePickerFragment] in custom [PdfActivity].
 */
class SignaturePickerDialogIntegrationActivity : PdfActivity(), SignaturePickerFragment.OnSignaturePickedListener {

    /** Name of the previously clicked signature form field (if any). Used to access it after a configuration change.  */
    private var signatureFormFieldName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // In case this activity in being recreated (e.g. during a configuration change), reattach
        // the activity as listener to the existing dialog. Calling restore() is safe, and won't do
        // anything in case the dialog isn't currently shown.
        SignaturePickerFragment.restore(supportFragmentManager, this)

        // Also restore information about any previously clicked signature for element. We'll use
        // this when adding an ink annotation later.
        if (savedInstanceState != null) {
            signatureFormFieldName = savedInstanceState.getString(STATE_FORM_FIELD_NAME, null)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Make sure to persist information of any clicked signature form element (so it outlives configuration changes).
        outState.putString(STATE_FORM_FIELD_NAME, signatureFormFieldName)
    }

    override fun onStart() {
        super.onStart()

        // When tapping a form element, PSPDFKit will normally handle showing the signature picker
        // itself. You can override this by registering a form element click listener.
        requirePdfFragment().addOnFormElementClickedListener { formElement ->
            when (formElement.type) {
                FormType.SIGNATURE -> {
                    onSignatureFormElementClicked(formElement as SignatureFormElement)
                    // By returning true, you intercept the event and prevent PSPDFKit from showing the signature picker itself.
                    true
                }
                // This click event is not interesting for us. Return false to let PSPDFKit handle this event.
                else -> false
            }
        }
    }

    /**
     * This callback handles showing the signature picker whenever a signature form element is clicked by the user.
     */
    private fun onSignatureFormElementClicked(formElement: SignatureFormElement) {
        // Keep reference of the stored signature form element so we can later on access it.
        signatureFormFieldName = formElement.formField.name

        SignaturePickerFragment.show(supportFragmentManager, this)
    }

    /**
     * This method is called by the signature picker, whenever the user selected a signature.
     */
    override fun onSignaturePicked(signature: Signature) {
        // You can add you custom signature handling logic here.
        // ...

        addInkSignature(signature)
    }

    /**
     * This method is called by the signature picker, if the user dismissed the picker without selecting a signature.
     */
    override fun onDismiss() {
        signatureFormFieldName = null
    }

    /**
     * This is an example that shows how to add a signature as [InkAnnotation] on top of the tapped signature form field.
     */
    private fun addInkSignature(signature: Signature) {
        val document = document ?: return
        val signatureFormFieldName = this.signatureFormFieldName ?: return

        // Retrieve the previously clicked signature form element. We do this asynchronously to not block the UI thread.
        document.formProvider.getFormFieldWithFullyQualifiedNameAsync(signatureFormFieldName)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { formField ->
                if (formField != null) {
                    val clickedSignatureFormElement = formField.formElement as SignatureFormElement

                    // We want to place the ink annotation on top of the signature field. We retrieve the widget annotation to access its position.
                    val formFieldAnnotation = clickedSignatureFormElement.annotation
                    // The signature object provides convenient conversion to ink annotations.
                    val inkSignature = signature.toInkAnnotation(document, formFieldAnnotation.pageIndex, formFieldAnnotation.boundingBox)
                    inkSignature.color = Color.RED

                    // Add the annotation to the document and select it.
                    requirePdfFragment().addAnnotationToPage(inkSignature, false)
                }
            }
    }
}

private const val STATE_FORM_FIELD_NAME = "Example.FORM_FIELD_NAME"