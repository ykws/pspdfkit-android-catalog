/*
 *   Copyright © 2018-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.pspdfkit.forms.FormType;
import com.pspdfkit.forms.SignatureFormElement;
import com.pspdfkit.listeners.DocumentSigningListener;
import com.pspdfkit.signatures.SignatureManager;
import com.pspdfkit.signatures.signers.InteractiveSigner;
import com.pspdfkit.signatures.signers.Signer;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.signatures.SignatureSignerDialog;

/**
 * Shows how to digitally sign document after clicking on the signature field.
 */
public class ManualSigningActivity extends PdfActivity implements DocumentSigningListener {

    @Override
    protected void onStart() {
        super.onStart();

        // When tapping a form element, PSPDFKit will normally handle showing the signature picker itself.
        // You can override this by registering a form element click listener.
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
     * This callback handles digital signing whenever a signature form element is clicked by the user.
     */
    private void onSignatureFormElementClicked(@NonNull final SignatureFormElement formElement) {
        // Retrieve the signer we've created before launching the example in ManualSigningExample.
        Signer signer = SignatureManager.getSigners().get("john_appleseed");
        if (signer != null) {
            // Provide a password to the signer, which will be used to unlock its private key.
            if (signer instanceof InteractiveSigner) {
                ((InteractiveSigner) signer).unlockPrivateKeyWithPassword("test");
            }

            // Show the signer dialog that handles the signing process.
            SignatureSignerDialog.show(
                    getSupportFragmentManager(),
                    new SignatureSignerDialog.Options.Builder(
                            getDocument(),
                            formElement.getFormField(),
                            signer
                    ).build(),
                    this
            );
        }
    }

    @Override
    public void onDocumentSigned(@NonNull Uri signedDocumentUri) {
        // Replace loaded document with signed document.
        setDocumentFromUri(signedDocumentUri, null);
    }

    @Override
    public void onDocumentSigningError(@Nullable Throwable error) {
        // Handle error.
    }

    @Override
    public void onSigningCancelled() {
    }
}
