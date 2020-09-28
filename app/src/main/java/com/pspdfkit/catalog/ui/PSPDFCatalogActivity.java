/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pspdfkit.PSPDFKit;
import com.pspdfkit.catalog.PSPDFExample;
import com.pspdfkit.catalog.R;
import com.pspdfkit.catalog.utils.ExamplesFactoryKt;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.configuration.policy.DefaultApplicationPolicy;
import com.pspdfkit.document.download.DownloadJob;
import com.pspdfkit.document.download.DownloadProgressFragment;
import com.pspdfkit.document.download.DownloadRequest;
import com.pspdfkit.example.ui.CatalogActivityWithSettings;
import com.pspdfkit.example.ui.CatalogPreferencesFragment;
import com.pspdfkit.example.utils.Utils;
import com.pspdfkit.signatures.SignatureManager;
import com.pspdfkit.ui.PdfActivityIntentBuilder;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

/**
 * Base for main activity of the catalog app. Lists all available examples.
 */
public abstract class PSPDFCatalogActivity extends CatalogActivityWithSettings implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int REQUEST_ASK_FOR_PERMISSION = 1;

    private static final String URI_SCHEME_FILE = "file";

    private static final String IS_WAITING_FOR_PERMISSION_RESULT = "PSPDFKit.MainActivity.waitingForResult";

    private static final String DOWNLOAD_PROGRESS_FRAGMENT = "DownloadProgressFragment";

    /** Optional extra with class name of the example that should be launched on when starting the activity. */
    public static final String EXTRA_LAUNCH_EXAMPLE = "LAUNCH_EXAMPLE";

    private RecyclerView examplesRecyclerView;
    private ExampleListAdapter exampleListAdapter;

    private boolean waitingForPermission;
    private SearchView searchView;

    @Nullable private List<PSPDFExample.Section> examples;

    public PSPDFCatalogActivity() {
        super(R.layout.activity_main_catalog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the activity was recreated, and see if the user already requested external storage permissions.
        if (savedInstanceState != null) {
            waitingForPermission = savedInstanceState.getBoolean(IS_WAITING_FOR_PERMISSION_RESULT, false);
        }

        final ExampleListAdapter.OnExampleClickListener clickListener = new ExampleListAdapter.OnExampleClickListener() {
            @Override
            public void onExampleClick(View view, @NonNull PSPDFExample example) {
                example.launchExample(PSPDFCatalogActivity.this, getConfiguration());
            }

            @Override
            public void onExampleLongClick(View view, @NonNull PSPDFExample example) {
                Toast.makeText(view.getContext(), example.getExampleName(), Toast.LENGTH_SHORT).show();
            }
        };

        examples = ExamplesFactoryKt.addExamplesForSupportedLanguagesIfNotPresent(this, getExamples());

        // Adapter for showing all available examples in a list. The callback method then launches
        // the clicked example.
        exampleListAdapter = new ExampleListAdapter(clickListener);
        exampleListAdapter.setSections(examples);

        // Setup the list view.
        examplesRecyclerView = findViewById(R.id.examples_recycler_view);
        examplesRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        StickyHeaderItemDecoration stickyHeaderItemDecoration = new StickyHeaderItemDecoration(this, exampleListAdapter);
        examplesRecyclerView.addItemDecoration(stickyHeaderItemDecoration);

        examplesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        examplesRecyclerView.setAdapter(exampleListAdapter);

        // Update preferred examples language and register language change preference listener.
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        updatePreferredLanguage(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // Launch specific example if launch example extra was provided.
        if (getIntent() != null && getIntent().hasExtra(EXTRA_LAUNCH_EXAMPLE)) {
            // When launching examples directly, ensure that PSPDFKit has completed initialization.
            // This method will block until PSPDFKit finishes initialization.
            PSPDFKit.isInitialized();
            launchExampleWithClassName(getIntent().getStringExtra(EXTRA_LAUNCH_EXAMPLE));
        }
    }

    /**
     * You can launch specific example by providing LAUNCH_EXAMPLE extra to launch intent. You'll need to provide:
     * <pre>-e LAUNCH_EXAMPLE [example_class_name]</pre> to either {@code adb shell am} or to Android Studio run
     * configuration.
     * <p>
     * For example, to start basic example: <pre>-e LAUNCH_EXAMPLE com.pspdfkit.example.examples.java.BasicExample</pre>
     */
    private void launchExampleWithClassName(@NonNull final String exampleClassName) {
        try {
            final Class<?> exampleClass = Class.forName(exampleClassName);
            if (PSPDFExample.class.isAssignableFrom(exampleClass)) {
                final Constructor<?> constructor = exampleClass.getConstructor(Context.class);
                ((PSPDFExample) constructor.newInstance(this)).launchExample(this, getConfiguration());
            } else {
                throw new IllegalArgumentException("Example class " + exampleClassName + " must be assignable to PSPDFExample");
            }
        } catch (Throwable ex) {
            throw new IllegalArgumentException("Can't launch example with class name " + exampleClassName, ex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);

        MenuItem searchAction = menu.findItem(R.id.search);
        final MenuItem settingsAction = menu.findItem(R.id.action_settings);

        final Drawable searchIcon = DrawableCompat.wrap(searchAction.getIcon());
        DrawableCompat.setTint(searchIcon, Color.WHITE);
        searchAction.setIcon(searchIcon);

        searchAction.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // On tablets the settings pane is always visible, so the settings action is null.
                if (settingsAction != null) {
                    settingsAction.setVisible(false);
                    settingsAction.setEnabled(false);
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (settingsAction != null) {
                    settingsAction.setVisible(true);
                    settingsAction.setEnabled(true);
                }
                return true;
            }
        });

        searchView = (SearchView) searchAction.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();
        searchView.setQueryHint("Search Examples...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // List will always contain at least one header followed by an example item.
                if (exampleListAdapter.flattenedItems.size() > 1) {
                    final View exampleItem = examplesRecyclerView.getChildAt(1);
                    exampleItem.requestFocus();
                    Utils.hideKeyboard(exampleItem);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                exampleListAdapter.setQuery(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchView != null) {
            EditText editText = searchView.findViewById(com.pspdfkit.R.id.search_src_text);
            // This will prevent the activity from being leaked.
            editText.setCursorVisible(false);

            searchView.setOnQueryTextListener(null);
            searchView = null;
        }

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

        // Perform examples cleanup.
        if (examples != null) {
            for (PSPDFExample.Section section : examples) {
                for (PSPDFExample example : section) {
                    example.onDestroy();
                }
            }
        }
    }

    @NonNull
    protected abstract List<PSPDFExample.Section> getExamples();

    @Override
    protected void onResume() {
        super.onResume();

        final Intent intent = getIntent();
        if (waitingForPermission) return;
        findViewById(R.id.loading_progress_bar).setVisibility(View.GONE);
        if (intent != null && (Intent.ACTION_VIEW.equals(intent.getAction()) || Intent.ACTION_EDIT.equals(intent.getAction()))) {
            // When opening local files outside of android's Storage Access Framework ask for permissions to external storage.
            if (intent.getData() != null &&
                URI_SCHEME_FILE.equals(intent.getData().getScheme()) &&
                !Utils.requestExternalStorageRwPermission(this, REQUEST_ASK_FOR_PERMISSION)) {
                waitingForPermission = true;
                return;
            }
            // We already have read/write permissions to external storage or don't need them.
            showDocument(intent);
        }

        // Since loading of certificates requires file I/O and also isInitialized() might block if initialization is
        // still in progress, calls to these methods are performed on a background thread.
        Completable
            .fromAction(() -> {
                if (PSPDFKit.isInitialized()) {
                    // Reset any custom policy that might exist.
                    PSPDFKit.setApplicationPolicy(new DefaultApplicationPolicy());

                    // Remove any signers and certificates that examples might have added.
                    SignatureManager.clearSigners();
                    SignatureManager.addTrustedCertificatesFromOs();
                    SignatureManager.addTrustedCertificatesFromAdobeCa();
                }
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (waitingForPermission && requestCode == REQUEST_ASK_FOR_PERMISSION) {
            waitingForPermission = false;
            if (getIntent() != null) {
                // We attempt to open document after permissions have been requested.
                showDocument(getIntent());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);

        // Retain if we are currently waiting for a result of permission request so we don't set it off twice by accident.
        outState.putBoolean(IS_WAITING_FOR_PERMISSION_RESULT, waitingForPermission);
    }

    private void showDocument(@NonNull final Intent intent) {
        final Uri uri = intent.getData();
        if (uri != null) {
            // If the URI can be resolved to a local filesystem path, we can directly access it for best performance.
            if (PSPDFKit.isLocalFileUri(this, uri)) {
                openDocumentAndFinishActivity(uri);
            }
            // All other URIs will be downloaded to the filesystem before opening them. While this is not necessary for all URI types
            // (e.g. content:// URIs could be opened directly as well) it ensures maximum compatibility with arbitrary sources as well as better performance.
            else {
                // Find the DownloadProgressFragment for showing download progress, or create a new one.
                DownloadProgressFragment downloadFragment = (DownloadProgressFragment) getSupportFragmentManager().findFragmentByTag(DOWNLOAD_PROGRESS_FRAGMENT);
                if (downloadFragment == null) {
                    final DownloadRequest request;
                    try {
                        request = new DownloadRequest.Builder(this).uri(uri).build();
                    } catch (Exception ex) {
                        showDownloadErrorAndFinishActivity();
                        return;
                    }

                    final DownloadJob job = DownloadJob.startDownload(request);
                    downloadFragment = new DownloadProgressFragment();
                    downloadFragment.show(getSupportFragmentManager(), DOWNLOAD_PROGRESS_FRAGMENT);
                    downloadFragment.setJob(job);
                }

                // Once the download is complete we launch the PdfActivity from the downloaded file.
                downloadFragment.getJob().setProgressListener(new DownloadJob.ProgressListenerAdapter() {
                    @Override
                    public void onComplete(@NonNull File output) {
                        openDocumentAndFinishActivity(Uri.fromFile(output));
                    }
                });
            }
        }
    }

    private void openDocumentAndFinishActivity(@NonNull final Uri uri) {
        final PdfActivityConfiguration configuration = getConfiguration().build();
        final Intent intent = PdfActivityIntentBuilder.fromUri(PSPDFCatalogActivity.this, uri)
            .configuration(configuration)
            .build();
        startActivity(intent);
        finish();
    }

    private void showDownloadErrorAndFinishActivity() {
        new AlertDialog.Builder(this)
            .setTitle("Download error")
            .setMessage("PSPDFKit could not download the PDF file from the given URL.")
            .setNeutralButton("Exit catalog app", (dialog, which) -> dialog.dismiss())
            .setOnDismissListener(dialog -> finish())
            .setCancelable(false)
            .show();
    }

    @Override
    public void onSharedPreferenceChanged(@NonNull final SharedPreferences sharedPreferences,
                                          @NonNull final String key) {
        if (key.equals(CatalogPreferencesFragment.PREF_PREFERRED_LANGUAGE)) {
            updatePreferredLanguage(sharedPreferences);
        }
    }

    private void updatePreferredLanguage(@NonNull final SharedPreferences sharedPreferences) {
        final String preferredLanguageString = sharedPreferences.getString(CatalogPreferencesFragment.PREF_PREFERRED_LANGUAGE, null);
        PSPDFExample.ExampleLanguage preferredLanguage = PSPDFExample.ExampleLanguage.KOTLIN;
        if (preferredLanguageString != null && preferredLanguageString.equals(getString(R.string.language_java))) {
            preferredLanguage = PSPDFExample.ExampleLanguage.JAVA;
        }
        if (exampleListAdapter != null) {
            exampleListAdapter.setFilteredLanguage(preferredLanguage);
        }
    }
}
