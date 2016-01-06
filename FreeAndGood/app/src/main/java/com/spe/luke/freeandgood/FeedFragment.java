package com.spe.luke.freeandgood;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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

import java.util.List;

/**
 * Created by Luke on 6/24/2015.
 */
public class FeedFragment extends Fragment
{
    private RecyclerView recyclerView;
    ProgressBar pb;
    String saleType;
    public List<Post> postList;
    public FeedGridAdapter feedGridAdapter;
    private LruCache<String, Bitmap> mMemoryCache;

    public final static String POSTID = "com.spe.luke.freeandgood.POSTID";
    public final static String PHOTOCOUNT = "com.spe.luke.freeandgood.PHOTOCOUNT";
    public final static String POST_TITLE = "com.spe.luke.freeandgood.POST_TITLE";
    public final static String SALETYPE = "com.spe.luke.freeandgood.SALETYPE";

    public FeedFragment()
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

        FeedFragment.this.pb = (ProgressBar) layout.findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);
        new PostTask(getActivity(), recyclerView).execute("");

        // start caching of bitmaps
        BitmapCache.InitBitmapCache();

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(View view, int position)
                    {
                        Post post = postList.get(position);

                        int postId = post.getPostId();
                        int count = post.getCount();

//                        String photo1 = post.getPhoto1();
//                        String photo2 = post.getPhoto2();
//                        String photo3 = post.getPhoto3();

                        Intent intent = new Intent(getActivity(), FullPostActivity.class);
                        Log.d("COUNT", Integer.toString(count));
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
            FeedFragment.this.pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Post> doInBackground(String... params)
        {
            HttpManagerGET manager = new HttpManagerGET();
            String data = manager.getData("http://speffs.one/SPE/getPostsWithBids.php");
            FeedFragment.this.postList = PostJSONParser.parseFeed(data);

            return postList;
        }

        @Override
        protected void onPostExecute(List<Post> listy)
        {
            listy = postList;
            FeedFragment.this.pb.setVisibility(View.INVISIBLE);

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

            inflater = LayoutInflater.from(context);
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

        // load Bitmap either from our cache or asynchronously
        public void loadBitmap(ImageView imageView, Integer position, FeedViewHolder holder, String base64)
        {

            Bitmap bitmap;
            bitmap = BitmapCache.getBitmapFromMemCache(position);


            if (bitmap != null)
            {
                imageView.setImageBitmap(bitmap);
            } else
            {
                new ImageLoaderTask(position, holder,
                        imageView, base64, feedGridAdapter).executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, (Integer[]) null);
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