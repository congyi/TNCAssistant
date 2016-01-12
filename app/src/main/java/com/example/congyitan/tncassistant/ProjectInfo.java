package com.example.congyitan.tncassistant;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

    //all the input boxes
    EditText blkno, postalcode, streetname;
    Spinner tcspinner;

    //variables to store data
    String mPostalcode, mBlkno, mStreetname, mProjectPhase, mTownCouncil;
    Bundle mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "I'm here in ProjectInfo's onCreate");

        //retrieve intent data from calling activity
        mData = getIntent().getExtras();
        mPostalcode = mData.getString("postalcode");
        Log.d(TAG, "Postal code in Bundle mData is: " + String.valueOf(mPostalcode));

        //inflate layout
        setContentView(R.layout.activity_project_info);

        //initialise views
        blkno = (EditText)findViewById(R.id.blknoET);
        postalcode = (EditText)findViewById(R.id.postalcodeET);
        streetname = (EditText)findViewById(R.id.streetnameET);
        tcspinner = (Spinner)findViewById(R.id.tc_spinner);

        //Set toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_file);
            getSupportActionBar().setTitle(R.string.project_info);
        }

        updateTextFields();
    }

    //triggered by onClick: in xml. This generates the choices in the Spinner
    public void onPhaseRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

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
                    tcspinner.setAdapter(adapter);
                    mProjectPhase = "Phase 5 - 5MW HDB Project";
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
                    tcspinner.setAdapter(adapter);
                    mProjectPhase = "Phase 6 - 20MW HDB Project";
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
                    tcspinner.setAdapter(adapter);
                    mProjectPhase = "SolarNova Phase 1";
                    break;
                }
        }
    }

    //Save data keyed into the text file
    @Override
    public void onBackPressed() {

        Log.d(TAG, "I'm here in ProjectInfo's onBackPressed");

        //gather all the required info
        mPostalcode = postalcode.getText().toString();
        mBlkno = blkno.getText().toString();
        mStreetname = streetname.getText().toString();
        if(tcspinner.getSelectedItem() != null)
            mTownCouncil = tcspinner.getSelectedItem().toString();

        //get the directory and file we want to write to
        File projectInfoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File projectInfoFile = new File(projectInfoDir, "TNCAssistant/" + mPostalcode + "/info.txt");

        //write inputs into the txt file. We don't ever change the postal code
        try {
            FileWriter newFileWriter = new FileWriter(projectInfoFile);

            //postal code should never be null
            newFileWriter.write(mPostalcode + "\n");

            if (mBlkno != null)
                newFileWriter.write(mBlkno + "\n"); //block number 2nd line
            else
                newFileWriter.write("\n");

            if (mStreetname != null)
                newFileWriter.write(mStreetname + "\n"); //street name 3rd line
            else
                newFileWriter.write("\n");

            if (mProjectPhase != null)
                newFileWriter.write(mProjectPhase + "\n"); //project phase 4th line
            else
                newFileWriter.write("\n");

            if (mTownCouncil != null)
                newFileWriter.write(mTownCouncil + "\n"); //town council 5th line
            else
                newFileWriter.write("\n");

            newFileWriter.flush();
            newFileWriter.close();

        } catch (IOException ex) {
            // Error occurred while creating the File
            Log.d(TAG, "Error creating/writing file");
        }

        //Put data to be passed back into a Bundle
        mData.putString("postalcode", mPostalcode);

        if(blkno.getText() != null)
            mData.putString("blkno", mBlkno);

        if(streetname.getText() != null)
            mData.putString("streetname", mStreetname);

        //store the data to return back to ProjectBuilder and close this Activity
        Intent output = new Intent();
        output.putExtras(mData);

        //give an OK to the previous activity (that will be called after this method)
        // close this activity
        setResult(RESULT_OK, output);
        finish();
    }

    //this method updates the EditText fields if a Project already exists
    private void updateTextFields () {

        Log.d(TAG, "I'm here in ProjectInfo's updateTextFields");

        //get the file and directory
        File projectInfoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File projectInfoFile = new File(projectInfoDir, "TNCAssistant/" + mPostalcode + "/info.txt");

        //get the RadioButtons
        RadioButton phase5 = (RadioButton) findViewById(R.id.phase5);
        RadioButton phase6 = (RadioButton) findViewById(R.id.phase6);
        RadioButton sn1 = (RadioButton) findViewById(R.id.sn1);

        //for debugging
        Log.d(TAG, "projectInfoFile is: " + projectInfoFile);

        try {
            BufferedReader buf = new BufferedReader(new FileReader(projectInfoFile));

            //read in postal code
            //readLine() reads one line and stops and next line
            String tempString = buf.readLine();

            if (tempString != null) {
                postalcode.setText(tempString);
                tempString = null; //reset tempString
            }

            //read in blk no (if any)
            tempString = buf.readLine();

            if (tempString != null) {
                blkno.setText(tempString);
                tempString = null; //reset tempString
            }

            //read in streetname (if any)
            tempString = buf.readLine();

            if (tempString != null) {
                streetname.setText(tempString);
                tempString = null; //reset tempString
            }

            //read in project phase (if any)
            tempString = buf.readLine();

            if (tempString != null) {

                if(tempString.equals("Phase 5 - 5MW HDB Project")) {

                    phase5.setChecked(true); //set checkbox for phase 5
                    mProjectPhase = "Phase 5 - 5MW HDB Project";
                    tempString = null; //reset tempString
                    tempString = buf.readLine(); //read in towncouncil (if any)

                    if (tempString != null){
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                                R.array.phase5_array, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        tcspinner.setAdapter(adapter);

                        switch (tempString){
                            case "Hougang":
                                tcspinner.setSelection(0); break;
                            case "Anhchorvale":
                                tcspinner.setSelection(1); break;
                            case "Ang Mo Kio":
                                tcspinner.setSelection(2); break;
                            case "Serangoon":
                                tcspinner.setSelection(3); break;
                        }
                    }
                }

                if(tempString.equals("Phase 6 - 20MW HDB Project")) {

                    phase6.setChecked(true); //set checkbox for phase 6
                    mProjectPhase = "Phase 6 - 20MW HDB Project";
                    tempString = null; //reset tempString
                    tempString = buf.readLine(); //read in towncouncil (if any)

                    if(tempString != null){
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                                R.array.phase6_array, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        tcspinner.setAdapter(adapter);

                        switch (tempString) {
                            case "Tampines Town Council":
                                tcspinner.setSelection(0);
                                break;
                            case "Sembawang Town Council":
                                tcspinner.setSelection(1);
                                break;
                            case "Jurong Town Council":
                                tcspinner.setSelection(2);
                                break;
                            case "Marine Parade Town Council":
                                tcspinner.setSelection(3);
                                break;
                            case "Comm-Indst":
                                tcspinner.setSelection(4);
                                break;
                        }
                    }
                }

                if(tempString.equals("SolarNova Phase 1")){

                    sn1.setChecked(true); //set checkbox for sn1
                    mProjectPhase = "SolarNova Phase 1";
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                            R.array.sn1_array, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    tcspinner.setAdapter(adapter);

                    tcspinner.setSelection(0);
                }

                tempString = null; //reset tempString
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
            savedInstanceState.putString("postalcode", mPostalcode);
        if (mBlkno != null)
            savedInstanceState.putString("blkno", mBlkno);
        if (mStreetname != null)
            savedInstanceState.putString("streetname", mStreetname);
        if (tcspinner != null)
            savedInstanceState.putInt("tcspinner", tcspinner.getSelectedItemPosition());
        if (mProjectPhase != null)
            savedInstanceState.putString("projectphase", mProjectPhase);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d(TAG, "I'm here in ProjectInfo's onRestoreInstanceState");

        mPostalcode = savedInstanceState.getString("postalcode");
        mBlkno = savedInstanceState.getString("blkno");
        mStreetname = savedInstanceState.getString("streetname");
        tcspinner.setSelection(savedInstanceState.getInt("tcspinner"));
        mProjectPhase = savedInstanceState.getString("projectphase");
    }
}
