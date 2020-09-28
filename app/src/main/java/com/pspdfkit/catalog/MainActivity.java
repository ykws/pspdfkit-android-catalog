/*
 *   Copyright © 2014-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog;

import androidx.annotation.NonNull;

import com.pspdfkit.catalog.examples.java.AnnotationFlagsExample;
import com.pspdfkit.catalog.examples.java.AnnotationOverlayExample;
import com.pspdfkit.catalog.examples.java.AnnotationSelectionCustomizationExample;
import com.pspdfkit.catalog.examples.java.AnnotationWithAlphaCreationExample;
import com.pspdfkit.catalog.examples.java.CustomAnnotationCreationToolbarExample;
import com.pspdfkit.catalog.examples.java.CustomAnnotationInspectorExample;
import com.pspdfkit.catalog.examples.java.CustomAnnotationNoteHinterProviderExample;
import com.pspdfkit.catalog.examples.java.CustomDocumentDownloadExample;
import com.pspdfkit.catalog.examples.java.CustomFormHighlightColorExample;
import com.pspdfkit.catalog.examples.java.CustomInkSignatureExample;
import com.pspdfkit.catalog.examples.java.CustomInlineSearchExample;
import com.pspdfkit.catalog.examples.java.CustomPageTemplatesExample;
import com.pspdfkit.catalog.examples.java.CustomSearchUiExample;
import com.pspdfkit.catalog.examples.java.CustomShareDialogExample;
import com.pspdfkit.catalog.examples.java.CustomSharingMenuExample;
import com.pspdfkit.catalog.examples.java.CustomStampAnnotationsExample;
import com.pspdfkit.catalog.examples.java.CustomToolbarIconGroupingExample;
import com.pspdfkit.catalog.examples.java.DisabledAnnotationPropertyExample;
import com.pspdfkit.catalog.examples.java.DocumentProcessingExample;
import com.pspdfkit.catalog.examples.java.DocumentSharingExample;
import com.pspdfkit.catalog.examples.java.DocumentSwitcherExample;
import com.pspdfkit.catalog.examples.java.DynamicMultimediaAnnotationExample;
import com.pspdfkit.catalog.examples.java.FilterableThumbnailGridExample;
import com.pspdfkit.catalog.examples.java.FormEditingInFragmentExample;
import com.pspdfkit.catalog.examples.java.FormsJavaScriptExample;
import com.pspdfkit.catalog.examples.java.IndexedFullTextSearchExample;
import com.pspdfkit.catalog.examples.java.JavaScriptActionsExample;
import com.pspdfkit.catalog.examples.java.JavaScriptCalculatorExample;
import com.pspdfkit.catalog.examples.java.JavaScriptFormFillingExample;
import com.pspdfkit.catalog.examples.java.RandomDocumentReplacementExample;
import com.pspdfkit.catalog.examples.java.RotatePageExample;
import com.pspdfkit.catalog.examples.java.ScreenReaderExample;
import com.pspdfkit.catalog.examples.java.SplitDocumentExample;
import com.pspdfkit.catalog.examples.java.ToolbarsInFragmentExample;
import com.pspdfkit.catalog.examples.java.VerticalScrollbarExample;
import com.pspdfkit.catalog.examples.java.XfdfExample;
import com.pspdfkit.catalog.examples.java.decryption.AesEncryptedFileExample;
import com.pspdfkit.catalog.examples.kotlin.AnnotationConfigurationExample;
import com.pspdfkit.catalog.examples.kotlin.AnnotationCreationExample;
import com.pspdfkit.catalog.examples.kotlin.AnnotationRenderingExample;
import com.pspdfkit.catalog.examples.kotlin.AnnotationSelectionViewStylingExample;
import com.pspdfkit.catalog.examples.kotlin.BasicExample;
import com.pspdfkit.catalog.examples.kotlin.ContainedSignaturesExample;
import com.pspdfkit.catalog.examples.kotlin.ConvertHtmlToPdfExample;
import com.pspdfkit.catalog.examples.kotlin.CustomActionsExample;
import com.pspdfkit.catalog.examples.kotlin.CustomApplicationPolicyExample;
import com.pspdfkit.catalog.examples.kotlin.CustomDataProviderExample;
import com.pspdfkit.catalog.examples.kotlin.CustomFragmentRuntimeConfigurationExample;
import com.pspdfkit.catalog.examples.kotlin.CustomLayoutExample;
import com.pspdfkit.catalog.examples.kotlin.CustomSignatureProviderExample;
import com.pspdfkit.catalog.examples.kotlin.DarkThemeExample;
import com.pspdfkit.catalog.examples.kotlin.DigitalSignatureExample;
import com.pspdfkit.catalog.examples.kotlin.DocumentComparisonExample;
import com.pspdfkit.catalog.examples.kotlin.DocumentDownloadExample;
import com.pspdfkit.catalog.examples.kotlin.DocumentFromCanvasExample;
import com.pspdfkit.catalog.examples.kotlin.DocumentJsonExample;
import com.pspdfkit.catalog.examples.kotlin.DocumentTabsExample;
import com.pspdfkit.catalog.examples.kotlin.EmptyActivityExample;
import com.pspdfkit.catalog.examples.kotlin.ExternalDocumentExample;
import com.pspdfkit.catalog.examples.kotlin.FileAnnotationCreationExample;
import com.pspdfkit.catalog.examples.kotlin.FormCreationExample;
import com.pspdfkit.catalog.examples.kotlin.FormFillingExample;
import com.pspdfkit.catalog.examples.kotlin.FragmentExample;
import com.pspdfkit.catalog.examples.kotlin.GenerateReportExample;
import com.pspdfkit.catalog.examples.kotlin.ImageDocumentExample;
import com.pspdfkit.catalog.examples.kotlin.InlineMediaExample;
import com.pspdfkit.catalog.examples.kotlin.KioskExample;
import com.pspdfkit.catalog.examples.kotlin.ManualSigningExample;
import com.pspdfkit.catalog.examples.kotlin.MergeDocumentsExample;
import com.pspdfkit.catalog.examples.kotlin.OcrExample;
import com.pspdfkit.catalog.examples.kotlin.OutlineProviderExample;
import com.pspdfkit.catalog.examples.kotlin.OverlayViewsExample;
import com.pspdfkit.catalog.examples.kotlin.PasswordExample;
import com.pspdfkit.catalog.examples.kotlin.PdfFromImageExample;
import com.pspdfkit.catalog.examples.kotlin.PdfUiFragmentExample;
import com.pspdfkit.catalog.examples.kotlin.PersistentAnnotationSidebarExample;
import com.pspdfkit.catalog.examples.kotlin.HideRevealAnnotationsCreationExample;
import com.pspdfkit.catalog.examples.kotlin.PersistentTabsExample;
import com.pspdfkit.catalog.examples.kotlin.ProgressProviderExample;
import com.pspdfkit.catalog.examples.kotlin.ReaderViewExample;
import com.pspdfkit.catalog.examples.kotlin.RuntimeConfigurationExample;
import com.pspdfkit.catalog.examples.kotlin.ScientificPaperExample;
import com.pspdfkit.catalog.examples.kotlin.SignaturePickerDialogIntegrationExample;
import com.pspdfkit.catalog.examples.kotlin.TeacherStudentExample;
import com.pspdfkit.catalog.examples.kotlin.UserInterfaceViewModesExample;
import com.pspdfkit.catalog.examples.kotlin.WatermarkExample;
import com.pspdfkit.catalog.examples.kotlin.ZoomExample;
import com.pspdfkit.catalog.examples.kotlin.instant.InstantExample;
import com.pspdfkit.catalog.service.DownloadedFilesObserverService;
import com.pspdfkit.catalog.ui.PSPDFCatalogActivity;
import com.pspdfkit.catalog.examples.kotlin.BookmarkHighlightingExample;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends PSPDFCatalogActivity {

    @Override
    protected void onResume() {
        super.onResume();
        DownloadedFilesObserverService.startService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DownloadedFilesObserverService.stopService(this);
    }

    @NonNull
    @Override
    protected List<PSPDFExample.Section> getExamples() {
        List<PSPDFExample.Section> sections = new ArrayList<>();
        sections.add(new PSPDFExample.Section(
            "Basic",
            new BasicExample(this),
            new KioskExample(this),
            new InstantExample(this),
            new DocumentTabsExample(this),
            new EmptyActivityExample(this),
            new ReaderViewExample(this)
        ));

        sections.add(new PSPDFExample.Section(
            "Opening Documents",
            new ExternalDocumentExample(this),
            new CustomDataProviderExample(this),
            new DocumentDownloadExample(this),
            new ProgressProviderExample(this),
            new PasswordExample(this),
            new AesEncryptedFileExample(this),
            new ImageDocumentExample(this)
        ));

        sections.add(new PSPDFExample.Section(
            "Behaviour Customization",
            new ScientificPaperExample(this),
            new DarkThemeExample(this),
            new ZoomExample(this),
            new RuntimeConfigurationExample(this),
            new CustomFragmentRuntimeConfigurationExample(this),
            new UserInterfaceViewModesExample(this),
            new CustomApplicationPolicyExample(this),
            new CustomAnnotationNoteHinterProviderExample(this),
            new RotatePageExample(this),
            new OutlineProviderExample(this)
        ));

        sections.add(new PSPDFExample.Section(
            "Annotations",
            new AnnotationCreationExample(this),
            new AnnotationWithAlphaCreationExample(this),
            new FileAnnotationCreationExample(this),
            new AnnotationRenderingExample(this),
            new AnnotationConfigurationExample(this),
            new AnnotationFlagsExample(this),
            new AnnotationSelectionCustomizationExample(this),
            new CustomAnnotationInspectorExample(this),
            new CustomStampAnnotationsExample(this),
            new AnnotationOverlayExample(this),
            new DocumentJsonExample(this),
            new XfdfExample(this),
            new HideRevealAnnotationsCreationExample(this)
        ));

        sections.add(new PSPDFExample.Section(
            "Forms",
            new FormFillingExample(this),
            new FormCreationExample(this),
            new CustomFormHighlightColorExample(this)
        ));

        sections.add(new PSPDFExample.Section(
            "Signing",
            new DigitalSignatureExample(this),
            new CustomSignatureProviderExample(this),
            new ContainedSignaturesExample(this),
            new ManualSigningExample(this),
            new SignaturePickerDialogIntegrationExample(this),
            new CustomInkSignatureExample(this)
        ));

        sections.add(new PSPDFExample.Section(
            "JavaScript",
            new JavaScriptActionsExample(this),
            new JavaScriptFormFillingExample(this),
            new FormsJavaScriptExample(this),
            new JavaScriptCalculatorExample(this)
        ));

        sections.add(new PSPDFExample.Section(
            "Document Processing",
            new DocumentProcessingExample(this),
            new DocumentFromCanvasExample(this),
            new OcrExample(this),
            new GenerateReportExample(this),
            new MergeDocumentsExample(this),
            new DocumentComparisonExample(this),
            new ConvertHtmlToPdfExample(this),
            new CustomPageTemplatesExample(this),
            new PdfFromImageExample(this)
        ));

        sections.add(new PSPDFExample.Section(
            "Layout Customization",
            new CustomLayoutExample(this),
            new FragmentExample(this),
            new ToolbarsInFragmentExample(this),
            new FormEditingInFragmentExample(this),
            new PdfUiFragmentExample(this),
            new CustomDocumentDownloadExample(this),
            new DocumentSwitcherExample(this),
            new VerticalScrollbarExample(this),
            new SplitDocumentExample(this),
            new PersistentAnnotationSidebarExample(this),
            new AnnotationSelectionViewStylingExample(this)
        ));

        sections.add(new PSPDFExample.Section(
            "Toolbar Customization",
            new CustomActionsExample(this),
            new CustomToolbarIconGroupingExample(this),
            new CustomAnnotationCreationToolbarExample(this),
            new CustomInlineSearchExample(this),
            new CustomSearchUiExample(this),
            new DisabledAnnotationPropertyExample(this)
        ));

        sections.add(new PSPDFExample.Section(
            "Sharing",
            new DocumentSharingExample(this),
            new CustomSharingMenuExample(this),
            new CustomShareDialogExample(this)
        ));

        sections.add(new PSPDFExample.Section(
            "Misc. examples",
            new WatermarkExample(this),
            new BookmarkHighlightingExample(this),
            new OverlayViewsExample(this),
            new PersistentTabsExample(this),
            new FilterableThumbnailGridExample(this),
            new ScreenReaderExample(this),
            new IndexedFullTextSearchExample(this),
            new InlineMediaExample(this),
            new DynamicMultimediaAnnotationExample(this),
            new RandomDocumentReplacementExample(this),
            new TeacherStudentExample(this)
        ));

        return sections;
    }
}
