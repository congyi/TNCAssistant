package com.example.congyitan.tncassistant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProjectBuilder extends AppCompatActivity {

    //for Log.d ; debugging
    private static final String TAG = "ProjectBuilder";

    // Storage for camera image URI components
    private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";
    private final static String CAPTURED_PHOTO_URI_KEY = "mCapturedImageURI";

    // Required for camera operations in order to save the image file on resume.
    String mCurrentPhotoPath;
    private Uri mCapturedImageURI;
    int buttonId;

    // Activity result key for camera
    static final int REQUEST_IMAGE_CAPTURE= 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toobar
        Toolbar toolbar;

        //Get message from Intent (specifically the projectTitle)
        Bundle bundle = getIntent().getExtras();
        String projectTitle = bundle.getString("projectTitle");

        //Create and initialize the new Project class!
        Projects newProject = new Projects();

        //Set view and populate title for the toolbar
        setContentView(R.layout.activity_project_builder);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(projectTitle);

    }

    //called when imageButton is pressed
    public void takePicture(View view) {

        buttonId = view.getId();
        dispatchTakePictureIntent();
    }

    //starts image capture process
    private void dispatchTakePictureIntent() {

        //create an intent to start the native cameraApp
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // create a file to save the image
        File photoFile = null;
        try {
            photoFile = getOutputMediaFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Log.d(TAG, "Error creating file");
        }
        mCapturedImageURI = getOutputMediaFileUri();
        Log.d(TAG, "mCapturedImageURI is " + mCapturedImageURI);

        // set the image file name
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

        if (photoFile != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState){
        savedInstanceState.putString("mCapturedImageURI", mCapturedImageURI.toString());
        savedInstanceState.putString("mCurrentPhotoPath", mCurrentPhotoPath);
        savedInstanceState.putInt("buttonId", buttonId);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey("mCapturedImageURI")) {
            mCapturedImageURI = Uri.parse(savedInstanceState.getString("mCapturedImageURI"));
        }

        if (savedInstanceState.containsKey("mCurrentPhotoPath")) {
            mCurrentPhotoPath = savedInstanceState.getString("mCurrentPhotoPath");
        }

        if (savedInstanceState.containsKey("buttonId")) {
            buttonId = savedInstanceState.getInt("buttonId");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            addPhotoToGallery();
            Log.d(TAG, "mCurrentPhotoPath in OnActivityResult is " + mCurrentPhotoPath);
            ImageButton mThumbnailImageButton = (ImageButton)findViewById(buttonId);
            setFullImageFromFilePath(mCurrentPhotoPath,mThumbnailImageButton); //Show the thumb-sized image
        } else
            Toast.makeText(ProjectBuilder.this, "Image Capture Failed or Cancelled", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Image Capture Failed or Cancelled");
    }

    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(){

       Uri mUri = null;

        try {
            mUri =  Uri.fromFile(getOutputMediaFile());
        } catch (IOException ex) {
            Log.d(TAG, "Error creating mUri");
        }
        return mUri;
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile() throws IOException {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "TnCAssistant");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(TAG, "Failed to create directory");
                return null;
            }
        }
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                mediaStorageDir     /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "mCurrentPhotoPath is "+ mCurrentPhotoPath);
        return image;
    }

    /**
     * Add the picture to the photo gallery.
     * Must be called on all camera images or they will
     * disappear once taken.
     */

    protected void addPhotoToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    /**
     * Scale the photo down and fit it to our image views.
     *
     * "Drastically increases performance" to set images using this technique.
     * Read more:http://developer.android.com/training/camera/photobasics.html
     */
    private void setFullImageFromFilePath(String imagePath, ImageButton imageButton) {

        // Get the dimensions of the View
        int targetW = imageButton.getWidth();
        int targetH = imageButton.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        //bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        imageButton.setImageBitmap(bitmap);
    }
}
