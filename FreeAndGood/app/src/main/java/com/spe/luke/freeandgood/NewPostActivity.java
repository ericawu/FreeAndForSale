package com.spe.luke.freeandgood;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.*;


import android.widget.ViewFlipper;

import net.yazeed44.imagepicker.model.ImageEntry;
import net.yazeed44.imagepicker.ui.PickerActivity;
import net.yazeed44.imagepicker.util.Picker;
import net.yazeed44.imagepicker.util.AlbumEntry;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Luke on 6/25/2015.
 */
public class NewPostActivity extends AppCompatActivity
{
    String[] categorySpinner;
    String[] buymethodSpinner;

    JSONObject newPost;
    private ViewFlipper mViewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        final GestureDetector detector = new GestureDetector(NewPostActivity.this, new SwipeGestureDetector(NewPostActivity.this, mViewFlipper));
        mViewFlipper.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(final View view, final MotionEvent event)
            {
                detector.onTouchEvent(event);
                return true;
            }
        });

        // set up all text fields and spinners
        final EditText title = (EditText) findViewById(R.id.edit_title);
        final EditText description = (EditText) findViewById(R.id.edit_description);
        final EditText price = (EditText) findViewById(R.id.edit_price);

        final Spinner categories = (Spinner) findViewById(R.id.category_spinner);
        // potential categories
        this.categorySpinner = new String[]{
                "[Categories]", "Furniture", "Food", "Clothing", "Electronics", "Appliances",
                "Games", "Textbooks", "Misc"
        };
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorySpinner);
        categories.setAdapter(adapter1);

        final Spinner buymethods = (Spinner) findViewById(R.id.buymethod_spinner);
        // potential buying options
        this.buymethodSpinner = new String[]{
                "[Sale Type]", "Free", "Auction", "Fixed Price"
        };
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, buymethodSpinner);
        buymethods.setAdapter(adapter2);

        final Button btnAddPost = (Button) findViewById(R.id.btnAddPost);
        btnAddPost.setEnabled(true);
        btnAddPost.setOnClickListener(new View.OnClickListener()
                                      {
                                          @Override
                                          public void onClick(View view)
                                          {

                                              String mTitle = title.getText().toString();
                                              String mPrice = price.getText().toString();
                                              String mDescription = description.getText().toString();
                                              String mCategory = categories.getSelectedItem().toString();
                                              String mPhoto1, mPhoto2, mPhoto3;
                                              if (bitmaps[0] == null)
                                              {
                                                  Bitmap defaultPic = BitmapFactory.decodeResource(getResources(), R.drawable.no_photo_icon);
                                                  bitmaps[0] = defaultPic;
                                                  numOfPics++;
                                                  thumbnailBitmap = defaultPic;
                                              }

                                              mPhoto1 = imageToBase64(bitmaps[0]);
                                              mPhoto2 = imageToBase64(bitmaps[1]);
                                              mPhoto3 = imageToBase64(bitmaps[2]);

                                              String mThumbnail = imageToBase64(thumbnailBitmap);

                                              String mBuymethod = buymethods.getSelectedItem().toString();

                                              if (mBuymethod.equals("Free"))
                                              {
                                                  mPrice = "0.00";
                                              }


                                              if (mTitle.isEmpty() || mPrice.isEmpty() && !mBuymethod.equals("Free") || mDescription.isEmpty() || mCategory.isEmpty()
                                                      || mCategory.equals("[Categories]") || mBuymethod.isEmpty() || mBuymethod.equals("[Sale Type]"))
                                              {
                                                  Toast toast = Toast.makeText(NewPostActivity.this,
                                                          "One or more fields is empty. FILL EM UP!",
                                                          Toast.LENGTH_SHORT);
                                                  toast.show();
                                              } else
                                              {
                                                  btnAddPost.setEnabled(false);
                                                  //create json object to send
                                                  newPost = new JSONObject();
                                                  try
                                                  {
                                                      newPost.put("userId", "2");
                                                      newPost.put("tag1", " ");
                                                      newPost.put("tag2", " ");
                                                      newPost.put("tag3", " ");
                                                      newPost.put("message", "");
                                                      newPost.put("postTitle", mTitle);
                                                      newPost.put("description", mDescription);
                                                      newPost.put("category", mCategory);
                                                      newPost.put("photo1", mPhoto1);
                                                      newPost.put("photo2", mPhoto2);
                                                      newPost.put("photo3", mPhoto3);
                                                      newPost.put("feedPhoto", mThumbnail);
                                                      newPost.put("saleType", mBuymethod);
                                                      newPost.put("count", numOfPics);
                                                      newPost.put("bid", "0.00");
                                                      newPost.put("price", "0.00");
                                                      Log.d("&&&", Integer.toString(numOfPics));

                                                      switch (mBuymethod)
                                                      {
                                                          case "Auction":
                                                              newPost.put("bid", mPrice);
                                                              break;
                                                          case "Free":
                                                              newPost.put("price", mPrice);
                                                              break;
                                                          case "Fixed Price":
                                                              newPost.put("price", mPrice);
                                                              break;
                                                      }

                                                      Log.d("**************", newPost.toString());

                                                      new newPostTask().execute(newPost);

                                                  } catch (JSONException e)
                                                  {
                                                      e.printStackTrace();
                                                  }
                                              }

                                          }
                                      }


        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private class newPostTask extends AsyncTask<JSONObject, Void, Void>
    {

        @Override
        protected Void doInBackground(JSONObject... params)
        {
            HttpURLConnection con;

            try
            {
                URL url = new URL("http://speffs.one/SPE/postWithBids.php");

                con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setChunkedStreamingMode(0);
                con.connect();
                con.setRequestMethod("POST");

                OutputStream out = new BufferedOutputStream(con.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                writer.write(newPost.toString());
                Log.d("newPostTaskDONE", writer.toString());
                writer.flush();
                writer.close();

                out.close();
                con.disconnect();


            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Toast toast = Toast.makeText(NewPostActivity.this, "Post Added", Toast.LENGTH_SHORT);
            toast.show();

            Intent intent = new Intent(NewPostActivity.this, MainActivity.class);
            NavUtils.navigateUpTo(NewPostActivity.this, intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_full_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home)
        {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Quit")
                    .setMessage("Are you sure you want to heartlessly abandon this new post?" + "\n" +
                            "All work on it will be lost.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<AlbumEntry> mSelectedImages;

    public void onClickPickImageMultipleWithLimit(View view)
    {
        new Picker.Builder(this, new MyPickListener(), R.style.Theme_AppCompat_Light_DarkActionBar)
                .setLimit(3)
                .build()
                .startActivity();
    }

    // create array of bitmaps from picks
    Bitmap[] bitmaps = new Bitmap[3];
    Bitmap thumbnailBitmap;
    int numOfPics;

    public class newPickerActivity extends PickerActivity
    {

    }

    private class MyPickListener implements Picker.PickListener
    {
        @Override
        public void onPickedSuccessfully(ArrayList<ImageEntry> arrayList)
        {
            newPickerActivity myPickerActivity = new newPickerActivity();

            numOfPics = arrayList.size();

            for (int i = 0; i < numOfPics; i++)
            {
                if (i == 0)
                {
                    thumbnailBitmap = decodeSampledBitmapFromResource(arrayList.get(i).path, 200, 200);
                }
                bitmaps[i] = decodeSampledBitmapFromResource(arrayList.get(i).path, 525, 525);
                Log.d("STRING OF PATHS", arrayList.get(i).toString());
            }
            setupImageSamples(bitmaps);
        }

        public Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight)
        {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(path, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(path, options);
        }

        public int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight)
        {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth)
            {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth)
                {
                    inSampleSize *= 2;
                }
            }
            return inSampleSize;
        }


        @Override
        public void onCancel()
        {
            // user canceled the picking
        }
    }

    private void setupImageSamples(Bitmap[] bitmap)
    {
        if (mViewFlipper.getChildCount() > 0)
        {
            mViewFlipper.removeAllViews();
        }
        //fill with the adapter and stuff for our new imageviewer
        for (int i = 0; i < numOfPics; i++)
        {
            ImageView imageView = new ImageView(NewPostActivity.this);
            imageView.setImageBitmap(bitmap[i]);
            mViewFlipper.addView(imageView);
        }
    }

    public static String imageToBase64(Bitmap image)
    {
        if (image == null) return null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        //create a byte array
        byte[] b = baos.toByteArray();

        // encode the image
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("******ENCODED", imageEncoded);

        return imageEncoded;
    }

    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Quit")
                .setMessage("Are you sure you want to heartlessly abandon this new post?" + "\n" +
                        "All work on it will be lost.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}