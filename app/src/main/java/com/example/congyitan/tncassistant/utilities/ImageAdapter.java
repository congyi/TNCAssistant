package com.example.congyitan.tncassistant.utilities;

/**
 * Created by Congyi Tan on 1/17/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;


import com.example.congyitan.tncassistant.ImageCollector;
import com.example.congyitan.tncassistant.R;

import java.lang.ref.WeakReference;

public class ImageAdapter extends BaseAdapter {

    //for Log.d ; debugging
    private static final String TAG = "ImageAdapter";

    private Context mContext;
    private int mIBHeight, mGridWidth, mIBWidth;
    private float scaleFactor;
    private static final int padding  = 5;

    private ImageAdapterListener mListener;

    // Keep all Images in array
    public int[] mThumbIds = {
            R.drawable.img_ballast, R.drawable.img_cabling,
            R.drawable.img_celltech, R.drawable.img_continuity,
            R.drawable.img_continuityread, R.drawable.img_earth,
            R.drawable.img_energymeter, R.drawable.img_labelling,
            R.drawable.img_lightningtape, R.drawable.img_overview,
            R.drawable.img_pv150, R.drawable.img_specs,
            R.drawable.img_surge, R.drawable.img_switchrm,
            R.drawable.img_warninglabel
    };

    // Constructor
    public ImageAdapter(Context c, int GridWidth){

        Log.d(TAG, "I'm here in ImageAdapter's constructor");

        mContext = c;
        mGridWidth =  GridWidth;
        setIBDimensions();
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageButton imageButton = new ImageButton(mContext);

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            //Log.d(TAG, "IMBWidth is " + String.valueOf(mIBWidth) + ", mIBHeight is " + String.valueOf(mIBHeight));
            imageButton.setLayoutParams(new GridView.LayoutParams(mIBWidth, mIBHeight));
            imageButton.setPadding(padding, padding, padding, padding);
            imageButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
        }
        else {
            imageButton = (ImageButton) convertView;
        }

        Log.d(TAG, "mThumbsIds is " +  mThumbIds[position]);

        BitmapWorkerTask placeImage = new BitmapWorkerTask(imageButton, position);
        placeImage.execute();

        //imageButton.setImageBitmap(
                //decodeSampledBitmapFromResource(mContext.getResources(), mThumbIds[position]));

        return imageButton;
    }

    private Bitmap decodeSampledBitmapFromResource(Resources res, int resId) {


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = (int) Math.ceil(scaleFactor); //ceil to scale it down a little more than needed
        // Calculate inSampleSize
       //options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        return BitmapFactory.decodeResource(res, resId, options);
    }

    private void setIBDimensions(){

        //Since there are two columns in the GridView, we take the GridView.width - paddings, and divide by 2
        //to get the width of the ImageButton
        //ceil to make it a little bigger than needed
        mIBWidth = (int) Math.ceil((mGridWidth - (4 * padding))/2);

        //pick a random image from Drawable (they should all be the same)
        //get its width and divide it by the ImageButton height to get the scaleFactor
        Drawable d = mContext.getResources().getDrawable(R.drawable.img_ballast, null);
        scaleFactor = (float) d.getIntrinsicWidth() / (float) mIBWidth;

        //with the scaleFactor, we can determine the height of ImageButton:
        //Add padding to top and bottom
        //ceil to make it a little bigger than needed
        mIBHeight= ((int) Math.ceil((float) d.getIntrinsicHeight() / scaleFactor)) + (2 * padding);

    }

    class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {

        private final WeakReference<ImageButton> imageButtonReference;

        private int mPosition;

        public BitmapWorkerTask(ImageButton imageButton, int position) {
            // Use a WeakReference to ensure the ImageButton can be garbage collected
            imageButtonReference = new WeakReference<ImageButton>(imageButton);
            mPosition = position;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Void... params) {

            return decodeSampledBitmapFromResource
                    (mContext.getResources(), mThumbIds[mPosition]);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageButtonReference != null && bitmap != null) {

                Animation myFadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fadein);
                final ImageButton imageButton = imageButtonReference.get();

                if (imageButton != null) {

                    imageButton.setImageBitmap(bitmap);
                    imageButton.setAnimation(myFadeInAnimation);
                    mListener = (ImageAdapterListener) mContext;
                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            mListener.imageButtonPressed(v);
                        }
                    });
                }
            }
        }
    }

    public interface ImageAdapterListener {
         void imageButtonPressed(View v);
    }
}