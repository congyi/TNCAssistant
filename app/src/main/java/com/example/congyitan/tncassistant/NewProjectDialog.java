package com.example.congyitan.tncassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;


public class NewProjectDialog extends DialogFragment{

    //for Log.d ; debugging
    private static final String TAG = "NewProjectDialog";

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NewProjectDialogListener {
        void onDialogOK(Bundle myData);
        void onDialogCancel();
    }

    // Use this instance of the interface to deliver action events
    NewProjectDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NewProjectDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        int showError = getArguments().getInt("showerror");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder newProjectDialog = new AlertDialog.Builder(getActivity());

       //prevent dialog from closing
        setCancelable(false);

       //set dialog title
        newProjectDialog.setTitle(R.string.new_project_title);

        //inflate view so that findViewById on the next line works (is using final dangerous)
        View mView = View.inflate(getActivity(),R.layout.dialog_new_project, null);
        newProjectDialog.setView(mView);

        //get the EditText view object
        final EditText postalcodeET = (EditText) mView.findViewById(R.id.postalcodeET);

        if(showError == 1)
        {
            TextView showErrorText = (TextView) mView.findViewById(R.id.error_display);
            showErrorText.setText("Postal code must be 6 digits long");
        }

        if(showError == 2)
        {
            TextView showErrorText = (TextView) mView.findViewById(R.id.error_display);
            showErrorText.setText("Project already exists on device");
        }

        //set OK button
        newProjectDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Bundle mData = new Bundle();

                //get the text that the user just input
                String postalcode = postalcodeET.getText().toString();

                Log.d(TAG, "User input postal code: " + postalcode); //for debugging

                //put it in a bundle to be passed back
                mData.putString("postalcode", postalcode);
                mListener.onDialogOK(mData);
            }
        });

        //set cancel button
        newProjectDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onDialogCancel();
            }
        });

        // Create the AlertDialog object and return it
        return newProjectDialog.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "I'm here in NewProjectDialog's onActivityCreated");

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}