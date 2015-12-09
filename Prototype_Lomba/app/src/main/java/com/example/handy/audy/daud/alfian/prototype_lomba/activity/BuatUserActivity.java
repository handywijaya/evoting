package com.example.handy.audy.daud.alfian.prototype_lomba.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class BuatUserActivity extends BaseActivity {

    ProgressDialog pDialog;

    EditText txtIdKtp, txtEmail, txtPassword, txtConfirmPassword;
    Button btnBuatAkun;

    String idKtp,email,idUser,password,confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_user);
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

        txtEmail = (EditText)findViewById(R.id.txtEmail);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText)findViewById(R.id.txtConfirmPassword);

        btnBuatAkun = (Button)findViewById(R.id.btnBuatAkun);
        btnBuatAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = txtEmail.getText().toString();
                password = txtPassword.getText().toString();
                confirmPassword = txtConfirmPassword.getText().toString();
                if(password.equals(confirmPassword) && !password.equalsIgnoreCase("")) {
                    new CreateUser().execute();
                }
                else {
                    if(password.equalsIgnoreCase("")) {
                        txtPassword.setError("Sandi wajib diisi");
                    }
                    if(confirmPassword.equalsIgnoreCase("")) {
                        txtConfirmPassword.setError("Konfirmasi Sandi wajib diisi");
                    }
                    else if(!password.equals(confirmPassword)) {
                        txtConfirmPassword.setError("Konfirmasi Sandi tidak cocok dengan Sandi");
                    }
                }
            }
        });

    }

    class CreateUser extends AsyncTask<String,String,String> {
        int success2 = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BuatUserActivity.this);
            pDialog.setMessage("Membuat akun....");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("tag", "create_user"));
            args.add(new BasicNameValuePair("idKtp", idKtp));
            args.add(new BasicNameValuePair("email", email));
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
                Toast.makeText(BuatUserActivity.this, "User berhasil dibuat cuy", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                i.putExtra("idKtp",idKtp);
                i.putExtra("idUser",idUser);
                startActivity(i);
                finish();

            }
            else {
                Toast.makeText(BuatUserActivity.this, "Wah, ada kesalahan teknis nih cuy", Toast.LENGTH_SHORT).show();
            }

            pDialog.dismiss();
        }
    }

}
