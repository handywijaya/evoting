package com.example.handy.audy.daud.alfian.prototype_lomba.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.example.handy.audy.daud.alfian.prototype_lomba.jsonparser.JSONParser;

/**
 * Created by Audy on 08-Dec-15.
 */
public class BaseActivity extends AppCompatActivity
{
    protected JSONParser jsonParser = new JSONParser();
    //protected static String urlWebService = "http://xalvsx.esy.es/api/index.php";
    protected static String urlWebService = "http://10.0.2.2:8079/Lomba/index.php";
    protected static final String TAG_SUCCESS = "success";
}
