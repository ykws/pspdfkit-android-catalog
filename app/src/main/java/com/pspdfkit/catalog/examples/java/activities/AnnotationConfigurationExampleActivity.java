/*
 *   Copyright © 2019-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.pspdfkit.annotations.AnnotationType;
import com.pspdfkit.annotations.configuration.AnnotationProperty;
import com.pspdfkit.annotations.configuration.FreeTextAnnotationConfiguration;
import com.pspdfkit.annotations.configuration.InkAnnotationConfiguration;
import com.pspdfkit.annotations.configuration.MarkupAnnotationConfiguration;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.PdfFragment;
import com.pspdfkit.ui.special_mode.controller.AnnotationTool;
import com.pspdfkit.ui.special_mode.controller.AnnotationToolVariant;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Shows how to change annotation configuration through {@link PdfFragment}.
 */
public class AnnotationConfigurationExampleActivity extends PdfActivity {

    @UiThread
    @Override
    public void onDocumentLoaded(@NonNull PdfDocument document) {
        super.onDocumentLoaded(document);

        PdfFragment fragment = getPdfFragment();
        if (fragment != null) {
            configureFreeTextDefaults(fragment);
            configureInkDefaults(fragment);
            configureHighlightDefaults(fragment);
        }
    }

    /**
     * Shows how to set annotation configuration for free-text annotations and control which properties are going to be
     * displayed in annotation inspector.
     */
    private void configureFreeTextDefaults(@NonNull final PdfFragment fragment) {
        // Annotation configuration can be configured through PdfFragment for each annotation type.
        fragment.getAnnotationConfiguration().put(
            AnnotationType.FREETEXT,
            FreeTextAnnotationConfiguration.builder(this)
                // Configure which color is used when creating free-text annotations.
                .setDefaultColor(Color.rgb(0, 0, 0))
                // Configure which colors are going to be available in the color picker.
                .setAvailableColors(
                    Arrays.asList(
                        Color.rgb(255, 255, 255),
                        Color.rgb(224, 224, 224),
                        Color.rgb(158, 158, 158),
                        Color.rgb(66, 66, 66),
                        Color.rgb(0, 0, 0)
                    )
                )
                // Configure default text size (in pt).
                .setDefaultTextSize(24)
                // Only the color property will be editable in the annotation inspector.
                .setSupportedProperties(EnumSet.of(AnnotationProperty.COLOR))
                // Disable annotation preview for free-text annotation.
                .setPreviewEnabled(false)
                .build()
        );
    }

    /**
     * Shows how to force annotation defaults.
     */
    private void configureInkDefaults(@NonNull final PdfFragment fragment) {
        fragment.getAnnotationConfiguration().put(
            AnnotationTool.INK,
            InkAnnotationConfiguration.builder(this)
                // Configure which color is used when creating ink annotations.
                .setDefaultColor(Color.rgb(252, 237, 140))
                // Configure which colors are going to be available in the color picker.
                .setAvailableColors(
                    Arrays.asList(
                        Color.rgb(244, 67, 54), // RED
                        Color.rgb(139, 195, 74), // LIGHT GREEN
                        Color.rgb(33, 150, 243), // BLUE
                        Color.rgb(252, 237, 140), // YELLOW
                        Color.rgb(233, 30, 99) // PINK
                    )
                )

                // Configure thickness picker range and default thickness.
                .setDefaultThickness(5)
                .setMinThickness(1)
                .setMaxThickness(20)

                // When true attributes like default color are always used as default when creating annotations.
                // When false last edited value is used, value from configuration is used only when creating annotation for the first time.
                .setForceDefaults(true)
                .setPreviewEnabled(false)

                // Build the configuration.
                .build()
        );
        fragment.getAnnotationConfiguration().put(
            AnnotationTool.INK,
            AnnotationToolVariant.fromPreset(AnnotationToolVariant.Preset.HIGHLIGHTER),
            InkAnnotationConfiguration.builder(this)
                // Configure which color is used when creating ink annotations.
                .setDefaultColor(Color.rgb(252, 237, 140))
                // Configure which colors are going to be available in the color picker.
                .setAvailableColors(
                    Arrays.asList(
                        Color.rgb(244, 67, 54), // RED
                        Color.rgb(139, 195, 74), // LIGHT GREEN
                        Color.rgb(33, 150, 243), // BLUE
                        Color.rgb(252, 237, 140), // YELLOW
                        Color.rgb(233, 30, 99) // PINK
                    )
                )

                // Configure thickness picker range and default thickness.
                .setDefaultThickness(5)
                .setMinThickness(1)
                .setMaxThickness(20)

                // When true attributes like default color are always used as default when creating annotations.
                // When false last edited value is used, value from configuration is used only when creating annotation for the first time.
                .setForceDefaults(true)
                .setPreviewEnabled(false)

                // Build the configuration.
                .build()
        );
    }

    /**
     * Shows how to disable annotation inspector for highlight annotation.
     */
    private void configureHighlightDefaults(@NonNull final PdfFragment fragment) {
        fragment.getAnnotationConfiguration().put(
            AnnotationType.HIGHLIGHT,
            MarkupAnnotationConfiguration.builder(this, AnnotationType.HIGHLIGHT)
                // Makes yellow default highlight color.
                .setDefaultColor(Color.rgb(252, 237, 140))
                // Return no supported properties. This disables annotation inspector.
                .setSupportedProperties(EnumSet.noneOf(AnnotationProperty.class))
                .build()
        );
    }
}