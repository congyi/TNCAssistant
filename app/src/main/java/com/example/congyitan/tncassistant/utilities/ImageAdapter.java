package com.example.congyitan.tncassistant.utilities;

/**
 * Created by Congyi Tan on 1/17/2016.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;

import com.example.congyitan.tncassistant.R;

import java.io.File;
import java.lang.ref.WeakReference;

public class ImageAdapter extends BaseAdapter {

    //for Log.d ; debugging
    private static final String TAG = "ImageAdapter";

    private Context mContext;
    private Resources mResources;
    private int mIBHeight, mGridWidth, mIBWidth, mImageDirectorySize;
    private float scaleFactor;
    private File mImageDir;
    private File mTempFileArray[];
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
    public ImageAdapter(Context c, int gridWidth, File imageDir){

        Log.d(TAG, "I'm here in ImageAdapter's constructor");

        mContext = c;
        mResources = c.getResources();

        mGridWidth =  gridWidth;
        mImageDir = imageDir;


        mTempFileArray = mImageDir.listFiles();
        mImageDirectorySize = mTempFileArray.length;

        setIBDimensions();
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    //not used
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return mThumbIds[position];
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
        //Log.d(TAG, "mThumbsIds is " + mThumbIds[position]);
        loadBitmap(imageButton, position);

        return imageButton;
    }

    public void loadBitmap(ImageButton imageButton, int resId) {

        Bitmap mPlaceHolderBitmap = decodeSampledBitmapFromResource(mResources, resId);

        if (cancelPotentialWork(resId,imageButton)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageButton, resId);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mResources, mPlaceHolderBitmap, task);
            imageButton.setImageDrawable(asyncDrawable);
            task.execute(resId);
        }
    }

    private Bitmap decodeSampledBitmapFromResource(Resources res, int resId) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = (int) Math.ceil(scaleFactor); //ceil to scale it down a little more than needed

        return BitmapFactory.decodeResource(res, resId, options);
    }

    private Bitmap decodeSampledBitmapFromFile(String imagePath) {

        float imageScaleFactor;
        Drawable d = Drawable.createFromPath(imagePath);

        imageScaleFactor = Math.max(d.getIntrinsicWidth()/mIBWidth,d.getIntrinsicHeight()/mIBHeight);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = (int) Math.ceil(imageScaleFactor); //ceil to scale it down a little more than needed

        return BitmapFactory.decodeFile(imagePath, options);
    }

    private String getImageNameFromFile (File inputFile){

        String outputName = null;

        int endIndex = inputFile.getName().indexOf('.'); //get index so i can remove .jpg below

        //start index to be 0, imageName should be img_xxxyyyzz
        outputName = inputFile.getName().substring(0, endIndex);
        //Log.d(TAG, "Image name is: " + imageName);

        return outputName;
    }

    private void setIBDimensions(){

        //Since there are two columns in the GridView, we take the GridView.width - paddings, and divide by 2
        //to get the width of the ImageButton
        //ceil to make it a little bigger than needed
        mIBWidth = (int) Math.ceil((mGridWidth - (4 * padding))/2);

        //pick a random image from Drawable Resources(they should all be the same)
        //get its width and divide it by the ImageButton height to get the scaleFactor
        Drawable d = mResources.getDrawable(R.drawable.img_ballast, null);
        scaleFactor = (float) d.getIntrinsicWidth() / (float) mIBWidth;

        //with the scaleFactor, we can determine the height of ImageButton:
        //Add padding to top and bottom
        //ceil to make it a little bigger than needed
        mIBHeight = ((int) Math.ceil((float) d.getIntrinsicHeight() / scaleFactor)) + (2 * padding);

    }

    //The cancelPotentialWork method checks if another running task is already
    // associated with the ImageView. If so, it attempts to cancel the previous
    // task by calling cancel(). In a small number of cases, the new task data matches
    // the existing task and nothing further needs to happen.
    public static boolean cancelPotentialWork(int resId, ImageButton imageButton) {

        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageButton);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == 0 || bitmapData != resId) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }

        // No task associated with the ImageButton, or an existing task was cancelled
        return true;
    }

    //getBitmapWorkerTask() is used above to retrieve the task associated with a particular
    // ImageView:
    private static BitmapWorkerTask getBitmapWorkerTask(ImageButton imageButton) {
        if (imageButton != null) {
            final Drawable drawable = imageButton.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    static class AsyncDrawable extends BitmapDrawable {

        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        //Constructor
        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

        private final WeakReference<ImageButton> imageButtonReference;
        private int data = 0;
        private int mPosition;

        //constructor
        public BitmapWorkerTask(ImageButton imageButton, int position) {
            // Use a WeakReference to ensure the ImageButton can be garbage collected
            imageButtonReference = new WeakReference<ImageButton>(imageButton);
            mPosition = position;

        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {

            //Check if there are images in the Pictures Directory
            //If not, means nothing to update -> so return template Drawable
            if (mImageDirectorySize == 0) {
                return decodeSampledBitmapFromResource (mResources, (int) getItemId(mPosition));
            }

            else {

                //get the ThumbsId[position] resource name so we can compare to our file
                String thumbsIdName = mResources.getResourceEntryName((int) getItemId(mPosition));
                //Log.d(TAG,"thumbsIdName is " + thumbsIdName);

                //check the Pictures Directory to see if there's a fileName that matches
                //if so, set the Bitmap to the imageButton
                for (int i = 0; i < mImageDirectorySize; i++) {

                    //start index to be 0, imageName should be img_xxxyyyzz
                    String thisImageName = getImageNameFromFile(mTempFileArray[i]);
                    //Log.d(TAG,"thisImageName is " + thisImageName);

                    if(thisImageName.equals(thumbsIdName)){
                        String imagePath = mTempFileArray[i].getAbsolutePath();
                        return decodeSampledBitmapFromFile(imagePath);
                    }
                }

                //means there's no matching bitmap on file for this image
                //so load the default template
                return decodeSampledBitmapFromResource (mResources, (int) getItemId(mPosition));
            }
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (isCancelled()) {
                bitmap = null;
            }

            if (imageButtonReference != null && bitmap != null) {

                final ImageButton imageButton = imageButtonReference.get();
                final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageButton);

                if (this == bitmapWorkerTask && imageButton != null) {
                    Animation myFadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fadein);
                    imageButton.setImageBitmap(bitmap);
                    imageButton.setAnimation(myFadeInAnimation);
                    mListener = (ImageAdapterListener) mContext;
                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            mListener.imageButtonPressed(v, (int) getItemId(mPosition));
                        }
                    });
                }
            }
        }
    }

    public interface ImageAdapterListener {
         void imageButtonPressed(View v, int resId);
    }

}