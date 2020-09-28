/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.forms.CheckBoxFormElement;
import com.pspdfkit.forms.FormElement;
import com.pspdfkit.forms.FormField;
import com.pspdfkit.forms.FormType;
import com.pspdfkit.forms.RadioButtonFormElement;
import com.pspdfkit.forms.RadioButtonFormField;
import com.pspdfkit.forms.TextFormElement;
import com.pspdfkit.ui.PdfActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * This activity shows how to programmatically fill document forms.
 */
public class FormFillingActivity extends PdfActivity {

    private static final int RESET_FORM_MENU_ITEM_ID = 1;
    private static final int FILL_BY_FIELD_NAME = 2;

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, RESET_FORM_MENU_ITEM_ID, 0, "Reset form");
        menu.add(0, FILL_BY_FIELD_NAME, 0, "Fill by field name");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == RESET_FORM_MENU_ITEM_ID) {
            resetForm();
            return true;
        } else if (item.getItemId() == FILL_BY_FIELD_NAME) {
            fillByName();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @UiThread
    @Override
    public void onDocumentLoaded(@NonNull final PdfDocument document) {
        super.onDocumentLoaded(document);
        fillAllFormFields();
    }

    /**
     * Fills form fields in the document with text {@code "Example <field name>"}.
     */
    private void fillAllFormFields() {
        if (getDocument() == null) return;
        getDocument().getFormProvider()
            .getFormElementsAsync()
            .subscribe(formElements -> {
                for (FormElement formElement : formElements) {
                    switch (formElement.getType()) {
                        case TEXT:
                            TextFormElement textFormElement = (TextFormElement) formElement;
                            switch (textFormElement.getInputFormat()) {
                                case NORMAL:
                                    textFormElement.setText("Example " + formElement.getName());
                                    break;
                                case DATE:
                                    textFormElement.setText("03/14/1994");
                                    break;
                            }
                            break;
                        case CHECKBOX:
                            ((CheckBoxFormElement) formElement).toggleSelection();
                            break;
                        case RADIOBUTTON:
                            ((RadioButtonFormElement) formElement).toggleSelection();
                        default:
                            // Do nothing.
                    }
                }
            });
    }

    /**
     * Resets all form fields in the document to their default values.
     */
    private void resetForm() {
        if (getDocument() == null) return;
        getDocument().getFormProvider()
            .getFormFieldsAsync()
            .subscribe(formFields -> {
                for (FormField formField : formFields) {
                    formField.reset();
                }
            });
    }

    /**
     * Shows how to query form fields/elements by their name.
     */
    private void fillByName() {
        if (getDocument() == null) return;

        // Form fields can be queried by their fully qualified name.
        // Each form field can have multiple child form elements that are
        // the widget annotations that are visually representing actionable
        // controls inside the form field.
        getDocument().getFormProvider().getFormFieldWithFullyQualifiedNameAsync("Sex").subscribe(formField -> {
            RadioButtonFormField formElement = (RadioButtonFormField) formField;
            // Sex radio button field has 2 child form elements. These are representing 2 radio buttons in the radio group.

            // First radio element has the name "Sex.0" and represents the MALE option.
            //      formElement.getFormElements().get(0)

            // Second radio element has name "Sex.1" and represents the FEMALE option.
            //      formElement.getFormElements().get(1)

            // Select the MALE radio option.
            formElement.getFormElements().get(0).select();
        });

        // Form elements (visible portion of the form field) can be queried by their name and filled that way.
        getDocument().getFormProvider().getFormElementWithNameAsync("Name_Last").subscribe(formElement -> {
            TextFormElement textFormElement = (TextFormElement) formElement;
            textFormElement.setText("John");
        });

        getDocument().getFormProvider().getFormElementWithNameAsync("Name_First").subscribe(formElement -> {
            TextFormElement textFormElement = (TextFormElement) formElement;
            textFormElement.setText("Appleseed");
        });

        // Querying form elements by name can be slow. If you need to fill many form
        // elements at once, retrieve list of all form fields/elements first and iterate through it.
        getDocument().getFormProvider()
            .getFormElementsAsync()
            .subscribe(formElements -> {
                // For the sake of example we'll fill only address fields here.
                Map<String, String> formFillMap = new HashMap<>();
                formFillMap.put("Address_1", "7440-7498 S Hanna St.");
                formFillMap.put("Address_2", "");
                formFillMap.put("City", "Fort Wayne");
                formFillMap.put("STATE", "IN");
                formFillMap.put("ZIP", "46774");
                for (FormElement formElement : formElements) {
                    if (formElement.getType() == FormType.TEXT) {
                        TextFormElement textFormElement = (TextFormElement) formElement;
                        if (formFillMap.containsKey(textFormElement.getName())) {
                            textFormElement.setText(formFillMap.get(textFormElement.getName()));
                        }
                    }
                }
            });
    }
}
