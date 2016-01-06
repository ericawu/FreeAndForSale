package com.spe.luke.freeandgood;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luke on 7/6/2015.
 */
public class PostJSONParser
{
    // The list will be what we want from the database
    public static List<Post> parseFeed(String content)
    {
        try
        {
            JSONArray jsonArray = new JSONArray(content);
            List<Post> postList = new ArrayList<>();

            Log.d("********JSON LENGTH", Integer.toString(jsonArray.length()));
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Post post = new Post();



                post.setPostId(jsonObject.getInt("itemId"));
                post.setTitle(jsonObject.getString("postTitle"));
                post.setPrice(jsonObject.getString("price"));
                post.setBid(jsonObject.getString("bid"));
                post.setCategory(jsonObject.getString("category"));
//                post.setDescription(jsonObject.getString("description"));
//                post.setPhotos(jsonObject.getString("photo1"),
//                    jsonObject.getString("photo2"),
//                    jsonObject.getString("photo3"));
                post.setFeedPhoto(jsonObject.getString("feedPhoto"));
                post.setCount(jsonObject.getInt("count"));
                post.setUserId(jsonObject.getInt("userId"));
                post.setSaleType(jsonObject.getString("saleType"));



                postList.add(post);

                Log.d("******** POST ADDED", postList.toString());
            }
            return postList;

        } catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
