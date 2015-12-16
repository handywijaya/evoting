package com.example.handy.audy.daud.alfian.prototype_lomba.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.example.handy.audy.daud.alfian.prototype_lomba.jsonparser.JSONParser;

/**
 * Created by Audy on 08-Dec-15.
 */
public class BaseActivity extends AppCompatActivity
{
    protected JSONParser jsonParser = new JSONParser();
    protected static String urlWebService = "http://beligameori.com/api/index.php";
    //protected static String urlWebService = "http://10.0.2.2:8079/Lomba/index.php";
    protected static final String TAG_SUCCESS = "success";
    protected static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
