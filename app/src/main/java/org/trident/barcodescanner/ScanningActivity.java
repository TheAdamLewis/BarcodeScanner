/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trident.barcodescanner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class ScanningActivity extends Activity implements View.OnClickListener {

    // URL Constants
    private static final String URL_AUTHORITY = "https://attendence-program.000webhostapp.com/AttendenceProgram/QRAttendence.html";
    private static final String KEY_ID = "ID";
    private static final String KEY_ACTION = "ACTION";
    private static final String VALUE_SIGNIN = "SIGNIN";
    private static final String VALUE_SIGNOUT = "SIGNOUT";

    // use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private TextView timeMessage;
    private TextView barcodeValue;

    private Date m_time;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("dd MMM YYYY hh:mm");

    private boolean m_signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        m_signIn = getIntent().getBooleanExtra(MainActivity.EXTRA_SIGN_IN, false);
        m_time = new Date();
        timeMessage = (TextView)findViewById(R.id.time_string);
        barcodeValue = (TextView)findViewById(R.id.barcode_value);

        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);

        timeMessage.setText("Time: " + TIME_FORMAT.format(m_time));

        findViewById(R.id.read_barcode).setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(BarcodeCaptureActivity.AutoCapture, true);

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }

    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    timeMessage.setText(R.string.barcode_success);
                    barcodeValue.setText(barcode.displayValue);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue + " - time: " + TIME_FORMAT.format(m_time));

                    Toast.makeText(ScanningActivity.this, "Barcode Scanned: " + barcode.displayValue, Toast.LENGTH_LONG).show();
                } else {
                    timeMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
                //open new activity

            } else {
                timeMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String buildURL(String ID, String p_action)
    {
        String action = p_action.equals("SIGNIN") ? VALUE_SIGNIN : VALUE_SIGNOUT;

        StringBuilder builder = new StringBuilder(URL_AUTHORITY)
                .append("?")
                .append(KEY_ID)
                .append("=")
                .append(ID)
                .append("&")
                .append(KEY_ACTION)
                .append("=")
                .append(action);

        Log.i(TAG, "PINGING: " + builder.toString());

        return builder.toString();
    }
}
