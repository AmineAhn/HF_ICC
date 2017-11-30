package com.android.aminopia.hf_icc;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class ActivityLogin extends AppCompatActivity
{

    public LoginButton     loginButton       ;
    public CallbackManager callbackManager   ;
    public String[]        permissions       ;
    public PrefUtil        prefUtil          ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate   (savedInstanceState)     ;
        setContentView   (R.layout.activity_login);
        initializeWidgets()                       ;
        setupPermissions (permissions)            ;
        registerCallback ()                       ;



    }



    private void registerCallback()
    {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                Toast.makeText(getApplicationContext(), "ACTLOGIN > LOGINRESULT > ONSUCCESS >" , Toast.LENGTH_LONG).show();
                String accessToken = loginResult.getAccessToken().getToken();
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback()
                {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse graphResponse)
                    {
                        getFacebookData(object);
                    }
                });
                Bundle parameters = new Bundle()                           ;
                parameters.putString("fields", "id,email,birthday,friends");
                graphRequest.setParameters(parameters)                     ;
                graphRequest.executeAsync()                                ;
            }

            @Override
            public void onCancel()
            {
                Toast.makeText(getApplicationContext(), "ACTLOGIN > LOGINRESULT > ONCANCEL >" , Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(FacebookException error)
            {
                Toast.makeText(getApplicationContext(), "ACTLOGIN > LOGINRESULT > ONERROR >" , Toast.LENGTH_LONG).show();

            }
        });
    }

    private void setupPermissions(String[] permissions)
    {
        loginButton.setReadPermissions(Arrays.asList(permissions));
    }

    private void initializeWidgets()
    {
        loginButton     = findViewById(R.id.login_button)              ;
        callbackManager = CallbackManager.Factory.create()             ;
        permissions     = new String[]{"public_profile", "user_photos"};
        prefUtil        = new PrefUtil(getParent())                    ;
    }



    private Bundle getFacebookData(JSONObject object)
    {
        Bundle bundle = new Bundle();
        try {
            String id = object.getString("id");
            URL profile_pic;
            try
            {
                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=200");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
            } catch (MalformedURLException e)
            {
                e.printStackTrace();
                return null;
            }
            bundle.putString("idFacebook", id);
            if (object.has("first_name"))bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))bundle.putString("email", object.getString("email"));
            if (object.has("gender"))bundle.putString("gender", object.getString("gender"));
            prefUtil.saveFacebookUserInfo(object.getString("first_name"),
                    object.getString("last_name"),object.getString("email"),
                    object.getString("gender"), profile_pic.toString());
            } catch (Exception e)
        {
            Log.d("EXCEPTION_BUNDLE", "BUNDLE Exception : "+e.toString());
        }
        return bundle;
    }



    private void printKeyHash()
    {
        // I USED IT ONLY ONCE
        try
        {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.amine.hf_challenge", PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures)
            {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(messageDigest.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
