package com.example.handy.audy.daud.alfian.prototype_lomba.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.HasilVoting;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.PilihanJawaban;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.Soal;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HasilVotingActivity extends BaseActivity {

    ProgressDialog pDialog;
    Button btnKembali;
    TextView judul, tvSoal;
    String idPertanyaan;
    Soal soal;
    List<PilihanJawaban> listPilihanJawaban;
    List<HasilVoting> listHasilVoting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_voting);
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

        //idPertanyaan = getIntent().getStringExtra("idPertanyaan");
        idPertanyaan = "1";
        soal = new Soal();
        listPilihanJawaban = new ArrayList<>();
        listHasilVoting = new ArrayList<>();

        judul = (TextView) findViewById(R.id.judul);
        tvSoal = (TextView) findViewById(R.id.tvSoal);

        btnKembali = (Button) findViewById(R.id.btnKembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        new LoadHasilVoting().execute();

        Log.e("List Jawaban", listPilihanJawaban.toString());
        Log.e("Hasil voting", listHasilVoting.toString());
    }

    class LoadHasilVoting extends AsyncTask<String, String, String> {

        int success = 0;
        int success1 = 0;
        int success2 = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HasilVotingActivity.this);
            pDialog.setMessage("Memuat hasil....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> argsPertanyaan = new ArrayList<>();
            argsPertanyaan.add(new BasicNameValuePair("tag", "tag_pertanyaan"));
            argsPertanyaan.add(new BasicNameValuePair("idPertanyaan", idPertanyaan));
            JSONObject jsonObjectPertanyaan = jsonParser.makeHttpRequest(urlWebService,"POST",argsPertanyaan);

            List<NameValuePair> argsPilihanJawaban = new ArrayList<>();
            argsPilihanJawaban.add(new BasicNameValuePair("tag", "get_pilihan_jawaban"));
            argsPilihanJawaban.add(new BasicNameValuePair("idPertanyaan", idPertanyaan));
            JSONObject jsonObjectPilihanJawaban = jsonParser.makeHttpRequest(urlWebService,"POST",argsPilihanJawaban);

            List<NameValuePair> argsHasilVote = new ArrayList<>();
            argsHasilVote.add(new BasicNameValuePair("tag", "get_hasil_vote"));
            argsHasilVote.add(new BasicNameValuePair("idPertanyaan", idPertanyaan));
            JSONObject jsonObjectHasilVote = jsonParser.makeHttpRequest(urlWebService,"POST",argsPilihanJawaban);

            try {

                success = jsonObjectPertanyaan.getInt(TAG_SUCCESS);
                if(success == 1) {
                    JSONArray arraySoal = jsonObjectPertanyaan.getJSONArray("items");

                    for(int i=0; i<arraySoal.length();i++) {
                        JSONObject c = arraySoal.getJSONObject(i);

                        soal.setIdSoal(c.getString("ID_SOAL"));
                        soal.setJudul(c.getString("ISI_SOAL"));
                        soal.setKategori(c.getString("KATEGORI"));
                    }
                }

                success1 = jsonObjectPilihanJawaban.getInt(TAG_SUCCESS);
                if(success1 == 1) {
                    JSONArray arrayPilihanJawaban = jsonObjectPilihanJawaban.getJSONArray("items");

                    for(int i=0; i<arrayPilihanJawaban.length();i++) {
                        JSONObject c = arrayPilihanJawaban.getJSONObject(i);

                        PilihanJawaban pilihanJawaban = new PilihanJawaban();
                        pilihanJawaban.setIdPilihan(c.getString("ID_PILIHAN"));
                        pilihanJawaban.setIdSoal(c.getString("ID_SOAL"));
                        pilihanJawaban.setNamaPilihan(c.getString("NAMA_PILIHAN"));
                        listPilihanJawaban.add(pilihanJawaban);
                    }
                }

                success2 = jsonObjectHasilVote.getInt(TAG_SUCCESS);
                if(success2 == 1) {
                    JSONArray arrayHasilVote = jsonObjectHasilVote.getJSONArray("items");

                    for(int i=0; i<arrayHasilVote.length(); i++) {
                        JSONObject c = arrayHasilVote.getJSONObject(i);

                        HasilVoting hasilVoting = new HasilVoting();
                        hasilVoting.setIdJawaban(c.getString("ID_JAWABAN"));
                        hasilVoting.setTotal(c.getString("TOTAL"));
                        listHasilVoting.add(hasilVoting);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(success == 1 && success1 == 1 && success2 == 1) {
                pDialog.dismiss();
                tvSoal.setText(soal.getJudul());
            }
        }
    }

}
