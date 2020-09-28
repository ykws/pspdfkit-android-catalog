/*
 *   Copyright © 2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.kotlin

import android.content.Context
import android.net.Uri
import com.pspdfkit.catalog.PSPDFExample
import com.pspdfkit.catalog.R
import com.pspdfkit.catalog.examples.kotlin.activities.TeacherStudentActivity
import com.pspdfkit.catalog.tasks.ExtractAssetTask.extract
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.configuration.page.PageLayoutMode
import com.pspdfkit.configuration.sharing.ShareFeatures
import com.pspdfkit.preferences.PSPDFKitPreferences
import com.pspdfkit.ui.PdfActivityIntentBuilder
import com.pspdfkit.ui.special_mode.controller.AnnotationTool
import java.util.ArrayList

/**
 * This example shows how to swap between documents and sync view state, annotations, and bookmarks.
 */
class TeacherStudentExample(context: Context)
    : PSPDFExample(context, R.string.teacherStudentExampleTitle, R.string.teacherStudentExampleDescription) {
    override fun launchExample(context: Context, configuration: PdfActivityConfiguration.Builder) {

        // We'll disable redaction tool in annotation creation toolbar to prevent creation of redaction annotations.
        val annotationTools: MutableList<AnnotationTool> = ArrayList(listOf(*AnnotationTool.values()))
        annotationTools.remove(AnnotationTool.REDACTION)

        configuration
            // Turn off saving, so we have the clean original document every time the example is launched.
            .autosaveEnabled(false)
            // Use single page mode.
            .layoutMode(PageLayoutMode.SINGLE)
            // Disable all the menu items but annotations editing and bookmark list.
            .disableOutline()
            .disableDocumentInfoView()
            .disableAnnotationList()
            .disableSearch()
            .hideSettingsMenu()
            .setEnabledShareFeatures(ShareFeatures.none())
            .disablePrinting()
            .hideThumbnailGrid()
            .setRedactionUiEnabled(false)
            .enabledAnnotationTools(annotationTools)

        // The annotation creator written into newly created annotations. If not set, or set to null
        // a dialog will normally be shown when creating an annotation, asking you to enter a name.
        // We are going to skip this part and set it as "John Doe" only if it was not yet set.
        if (!PSPDFKitPreferences.get(context).isAnnotationCreatorSet) {
            PSPDFKitPreferences.get(context).setAnnotationCreator("John Doe")
        }
        // Extracts the documents from the assets and loads the teacher version first.
        extract("Teacher.pdf", title, context) { teacherDocumentFile->
            extract("Student.pdf", title, context) { studentDocumentFile ->
                val teacherDocumentUri = Uri.fromFile(teacherDocumentFile)
                val studentDocumentUri = Uri.fromFile(studentDocumentFile)
                val intent = PdfActivityIntentBuilder.fromUri(context, teacherDocumentUri)
                    .configuration(configuration.build())
                    .activityClass(TeacherStudentActivity::class)
                    .build()
                intent.putExtra(TeacherStudentActivity.STUDENT_URI_KEY, studentDocumentUri)
                intent.putExtra(TeacherStudentActivity.TEACHER_URI_KEY, teacherDocumentUri)
                context.startActivity(intent)
            }
        }
    }
}
