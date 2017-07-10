
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
        import android.widget.ImageView;
        import com.example.congyitan.tncassistant.R;
        import java.io.File;
        import java.lang.ref.WeakReference;

public class ImageAdapter extends BaseAdapter {

    //for Log.d ; debugging
    private static final String TAG = "ImageAdapter";

    private Context mContext;
    private Resources mResources;
    private int mIBHeight, mGridWidth, mIBWidth, mImageDirectorySize;
    private File mTempFileArray[];
    private static final int padding  = 5;

    // Keep all Images in array
    public int[] mThumbIds = {
            R.drawable.img_ballast, R.drawable.img_cabling,
            R.drawable.img_celltech, R.drawable.img_continuity,
            R.drawable.img_continuityread, R.drawable.img_earth,
            R.drawable.img_energymeter, R.drawable.img_labelling,
            R.drawable.img_lightningtape, R.drawable.img_overview,
            R.drawable.img_pv150, R.drawable.img_specs,
            R.drawable.img_surge, R.drawable.img_switchrm,
            R.drawable.img_warninglabel, R.drawable.img_tilt
    };


    // Constructor
    public ImageAdapter(Context c, int gridWidth, File imageDir){

        Log.d(TAG, "I'm here in ImageAdapter's constructor");

        mContext = c;
        mResources = c.getResources();

        mGridWidth =  gridWidth;

        mTempFileArray = imageDir.listFiles();
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

        ImageView imageView = new ImageView(mContext);

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            //Log.d(TAG, "IMBWidth is " + String.valueOf(mIBWidth) + ", mIBHeight is " + String.valueOf(mIBHeight));
            imageView.setLayoutParams(new GridView.LayoutParams(mIBWidth, mIBHeight));
            imageView.setPadding(padding, padding, padding, padding);
        }
        else {
            imageView = (ImageView) convertView;
        }

        //this is where the AsyncTask begins
        loadBitmap(imageView, position);

        return imageView;
    }

    public void loadBitmap(ImageView imageView, int position) {

        //Bitmap mPlaceHolderBitmap = decodeSampledBitmapFromResource(mResources, (int) getItemId(15));

        if (cancelPotentialWork(position, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView , position);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mResources, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(position);
        }
    }

    //The cancelPotentialWork method checks if another running task is already
    //associated with the ImageView. If so, it attempts to cancel the previous
    //task by calling cancel(). In a small number of cases, the new task data matches
    //the existing task and nothing further needs to happen.
    public static boolean cancelPotentialWork(int resId, ImageView imageView) {

        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.mPosition;
            // If bitmapData is not yet set or it differs from the new data
            if ( bitmapData != resId) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }

        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId) {

        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, mIBWidth, mIBHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resId, options);
    }

    public Bitmap decodeSampledBitmapFromFile(String imagePath) {

        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, mIBWidth, mIBHeight);
        options.inJustDecodeBounds = false;

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

        //Log.d(TAG, "I'm here in ImageAdapter's setIBDimensions");

        float IBScaleFactor;

        //Since there are two columns in the GridView, we take the GridView.width - paddings, and divide by 2
        //to get the width of the ImageView
        //ceil to make it a little bigger than needed
        mIBWidth = (int) Math.ceil((mGridWidth - (4 * padding))/2);

        //pick a random image from Drawable Resources(they should all be the same)
        //get its width and divide it by the ImageView height to get the scaleFactor
        Drawable d = mResources.getDrawable(R.drawable.img_ballast, null);
        IBScaleFactor = (float) d.getIntrinsicWidth() / (float) mIBWidth;

        //with the scaleFactor, we can determine the height of ImageView:
        //Add padding to top and bottom
        //ceil to make it a little bigger than needed
        mIBHeight = ((int) Math.ceil((float) d.getIntrinsicHeight() / IBScaleFactor));

        Log.d(TAG, "mIBWidth: " + mIBWidth + ", mIBHeight: " + mIBHeight);

    }

    //getBitmapWorkerTask() is used above to retrieve the task associated with a particular
    // ImageView:
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
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
        public AsyncDrawable(Resources res, BitmapWorkerTask bitmapWorkerTask) {

            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;
        private int mPosition;

        //constructor
        public BitmapWorkerTask(ImageView imageView, int position) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);

        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {

            mPosition = params[0];

            //get the ThumbsId[position] resource name so we can compare to our file
            String thumbsIdName = mResources.getResourceEntryName((int) getItemId(mPosition));
            //Log.d(TAG,"thumbsIdName is " + thumbsIdName);

            //check the Pictures Directory to see if there's a fileName that matches
            //if so, set the Bitmap to the imageView
            for (int i = 0; i < mImageDirectorySize; i++) {

                //start index to be 0, imageName should be img_xxxyyyzz
                String fileImageName = getImageNameFromFile(mTempFileArray[i]);
                //Log.d(TAG,"thisImageName is " + thisImageName);

                if (thumbsIdName.equals(fileImageName)) {
                    String fileImagePath = mTempFileArray[i].getAbsolutePath();
                    return decodeSampledBitmapFromFile(fileImagePath);
                }
            }

            return decodeSampledBitmapFromResource(mResources, (int) getItemId(mPosition));

            //File placeholderFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    //"TNCAssistant/1.png");
            //means there's no matching bitmap on file for this image
            //so load the default template
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {

                //Log.d(TAG, "size of bitmap: " + bitmap.getAllocationByteCount());

                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

                if (this == bitmapWorkerTask && imageView != null) {

                    Animation myFadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fadein);

                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    imageView.setImageBitmap(bitmap);
                    imageView.setAnimation(myFadeInAnimation);

                }
            }
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        //Log.d(TAG,"inSampleSize is "+ inSampleSize);

        return inSampleSize;
    }

    public void loadBitmapFromCamera(int position, ImageView imageView, File imageDir){

        //update the tempFileArray with the newly capture image
        mTempFileArray =  imageDir.listFiles();
        mImageDirectorySize = mTempFileArray.length;

        if (cancelPotentialWork(position, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView , position);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mResources, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(position);
        }
    }

}