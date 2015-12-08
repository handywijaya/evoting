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
import android.widget.TextView;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Profile extends BaseActivity {

    ProgressDialog progressDialog;

    String noKTP, nama, tempatTanggalLahir, jenisKelamin, alamat, RTRW, kelurahan, kecamatan, agama, statusPerkawinan, pekerjaan, kewarganegaraan;

    TextView txtNoKTP, txtNama, txtTempatTanggalLahir, txtJenisKelamin, txtAlamat, txtRTRW, txtKelurahan, txtKecamatan, txtAgama, txtStatusPerkawinan, txtPekerjaan, txtKewarganegaraan;


    Button btnKembali;

    private JSONArray profile;

    private static final String TAG_IDKTP = "ID_KTP";
    private static final String TAG_NAMA = "NAMA";
    private static final String TAG_TEMPATLAHIR = "TEMPAT_LAHIR";
    private static final String TAG_TANGGALLAHIR = "TANGGAL_LAHIR";
    private static final String TAG_JENISKELAMIN = "JENIS_KELAMIN";
    private static final String TAG_GOLONGANDARAH = "GOLONGAN_DARAH";
    private static final String TAG_ALAMAT = "ALAMAT";
    private static final String TAG_RT = "RT";
    private static final String TAG_RW = "RW";
    private static final String TAG_KELURAHAN = "Kelurahan";
    private static final String TAG_KECAMATAN = "KECAMATAN";
    private static final String TAG_AGAMA = "AGAMA";
    private static final String TAG_STATUSPERKAWINAN = "STATUS_PERKAWINAN";
    private static final String TAG_PEKERJAAN = "PEKERJAAN";
    private static final String TAG_KEWARGANEGARAAN = "KEWARGANEGARAAN";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        try
        {
            noKTP = getIntent().getStringExtra("noKTP");
            noKTP = "2222222";

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


        txtNoKTP = (TextView)findViewById(R.id.txtNoKTP);
        txtNama = (TextView)findViewById(R.id.txtNama);
        txtTempatTanggalLahir= (TextView)findViewById(R.id.txtTempatTanggalLahir);
        txtJenisKelamin = (TextView)findViewById(R.id.txtJenisKelamin);
        txtAlamat = (TextView)findViewById(R.id.txtAlamat);
        txtRTRW = (TextView)findViewById(R.id.txtRTRW);
        txtKelurahan = (TextView)findViewById(R.id.txtKelurahan);
        txtKecamatan = (TextView)findViewById(R.id.txtKecamatan);
        txtAgama = (TextView)findViewById(R.id.txtAgama);
        txtStatusPerkawinan = (TextView)findViewById(R.id.txtStatusPerkawinan);
        txtPekerjaan = (TextView)findViewById(R.id.txtPekerjaan);
        txtKewarganegaraan = (TextView)findViewById(R.id.txtKewarganegaraan);

        btnKembali = (Button) findViewById(R.id.btnKembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        new LoadData().execute();
    }

    class LoadData extends AsyncTask<String, String, String>
    {
        int flag = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Profile.this);
            progressDialog.setMessage("Mengambil Data..");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);

            progressDialog.dismiss();
            if(flag == 1)
            {
                txtNoKTP.setText(noKTP);
                txtNama.setText(nama);
                txtKecamatan.setText(kecamatan);
                txtJenisKelamin.setText(jenisKelamin);
                txtTempatTanggalLahir.setText(tempatTanggalLahir);
                txtAlamat.setText(alamat);
                txtKelurahan.setText(kelurahan);
                txtKecamatan.setText(kecamatan);
                txtAgama.setText(agama);
                txtStatusPerkawinan.setText(statusPerkawinan);
                txtPekerjaan.setText(pekerjaan);
                txtKewarganegaraan.setText(kewarganegaraan);


            }
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            noKTP = "2222222";
            args.add(new BasicNameValuePair("noKTP", noKTP));
            args.add(new BasicNameValuePair("tag", "get_data_profile"));

            JSONObject jsonObject = jsonParser.makeHttpRequest(urlWebService, "POST", args);
            Log.d("CreateResponse", jsonObject.toString());

            try
            {
                int success = jsonObject.getInt(TAG_SUCCESS);
                if(success == 1)
                {
                    flag = 1;
                    profile = jsonObject.getJSONArray("items");
                    JSONObject p = profile.getJSONObject(0);
                    noKTP = p.getString(TAG_IDKTP);
                    nama = p.getString(TAG_NAMA);

                    String tempat = p.getString(TAG_TEMPATLAHIR);
                    String tanggal = p.getString(TAG_TANGGALLAHIR);


                    tempatTanggalLahir = tempat + ", " + tanggal;

                    jenisKelamin = p.getString(TAG_JENISKELAMIN);
                    alamat = p.getString(TAG_ALAMAT);

                    String RT = p.getString(TAG_RT);
                    String RW = p.getString(TAG_RW);

                    RTRW = RT + " / " + RW;

                    kelurahan = p.getString(TAG_KELURAHAN);

                    kecamatan = p.getString(TAG_KECAMATAN);

                    agama = p.getString(TAG_AGAMA);

                    statusPerkawinan = p.getString(TAG_STATUSPERKAWINAN);

                    pekerjaan = p.getString(TAG_PEKERJAAN);

                    kewarganegaraan = p.getString(TAG_KEWARGANEGARAAN);

                }
                else
                {
                    flag = 0;
                }
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }
            return null;
        }
    }

}
