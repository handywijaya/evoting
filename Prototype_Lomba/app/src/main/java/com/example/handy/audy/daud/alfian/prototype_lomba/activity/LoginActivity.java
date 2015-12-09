package com.example.handy.audy.daud.alfian.prototype_lomba.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity {

    ProgressDialog pDialog;

    EditText txtIdKtp, txtPassword;
    Button btnLogin;

    String idKtp,idUser,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        idKtp = getIntent().getStringExtra("idKtp");

        txtIdKtp = (EditText)findViewById(R.id.txtIdKtp);
        txtIdKtp.setText(idKtp);

        txtPassword = (EditText)findViewById(R.id.txtPassword);

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = txtPassword.getText().toString();
                if(password.equalsIgnoreCase("")) {
                    txtPassword.setError("Sandi wajib diisi");
                }
                else {
                    new LoginUser().execute();
                }
            }
        });
    }

    class LoginUser extends AsyncTask<String,String,String> {
        int success2 = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Menunggu....");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("tag", "login_user"));
            args.add(new BasicNameValuePair("idKtp", idKtp));
            args.add(new BasicNameValuePair("password", password));
            JSONObject jsonObject2 = jsonParser.makeHttpRequest(urlWebService,"POST",args);

            try {
                success2 = jsonObject2.getInt(TAG_SUCCESS);

                if(success2 == 1) {
                    idUser = jsonObject2.getString("idUser");
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            if(success2 == 1) {
                Toast.makeText(LoginActivity.this, "Login berhasil cuy", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                i.putExtra("idKtp",idKtp);
                i.putExtra("idUser",idUser);
                startActivity(i);
                finish();

            }
            else {
                Toast.makeText(LoginActivity.this, "Password anda salah", Toast.LENGTH_SHORT).show();
            }

            pDialog.dismiss();
        }
    }

}
