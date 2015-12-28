package com.example.congyitan.tncassistant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by Congyi Tan on 09/12/2015.
 */
public class UploadFiles extends AsyncTask<Void, Integer, Boolean> {

    private DropboxAPI<?> mApi;
    private String mPath;
    private ArrayList<File> mFiles;

    private int counter;
    private int mDirSize;

    private DropboxAPI.UploadRequest mRequest;
    private Context mContext;
    private ProgressDialog mDialog;

    private String mErrorMsg;

    //Constructor for class
    public UploadFiles(Context context, DropboxAPI<?> api, String dropboxPath,
                       ArrayList<File> files) {

        mContext = context.getApplicationContext(); //Get context this way so we don't accidentally leak activities
        mApi = api; //get the Dropbox API
        mPath = dropboxPath;

        mFiles = files;
        mDirSize = files.size(); //get the size of arraylist
        counter = 0;
    }

    @Override
    protected void onPreExecute() {
        //sets progress dialog
        mDialog = new ProgressDialog(mContext);
        mDialog.setMax(mDirSize);
        mDialog.setMessage("Uploading " + mFiles.get(counter).getName());
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setProgress(0);
        mDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // This will cancel the putFile operation
                mRequest.abort();
            }
        });
        mDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        String path = "";

        try {
            // By creating a request, we get a handle to the putFile operation,
            // so we can cancel it later if we want to
            for(counter = 0; counter < mDirSize; counter++){

                FileInputStream fis = new FileInputStream(mFiles.get(counter));

                if (mFiles.get(counter).getName().contains("img_"))
                    path = mPath + "/images/" + mFiles.get(counter).getName();
                else
                    path = mPath + mFiles.get(counter).getName();

                mRequest = mApi.putFileOverwriteRequest(path, fis, mFiles.get(counter).length(),

                    new ProgressListener() {
                        @Override
                        public long progressInterval() {
                            return 500;    // Update the progress bar every half-second or so
                        }
                        @Override
                        public void onProgress(long bytes, long total) {
                            publishProgress(counter);
                        }
                    });

                if (mRequest != null)
                     mRequest.upload();
            }

            if (counter == mDirSize) //return success if counter reached size of file array (upload success)
                return true;

        } catch (DropboxUnlinkedException e) {
            // This session wasn't authenticated properly or user unlinked
            mErrorMsg = "This app wasn't authenticated properly.";
        } catch (DropboxFileSizeException e) {
            // File size too big to upload via the API
            mErrorMsg = "This file is too big to upload";
        } catch (DropboxPartialFileException e) {
            // We canceled the operation
            mErrorMsg = "Upload canceled";
        } catch (DropboxServerException e) {
            // Server-side exception.  These are examples of what could happen,
            // but we don't do anything special with them here.
            if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                // Unauthorized, so we should unlink them.  You may want to
                // automatically log the user out in this case.
                showToast("Error 401.");
            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                // Not allowed to access this
                showToast("Error 403.");
            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                // path not found (or if it was the thumbnail, can't be
                // thumbnailed)
                showToast("Error 404.");
            } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
                // user is over quota
                showToast("Not enough storage space on Dropbox.");
            } else {
                showToast("Generic error.");
            }
            // This gets the Dropbox error, translated into the user's language
            mErrorMsg = e.body.userError;
            if (mErrorMsg == null) {
                mErrorMsg = e.body.error;
            }
        } catch (DropboxIOException e) {
            // Happens all the time, probably want to retry automatically.
            mErrorMsg = "Network error.  Try again.";
        } catch (DropboxParseException e) {
            // Probably due to Dropbox server restarting, should retry
            mErrorMsg = "Dropbox error.  Try again.";
        } catch (DropboxException e) {
            // Unknown error
            mErrorMsg = "Unknown error.  Try again.";
        } catch (FileNotFoundException e) {

            mErrorMsg = "File Not Found Exception error.  Try again.";
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        mDialog.setMessage("Uploading " + mFiles.get(counter).getName());
        int percent = (int)((counter/mDirSize * 100) + 0.5);
        mDialog.setProgress(percent);
    }

    @Override
    protected void onPostExecute(Boolean result) {

        mDialog.dismiss();
        if (result) {
            showToast("Files successfully uploaded");
        } else {
            showToast(mErrorMsg);
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        error.show();
    }
}
