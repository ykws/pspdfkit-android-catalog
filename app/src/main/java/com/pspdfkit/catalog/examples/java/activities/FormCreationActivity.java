/*
 *   Copyright © 2018-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import com.pspdfkit.annotations.actions.UriAction;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.forms.CheckBoxFormConfiguration;
import com.pspdfkit.forms.ComboBoxFormConfiguration;
import com.pspdfkit.forms.FormOption;
import com.pspdfkit.forms.ListBoxFormConfiguration;
import com.pspdfkit.forms.PushButtonFormConfiguration;
import com.pspdfkit.forms.RadioButtonFormConfiguration;
import com.pspdfkit.forms.SignatureFormConfiguration;
import com.pspdfkit.forms.TextFormConfiguration;
import com.pspdfkit.ui.PdfActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * This activity shows how to programmatically create forms.
 */
public class FormCreationActivity extends PdfActivity {

    @UiThread
    @Override
    public void onDocumentLoaded(@NonNull final PdfDocument document) {
        super.onDocumentLoaded(document);

        // Retrieve existing form elements and create form fields only when there are no forms.
        document.getFormProvider().getFormElementsAsync().subscribe((formElements, throwable) -> {
            if (!formElements.isEmpty()) return;
            createForms();
        });
    }

    /**
     * Creates several forms and attach them to the document.
     */
    private void createForms() {
        if (getDocument() == null) return;

        createTextFormField();
        createMultiLineFormField();
        createCheckBoxFormField();
        createRadioButtonFormField();
        createPushButtonFormField();
        createSignatureFormField();
        createComboBoxFormField();
        createListBoxFormField();
    }

    /**
     * Creates and attaches to the document a text form field.
     */
    private void createTextFormField() {
        final PdfDocument document = getDocument();
        if(document == null) return;

        RectF rectF = new RectF(
            30, // left
            750, // top
            200, // right
            720 // bottom
        );
        TextFormConfiguration textFormConfiguration = new TextFormConfiguration.Builder(0, rectF)
            .setText("Example text")
            // Setting the text field as required will draw a red border around the field.
            .setRequired(true)
            .build();

        document.getFormProvider().addFormElementToPage("textfield-1", textFormConfiguration);
    }

    private void createMultiLineFormField() {
        final PdfDocument document = getDocument();
        if(document == null) return;

        RectF rectF = new RectF(
                350, // left
                750, // top
                520, // right
                680 // bottom
        );
        TextFormConfiguration textFormConfiguration = new TextFormConfiguration.Builder(0, rectF)
                .setText("Multiline text field")
                // Setting the text field as multiline allows multi-line text input into the field.
                .setMultiLine(true)
                // Disabling scrolling will also prevent entering more text that would require field scrolling.
                .setScrollEnabled(false)
                .build();

        document.getFormProvider().addFormElementToPage("textfield-2", textFormConfiguration);
    }

    /**
     * Creates and attaches to the document a checkbox form field with two checkbox form elements.
     */
    private void createCheckBoxFormField() {
        final PdfDocument document = getDocument();
        if(document == null) return;

        RectF rectFCheckBoxFormConfiguration1 = new RectF(
            30, // left
            650, // top
            60, // right
            620 // bottom
        );
        CheckBoxFormConfiguration checkBoxFormConfiguration1 = new CheckBoxFormConfiguration.Builder(0, rectFCheckBoxFormConfiguration1)
            .select()
            .build();

        RectF rectFCheckBoxFormConfiguration2 = new RectF(
            30, // left
            600, // top
            60, // right
            570 // bottom
        );
        CheckBoxFormConfiguration checkBoxFormConfiguration2 = new CheckBoxFormConfiguration.Builder(0, rectFCheckBoxFormConfiguration2)
            .deselect()
            .build();

        List<CheckBoxFormConfiguration> checkBoxFormConfigurationList = Arrays.asList(checkBoxFormConfiguration1, checkBoxFormConfiguration2);
        document.getFormProvider().addFormElementsToPage("checkboxfield-1", checkBoxFormConfigurationList);
    }

