/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.pspdfkit.configuration.activity.PdfActivityConfiguration;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Abstract example class which provides {@link #launchExample(Context, PdfActivityConfiguration.Builder)}
 * as a generic way of launching a catalog app example.
 */
public abstract class PSPDFExample {

    // Documents used in our examples.
    public static final String QUICK_START_GUIDE = "Guide-v6.pdf";
    public static final String ANNOTATIONS_EXAMPLE = "Annotations.pdf";

    /**
     * Enum with all supported example languages.
     */
    public enum ExampleLanguage {
        JAVA,
        KOTLIN
    }

    /**
     * A section is a named list of examples grouped together (e.g. "Multimedia examples").
     */
    public static class Section extends ArrayList<PSPDFExample> {
        @NonNull private final String name;

        public Section(@NonNull final String name, @NonNull final PSPDFExample... examples) {
            this.name = name;
            Collections.addAll(this, examples);
        }

        public Section(@NonNull final String name,
                       @NonNull final Collection<PSPDFExample> examples) {
            this.name = name;
            addAll(examples);
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Short title of the example.
     */
    @NonNull public String title;
    /**
     * Full description of the example.
     */
    @NonNull public String description;

    /**
     * Convenience constructor. Examples can pass their {@code title} and {@code description}
     * here.
     */
    public PSPDFExample(@NonNull String title, @NonNull String description) {
        this.title = title;
        this.description = description;
    }

    /**
     * Convenience constructor. Examples can pass their {@code title} and {@code description} here.
     */
    public PSPDFExample(@NonNull final Context context,
                        @StringRes final int title,
                        @StringRes final int description) {
        this.title = context.getString(title);
        this.description = context.getString(description);
    }

    /**
     * Examples have to implement this method with their example code. The given {@code configuration}
     * contains all default settings for the example, and can be further tweaked if necessary.
     * @param context       Context for launching examples.
     * @param configuration Default configuration as created by the preferences.
     */
    public abstract void launchExample(@NonNull Context context, @NonNull PdfActivityConfiguration.Builder configuration);

    /**
     * Returns the example simple class name as given in the source code.
     * @return the example simple class name.
     */
    @NonNull
    public String getExampleName() {
        return getClass().getSimpleName();
    }

    /**
     * Returns the language of this example.
     */
    @NonNull
    public final ExampleLanguage getExampleLanguage() {
        return isKotlin() ? ExampleLanguage.KOTLIN : ExampleLanguage.JAVA;
    }

    /**
     * Returns {@code true} when this example is written in Kotlin.
     */
    private boolean isKotlin() {
        final Annotation[] annotations = getClass().getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(kotlin.Metadata.class)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Called when the owning activity gets destroyed. Example is supposed to clean any required
     * resources.
     */
    public void onDestroy() {
    }
}
