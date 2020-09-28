/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.instant.activities;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.pspdfkit.catalog.examples.java.instant.api.InstantExampleDocumentDescriptor;
import com.pspdfkit.document.sharing.DocumentSharingIntentHelper;
import com.pspdfkit.document.sharing.ShareTarget;
import com.pspdfkit.catalog.R;
import com.pspdfkit.instant.ui.InstantPdfActivity;
import com.pspdfkit.ui.actionmenu.ActionMenu;
import com.pspdfkit.ui.actionmenu.ActionMenuItem;
import com.pspdfkit.ui.actionmenu.ActionMenuListener;
import com.pspdfkit.ui.actionmenu.FixedActionMenuItem;
import com.pspdfkit.ui.actionmenu.SharingMenu;

import java.util.Collections;

/**
 * Extends {@link InstantPdfActivity} with the ability to share the Instant document with other users.
 */
public class InstantExampleActivity extends InstantPdfActivity implements SharingMenu.SharingMenuListener, ActionMenuListener {

    /** Name of the extra holding {@link InstantExampleDocumentDescriptor} of the displayed document. */
    public static final String DOCUMENT_DESCRIPTOR = "InstantExampleActivity.DocumentDescriptor";

    /** Descriptor for the displayed document. */
    private InstantExampleDocumentDescriptor documentDescriptor;

    /** Main toolbar icons color. */
    private int mainToolbarIconsColor;

    /** Menu with collaborate sharing actions. */
    private SharingMenu collaborateMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        documentDescriptor = getIntent().getParcelableExtra(DOCUMENT_DESCRIPTOR);
        if (documentDescriptor == null) {
            throw new IllegalStateException("InstantExampleActivity was not initialized with proper arguments: Missing document descriptor extra!");
        }

        final TypedArray a = getTheme().obtainStyledAttributes(null, R.styleable.pspdf__ActionBarIcons, R.attr.pspdf__actionBarIconsStyle, R.style.PSPDFKit_ActionBarIcons);
        mainToolbarIconsColor = a.getColor(R.styleable.pspdf__ActionBarIcons_pspdf__iconsColor, ContextCompat.getColor(this, R.color.white));
        a.recycle();

        initCollaborateMenu();
    }

    private void initCollaborateMenu() {
        collaborateMenu = new SharingMenu(this, this);
        collaborateMenu.setTitle(getString(R.string.instant_collaborate));

        collaborateMenu.addMenuItem(new FixedActionMenuItem(this, R.id.open_in_browser, R.drawable.ic_open_in_browser, R.string.instant_open_in_browser));
        collaborateMenu.addMenuItem(new FixedActionMenuItem(this, R.id.share_document_link, R.drawable.pspdf__ic_open_in, R.string.instant_share_document_link));

        collaborateMenu.addActionMenuListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem collaborateMenuItem = menu.add(0, R.id.instant_collaborate, 0, getString(R.string.instant_collaborate));
        collaborateMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem collaborateMenuItem = menu.findItem(R.id.instant_collaborate);
        collaborateMenuItem.setEnabled(getDocument() != null);

        Drawable collaborateItemIcon = ContextCompat.getDrawable(this, R.drawable.ic_collaborate);
        DrawableCompat.setTint(collaborateItemIcon, mainToolbarIconsColor);
        collaborateItemIcon.setAlpha(getDocument() != null ? 255 : 128);
        collaborateMenuItem.setIcon(collaborateItemIcon);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.instant_collaborate) {
            collaborateMenu.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void performShare(@NonNull ShareTarget shareTarget) {
    }

    @Override
    public boolean onPrepareActionMenu(@NonNull ActionMenu actionMenu) {
        return true;
    }

    @Override
    public void onDisplayActionMenu(@NonNull ActionMenu actionMenu) {
    }

    @Override
    public void onRemoveActionMenu(@NonNull ActionMenu actionMenu) {
    }

    @Override
    public boolean onActionMenuItemClicked(@NonNull ActionMenu actionMenu, @NonNull ActionMenuItem menuItem) {
        if (menuItem.getItemId() == R.id.share_document_link) {
            // Sharing link to the Instant document.
            showShareTextMenu(R.string.instant_share_document_link, documentDescriptor.webUrl);
            return true;
        }  else if (menuItem.getItemId() == R.id.open_in_browser) {
            // Opens Instant document link in the web browser.
            showOpenInBrowserMenu();
            return true;
        }
        return false;
    }

    private void showOpenInBrowserMenu() {
        final Intent shareIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(documentDescriptor.webUrl));
        SharingMenu sharingMenu = new SharingMenu(this, shareTarget -> {
            shareIntent.setPackage(shareTarget.getPackageName());
            startActivity(shareIntent);
        });
        sharingMenu.setTitle(R.string.instant_open_in_browser);
        sharingMenu.setShareIntents(Collections.singletonList(shareIntent));

        collaborateMenu.dismiss();
        sharingMenu.show();
    }

    private void showShareTextMenu(@StringRes int titleRes, @NonNull String textToShare) {
        final Intent shareIntent = DocumentSharingIntentHelper.getShareTextIntent(textToShare);
        SharingMenu sharingMenu = new SharingMenu(this, shareTarget -> {
            shareIntent.setPackage(shareTarget.getPackageName());
            startActivity(shareIntent);
        });
        sharingMenu.setTitle(titleRes);
        sharingMenu.setShareIntents(Collections.singletonList(shareIntent));

        collaborateMenu.dismiss();
        sharingMenu.show();
    }

    @Override
    public boolean onActionMenuItemLongClicked(@NonNull ActionMenu actionMenu, @NonNull ActionMenuItem menuItem) {
        return false;
    }
}
