/*
 *   Copyright © 2019-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Sticky header class in charge of detecting when a header view is the top position, and drawing a view that sticks to
 * the top of the recycler view list.
 */
public class StickyHeaderItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private static final String LOG_TAG = "StickyHeaderItem";

    @NonNull private final Drawable divider;

    @NonNull private StickyHeaderController controller;
    private int stickyHeaderHeight;

    public StickyHeaderItemDecoration(@NonNull final Context context,
                                      @NonNull final StickyHeaderController controller) {
        this.controller = controller;

        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        final Drawable divider = a.getDrawable(0);
        a.recycle();
        if (divider == null) {
            throw new IllegalStateException("@android:attr/listDivider was not set in the theme.");
        }
        this.divider = divider;
    }

    @Override
    public void onDrawOver(@NonNull final Canvas canvas,
                           @NonNull final RecyclerView parent,
                           @NonNull final RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);

        final View topChild = parent.getChildAt(0);
        if (topChild == null) {
            return;
        }

        final int topChildPosition = parent.getChildAdapterPosition(topChild);
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return;
        }

        final int headerPos = controller.getHeaderPositionForItem(topChildPosition);
        if (headerPos == RecyclerView.NO_POSITION) return;

        final View currentHeader = getHeaderViewForItem(headerPos, parent);
        fixLayoutSize(parent, currentHeader);

        final int contactPoint = currentHeader.getBottom();

        // Get the first child view that is in contact with the current header.
        final View childInContact = getChildInContact(parent, contactPoint, headerPos);
        boolean isFirstHeaderDockedToTop = true;
        boolean isInContactWithAnotherHeader = false;
        if (childInContact != null) {
            int childInContactPosition = parent.getChildAdapterPosition(childInContact);
            isFirstHeaderDockedToTop = childInContactPosition == RecyclerView.NO_POSITION;
            isInContactWithAnotherHeader = controller.isHeader(childInContactPosition);
        }

        if (isInContactWithAnotherHeader) {
            // Move the current header out of the view to make space for docking the next header.
            moveHeader(canvas, currentHeader, childInContact);
            return;
        }

        drawHeader(
            canvas,
            currentHeader,
            // Don't draw divider for the first header if it's docked to the top.
            !isFirstHeaderDockedToTop);
    }

    @NonNull
    private View getHeaderViewForItem(int headerPosition, RecyclerView parent) {
        int layoutResId = controller.getHeaderLayout(headerPosition);
        View header = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        controller.bindHeaderData(header, headerPosition);
        return header;
    }

    private void drawHeader(@NonNull Canvas canvas, @NonNull View header, boolean drawDivider) {
        canvas.save();
        canvas.translate(0, 0);
        header.draw(canvas);

        if (drawDivider) {
            drawDivider(canvas, header);
        }

        canvas.restore();
    }

    private void moveHeader(@NonNull final Canvas canvas,
                            @NonNull final View currentHeader,
                            @NonNull final View nextHeader) {
        canvas.save();
        canvas.translate(0, nextHeader.getTop() - currentHeader.getHeight());

        currentHeader.draw(canvas);
        drawDivider(canvas, currentHeader);

        canvas.restore();
    }

    private void drawDivider(@NonNull final Canvas canvas,
                             @NonNull final View header) {
        divider.setBounds(header.getLeft(), header.getBottom() - divider.getIntrinsicHeight(), header.getRight(), header.getBottom());
        divider.draw(canvas);
    }

    @Nullable
    private View getChildInContact(@NonNull final RecyclerView parent,
                                   int contactPoint,
                                   int currentHeaderPos) {
        View childInContact = null;
        for (int i = 0; i < parent.getChildCount(); i++) {
            int heightTolerance = 0;
            View child = parent.getChildAt(i);

            // Measure height tolerance with child if is a header.
            if (currentHeaderPos != i) {
                boolean isChildHeader = controller.isHeader(parent.getChildAdapterPosition(child));
                if (isChildHeader) {
                    heightTolerance = stickyHeaderHeight - child.getHeight();
                }
            }

            // Add height tolerance if the child top position is in the display area.
            int childBottomPosition;
            if (child.getTop() > 0) {
                childBottomPosition = child.getBottom() + heightTolerance;
            } else {
                childBottomPosition = child.getBottom();
            }

            if (childBottomPosition > contactPoint) {
                if (child.getTop() <= contactPoint) {
                    // This child overlaps the contact point.
                    childInContact = child;
                    break;
                }
            }
        }
        return childInContact;
    }

    /**
     * Measures and layouts the top sticky header.
     *
     * @param parent RecyclerView parent.
     */
    private void fixLayoutSize(ViewGroup parent, View view) {

        // Specs for parent (RecyclerView)
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);

        // Specs for children (headers)
        int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, parent.getPaddingLeft() + parent.getPaddingRight(), view.getLayoutParams().width);
        int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, parent.getPaddingTop() + parent.getPaddingBottom(), view.getLayoutParams().height);

        view.measure(childWidthSpec, childHeightSpec);

        view.layout(0, 0, view.getMeasuredWidth(), stickyHeaderHeight = view.getMeasuredHeight());
    }

    /**
     * Sticky header controller implemented by {@link ExampleListAdapter}.
     */
    public interface StickyHeaderController {
        /**
         * Called by {@link StickyHeaderItemDecoration} to fetch the position of the header item in the adapter.
         *
         * @param itemPosition Adapter's position of the item for which to do the search of the position of the header
         *                     item.
         * @return Position of the header item in the adapter.
         */
        int getHeaderPositionForItem(int itemPosition);

        /**
         * Called by {@link StickyHeaderItemDecoration} to get layout resource id for the header item at specified
         * adapter's position.
         *
         * @param headerPosition Position of the header item in the adapter.
         * @return Layout resource id.
         */
        int getHeaderLayout(int headerPosition);

        /**
         * Called by {@link StickyHeaderItemDecoration} to setup the header view.
         *
         * @param header         Header view to set the data on.
         * @param headerPosition Position of the header item in the adapter.
         */
        void bindHeaderData(View header, int headerPosition);

        /**
         * Called by {@link StickyHeaderItemDecoration} to verify whether the item represents a header.
         *
         * @param itemPosition adapter item position.
         * @return {@code true} if the item at the specified adapter's position represents a header.
         */
        boolean isHeader(int itemPosition);
    }
}