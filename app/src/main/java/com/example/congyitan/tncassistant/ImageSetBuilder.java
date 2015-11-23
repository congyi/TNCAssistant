package com.example.congyitan.tncassistant;

import android.app.Activity;
import android.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ImageSetBuilder extends Fragment {

    Toolbar mToolbar;

    //for Log.d ; debugging
    private static final String TAG = "ImageSetBuilder";

    // Storage for camera image components
    private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";
    private final static String CAPTURED_PHOTO_URI_KEY = "mCapturedImageURI";
    private final static String BUTTON_ID = "buttonId";

    // Required for camera operations in order to save the image file on resume.
    String mCurrentPhotoPath;
    private Uri mCapturedImageURI;
    int buttonId;

    // Activity result key for camera
    static final int REQUEST_IMAGE_CAPTURE= 1;

    public ImageSetBuilder() {
        Log.d(TAG, "I'm here in ImageSetBuilder's Constructor");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "I'm here in ImageSetBuilder's OnCreate");

        mCurrentPhotoPath = null;
        mCapturedImageURI = null;
        buttonId = -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "I'm here in ImageSetBuilder's OnCreateView");

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        View view = inflater.inflate(R.layout.fragment_image_set_builder, container, false);
        mToolbar = (Toolbar)view.findViewById(R.id.toolbar_image_set_builder);

        if(mToolbar != null){
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_image);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState){
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

        Log.d(TAG, "I'm here in ImageSetBuilder's onSaveInstanceState");
        if(mCapturedImageURI != null)
            savedInstanceState.putString("mCapturedImageURI", mCapturedImageURI.toString());
        if(mCurrentPhotoPath != null)
            savedInstanceState.putString("mCurrentPhotoPath", mCurrentPhotoPath);
        if(buttonId != -1)
            savedInstanceState.putInt("buttonId", buttonId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "I'm here in ImageSetBuilder's onActivityCreated");

        if(savedInstanceState != null) {
            if (savedInstanceState.getString("mCapturedImageURI") != null)
                mCapturedImageURI = Uri.parse(savedInstanceState.getString("mCapturedImageURI"));
            if(savedInstanceState.getString("mCurrentPhotoPath") != null)
                 mCurrentPhotoPath = savedInstanceState.getString("mCurrentPhotoPath");
            if(savedInstanceState.getInt("buttonId") != -1)
                buttonId = savedInstanceState.getInt("buttonId");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "I'm here in ImageSetBuilder's onActivityResult");

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            addPhotoToGallery();
            Log.d(TAG, "mCurrentPhotoPath in OnActivityResult is " + mCurrentPhotoPath);
            ImageButton mThumbnailImageButton = (ImageButton) getActivity().findViewById(buttonId);
            setFullImageFromFilePath(mCurrentPhotoPath, mThumbnailImageButton); //Show the thumb-sized image
        } else
            Toast.makeText(getActivity(), "Image Capture Failed or Cancelled", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Image Capture Failed or Cancelled");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
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
        Log.d(TAG, "mCurrentPhotoPath is " + mCurrentPhotoPath);
        return image;
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
        getActivity().sendBroadcast(mediaScanIntent);
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


    private OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Context context) {

        Log.d(TAG,"I'm here in ImageSetBuilder's OnAttach");

        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

       // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


     /* This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
*/

public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    public void onFragmentInteraction(Uri uri);
}


}