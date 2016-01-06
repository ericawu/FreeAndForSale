package com.spe.luke.freeandgood;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class FullPostActivity extends AppCompatActivity
{
    private ViewFlipper mViewFlipper;

    JSONObject newBid;
    ProgressBar pb;
    int count;
    int postId;
    double currentBid;
    String saleType;

    String postTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Intent intent = getIntent();
        postTitle = intent.getExtras().getString(FeedFragment.POST_TITLE);
        count = intent.getExtras().getInt(FeedFragment.PHOTOCOUNT);
        postId = intent.getExtras().getInt(FeedFragment.POSTID);
        saleType = intent.getExtras().getString(FeedFragment.SALETYPE);

        super.onCreate(savedInstanceState);

        switch (saleType)
        {
            case "Auction":
                setContentView(R.layout.activity_full_post_auction);
                break;
            case "Fixed Price":
                setContentView(R.layout.activity_full_post_sale);
                break;
            default:
                setContentView(R.layout.activity_full_post_free);
                break;
        }


        this.pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        new PostTask(FullPostActivity.this).execute(postId, count);

        mViewFlipper = (ViewFlipper) findViewById(R.id.fullPostViewFlipper);
        if (count == 0)
        {
            mViewFlipper.setVisibility(View.INVISIBLE);
        } else
        {
            final GestureDetector detector = new GestureDetector(FullPostActivity.this, new SwipeGestureDetector(FullPostActivity.this, mViewFlipper));
            mViewFlipper.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(final View view, final MotionEvent event)
                {
                    detector.onTouchEvent(event);
                    return true;
                }
            });
        }


        final EditText offer = (EditText) findViewById(R.id.edit_bid);
        final EditText message = (EditText) findViewById(R.id.addMessage);


        final Button actionButton;
        switch (saleType)
        {
            case "Auction":
                actionButton = (Button) findViewById(R.id.button_bid);
                break;
            case "Fixed Price":
                actionButton = (Button) findViewById(R.id.button_buy);
                break;
            default:
                actionButton = (Button) findViewById(R.id.button_free);
        }
        actionButton.setEnabled(true);
        actionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (offer.getText().toString().equals(""))
                {
                    Toast toast = Toast.makeText(FullPostActivity.this, "You didn't make an offer, peasant.", Toast.LENGTH_SHORT);
                    toast.show();
                } else
                {

                    newBid = new JSONObject();
                    switch (saleType)
                    {
                        case "Auction":
                            final Double mOffer = Double.parseDouble(offer.getText().toString());
                            if (mOffer <= currentBid)
                            {
                                Toast toast = Toast.makeText(FullPostActivity.this, "Your bid is lower than the current offer, moneybags.", Toast.LENGTH_SHORT);
                                toast.show();
                            } else
                            {
                                new AlertDialog.Builder(FullPostActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Bid")
                                        .setMessage("Are you sure you want to bid $" + String.format("%.2f", mOffer) + " on this item?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                actionButton.setEnabled(false);
                                                String mMessage = message.getText().toString();
                                                try
                                                {
                                                    newBid.put("bid", mOffer);
                                                    newBid.put("message", mMessage);
                                                    newBid.put("userId", 2);
                                                    newBid.put("itemId", postId);
                                                } catch (JSONException e)
                                                {
                                                    e.printStackTrace();
                                                }

                                                new BidTask().execute(newBid);
                                            }

                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                            }
                            break;
                        case "Fixed Price":

                            break;
                        default:
                            break;
                    }
                }

            }
        });

    }

    private class BidTask extends AsyncTask<JSONObject, Void, Void>
    {

        @Override
        protected Void doInBackground(JSONObject... params)
        {
            HttpURLConnection con;
            try
            {
                URL url = new URL("http://speffs.one/SPE/postBid.php");

                con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setChunkedStreamingMode(0);
                con.connect();
                con.setRequestMethod("POST");

                OutputStream out = new BufferedOutputStream(con.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                JSONObject object = params[0];
                writer.write(object.toString());
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
            Toast toast = Toast.makeText(FullPostActivity.this, "Nice bid, bro!", Toast.LENGTH_SHORT);
            toast.show();

            finish();
            startActivity(getIntent());
        }
    }

    private class PostTask extends AsyncTask<Integer, Void, Post>
    {
        private Activity mActivity;
        private int count;
        Post post = new Post();


        private PostTask(Activity activity)
        {
            this.mActivity = activity;
        }

        @Override
        protected void onPreExecute()
        {
            pb.setVisibility(View.VISIBLE);
            pb.incrementProgressBy(20);
        }

        @Override
        protected Post doInBackground(Integer... params)
        {
            int postId = params[0];
            count = params[1];
            Log.d("PostID", Integer.toString(postId));

            JSONObject postIdJSON = new JSONObject();


            try
            {
                postIdJSON.put("id", postId);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            Log.d("postIdJSON", postIdJSON.toString());

            BufferedReader reader = null;
            try
            {
                URL url = new URL("http://speffs.one/SPE/getPostWithBids.php");

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setChunkedStreamingMode(0);
                con.connect();
                con.setRequestMethod("GET");
                con.setReadTimeout(15000);


                OutputStream out = new BufferedOutputStream(con.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                writer.write(postIdJSON.toString());
                writer.flush();
                writer.close();

                pb.incrementProgressBy(30);

                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }

                String fullPostJSON = sb.toString();

                Log.d("fullPostList", fullPostJSON);

                JSONArray jsonArray = new JSONArray(fullPostJSON);
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                post.setPostId(jsonObject.getInt("itemId"));
                post.setTitle(jsonObject.getString("postTitle"));
                post.setPrice(jsonObject.getString("price"));
                post.setBid(jsonObject.getString("bid"));

                post.setCategory(jsonObject.getString("category"));
                post.setDescription(jsonObject.getString("description"));
                post.setPhotos(jsonObject.getString("photo1"),
                        jsonObject.getString("photo2"),
                        jsonObject.getString("photo3"));
                post.setCount(jsonObject.getInt("count"));
                post.setUserId(jsonObject.getInt("userId"));
                post.setSaleType(jsonObject.getString("saleType"));

                pb.incrementProgressBy(30);
                return post;

            } catch (Exception e)
            {
                e.printStackTrace();
                return null;

            } finally
            {
                if (reader != null)
                {
                    try
                    {
                        reader.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

        }

        @Override
        protected void onPostExecute(Post post)
        {
            //posty = post;
            currentBid = post.getBid();

            switch (saleType)
            {
                case "Auction":
                    TextView bidView = (TextView) findViewById(R.id.currentBid);
                    bidView.setText("Current Bid: $" + String.format("%.2f", post.getBid()));
                    break;
                case "Fixed Price":
                    TextView priceView = (TextView) findViewById(R.id.price);
                    priceView.setText("Price: $" + String.format("%.2f", post.getPrice()));
                    break;
                default:
                    TextView freePriceView = (TextView) findViewById(R.id.freePrice);
                    freePriceView.setText("Price: FREE!");
                    break;
            }

            TextView descriptionView = (TextView) findViewById(R.id.fullPostDescription);
            descriptionView.setText("Description:" + "\n" + post.getDescription());

            Bitmap[] bitmaps = new Bitmap[count];

            if (count > 0)
            {
                String photo1 = post.getPhoto1();
                Bitmap photo1View = base64toBitmap(photo1);
                bitmaps[0] = photo1View;
                if (count > 1)
                {
                    String photo2 = post.getPhoto2();
                    Bitmap photo2View = base64toBitmap(photo2);
                    bitmaps[1] = photo2View;
                    if (count > 2)
                    {
                        String photo3 = post.getPhoto3();
                        Bitmap photo3View = base64toBitmap(photo3);
                        bitmaps[2] = photo3View;
                    }
                }

            } else
            {
                //put blank image in imageview
            }
            setupImageSamples(bitmaps);

            pb.setVisibility(View.INVISIBLE);
        }
    }

    private void setupImageSamples(Bitmap[] bitmap)
    {
        Log.d("BITMAP ARRAY", Integer.toString(bitmap.length));
        //fill with the adapter and stuff for our new imageviewer
        for (int i = 0; i < bitmap.length; i++)
        {
            ImageView imageView = new ImageView(FullPostActivity.this);
            imageView.setImageBitmap(bitmap[i]);
            mViewFlipper.addView(imageView);
        }
    }

    private Bitmap base64toBitmap(String base64)
    {
        byte[] imageAsBytes = Base64.decode(base64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_full_post, menu);
        getSupportActionBar().setTitle(postTitle);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
