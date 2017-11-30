package com.android.aminopia.hf_icc;

import java.util.ArrayList;

/**
 * Created by Amine on 11/29/2017.
 */

public class FacebookAlbum
{
    private String albumID;
    private ArrayList<Images> albumImgs;

    public FacebookAlbum()
    {
        super();
    }
    public FacebookAlbum(String albumID) {
        this.albumID = albumID;
    }

    public FacebookAlbum(ArrayList<Images> albumImgs) {
        this.albumImgs = albumImgs;
    }

    public FacebookAlbum(String albumID, ArrayList<Images> albumImgs) {
        this.albumID = albumID;
        this.albumImgs = albumImgs;
    }

    public String getAlbumID() {
        return albumID;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    public ArrayList<Images> getAlbumImgs() {
        return albumImgs;
    }

    public void setAlbumImgs(ArrayList<Images> albumImgs) {
        this.albumImgs = albumImgs;
    }


}