    /**
     * Creates and attaches to the document a radio button form field with two radio button form elements.
     */
    private void createRadioButtonFormField() {
        final PdfDocument document = getDocument();
        if(document == null) return;

        RectF rectFRadioButtonFormConfiguration1 = new RectF(
            30, // left
            500, // top
            60, // right
            470 // bottom
        );
        RadioButtonFormConfiguration radioButtonFormConfiguration1 = new RadioButtonFormConfiguration.Builder(0, rectFRadioButtonFormConfiguration1)
            .select()
            .build();

        RectF rectFRadioButtonFormConfiguration2 = new RectF(
            30, // left
            450, // top
            60, // right
            420 // bottom
        );
        RadioButtonFormConfiguration radioButtonFormConfiguration2 = new RadioButtonFormConfiguration.Builder(0, rectFRadioButtonFormConfiguration2)
            .deselect()
            .build();

        List<RadioButtonFormConfiguration> radioButtonFormConfigurationList = Arrays.asList(radioButtonFormConfiguration1, radioButtonFormConfiguration2);
        document.getFormProvider().addFormElementsToPage("radiobuttonfield-1", radioButtonFormConfigurationList);
    }

    /**
     * Creates and attaches to the document a push button form field.
     */
    private void createPushButtonFormField() {
        final PdfDocument document = getDocument();
        if(document == null) return;

        RectF rectFPushButtonFormConfiguration = new RectF(
            30, // left
            350, // top
            120, // right
            260 // bottom
        );

        PushButtonFormConfiguration pushButtonFormConfiguration = new PushButtonFormConfiguration.Builder(
            0,
            rectFPushButtonFormConfiguration,
            getBitmapFromAsset("images/android.png")
        )
            .setAction(new UriAction("https://developer.android.com/index.html"))
            .build();

        document.getFormProvider().addFormElementToPage("pushbuttonfield-1", pushButtonFormConfiguration);
    }

    /**
     * Creates and attaches to the document a signature form field.
     */
    private void createSignatureFormField() {
        final PdfDocument document = getDocument();
        if(document == null) return;

        RectF rectFSignatureFormConfiguration = new RectF(
            30, // left
            190, // top
            200, // right
            160 // bottom
        );

        SignatureFormConfiguration signatureFormConfiguration = new SignatureFormConfiguration.Builder(0, rectFSignatureFormConfiguration)
            .build();

        document.getFormProvider().addFormElementToPage("signaturefield-1", signatureFormConfiguration);
    }

    /**
     * Creates and attaches to the document a combo box form field.
     */
    private void createComboBoxFormField() {
        final PdfDocument document = getDocument();
        if(document == null) return;

        RectF rectFComboBoxFormConfiguration = new RectF(
            350, // left
            650, // top
            520, // right
            620 // bottom
        );
        ComboBoxFormConfiguration comboBoxFormConfiguration = new ComboBoxFormConfiguration.Builder(0, rectFComboBoxFormConfiguration)
            .setFormOptions(Arrays.asList(
                new FormOption("L1", "42"),
                new FormOption("L2", "43")
            ))
            .setCustomText("Custom text")
            .build();

        document.getFormProvider().addFormElementToPage("comboboxfield-1", comboBoxFormConfiguration);
    }

    /**
     * Creates and attaches to the document a list box form field.
     */
    private void createListBoxFormField() {
        final PdfDocument document = getDocument();
        if(document == null) return;

        RectF rectFListBoxFormConfiguration = new RectF(
            350, // left
            500, // top
            520, // right
            420 // bottom
        );
        ListBoxFormConfiguration listBoxFormConfiguration = new ListBoxFormConfiguration.Builder(0, rectFListBoxFormConfiguration)
            .setFormOptions(Arrays.asList(
                new FormOption("L1", "42"),
                new FormOption("L2", "43"),
                new FormOption("L3", "44"),
                new FormOption("L4", "45")
            ))
            .setMultiSelectionEnabled(true)
            .setSelectedIndexes(Arrays.asList(1, 2))
            .build();

        document.getFormProvider().addFormElementToPage("listboxfield-1", listBoxFormConfiguration);
    }

    private Bitmap getBitmapFromAsset(@NonNull final String path) {
        Bitmap bitmap;
        try (InputStream is = getAssets().open(path)) {
            bitmap = BitmapFactory.decodeStream(is);
        } catch (final IOException e) {
            bitmap = null;
        }
        return bitmap;
    }
}
