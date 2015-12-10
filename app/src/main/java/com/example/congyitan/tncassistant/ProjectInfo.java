package com.example.congyitan.tncassistant;


import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ProjectInfo extends AppCompatActivity {

    //for Log.d ; debugging
    private static final String TAG = "ProjectInfo";

    EditText blkno;
    EditText postalcode;
    EditText streetname;

    Integer mPostalcode;
    String mBlkno;
    String mStreetname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle myData = getIntent().getExtras();
        if (myData != null)
            mPostalcode = myData.getInt("postalcode");

        //inflate layout
        setContentView(R.layout.activity_project_info);

        blkno = (EditText) findViewById(R.id.blknoET);
        postalcode = (EditText) findViewById(R.id.postalcodeET);
        streetname = (EditText ) findViewById(R.id.streetnameET);

        //Set toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_file);
            getSupportActionBar().setTitle(R.string.project_info);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        updateTextFields();
    }

    //triggered by onClick: in xml. This generates the choices in the Spinner
    public void onPhaseRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        Spinner tcSpinner = (Spinner) findViewById(R.id.tc_spinner);

        // Check which radio button was clicked
        switch(view.getId()) {

            //generate the spinner for the radio button that was clicked
            case R.id.phase5:
                if (checked) {
                    // Create an ArrayAdapter using the string array and a default spinner layout
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                            R.array.phase5_array, android.R.layout.simple_spinner_item);
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    tcSpinner.setAdapter(adapter);

                    break;
                }

            case R.id.phase6:
                if (checked) {
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.phase6_array, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                tcSpinner.setAdapter(adapter);

                break;
            }

            case R.id.sn1:
                if (checked) {
                    // Create an ArrayAdapter using the string array and a default spinner layout
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                            R.array.sn1_array, android.R.layout.simple_spinner_item);
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    tcSpinner.setAdapter(adapter);

                    break;
                }
        }
    }

    //Save data keyed into the text file
    @Override
    public void onBackPressed() {

        Log.d(TAG, "I'm here in ProjectInfo's onBackPressed");

        //get the text from the EditText boxes
        mBlkno = blkno.getText().toString();
        mStreetname = streetname.getText().toString();

        //get the file and directory
        File projectInfoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File projectInfoFile = new File(projectInfoDir, "TNCAssistant/" + mPostalcode + "/info.txt");

        //Write inputs into the txt file. We don't ever change the postal code
        try {
            FileWriter newFileWriter = new FileWriter(projectInfoFile);

            newFileWriter.write(mPostalcode + "\n");

            if (mBlkno != null)
                newFileWriter.write(mBlkno + "\n");
            else
                newFileWriter.write("\n");

            if (mStreetname != null)
                newFileWriter.write(mStreetname);
            else
                newFileWriter.write("\n");

            newFileWriter.flush();
            newFileWriter.close();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Log.d(TAG, "Error creating/writing file");
        }
    }

    //this method updates the EditText fields if a Project already exists
    private void updateTextFields () {

        //get the file and directory
        File projectInfoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File projectInfoFile = new File(projectInfoDir, "TNCAssistant/" + String.valueOf(mPostalcode) + "/info.txt");

        Log.d(TAG, "projectInfoFile is: " + projectInfoFile);

        try {
            BufferedReader buf = new BufferedReader(new FileReader(projectInfoFile));

            //read in postal code
            String tempString = buf.readLine();

            if (tempString != null) {
                postalcode.setText(tempString);
                tempString = null;
            }

            //read in blk no (if any)
            tempString = buf.readLine();

            if (tempString != null) {
                blkno.setText(tempString);
                tempString = null;
            }

            //read in streetname (if any)
            tempString = buf.readLine();

            if (tempString != null) {
                streetname.setText(tempString);
                tempString = null;
            }

            buf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState){
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "I'm here in ProjectInfo's onSaveInstanceState");

        if (mPostalcode != null)
            savedInstanceState.putInt("postalcode", mPostalcode);
        if (mBlkno != null)
            savedInstanceState.putString("blkno", mBlkno);
        if (streetname != null)
            savedInstanceState.putString("streetname", mStreetname);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d(TAG, "I'm here in ProjectInfo's onRestoreInstanceState");

        mPostalcode = savedInstanceState.getInt("postalcode");

        if (savedInstanceState.getString("blkno") != null)
            mBlkno = savedInstanceState.getString("blkno");

        if (savedInstanceState.getString("streetname") != null)
        mStreetname = savedInstanceState.getString("streetname");
    }
}
