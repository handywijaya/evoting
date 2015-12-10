package com.example.handy.audy.daud.alfian.prototype_lomba.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;
import com.example.handy.audy.daud.alfian.prototype_lomba.gcm.service.QuickstartPreferences;
import com.example.handy.audy.daud.alfian.prototype_lomba.gcm.service.RegistrationIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "LoginActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    //private ProgressBar mRegistrationProgressBar;
    //private TextView mInformationTextView;

    private Boolean servicesExist;

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

        txtPassword.requestFocus();

        servicesExist = checkPlayServices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    class LoginUser extends AsyncTask<String,String,String> {
        int success2 = 0, success3 = 0;
        JSONObject jsonObject3;

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

                if(success2 >= 1) {
                    idUser = jsonObject2.getString("idUser");

                    args = new ArrayList<NameValuePair>();
                    args.add(new BasicNameValuePair("tag", "get_kampung"));
                    args.add(new BasicNameValuePair("idKtp", idKtp));
                    jsonObject3 = jsonParser.makeHttpRequest(urlWebService,"POST",args);

                    success3 = jsonObject3.getInt(TAG_SUCCESS);
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
            if(success2 >= 1) {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("idKtp",idKtp);
                editor.putString("idUser", idUser);
                editor.putBoolean("logged_in", true);
                editor.commit();

                if(success3 >= 1){
                    if (servicesExist) {
                        // Start IntentService to register this application with GCM.
                        RegistrationIntentService register = new RegistrationIntentService();
                        JSONObject kampung;
                        String[] topics = new String[4];
                        try{
                            kampung = jsonObject3.getJSONObject("items");
                            topics[0] = kampung.getString("KECAMATAN") + "_" + kampung.getString("KELURAHAN") + "_" + kampung.getString("RW") + "_" + kampung.getString("RT");
                            topics[1] = kampung.getString("KECAMATAN") + "_" + kampung.getString("KELURAHAN") + "_" + kampung.getString("RW");
                            topics[2] = kampung.getString("KECAMATAN") + "_" + kampung.getString("KELURAHAN");
                            topics[3] = kampung.getString("KECAMATAN");
                            //register.setTOPICS(topics);

                            mRegistrationBroadcastReceiver = new ListenerBroadcastReceiver();
                            registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

                            Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
                            intent.putExtra("topics", topics);
                            startService(intent);

                            Toast.makeText(getApplicationContext(), "Login sukses!", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this, "Terdapat kesalahan dalam pengambilan data", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(LoginActivity.this, "Password anda salah", Toast.LENGTH_SHORT).show();
            }

            pDialog.dismiss();
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public class ListenerBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);
            boolean sentToken = sharedPreferences
                    .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
            if (sentToken) {
                Intent i = new Intent(context,MainActivity.class);
                i.putExtra("idKtp",idKtp);
                i.putExtra("idUser",idUser);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(context, "Terdapat kesalahan dalam konfirmasi token", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
