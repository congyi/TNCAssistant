package com.example.congyitan.tncassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class NewProjectDialog extends DialogFragment{

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NewProjectDialogListener {
        public void onDialogOK(String projectTitle);
        public void onDialogCancel(DialogFragment dialog);
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //inflate view so that findViewbyId on the next line works
        View view = View.inflate(getActivity(),R.layout.new_project_dialog, null);
        //Link tempEdit object to the text-edit box so we can retrieve data from it below upon button click
        final EditText tempEdit = (EditText)view.findViewById(R.id.project_title);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder newProjectDialog = new AlertDialog.Builder(getActivity());

       //prevent dialog from closing
        setCancelable(false);

       //set dialog title
        newProjectDialog.setTitle(R.string.new_project_title);

        //set the view
        newProjectDialog.setView(R.layout.new_project_dialog);

        //set OK button
        newProjectDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               String projectTitle = tempEdit.getText().toString();
                mListener.onDialogOK(projectTitle);
            }
        });

        //set cancel button
        newProjectDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onDialogCancel(NewProjectDialog.this);
            }
        });

        // Create the AlertDialog object and return it
        return newProjectDialog.create();
    }
}