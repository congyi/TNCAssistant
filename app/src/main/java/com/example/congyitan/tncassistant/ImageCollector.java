package com.example.congyitan.tncassistant;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;

public class ImageCollector extends AppCompatActivity {

    //for Log.d ; debugging
    private static final String TAG = "ImageCollector";

    // Required for camera operations in order to save the image file on resume.
    String mCurrentPhotoPath = null;
    private Uri mCapturedImageURI = null;
    int buttonId = -1;
    String buttonTag = null;

    // Activity result key for camera
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "I'm here in ImageCollector's OnCreateView");

        //inflate layout
        setContentView(R.layout.activity_image_collector);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //set up toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        if (mToolbar != null){
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_image);
            getSupportActionBar().setTitle("Acquire these pictures");
        }
    }

    //called when imageButton is pressed
    public void takePicture(View view) {

        buttonId = view.getId();
        buttonTag = view.getTag().toString();

        Log.d(TAG, "I'm here in takePicture and buttonId =" + String.valueOf(buttonId));
        dispatchTakePictureIntent();
    }

    //starts image capture process
    private void dispatchTakePictureIntent() {

        Bundle myData = getIntent().getExtras();
        String postalcode = String.valueOf(myData.getInt("postalcode"));

        Log.d(TAG, "I'm here in ImageCollector's dispatchTakePictureIntent");

        //create an intent to start the native cameraApp
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File imageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "TNCAssistant/" + postalcode);

        // Create the storage directory if it does not exist
        if (!imageDir.exists()) {
            if (!imageDir.mkdirs()) {
                Log.d(TAG, "Failed to create directory");
            }
        }

        File imageFile = new File(imageDir, buttonTag + ".jpg");
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        Log.d(TAG, "File is:" + imageFile.getAbsolutePath());

        //creates the Uri to be input as part of Camera Activity Intent
        mCapturedImageURI = Uri.fromFile(imageFile);
        Log.d(TAG, "mCapturedImageURI is " + mCapturedImageURI);

        // set the image file name
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

        if (imageFile != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "I'm here in ImageCollector's onActivityResult");

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            //Identify which picture was taken
            ImageButton mThumbnailImageButton = (ImageButton) findViewById(buttonId);

            //Show the thumb-sized image
            setImageFromFilePath(mThumbnailImageButton);

        }
        else
            Log.d(TAG, "Image Capture Failed or Cancelled");
    }

    //Scale the photo down and fit it to our image views. Drastically increases performance
    private void setImageFromFilePath(ImageButton imageButton) {

        Log.d(TAG, "I'm here in ImageCollector's setFullImageFromFilePath");

        // Get the dimensions of the ImageButton
        int targetW = imageButton.getWidth();
        int targetH = imageButton.getHeight();

        // Set Bitmap options
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        //Get the Bitmap image from camera
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageButton.setImageBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu_image_collector, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

        Log.d(TAG, "I'm here in ImageCollector's onSaveInstanceState");

        if (mCapturedImageURI != null)
            savedInstanceState.putString("mCapturedImageURI", mCapturedImageURI.toString());
        if (mCurrentPhotoPath != null)
            savedInstanceState.putString("mCurrentPhotoPath", mCurrentPhotoPath);
        if (buttonId != -1)
            savedInstanceState.putInt("buttonId", buttonId);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d(TAG, "I'm here in ImageCollector's onActivityCreated");

        if (savedInstanceState != null) {
            if (savedInstanceState.getString("mCapturedImageURI") != null)
                mCapturedImageURI = Uri.parse(savedInstanceState.getString("mCapturedImageURI"));
            if (savedInstanceState.getString("mCurrentPhotoPath") != null)
                mCurrentPhotoPath = savedInstanceState.getString("mCurrentPhotoPath");
            if (savedInstanceState.getInt("buttonId") != -1)
                buttonId = savedInstanceState.getInt("buttonId");
        }
    }
}
