package com.example.congyitan.tncassistant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProjectBuilder extends AppCompatActivity implements ProjectBuilderAdapter.ProjectBuilderClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProjectBuilderAdapter myProjectBuilderAdapter;

    //variables to store data
    Bundle mData;
    String mTownCouncil, mProjectPhase, mBlkno, mStreetname, mPostalcode;

    // Dropbox API stuff. You don't need to change these, leave them alone.
    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    // Replace this with your app key and secret assigned by Dropbox.
    private final String APP_KEY = "oipcgzvnkmgvy0v";
    private final String APP_SECRET = "seizvlgz3jguucc";

    //for Log.d ; debugging
    private static final String TAG = "ProjectBuilder";

    static final int PROJECTBUILDER_REQUEST = 1; //the request code


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "I'm here in ProjectBuilder's onCreate");
        
        Context thisContext = ProjectBuilder.this;

        mData = getIntent().getExtras();
        mPostalcode = mData.getString("postalcode");

        //inflate layout
        setContentView(R.layout.activity_project_builder);

        //Set toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_home);
            getSupportActionBar().setTitle(R.string.new_project_home);
        }

        //Build the list of items in RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.project_builder_list);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        myProjectBuilderAdapter = new ProjectBuilderAdapter(constructProjectBuilderList(),thisContext);
        mRecyclerView.setAdapter(myProjectBuilderAdapter);
        myProjectBuilderAdapter.setProjectBuilderClickListener(this);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Quit building project?")
                        //.setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        ProjectBuilder.super.onBackPressed();
                    }
                }).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project_builder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.save_to_dropbox:

                saveFilestoDropbox();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    // This method creates an ArrayList that has ProjectBuilderListItem objects
    public List<ProjectBuilderListItem> constructProjectBuilderList() {

        List<ProjectBuilderListItem> list = new ArrayList<>();

        int[] icons = {R.drawable.ic_file_dark, R.drawable.ic_camera_dark, R.drawable.ic_measurements_dark,
                       R.drawable.ic_add_edit_files_dark, R.drawable.ic_code_dark, R.drawable.ic_print_dark};
        String[] title = {"Project Info", "Add/Edit Images", "Add/Edit Measurements", "Add/Remove Attachments",
                          "View/Edit LaTeX code", "View/Generate Report"};

        for (int i = 0; i < title.length && i < icons.length; i++) {
            ProjectBuilderListItem tempListItem = new ProjectBuilderListItem(icons[i],title[i]);
            list.add(tempListItem);
        }
        return list;
    }

    @Override
    public void onListItemClicked(View view, int position) {

        Log.d(TAG, "I'm here in ProjectBuilder's onListItemClicked");

        if(position == 0) {
            Intent intent = new Intent(ProjectBuilder.this, ProjectInfo.class);

            mData.putString("postalcode", mPostalcode);
            intent.putExtras(mData);
            startActivityForResult(intent, PROJECTBUILDER_REQUEST);
        }

        if(position == 1) {
            Intent intent = new Intent(ProjectBuilder.this, ImageCollector.class);

            mData.putString("postalcode", mPostalcode);
            intent.putExtras(mData);
            startActivityForResult(intent, PROJECTBUILDER_REQUEST);
        }
    }

    //We just came back from ProjectInfo. Retrieve the postalcode from it to ID our project
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PROJECTBUILDER_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK)
                mPostalcode = data.getExtras().getString("postalcode");
        }
    }

    private void saveFilestoDropbox (){

        //get the Dropbox access token that was stored (if any)
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null); //this secret refers to the auth token

        //check to see if there was keys stored --> user logged in at MainActivity
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0){
            showToast("No Dropbox account was linked");
            return;
        }

        //start a session that IS LINKED (to the login in MainActivity)
        AppKeyPair newKeyPair = new AppKeyPair(APP_KEY,APP_SECRET);
        AndroidAuthSession thisSession =  new AndroidAuthSession(newKeyPair,secret);

        //create a DropboxAPI object based on the newly linked session directly above
        DropboxAPI<AndroidAuthSession> mApi = new DropboxAPI<AndroidAuthSession>(thisSession);

        //get the local directory for the project documents
        File projectDir = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "TNCAssistant/" + String.valueOf(mPostalcode));

        //extract project info from info.txt. this gives us the required info to form the Dropbox upload directory.
        File projectInfoFile = new File(projectDir,"/info.txt");

        if(projectInfoFile.exists())
            extractProjectInfo(projectInfoFile);
        else{
            showToast("Something went wrong locating your project file");
            return;
        }

        //if required info not present, just exit immediately
        if(mTownCouncil == null || mProjectPhase == null || mBlkno == null || mStreetname == null){
            showToast("Please fill in Project Phase, Town Council, Street Name and Blk No in Project Info");
            return;
        }

        //create an arraylist of files in the local documents directory
        ArrayList<File> filesToUpload = new ArrayList<File>(Arrays.asList(projectDir.listFiles()));

        //get the local directory for the project images
        File imageDir = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "TNCAssistant/" + String.valueOf(mPostalcode));

        //store list of files in a temp array
        File[] tempArray = imageDir.listFiles();
        int directorySize = tempArray.length; //gets the number of files in the local image directory

        Log.d(TAG,"Image directorySize is: " + directorySize);

        //consolidate this in my arraylist
        for(int i = 0; i < directorySize; i++)
            filesToUpload.add(tempArray[i]);

        //define Dropbox directory to upload to
        String uploadDir = "/HDB Testing and Commissioning/" +
                mProjectPhase + "/Blocks/" + mTownCouncil + "/" + mBlkno + " " + mStreetname + "/" ;

        //upload all the files
        UploadFiles upload = new UploadFiles(this, mApi, uploadDir, filesToUpload);
        upload.execute();
    }

    private void extractProjectInfo(File fileToExtract){

        Log.d(TAG, "I'm here in ProjectBuilder's extractProjectInfo");

        try {

            BufferedReader buf = new BufferedReader(new FileReader(fileToExtract));

            //read in postal code
            //readLine() reads one line and stops and next line
            String tempString = buf.readLine();

            if (tempString != null) {
                mPostalcode = tempString;
                tempString = null; //reset tempString
            }

            //read in blk no (if any)
            tempString = buf.readLine();

            if (tempString != null) {
                mBlkno = tempString;
                tempString = null; //reset tempString
            }

            //read in streetname (if any)
            tempString = buf.readLine();

            if (tempString != null) {
                mStreetname = tempString;
                tempString = null; //reset tempString
            }

            //read in project phase (if any)
            tempString = buf.readLine();

            if (tempString != null) {
                mProjectPhase = tempString;
                tempString = null; //reset tempString
            }

            //read in town council (if any)
            tempString = buf.readLine();

            if (tempString != null) {
                mTownCouncil = tempString;
                tempString = null; //reset tempString
            }

            buf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //simple method to show a Toast in this Activity
    private void showToast(String msg) {
        Toast toastMessage = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toastMessage.show();
    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState){
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

        Log.d(TAG, "I'm here in ProjectBuilder's onSaveInstanceState");

        savedInstanceState.putString("postalcode", mPostalcode);
        savedInstanceState.putString("blkno", mBlkno);
        savedInstanceState.putString("streetname", mStreetname);
        savedInstanceState.putString("towncouncil", mTownCouncil);
        savedInstanceState.putString("projectphase", mProjectPhase);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d(TAG, "I'm here in ProjectBuilder's onRestoreInstanceState");

        mPostalcode = savedInstanceState.getString("postalcode");
        mBlkno = savedInstanceState.getString("blkno");
        mStreetname = savedInstanceState.getString("streetname");
        mTownCouncil = savedInstanceState.getString("towncouncil");
        mProjectPhase = savedInstanceState.getString("projectphase");

    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }

    //for Dropbox API to work - do not change
    private void checkAppKeySetup() {
        // Check to make sure that we have a valid app key
        if (APP_KEY.startsWith("CHANGE") ||
                APP_SECRET.startsWith("CHANGE")) {
            showToast("You must apply for an app key and secret from developers.dropbox.com, and add them to the app before trying it.");
            finish();
            return;
        }
    }
}


