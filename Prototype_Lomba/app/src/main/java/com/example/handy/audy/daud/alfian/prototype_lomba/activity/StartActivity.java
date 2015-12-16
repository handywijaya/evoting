package com.example.handy.audy.daud.alfian.prototype_lomba.activity;

import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.Soal;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StartActivity extends BaseActivity {

    ProgressDialog pDialog;
    EditText txtIdKtp;
    Button btnLogin;
    String idKtp,idUser;

    NfcAdapter nfc;
    String[][] techListsArray;
    IntentFilter[] intentFiltersArray;
    PendingIntent pendingIntent;

    ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ivLogo = (ImageView)findViewById(R.id.ivLogo);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        boolean logged_in = sharedPreferences.getBoolean("logged_in", false);
        if(logged_in) {
            idKtp = sharedPreferences.getString("idKtp",null);
            idUser = sharedPreferences.getString("idUser",null);
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            i.putExtra("idKtp",idKtp);
            i.putExtra("idUser",idUser);
            if(getIntent().getStringExtra("idSoal") != null) {
                Log.e("idSoal","ada id soal nih");
                i.putExtra("idSoal",getIntent().getStringExtra("idSoal"));
            }
            startActivity(i);
            finish();
        }

        txtIdKtp = (EditText)findViewById(R.id.txtIdKtp);

        //nanti hapus
        btnLogin = (Button)findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idKtp = txtIdKtp.getText().toString();
                new CheckKtp().execute();
            }
        });
        //nanti hapus

        nfc = NfcAdapter.getDefaultAdapter(this);

        /* nanti buka
        if (nfc == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }*/

        if(nfc!=null) {
            pendingIntent = PendingIntent.getActivity(
                    this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            try {
                ndef.addDataType("text/plain");
            } catch (IntentFilter.MalformedMimeTypeException e) {
                throw new RuntimeException("fail", e);
            }
            intentFiltersArray = new IntentFilter[]{ndef};

            techListsArray = new String[][]{new String[]{NfcF.class.getName()}};
        }

    }

    private void readIdKtp() {
        idKtp = txtIdKtp.getText().toString();
        new CheckKtp().execute();
    }

    public void onPause() {
        super.onPause();
        if(nfc != null) {
            nfc.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onResume() {
            super.onResume();
        if(nfc != null) {
            nfc.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        }
    }

    public void onNewIntent(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        new NdefReaderTask().execute(tagFromIntent);
        //do something with tagFromIntent
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            try {
                Tag tag = params[0];

                Ndef ndef = Ndef.get(tag);
                if (ndef == null) {

                    // NDEF is not supported by this Tag.
                    return null;
                }
                NdefMessage ndefMessage = ndef.getCachedNdefMessage();

                NdefRecord[] records = ndefMessage.getRecords();
                for (NdefRecord ndefRecord : records) {
                    if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                        try {
                            return readText(ndefRecord);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            byte[] payload = record.getPayload();
            String a = "UTF-8";
            String b = "UTF-16";
            String textEncoding = ((payload[0] & 128) == 0) ? a : b;
            int languageCodeLength = payload[0] & 0063;

            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if(result.equals("kosong")) {
                    Toast.makeText(StartActivity.this, "Data kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    txtIdKtp.setText(result);
                    readIdKtp();
                }
            }
        }
    }

    class CheckKtp extends AsyncTask<String,String,String> {
        int success2 = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(StartActivity.this);
            pDialog.setMessage("Memeriksa data ktp....");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("tag", "check_ktp"));
            args.add(new BasicNameValuePair("idKtp", idKtp));
            JSONObject jsonObject2 = jsonParser.makeHttpRequest(urlWebService,"POST",args);

            try {
                success2 = jsonObject2.getInt(TAG_SUCCESS);

                if(success2 == 2) {
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
                Intent i = new Intent(getApplicationContext(),BuatUserActivity.class);
                i.putExtra("idKtp",idKtp);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setExitTransition(new Explode());
                    startActivity(i, ActivityOptions.makeSceneTransitionAnimation(StartActivity.this).toBundle());
                }
                else {
                    startActivity(i);
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
                //startActivity(i);
                //finish();
            }
            else if(success2 == 2) {
                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                i.putExtra("idKtp",idKtp);
                i.putExtra("idUser",idUser);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //getWindow().setExitTransition(new Explode());
                    startActivity(i, ActivityOptions.makeSceneTransitionAnimation(StartActivity.this).toBundle());
                }
                else {
                    startActivity(i);
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },1000);
                //startActivity(i);
                //finish();
            }
            else {
                Toast.makeText(StartActivity.this, "Data ktp tidak valid", Toast.LENGTH_SHORT).show();
            }

            pDialog.dismiss();
        }
    }

}
