/*
 *   Copyright © 2019-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.ui;

import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.utils.ExamplesFactoryKt;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.pspdfkit.catalog.ui.StickyHeaderItemDecoration.StickyHeaderController;

/**
 * Example list adapter that recycles the views and shows sections and example items. Used by {@link
 * PSPDFCatalogActivity} to show the full list of examples.
 */
public class ExampleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyHeaderController {

    /** View type for the section name. */
    private static final int VIEW_TYPE_SECTION = 0;
    /** View type for the example item containing a title and a description. */
    private static final int VIEW_TYPE_EXAMPLE = 1;

    @NonNull private final OnExampleClickListener listener;

    /** List containing sections only. */
    @NonNull private final List<PSPDFExample.Section> sections = new ArrayList<>();
    /** Full list flattened containing sections and example items. */
    @NonNull List<Object> flattenedItems = new ArrayList<>();

    /** The current search query or null. */
    @Nullable private String query;
    @Nullable private PSPDFExample.ExampleLanguage filteredLanguage = PSPDFExample.ExampleLanguage.KOTLIN;

    ExampleListAdapter(@NonNull final OnExampleClickListener clickListener) {
        this.listener = clickListener;
    }

    void setSections(@NonNull final List<PSPDFExample.Section> sections) {
        this.sections.clear();
        this.sections.addAll(sections);
        flattenedItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return ensurePrepared().size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SECTION) {
            return new SectionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false));
        } else {
            return new ExampleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_example, parent, false), listener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_SECTION) {
            PSPDFExample.Section section = (PSPDFExample.Section) flattenedItems.get(position);
            SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
            sectionViewHolder.sectionNameTextView.setText(section.getName());
        } else if (viewType == VIEW_TYPE_EXAMPLE) {
            PSPDFExample example = (PSPDFExample) flattenedItems.get(position);
            ExampleViewHolder exampleViewHolder = (ExampleViewHolder) holder;
            // Populate the list item
            exampleViewHolder.exampleTitleTextView.setText(example.title);
            exampleViewHolder.exampleDescriptionTextView.setText(example.description);
            if (example.getExampleLanguage() == PSPDFExample.ExampleLanguage.KOTLIN) {
                exampleViewHolder.kotlinBadge.setVisibility(View.VISIBLE);
                exampleViewHolder.javaBadge.setVisibility(View.GONE);
            } else {
                exampleViewHolder.kotlinBadge.setVisibility(View.GONE);
                exampleViewHolder.javaBadge.setVisibility(View.VISIBLE);
            }

            exampleViewHolder.example = example;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = flattenedItems.get(position);
        if (item instanceof PSPDFExample.Section) {
            return VIEW_TYPE_SECTION;
        } else {
            return VIEW_TYPE_EXAMPLE;
        }
    }

    void setQuery(@NonNull final String query) {
        this.query = query.toLowerCase(Locale.getDefault());
        // We perform the filtering while building the list of items to be displayed.
        flattenedItems.clear();
        notifyDataSetChanged();
    }

    /**
     * Filters only examples in a certain language.
     *
     * @param filteredLanguage Language of examples to filter for or {@code null} to reset the
     *                         filter.
     */
    void setFilteredLanguage(@Nullable final PSPDFExample.ExampleLanguage filteredLanguage) {
        this.filteredLanguage = filteredLanguage;
        // We perform the filtering while building the list of items to be displayed.
        flattenedItems.clear();
        notifyDataSetChanged();
    }

    @NonNull
    private List<Object> ensurePrepared() {
        if (flattenedItems.isEmpty()) {
            for (int sectionIndex = 0; sectionIndex < sections.size(); ++sectionIndex) {
                final PSPDFExample.Section section = sections.get(sectionIndex);
                int startIndex = flattenedItems.size();

                int itemsAdded = 0;
                for (PSPDFExample example : section) {
                    if (isExampleIncludedInFilter(example)) {
                        flattenedItems.add(example);
                        itemsAdded++;
                    }
                }

                // We only want to display the section if at least one item in it is displayed.
                if (itemsAdded > 0) {
                    flattenedItems.add(startIndex, section);
                }
            }
        }
        return flattenedItems;
    }

    private boolean isExampleIncludedInFilter(@NonNull final PSPDFExample example) {
        // First check the search query filter.
        if (TextUtils.isEmpty(query) ||
            example.title.toLowerCase(Locale.getDefault()).contains(query) ||
            example.description.toLowerCase(Locale.getDefault()).contains(query)) {

            // Then continue with the language filter.
            return filteredLanguage == example.getExampleLanguage() ||
                !ExamplesFactoryKt.isAvailableInBothLanguages(example);
        }
        return false;
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = RecyclerView.NO_POSITION;
        do {
            if (this.isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            itemPosition -= 1;
        } while (itemPosition >= 0);
        return headerPosition;
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        return R.layout.item_section;
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {
        PSPDFExample.Section section = (PSPDFExample.Section) flattenedItems.get(headerPosition);
        TextView textView = header.findViewById(R.id.sectionNameTextView);
        textView.setText(section.getName());
    }

    @Override
    public boolean isHeader(int itemPosition) {
        return getItemViewType(itemPosition) == VIEW_TYPE_SECTION;
    }

    private static class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView sectionNameTextView;

        SectionViewHolder(@NonNull final View view) {
            super(view);
            sectionNameTextView = view.findViewById(R.id.sectionNameTextView);
        }
    }

    private static class ExampleViewHolder extends RecyclerView.ViewHolder {

        TextView exampleTitleTextView;
        TextView exampleDescriptionTextView;
        PSPDFExample example;

        View javaBadge;
        View kotlinBadge;

        ExampleViewHolder(@NonNull final View view,
                          @NonNull final ExampleListAdapter.OnExampleClickListener listener) {
            super(view);
            exampleTitleTextView = view.findViewById(R.id.exampleTitleTextView);
            exampleDescriptionTextView = view.findViewById(R.id.exampleDescriptionTextView);
            kotlinBadge = view.findViewById(R.id.kotlinBadge);
            javaBadge = view.findViewById(R.id.javaBadge);

            // Debounce clicks on examples by 1000ms because example preparation could take
            // some time and double clicks could start the example 2 times.
            view.setOnClickListener(new DebouncedOnClickListener(v -> {
                if (example != null) {
                    listener.onExampleClick(v, example);
                }
            }));

            view.setOnLongClickListener(v -> {
                if (example != null) {
                    listener.onExampleLongClick(v, example);
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * Listens for a choice on {@link PSPDFExample}s from list.
     */
    public interface OnExampleClickListener {
        /**
         * Called when a particular {@link PSPDFExample} was chosen from list.
         *
         * @param view    received click {@link View}
         * @param example {@link PSPDFExample} chosen from list
         */
        void onExampleClick(View view, @NonNull final PSPDFExample example);

        /**
         * Called when a particular {@link PSPDFExample} was long pressed.
         *
         * @param view    received click {@link View}.
         * @param example {@link PSPDFExample} chosen from list.
         */
        void onExampleLongClick(View view, @NonNull final PSPDFExample example);
    }

    /**
     * This is a simple delegate for {@link OnClickListener} that debounces click events.
     */
    private static class DebouncedOnClickListener implements OnClickListener {

        private final static long DEBOUNCE_DELAY_MS = 1000;

        @NonNull private final OnClickListener listener;
        private long lastClickTime;

        private DebouncedOnClickListener(@NonNull final OnClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(@NonNull final View v) {
            if (SystemClock.elapsedRealtime() - lastClickTime < DEBOUNCE_DELAY_MS) return;
            lastClickTime = SystemClock.elapsedRealtime();
            listener.onClick(v);
        }
    }

}