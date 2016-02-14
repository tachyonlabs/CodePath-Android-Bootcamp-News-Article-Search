package com.tachyonlabs.nytimessearch.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class AlertDialogFragment extends android.support.v4.app.DialogFragment {
    // based on http://guides.codepath.com/android/Using-DialogFragment
    public AlertDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static AlertDialogFragment newInstance(String title, String message) {
        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
            }
        });

        return alertDialogBuilder.create();
    }
}
