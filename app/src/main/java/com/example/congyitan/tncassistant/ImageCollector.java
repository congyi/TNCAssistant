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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.congyitan.tncassistant.utilities.ImageAdapter;

import java.io.File;


public class ImageCollector extends AppCompatActivity {

    //for Log.d ; debugging
    private static final String TAG = "ImageCollector";

    // Required for camera operations in order to save the image file on resume.

    private Uri mCapturedImageURI = null;
    private Context mContext;

    int mGridWidth = 0; //initialise to 0; this will be checked and changed later
    int mThumbPosition;

    ImageView mImageView;

    String mImageName = null;
    String mCurrentPhotoPath;
    String mPostalCode;

    ImageAdapter mAdapter;

    GridView mImageGrid;

    AdapterView<?> mParent;

    File mImageDir;

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
        mImageGrid = (GridView) findViewById(R.id.image_collector_grid);

        //get the local image directory for the project
        mImageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "TNCAssistant/" + mPostalCode);

        //need this to detect when views have been drawn, otherwise getheight() & getwidth() returns 0
        mImageGrid.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        //Log.d(TAG, "I'm here in ImageCollector's imageGridOnLayoutChangeListener");
                        mGridWidth = mImageGrid.getWidth();
                        mAdapter = new ImageAdapter(mContext, mGridWidth, mImageDir);

                        mImageGrid.setAdapter(mAdapter);

                        //Log.d(TAG, "ImageGrid width is " + String.valueOf(mGridWidth));
                        mImageGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    }
                });
    }

    @Override
    protected void onStart(){
        super.onStart();

        mImageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mImageView = (ImageView) view;
                mThumbPosition = position;
                mImageName = mContext.getResources().getResourceEntryName((int) id);
                mParent = parent;

                dispatchTakePictureIntent(position);
            }
        });
    }
    //starts image capture process
    private void dispatchTakePictureIntent(int position) {

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
        Log.d(TAG, "mCapturedImageURI is " + mCapturedImageURI + " and mImageName is " + mImageName);

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

            //update the view
            mAdapter.loadBitmapFromCamera(mThumbPosition, mImageView, mImageDir);

        }

        else
            Log.d(TAG, "Image Capture Failed or Cancelled");
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
        if (mThumbPosition != -1)
            savedInstanceState.putInt("mThumbPosition", mThumbPosition);
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
            if (savedInstanceState.getInt("mThumbPosition") != -1)
               mThumbPosition = savedInstanceState.getInt("mThumbPosition");
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
