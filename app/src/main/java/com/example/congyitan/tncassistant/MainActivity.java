package com.example.congyitan.tncassistant;


import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

public class MainActivity extends AppCompatActivity  implements NewProjectDialog.NewProjectDialogListener {

    // Replace this with your app key and secret assigned by Dropbox.
    private final String APP_KEY = "oipcgzvnkmgvy0v";
    private final String APP_SECRET = "seizvlgz3jguucc";

    // Dropbox API stuff. You don't need to change these, leave them alone.
    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    private static final boolean USE_OAUTH1 = false;
    private boolean mLoggedIn;
    private Button dropBox;
    DropboxAPI<AndroidAuthSession> mApi;

    //for Log.d ; debugging
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We create a new AuthSession so that we can use the Dropbox API.
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        checkAppKeySetup(); //check to see if appKey has been changed from the default CHANGE_X

        //inflate the layout
        setContentView(R.layout.activity_main);

        //initialize the on-screen button. onClick listeners are attached to activity_main.xml
        dropBox = (Button)findViewById(R.id.auth_button);
    }

    //called when user presses the "New Project" button on MainActivity
    public void newProject(View view) {
        DialogFragment newFragment = new NewProjectDialog();
        newFragment.show(getFragmentManager(), "New Project Dialog");
    }

    //called when user presses the "Browse Projects" button on MainActivity
    public void browseProjects(View view) throws DropboxException {
        Intent intent = new Intent(MainActivity.this, BrowseProjects.class);
        startActivity(intent);
    }

    //called when user presses the "Connect to Dropbox" button on MainActivity
    public void dropboxAuth(View view) {

        if (mLoggedIn) {
            logOut();
        } else {
            // Start the remote authentication
            if (USE_OAUTH1) {
                mApi.getSession().startAuthentication(MainActivity.this);
            } else {
                mApi.getSession().startOAuth2Authentication(MainActivity.this);
            }
        }
    }

    //called when user keys in postal code in NewProjectDialog and pressed OK
    @Override
    public void onDialogOK(Bundle mData) {

        //write file to storage
        createProjectFile(mData);

        //start activity to build project: ProjectBuilder
        Intent intent = new Intent(MainActivity.this, ProjectBuilder.class);
        intent.putExtras(mData);
        startActivity(intent);
    }

    //called when user pressed cancel in NewProjectDialog
    @Override
    public void onDialogCancel() {
        //user pressed cancel in NewProjectDialog
        Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = mApi.getSession();

        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                storeAuth(session);
                setLoggedIn(true);

            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                Log.d(TAG, "Error authenticating", e);
            }
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
    }

    //creates directory for the new project
    //creates the file info.txt in the directory. info.txt has postalcode of the new project written into it
    private boolean createProjectFile(Bundle newProjectData) {

        //get the Bundle data from NewProjectDialog
        String postalcode = newProjectData.getString("postalcode");

        //Create the required directory for file
        File newProjectDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "TNCAssistant/" + postalcode);

        // Create the storage directory if it does not exist
        if (!newProjectDir.exists()) {
            if (!newProjectDir.mkdirs()) {
                Log.d(TAG, "Failed to create directory");
                return false;
            }
        }
        //debugging test to see if directory was created
        Log.d(TAG, "Directory is:" + newProjectDir.getAbsolutePath());

        //Create the txt file
        File newProjectFile = new File(newProjectDir, "info.txt");

        //Write data into the txt file
        try {
            FileWriter newFileWriter = new FileWriter(newProjectFile);
            newFileWriter.write(postalcode);
            newFileWriter.flush();
            newFileWriter.close();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Log.d(TAG, "Error creating/writing file");
        }

        //Get directory to store images
        File imageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "TNCAssistant/" + postalcode);

        // Create the storage directory if it does not exist
        if (!imageDir.exists()) {
            if (!imageDir.mkdirs()) {
                Log.d(TAG, "Failed to create directory");
            }
        }

        return true;
    }

    //for Dropbox API to work - do not change
    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
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

    //simple method to show a Toast in this Activity
    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        error.show();
    }

    //called when user presses "Unlink from Dropbox" in Activity
    private void logOut() {
        // Remove credentials from the session
        mApi.getSession().unlink();

        // Clear our stored keys
        clearKeys();
        // Change UI state to display logged out version
        setLoggedIn(false);
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }


    //Convenience function to change UI state based on being logged in
    private void setLoggedIn(boolean loggedIn) {
        mLoggedIn = loggedIn;
        if (loggedIn) {
            new DisplayLoggedinUser().execute();
            dropBox.setText("Unlink from Dropbox");
        } else {
            dropBox.setText("Link to Dropbox");
        }
    }

    //shows in MainActivity the user that is logged into Dropbox
    private class DisplayLoggedinUser extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            String s = new String();

            try {
                s = mApi.accountInfo().email.toString();

            }catch (DropboxException e) {
                e.printStackTrace();
            }
            return s;
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void onPostExecute(String s) {
            showToast("Logged in as: " + s);
        }
    }
}