package com.spe.luke.freeandgood.bitmaploading;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;


import com.spe.luke.freeandgood.FeedFragment;
import com.spe.luke.freeandgood.FeedFragment.FeedGridAdapter;
import com.spe.luke.freeandgood.bitmaploading.BitmapCache;

/**
 * Created by Luke on 7/13/2015.
 */

public class ImageLoaderTask extends AsyncTask<Integer, String, Bitmap>
{

    Integer m_position;
    FeedFragment.FeedGridAdapter.FeedViewHolder m_holder;
    ImageView m_photo;
    String m_base64;
    FeedFragment.FeedGridAdapter m_feedGridAdapter;

    public ImageLoaderTask(Integer position, FeedGridAdapter.FeedViewHolder holder, ImageView photo, String base64, FeedGridAdapter feedGridAdapter)
    {
        SyncCounter.inc();

        m_photo = photo; //Imageview
        m_base64 = base64; //String
        m_feedGridAdapter = feedGridAdapter;
        m_position = position;
        m_holder = holder; //the holder
    }

    @Override
    protected Bitmap doInBackground(Integer... params)
    {
        //re-sample sample image in the background to 200x200
        Bitmap bitmap = decodeSampledBitmapFromString(200, 200);

        return bitmap;
    }

    //set photoView and holder
    protected void onPostExecute(Bitmap bitmap)
    {
        if (bitmap != null)
        {
            BitmapCache.addBitmapToMemoryCache(m_position, bitmap);

            if (m_holder.image == null)
            {
                m_photo.setImageBitmap(bitmap);
            } else
            {
                if (m_holder.getLayoutPosition() == m_position)
                {
                    if (m_holder.image != null)
                    {
                        m_holder.image.setImageBitmap(bitmap);
                    }
                }
            }

            Log.i("GalleryInListView", "Bitmap loaded, notifying list adapter");
            m_feedGridAdapter.notifyDataSetChanged();
        } else
        {
            Log.e("GalleryInListView", "Error: Could not sample bitmap");
        }

        SyncCounter.dec();
    }


    //resample Bitmap to prevent out-of-memory crashes
    private Bitmap decodeSampledBitmapFromString(int reqWidth, int reqHeight)
    {
        Bitmap bitmap;

        //decode File and set inSampleSize
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        // turn the byte array into a bitmap
        byte[] imageAsBytes = Base64.decode(m_base64.getBytes(), Base64.DEFAULT);
         BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // decode base64 with inSampleSize set
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        return bitmap;
    }

    //calculate bitmap sample sizes
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {
            if (width > height)
            {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else
            {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }
}

