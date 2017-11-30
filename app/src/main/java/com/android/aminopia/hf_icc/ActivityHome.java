package com.android.aminopia.hf_icc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityHome extends AppCompatActivity {

    public ImageView ivProfilePicture;
    public TextView txtFullname      ;
    public LoginButton loginButton   ;
    public Button btnViewAblums      ;

    public CallbackManager callbackManager;
    public ArrayList<FacebookAlbum> albums   ;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
        initializeWidgets();
        albums =               GetFacebookAlbums();
    }

    private void initializeWidgets()
    {
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        txtFullname      = findViewById(R.id.txtFullname)     ;
        loginButton      = findViewById(R.id.login_button)    ;
        btnViewAblums    = findViewById(R.id.btnViewAlbums)   ;

        callbackManager  = CallbackManager.Factory.create()   ;
    }


    private ArrayList<FacebookAlbum> GetFacebookAlbums()
    {
        ArrayList<FacebookAlbum> facebookAlbums = new ArrayList<>();
        new GraphRequest(AccessToken.getCurrentAccessToken(),  //your fb AccessToken
                "/" + AccessToken.getCurrentAccessToken().getUserId() + "/albums",//user id of login user
                null,
                HttpMethod.GET,
                new GraphRequest.Callback()
                {
                    public void onCompleted(GraphResponse response) {
                        Log.d("TAG", "Facebook Albums: " + response.toString());
                        try
                        {
                            if (response.getError() == null)
                            {
                                JSONObject joMain = response.getJSONObject(); //convert GraphResponse response to JSONObject
                                if (joMain.has("data"))
                                {
                                    JSONArray jaData = joMain.optJSONArray("data"); //find JSONArray from JSONObject
                                    for (int i = 0; i < jaData.length(); i++)
                                    {//find no. of album using jaData.length()
                                        FacebookAlbum fbAlbum = new FacebookAlbum();
                                        JSONObject joAlbum = jaData.getJSONObject(i); //convert perticular album into JSONObject
                                        String fbAlbumID = joAlbum.optString("id");
                                        fbAlbum.setAlbumID(fbAlbumID);
                                        fbAlbum.setAlbumImgs(GetFacebookImages(fbAlbumID)); //find Album ID and get All Images from album
                                    }
                                }
                            }
                            else
                            {
                                Log.d("Test", response.getError().toString());
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
        return facebookAlbums;
    }

    public ArrayList<Images> GetFacebookImages(final String albumId)
    {

        final ArrayList<Images> images     = new ArrayList<>();
        Bundle            parameters = new Bundle()     ;
        parameters.putString("fields", "images")        ;
        /* make the API call */
        new GraphRequest(AccessToken.getCurrentAccessToken(),
                "/" + albumId + "/photos",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback()
                {
                    public void onCompleted(GraphResponse response)
                    {
                            /* handle the result */
                        Log.v("TAG", "Facebook Photos response: " + response);
                        try
                        {
                            if (response.getError() == null)
                            {
                                JSONObject joMain = response.getJSONObject();
                                if (joMain.has("data"))
                                {
                                    JSONArray jaData = joMain.optJSONArray("data");
                                    for (int i = 0; i < jaData.length(); i++)//Get no. of images
                                    {
                                        JSONObject joAlbum = jaData.getJSONObject(i);
                                        JSONArray jaImages=joAlbum.getJSONArray("images");
                                        //get images Array in JSONArray format
                                        if(jaImages.length()>0)
                                        {
                                            Images objImage = new Images();//Images is custom class with string url field
                                            objImage.setImgUrl(jaImages.getJSONObject(0).getString("source"));
                                            images.add(objImage);//lstFBImages is Images object array
                                        }
                                    }
                                    //set your adapter here
                                }
                            }
                            else
                            {
                                Log.v("TAG", response.getError().toString());
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
        return images;
    }


}
