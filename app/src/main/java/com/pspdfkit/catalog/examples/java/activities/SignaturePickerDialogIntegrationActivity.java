/*
 *   Copyright © 2018-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.pspdfkit.annotations.InkAnnotation;
import com.pspdfkit.annotations.WidgetAnnotation;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.forms.FormType;
import com.pspdfkit.forms.SignatureFormElement;
import com.pspdfkit.signatures.Signature;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.signatures.SignaturePickerFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Shows how to manually handle the {@link com.pspdfkit.ui.signatures.SignaturePickerFragment} in your {@link PdfActivity}.
 */
public class SignaturePickerDialogIntegrationActivity extends PdfActivity implements SignaturePickerFragment.OnSignaturePickedListener {

    private static final String STATE_FORM_FIELD_NAME = "Example.FORM_FIELD_NAME";

    /** Name of the previously clicked signature form field (if any). Used to access it after a configuration change. */
    @Nullable private String signatureFormFieldName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // In case this activity in being recreated (e.g. during a configuration change), reattach the activity as listener to the existing dialog.
        // Calling restore() is safe, and won't do anything in case the dialog isn't currently shown.
        SignaturePickerFragment.restore(getSupportFragmentManager(), this);

        // Also restore information about any previously clicked signature for element. We'll use this when adding an ink annotation later.
        if (savedInstanceState != null) {
            signatureFormFieldName = savedInstanceState.getString(STATE_FORM_FIELD_NAME, null);
        }
    }

    @Override protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Make sure to persist information of any clicked signature form element (so it outlives configuration changes).
        outState.putString(STATE_FORM_FIELD_NAME, signatureFormFieldName);
    }

    @Override protected void onStart() {
        super.onStart();

        // When tapping a form element, PSPDFKit will normally handle showing the signature picker itself. You can override this
        // by registering a form element click listener.
        getPdfFragment().addOnFormElementClickedListener(formElement -> {
            if (formElement.getType() == FormType.SIGNATURE) {
                onSignatureFormElementClicked((SignatureFormElement) formElement);

                // By returning true, you intercept the event and prevent PSPDFKit from showing the signature picker itself.
                return true;
            }

            // This click event is not interesting for us. Return false to let PSPDFKit handle this event.
            return false;
        });
    }

    /**
     * This callback handles showing the signature picker whenever a signature form element is clicked by the user.
     */
    private void onSignatureFormElementClicked(final @NonNull SignatureFormElement formElement) {
        // Keep reference of the stored signature form element so we can later on access it.
        signatureFormFieldName = formElement.getFormField().getName();

        SignaturePickerFragment.show(getSupportFragmentManager(), this);
    }

    /**
     * This method is called by the signature picker, whenever the user selected a signature.
     */
    @Override /* OnSignaturePickedListener */
    public void onSignaturePicked(@NonNull Signature signature) {
        // You can add you custom signature handling logic here.
        // ...

        addInkSignature(signature);
    }

    /**
     * This method is called by the signature picker, if the user dismissed the picker without selecting a signature.
     */
    @Override /* OnSignaturePickedListener */
    public void onDismiss() {
        signatureFormFieldName = null;
    }

    /**
     * This is an example that shows how to add a signature as {@link InkAnnotation} on top of the tapped signature form field.
     */
    private void addInkSignature(@NonNull final Signature signature) {
        final PdfDocument document = getDocument();
        final String signatureFormFieldName = this.signatureFormFieldName;
        if (document == null || signatureFormFieldName == null) return;

        // Retrieve the previously clicked signature form element. We do this asynchronously to not block the UI thread.
        document.getFormProvider().getFormFieldWithFullyQualifiedNameAsync(signatureFormFieldName)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(formField -> {
                if (formField != null) {
                    final SignatureFormElement clickedSignatureFormElement = (SignatureFormElement) formField.getFormElement();

                    // We want to place the ink annotation on top of the signature field. We retrieve the widget annotation to access its position.
                    final WidgetAnnotation formFieldAnnotation = clickedSignatureFormElement.getAnnotation();
                    // The signature object provides convenient conversion to ink annotations.
                    final InkAnnotation inkSignature = signature.toInkAnnotation(document, formFieldAnnotation.getPageIndex(), formFieldAnnotation.getBoundingBox());
                    inkSignature.setColor(Color.RED);

                    // Add the annotation to the document and select it.
                    getPdfFragment().addAnnotationToPage(inkSignature, false);
                }
            });
    }
}
