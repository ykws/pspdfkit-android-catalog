package com.pspdfkit.catalog.examples.java.activities;

import android.util.Log;

import com.pspdfkit.ui.PdfActivity;

import java.io.IOException;

public class BasicExampleActivity extends PdfActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        save();
    }

    private void save() {
        try {
            boolean result = getDocument().saveIfModified();
            Log.d("saveIfModified", "result  = " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
