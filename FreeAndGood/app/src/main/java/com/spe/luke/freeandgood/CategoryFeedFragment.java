package com.spe.luke.freeandgood;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spe.luke.freeandgood.bitmaploading.BitmapCache;
import com.spe.luke.freeandgood.bitmaploading.BitmapDecodeResize;
import com.spe.luke.freeandgood.bitmaploading.ImageLoaderTask;

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

public class CategoryFeedFragment extends Fragment
{
    private RecyclerView recyclerView;
    ProgressBar pb;
    public List<Post> postList;
    FeedGridAdapter feedGridAdapter;
    String saleType;
    private LruCache<String, Bitmap> mMemoryCache;

    public final static String POSTID = "com.spe.luke.freeandgood.POSTID";
    public final static String PHOTOCOUNT = "com.spe.luke.freeandgood.PHOTOCOUNT";
    public final static String POST_TITLE = "com.spe.luke.freeandgood.POST_TITLE";
    public final static String SALETYPE = "com.spe.luke.freeandgood.SALETYPE";

    public CategoryFeedFragment()
    {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View layout = inflater.inflate(R.layout.fragment_feed, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.feed_view);

        // makes spacing for each griditem
        recyclerView.addItemDecoration(new SpacesItemDecoration(8));
        recyclerView.setHasFixedSize(true);
        // Using Grid Layout Manager to gte grid effect
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(glm);


        CategoryFeedFragment.this.pb = (ProgressBar) layout.findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);


        String category = getArguments().getString("Category");
        new PostTask(getActivity(), recyclerView).execute(category);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(View view, int position)
                    {

                        Post post = postList.get(position);


                        int postId = post.getPostId();
                        int count = post.getCount();

                        Intent intent = new Intent(getActivity(), FullPostActivity.class);

                        intent.putExtra(PHOTOCOUNT, count);
                        intent.putExtra(POSTID, postId);
                        intent.putExtra(POST_TITLE, post.getTitle());
                        intent.putExtra(SALETYPE, post.getSaleType());

                        startActivity(intent);
                    }
                })
        );

        return layout;
    }


    // adds space between each item
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration
    {
        private int space;

        public SpacesItemDecoration(int space)
        {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
        {
            outRect.left = space / 2;
            outRect.right = space / 2;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == 0)
                outRect.top = space;
        }
    }

    private class PostTask extends AsyncTask<String, String, List<Post>>
    {
        RecyclerView mRecyclerView;
        Activity mContext;


        public PostTask(FragmentActivity context, RecyclerView recyclerView)
        {
            this.mRecyclerView = recyclerView;
            this.mContext = context;
        }

        @Override
        protected void onPreExecute()
        {
            CategoryFeedFragment.this.pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Post> doInBackground(String... params)
        {
            // sending information
            String category = params[0];
            JSONObject categoryJSON = new JSONObject();
            try
            {
                categoryJSON.put("category", category);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            BufferedReader reader = null;
            try
            {
                URL url = new URL("http://speffs.one/SPE/getByCategory.php");

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setChunkedStreamingMode(0);
                con.connect();
                con.setRequestMethod("GET");
                con.setConnectTimeout(15000);

                OutputStream out = new BufferedOutputStream(con.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                writer.write(categoryJSON.toString());
                writer.flush();
                writer.close();

                // receiving information
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }

                String fullPostJSON = sb.toString();

                Log.d("CategoryJSON", fullPostJSON);

                postList = PostJSONParser.parseFeed(fullPostJSON);
                Log.d("NumberofPosts", postList.toString());

                return postList;

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
        protected void onPostExecute(List<Post> listy)
        {
            listy = postList;
            CategoryFeedFragment.this.pb.setVisibility(View.INVISIBLE);

            feedGridAdapter = new FeedGridAdapter(getActivity(), listy);
            recyclerView.setAdapter(feedGridAdapter);
        }
    }

    public class FeedGridAdapter extends RecyclerView.Adapter<FeedGridAdapter.FeedViewHolder>
    {
        private LayoutInflater inflater;
        private Context context;


        public FeedGridAdapter(Context context, List<Post> listPosts)
        {

            this.context = context;
            postList = listPosts;

            inflater = LayoutInflater.from(getActivity());
        }


        @Override
        public FeedViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
        {
            View view = inflater.inflate(R.layout.grid_item_layout, viewGroup, false);
            FeedViewHolder holder = new FeedViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(FeedViewHolder viewHolder, int position)
        {
            Post currentPost = postList.get(position);

            saleType = currentPost.getSaleType();

            // In the parenthesis, put in the information for things we pull form the database.
            viewHolder.title.setText(currentPost.getTitle());


            String base64 = currentPost.getFeedPhoto();

            Bitmap bitmapForDisplay = new BitmapDecodeResize().decodeSampledBitmapFromResource(base64, 150, 150);
            viewHolder.image.setImageBitmap(bitmapForDisplay);

            switch (saleType)
            {
                case "Free":
                    viewHolder.saleType.setImageResource(R.drawable.free_icon);
                    viewHolder.price.setText("$" + String.format("%.2f", (currentPost.getPrice())));
                    break;
                case "Auction":
                    viewHolder.saleType.setImageResource(R.drawable.bid_icon);
                    viewHolder.price.setText("$" + String.format("%.2f", (currentPost.getBid())));
                    break;
                case "Fixed Price":
                    viewHolder.saleType.setImageResource(R.drawable.shoppingcart);
                    viewHolder.price.setText("$" + String.format("%.2f", (currentPost.getPrice())));
                    break;
            }
        }

        @Override
        public int getItemCount()
        {
            return postList.size();
        }

        public class FeedViewHolder extends RecyclerView.ViewHolder
        {
            public TextView title;
            public TextView price;
            public ImageView image;
            public ImageView saleType;

            public FeedViewHolder(View itemView)
            {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.feed_title);
                price = (TextView) itemView.findViewById(R.id.feed_price);
                image = (ImageView) itemView.findViewById(R.id.feed_image);
                saleType = (ImageView) itemView.findViewById(R.id.sale_type);
            }
        }

    }
}
