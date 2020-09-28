/*
 *   Copyright © 2014-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.activities;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import com.pspdfkit.catalog.R;
import com.pspdfkit.ui.PdfActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * This subclass of {@link PdfActivity} adds a set of custom actions.
 */
public class CustomActionsActivity extends PdfActivity {

    public static final String STRING_SAMPLE_ARG = "some_string_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Access any previously added intent extras as with normal activities.
        final String receivedString = getIntent().getStringExtra(STRING_SAMPLE_ARG);
        Toast.makeText(this, receivedString, Toast.LENGTH_SHORT).show();
    }

    /**
     * Override this method to get the list of menu item IDs. Menu items are gonna be ordered by default.
     * You can add your own menu item ids that you can later edit in {@link #onCreateOptionsMenu(Menu)}
     * or {@link #onPrepareOptionsMenu(Menu)}.
     */
    @NonNull
    @Override
    public List<Integer> onGenerateMenuItemIds(@NonNull List<Integer> menuItems) {
        // For example let's say we want to add custom menu items after the outline button.
        // First, we get an index of outline button (all default button ids can be retrieved
        // via MENU_OPTION_* variables defined in the PdfActivity.
        int indexOfOutlineButton = menuItems.indexOf(MENU_OPTION_OUTLINE);

        // Generate our custom item ids.
        List<Integer> customItems = new ArrayList<>();
        customItems.add(R.id.custom_action1);
        customItems.add(R.id.custom_action2);
        customItems.add(R.id.custom_action3);

        // Add items after the outline button.
        menuItems.addAll(indexOfOutlineButton + 1, customItems);

        // Return new menu items order.
        return menuItems;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This will populate menu with items ordered as specified in onGenerateMenuItemIds().
        super.onCreateOptionsMenu(menu);

        // Edit first button.
        MenuItem menuItem1 = menu.findItem(R.id.custom_action1);
        menuItem1.setTitle("Menu Item 1");
        menuItem1.setIcon(R.drawable.ic_arrow_left);

        // Edit second button.
        MenuItem menuItem2 = menu.findItem(R.id.custom_action2);
        menuItem2.setTitle("Menu Item 2");
        menuItem2.setIcon(R.drawable.ic_arrow_right);

        // Edit third button.
        MenuItem menuItem3 = menu.findItem(R.id.custom_action3);
        menuItem3.setTitle("Menu Item 3");
        menuItem3.setIcon(R.drawable.ic_collaborate);

        // Let's say we want to tint icons same as the default ones. We can read the color
        // from the theme, or specify the same color we have in theme. Reading from theme is a bit
        // more complex but a better way to do it, so here's how to:
        final TypedArray a = getTheme().obtainStyledAttributes(
                null,
                R.styleable.pspdf__ActionBarIcons,
                R.attr.pspdf__actionBarIconsStyle,
                R.style.PSPDFKit_ActionBarIcons
        );
        int mainToolbarIconsColor = a.getColor(R.styleable.pspdf__ActionBarIcons_pspdf__iconsColor, ContextCompat.getColor(this, R.color.white));
        a.recycle();

        // Tinting all custom menu drawables (you can do it the easier way if you iterate over your ids).
        Drawable icon1 = menuItem1.getIcon();
        DrawableCompat.setTint(icon1, mainToolbarIconsColor);
        menuItem1.setIcon(icon1);

        Drawable icon2 = menuItem2.getIcon();
        DrawableCompat.setTint(icon2, mainToolbarIconsColor);
        menuItem2.setIcon(icon2);

        Drawable icon3 = menuItem3.getIcon();
        DrawableCompat.setTint(icon3, mainToolbarIconsColor);
        menuItem3.setIcon(icon3);

        // All our menu items are marked as SHOW_AS_ALWAYS. If you want to just show the first 4
        // items for example and send others to the overflow, you can simply do:
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setShowAsAction(i < 4 ? MenuItem.SHOW_AS_ACTION_ALWAYS : MenuItem.SHOW_AS_ACTION_NEVER);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Here, you can edit your items when the menu is being invalidated.
        // To invalidate menu, call supportInvalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Override onOptionsItemSelected(MenuItem) to handle click events for your custom menu items.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean handled = false;

        final int itemId = item.getItemId();
        if (itemId == R.id.custom_action1) {
            handled = true;
            Toast.makeText(this, "Selected Action 1", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.custom_action2) {
            handled = true;
            Toast.makeText(this, "Selected Action 2", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.custom_action3) {
            handled = true;
            Toast.makeText(this, "Selected Action 3", Toast.LENGTH_SHORT).show();
        }

        // Return true if you have handled the current event. If your code has not handled the event,
        // pass it on to the superclass. This is important or standard PSPDFKit actions won't work.
        return handled || super.onOptionsItemSelected(item);
    }
}
