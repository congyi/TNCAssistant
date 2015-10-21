package com.example.congyitan.tncassistant;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class NewProjectDialog extends DialogFragment implements View.OnClickListener {

    Button ok, cancel;
    Communicator communicator;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //create dialog title
        getDialog().setTitle("Name this project");

        View view = inflater.inflate(R.layout.new_project_dialog, null);

        ok = (Button) view.findViewById(R.id.ok);
        cancel = (Button) view.findViewById(R.id.cancel);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

        setCancelable(false);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ok){
            communicator.onDialogMessage("OK was clicked");
            dismiss();
        }

        else{
            communicator.onDialogMessage("Cancel was clicked");
            dismiss();
        }
    }

    interface Communicator
    {
        public void onDialogMessage (String message);
    }
}