package com.spe.luke.freeandgood;

import java.io.*;

public class Post
{

    private double price;
    private double bid;
    private String description;
    private String category;
    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;
    private String tag5;
    private String title;
    private int postId;
    private int userId;
    private int count;
    private String time;
    private String imag1, imag2, imag3;
    private String feedPhoto;
    private String saleType;

    public Post()
    {
    }

    // set the price instance
    public void setTime(String string)
    {
        this.time = string;
    }

    // set the price instance
    public void setPrice(String price)
    {
        this.price = Double.parseDouble(price);
    }

    public void setBid(String bid)
    {
        this.bid = Double.parseDouble(bid);
    }

    // set the title
    public void setTitle(String title)
    {
        this.title = title;
    }

    // set the description
    public void setDescription(String desc)
    {
        this.description = desc;
    }

    // set category
    public void setCategory(String cate)
    {
        this.category = cate;
    }

    public void setPostId(int postId)
    {
        this.postId = postId;
    }

    public void setUserId(int UserId)
    {
        this.userId = UserId;
    }

    public void setCount(int count) {this.count = count;}

    public void setSaleType(String saleType) { this.saleType = saleType; }

    // set photos from database
    public void setPhotos(String a, String b, String c)
    {
        imag1 = a;
        imag2 = b;
        imag3 = c;
    }

    public void setFeedPhoto(String feedPhoto)
    {
        this.feedPhoto = feedPhoto;
    }

    public void setTags(String t1, String t2, String t3, String t4, String t5)
    {
        tag1 = t1;
        tag2 = t2;
        tag3 = t3;
        tag4 = t4;
        tag5 = t5;
    }


    public String getTag1()
    {
        return this.tag1;
    }

    public String getTag2()
    {
        return this.tag2;
    }

    public String getTag3()
    {
        return this.tag3;
    }

    public String getTag4()
    {
        return this.tag4;
    }

    public String getTag5()
    {
        return this.tag5;
    }

    public String getCategory()
    {
        return this.category;
    }

    public double getPrice()
    {
        return this.price;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getTitle()
    {
        return this.title;
    }

    public String getTime()
    {
        return this.time;
    }

    public String getPhoto1()
    {
        return this.imag1;
    }

    public String getPhoto2()
    {
        return this.imag2;
    }

    public String getPhoto3()
    {
        return this.imag3;
    }

    public String getFeedPhoto() {return this.feedPhoto;}

    public int getPostId()
    {
        return this.postId;
    }

    public int getUserId()
    {
        return this.userId;
    }

    public int getCount() { return this.count; }

    public double getBid() { return this.bid; }

    public String getSaleType() { return this.saleType; }


//	public static void main(String[] args) {
//		// test the Post data type
//		Post myPost = new Post();
//		myPost.setCategory("My Right");
//		myPost.UserId = 78;
//		myPost.setPrice("56.90");
//		myPost.setDescription("I am selling beans");
//		myPost.setTitle("Rat");
//
//		//myPost.postId = 15;
//
//		String image1 = "/Users/felixmadutsa/Desktop/images.jpg";
//		String image2 = "/Users/felixmadutsa/Desktop/images.jpg";
//		String image3 = "/Users/felixmadutsa/Desktop/images.jpg";
//
//		myPost.setPhotos(image1, image2, image3);
//		//myPost.deletePost();
//		myPost.addPost();
//		//myPost.editPost();
//		System.out.println(myPost.getPostId());
//		}
}
