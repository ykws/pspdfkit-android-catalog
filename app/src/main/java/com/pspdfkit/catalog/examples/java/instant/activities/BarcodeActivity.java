/*
 *   Copyright © 2018-2020 PSPDFKit GmbH. All rights reserved.
 *
 *   The PSPDFKit Sample applications are licensed with a modified BSD license.
 *   Please see License for details. This notice may not be removed from this file.
 */

package com.pspdfkit.catalog.examples.java.instant.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.pspdfkit.catalog.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import java.util.Arrays;

import static android.Manifest.permission.CAMERA;
import static com.pspdfkit.example.utils.Utils.dpToPx;
import static com.pspdfkit.example.utils.Utils.getThemeColor;

/**
 * Provides live QR code scanning. Start this activity for result and it will retrieve first recognized
 * encoded data with the BARCODE_ENCODED_KEY. Requires Manifest.permission.CAMERA.
 */
public class BarcodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    /**
     * This code should be used for receiving result of ActivityCompat.requestPermissions
     * for Manifest.permission.CAMERA.
     */
    private static final int ASK_FOR_CAMERA_PERMISSION_REQUEST_CODE = 1;
    public static final int BARCODE_RESULT_REQUEST_CODE = 2;
    public static final String BARCODE_ENCODED_KEY = "BARCODE_ENCODED_KEY";
    /**
     * UI parameters for barcode scanner input window (to hover over the QR code)
     */
    private static final int BORDER_LINE_LENGTH_DP = 60;
    private static final int BORDER_STROKE_WIDTH_DP = 5;
    private static final int BORDER_CORNER_RADIUS_DP = 5;

    /** Indicates that we are waiting for onRequestPermissionsResult. */
    private boolean waitingForPermissions = false;

    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        initScannerView();

        if (checkRequiredPermissions()) {
            waitingForPermissions = false;
        }
    }

    private void initScannerView() {
        scannerView.setFormats(Arrays.asList(BarcodeFormat.AZTEC, BarcodeFormat.QR_CODE));
        scannerView.setLaserEnabled(false);
        scannerView.setMaskColor(Color.TRANSPARENT);
        scannerView.setBorderColor(getThemeColor(this, R.attr.colorPrimary, R.color.color_primary));
        scannerView.setSquareViewFinder(true);
        scannerView.setBorderLineLength(dpToPx(this, BORDER_LINE_LENGTH_DP));
        scannerView.setBorderStrokeWidth(dpToPx(this, BORDER_STROKE_WIDTH_DP));
        scannerView.setIsBorderCornerRounded(true);
        scannerView.setBorderCornerRadius(dpToPx(this, BORDER_CORNER_RADIUS_DP));
    }

    /**
     * @return True when permission required for handling request with code `requestCode` has already been granted.
     */
    private boolean checkRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_DENIED) {
            if (!waitingForPermissions) {
                waitingForPermissions = true;
                requestPermissions(new String[]{CAMERA}, ASK_FOR_CAMERA_PERMISSION_REQUEST_CODE);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        waitingForPermissions = false;

        if (requestCode == ASK_FOR_CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("Camera Permission Granted");
            } else {
                showToast("Camera Permission Denied");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(CAMERA)) {
                        showPermissionRequiredDialog();
                    } else {
                        setResultCanceledAndFinish();
                    }
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showPermissionRequiredDialog() {
        new AlertDialog.Builder(this)
            .setMessage("You need to grant Camera Permission to scan QR code")
            .setPositiveButton(
                getString(android.R.string.ok),
                (dialogInterface, i) -> requestPermissions(
                    new String[]{CAMERA},
                    ASK_FOR_CAMERA_PERMISSION_REQUEST_CODE
                )
            )
            .setNegativeButton(
                android.R.string.cancel,
                (dialogInterface, i) -> setResultCanceledAndFinish()
            )
            .create()
            .show();
    }

    private void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private void setResultCanceledAndFinish() {
        setResult(Activity.RESULT_CANCELED, new Intent());
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        if (rawResult == null) {
            setResult(Activity.RESULT_CANCELED);
        } else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(BARCODE_ENCODED_KEY, rawResult.getText());
            setResult(Activity.RESULT_OK, returnIntent);
        }

        finish();
    }
}
