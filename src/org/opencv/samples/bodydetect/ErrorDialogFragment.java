package org.opencv.samples.bodydetect;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.gms.common.GooglePlayServicesUtil;

/* A fragment to display an error dialog */
public class ErrorDialogFragment extends DialogFragment {
	
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    
    public ErrorDialogFragment() { }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the error code and retrieve the appropriate dialog
        int errorCode = this.getArguments().getInt(DIALOG_ERROR);
        return GooglePlayServicesUtil.getErrorDialog(errorCode,
                this.getActivity(), REQUEST_RESOLVE_ERROR);
    }

    
    @Override
    public void onDismiss(DialogInterface dialog) {
        ((FdActivity)getActivity()).onDialogDismissed();
    }
}
