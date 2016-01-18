package com.example.congyitan.tncassistant;

import android.app.Activity;
import android.content.Context;
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
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.congyitan.tncassistant.utilities.ImageAdapter;

import java.io.File;


public class ImageCollector extends AppCompatActivity implements ImageAdapter.ImageAdapterListener {

    //for Log.d ; debugging
    private static final String TAG = "ImageCollector";

    // Required for camera operations in order to save the image file on resume.

    private Uri mCapturedImageURI = null;
    private Context mContext;

    int mGridWidth = 0; //initialise to 0; this will be checked and changed later

    ImageButton mImageButton;

    String mImageName = null;
    String mCurrentPhotoPath;
    String mPostalCode;

    ImageAdapter mAdapter;

    // Activity result key for camera
    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "I'm here in ImageCollector's OnCreate");

        Bundle data = getIntent().getExtras();
        mPostalCode = data.getString("postalcode");

        mContext = ImageCollector.this;

        //inflate layout
        setContentView(R.layout.activity_image_collector);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //set up toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        if (mToolbar != null){
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_image);
            getSupportActionBar().setTitle("Capture these images");
        }

        //get the gridview and put images in it
        final GridView imageGrid = (GridView) findViewById(R.id.image_collector_grid);

        //get the local image directory for the project
        final File imageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "TNCAssistant/" + mPostalCode);

        //need this to detect when views have been drawn, otherwise getheight() & getwidth() returns 0
        imageGrid.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //Log.d(TAG, "I'm here in ImageCollector's imageGridOnLayoutChangeListener");

                        mGridWidth = imageGrid.getWidth();
                        //Log.d(TAG, "ImageGrid width is " + String.valueOf(mGridWidth));

                        mAdapter = new ImageAdapter(mContext, mGridWidth, imageDir);

                        imageGrid.setAdapter(mAdapter);

                        imageGrid.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);

                        // updateThumbnails(imageGrid);
                    }
                });

    }

    private void updateThumbnails(ViewGroup parent){

        Log.d(TAG, "I'm here in ImageCollector's updateThumbnails");

        //get the local image directory for the project
        File imageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "TNCAssistant/" + mPostalCode);


        Log.d(TAG, "imageDir is: " + imageDir.toString());

        File[] tempFileArray = imageDir.listFiles();
        int directorySize = tempFileArray.length;

        Log.d(TAG, "directorySize: " + String.valueOf(directorySize));

        if (directorySize == 0) //means there are no images to update
            return;

        //update thumbnails of all the images on file
        for (int i = 0; i < directorySize; i++) {

            int endIndex = tempFileArray[i].getName().indexOf('.'); //get index so i can remove .jpg below

            //start index to be 0, imageName should be img_xxxyyyzz
            String imageName = tempFileArray[i].getName().substring(0, endIndex);
            Log.d(TAG, "Image name is: " + imageName);

            //retrieve the filepath for the [i]th image
            mCurrentPhotoPath = tempFileArray[i].getAbsolutePath();
            Log.d(TAG, "File is: " + tempFileArray[i].getAbsolutePath());

            //find the thumbnail that fits the [i]th image
            for (int j = 0; j < parent.getChildCount(); j++){

                View thisChild = parent.getChildAt(j);
                Log.d(TAG, "thisChild's TAG is: " + thisChild.getTag().toString());

                if((thisChild.getTag().toString()).equals(imageName)){
                    setImageFromFilePath((ImageButton)thisChild);
                    break;
                }
            }
        }
    }

    @Override
    public void imageButtonPressed(View v, int resId) {

        mImageButton = (ImageButton) v;
        mImageName = mContext.getResources().getResourceEntryName(resId);
        Log.d(TAG, "I'm here in takePicture and imageName = " + mImageName);

        dispatchTakePictureIntent();
    }

    //starts image capture process
    private void dispatchTakePictureIntent() {

        Log.d(TAG, "I'm here in ImageCollector's dispatchTakePictureIntent");

        //create an intent to start the native cameraApp
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File imageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "TNCAssistant/" + mPostalCode);

        // Create the storage directory if it does not exist
        if (!imageDir.exists()) {
                Log.d(TAG, "Image directory doesn't exist");
                showToast("Image directory doesn't exist");
            return;
            }

        File imageFile = new File(imageDir, mImageName + ".jpg");
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        Log.d(TAG, "File is: " + imageFile.getAbsolutePath());

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

            setImageFromFilePath(mImageButton);
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

        Log.d(TAG, "ImageButton height is " + String.valueOf(targetH) +
                " , and ImageButton width is " + String.valueOf(targetW));

        if (targetW == 0 || targetH == 0) //to avoid divide by zero crash
            return;

        // Set Bitmap options
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        //fill the imageButton with the bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageButton.setImageBitmap(bitmap);
        mAdapter.notifyDataSetChanged(); //let GridView adapter know that there was a change
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
        //if (mButtonId != -1)
        //    savedInstanceState.putInt("buttonId", mButtonId);
        if (mImageName != null)
            savedInstanceState.putString("mImageName", mImageName);
        if (mPostalCode != null)
            savedInstanceState.putString("mPostalCode", mPostalCode);

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
            //if (savedInstanceState.getInt("buttonId") != -1)
            //   mButtonId = savedInstanceState.getInt("buttonId");
            if (savedInstanceState.getString("mPostalCode") != null)
                mPostalCode = savedInstanceState.getString("mPostalCode");
            if (savedInstanceState.getString("mImageName") != null)
                mImageName = savedInstanceState.getString("mImageName");
        }
    }

    //simple method to show a Toast in this Activity
    private void showToast(String msg) {
        Toast toastMessage = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toastMessage.show();
    }


}
