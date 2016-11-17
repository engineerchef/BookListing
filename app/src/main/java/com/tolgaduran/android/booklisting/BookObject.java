package com.tolgaduran.android.booklisting;

/**
 * Created by Java_Engineer on 16.11.2016.
 */

public class BookObject {

    private String mTitle;
    private String mAurthor;
    private String mDescription;
    private String mUrl;
    private String mThumbNailUrl;

    public BookObject(String title, String author, String description, String url, String thumbNailUrl){

        mTitle = "";
        mAurthor = "";
        mDescription = "";
        mUrl = "";
        mThumbNailUrl = "";

        mTitle = title;
        mAurthor = author;
        mDescription = description;
        mUrl = url;
        mThumbNailUrl = thumbNailUrl;
    }

    public void setTitle(String title){
        this.mTitle = title;
    }

    public void setAurthor(String author){
        this.mAurthor = author;
    }

    public void setDescription(String description){
        this.mDescription = description;
    }

    public void setUrl(String url){
        this.mUrl = url;
    }

    public void setThumbNailUrl(String url){
        this.mThumbNailUrl = url;
    }

    public String getTitle() {return mTitle;}

    public String getAuthor(){
        return mAurthor;
    }

    public String getDescription(){
        return mDescription;
    }

    public String getUrl(){
        return  mUrl;
    }

    public String getThumbNailUrl() {return mThumbNailUrl;}
}
