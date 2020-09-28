/*
 *   Copyright © 2017-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.instant.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pspdfkit.catalog.examples.java.instant.api.InstantExampleDocumentDescriptor;
import com.pspdfkit.catalog.examples.java.instant.api.WebPreviewClient;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.catalog.R;
import com.pspdfkit.instant.client.InstantClient;
import com.pspdfkit.instant.ui.InstantPdfActivity;
import com.pspdfkit.instant.ui.InstantPdfActivityIntentBuilder;

import java.net.UnknownHostException;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import retrofit2.HttpException;

/**
 * Allows to connect to the example Instant server.
 */
public class InstantExampleConnectionActivity extends AppCompatActivity {

    /** Name of the extra in activity intent holding {@link PdfActivityConfiguration} that should be passed to created {@link InstantPdfActivity}. */
    public static final String CONFIGURATION_ARG = "InstantExampleConnectionActivity.PSPDFKitConfiguration";

    /** Configuration that will be passed to created {@link InstantExampleActivity}. */
    private PdfActivityConfiguration configuration;

    /** Client for connecting to PSPDFKit web example client. */
    private final WebPreviewClient apiClient = new WebPreviewClient();

    /** Disposable for the web preview server connections. */
    @Nullable private Disposable connectionDisposable;

    /**
     * Consumer for {@link WebPreviewClient} async methods.
     */
    private BiConsumer<InstantExampleDocumentDescriptor, Throwable> apiConsumer = (instantExampleDocumentDescriptor, throwable) -> {
        if (throwable != null) {
            handleError(throwable);
        } else {
            showInstantDocument(instantExampleDocumentDescriptor);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_instant_connect);

        // Extract PdfActivity configuration from extras.
        configuration = getIntent().getParcelableExtra(CONFIGURATION_ARG);
        if (configuration == null) {
            throw new IllegalStateException("InstantExampleConnectionActivity was not initialized with proper arguments: Missing configuration extra!");
        }

        // Configure toolbar.
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configure new document button.
        findViewById(R.id.button_new_document).setOnClickListener(v -> createNewDocument());

        // Configure edit document button.
        final Button scanQrCodeButton = findViewById(R.id.scan_qr_code);
        scanQrCodeButton.setOnClickListener(v -> startActivityForResult(new Intent(this, BarcodeActivity.class), BarcodeActivity.BARCODE_RESULT_REQUEST_CODE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(connectionDisposable != null) {
            connectionDisposable.dispose();
            connectionDisposable = null;
        }
    }

    private void createNewDocument() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, getString(R.string.instant_creating), true, false);
        if(connectionDisposable != null) connectionDisposable.dispose();
        connectionDisposable = apiClient.createNewDocument()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(throwable -> progressDialog.dismiss())
            .subscribe(apiConsumer);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BarcodeActivity.BARCODE_RESULT_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
            && data != null
            && data.getExtras() != null) {
            editDocument(data.getExtras().getString(BarcodeActivity.BARCODE_ENCODED_KEY));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void editDocument(final String url) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, getString(R.string.instant_connecting), true, false);

        if (connectionDisposable != null) connectionDisposable.dispose();
        connectionDisposable = apiClient.getDocument(url)
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext(throwable -> handleHttpException(throwable, url))
            .doOnError(throwable -> progressDialog.dismiss())
            .subscribe(apiConsumer);
    }

    private Single<InstantExampleDocumentDescriptor> handleHttpException(final Throwable exception, final String url) {
        if (exception instanceof HttpException) {
            HttpException httpException = (HttpException) exception;
            if (httpException.code() == 401) {
                // We need a basic auth request here.
                // Then we'll try the request again.
                return performBasicAuth()
                    .andThen(apiClient.getDocument(url))
                    .observeOn(AndroidSchedulers.mainThread());
            }
        }

        return Single.error(exception);
    }

    /** Asks the user for basic auth credentials and sets them on the apiClient. */
    private Completable performBasicAuth() {
        return Completable.create(emitter -> {
            @SuppressLint("InflateParams")
            final View basicAuthView = LayoutInflater.from(this).inflate(R.layout.dialog_basic_auth, null, false);
            final EditText username = basicAuthView.findViewById(R.id.username);
            final EditText password = basicAuthView.findViewById(R.id.password);

            new AlertDialog.Builder(this)
                .setTitle(R.string.instant_authentication_required)
                .setView(basicAuthView)
                .setPositiveButton(R.string.instant_login, (dialog, which) -> {
                    apiClient.setBasicAuthCredentials(username.getText().toString(), password.getText().toString());
                    emitter.onComplete();
                })
                .setOnCancelListener(dialog -> emitter.onError(new Exception("User cancelled basic auth.")))
                .show();
        });
    }

    private void showInstantDocument(@NonNull InstantExampleDocumentDescriptor descriptor) {
        // Clear the Instant client cache first.
        InstantClient.create(InstantExampleConnectionActivity.this, descriptor.serverUrl).removeLocalStorage();

        InstantPdfActivityIntentBuilder builder = InstantPdfActivityIntentBuilder.fromInstantDocument(
            InstantExampleConnectionActivity.this,
            descriptor.serverUrl,
            descriptor.jwt
        );
        Intent intent = builder
            .configuration(configuration)
            .activityClass(InstantExampleActivity.class)
            .build();
        intent.putExtra(InstantExampleActivity.DOCUMENT_DESCRIPTOR, descriptor);
        startActivity(intent);

        finish();
    }

    private void handleError(@NonNull Throwable throwable) {
        int errorText = R.string.instant_error_something_went_wrong;
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            int httpCode = httpException.code();
            if (httpCode >= 400 && httpCode < 500) {
                errorText = R.string.instant_error_invalid_id;
            } else if (httpCode >= 500) {
                errorText = R.string.instant_error_server_error;
            }
        } else if (throwable instanceof UnknownHostException) {
            errorText = R.string.instant_error_no_connection;
        }
        Toast.makeText(
            InstantExampleConnectionActivity.this,
            getString(R.string.instant_error_connection_failed, getString(errorText)),
            Toast.LENGTH_LONG
        ).show();
    }
}
