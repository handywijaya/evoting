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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;
import com.example.handy.audy.daud.alfian.prototype_lomba.adapter.ListRiwayatVoteAdapter;
import com.example.handy.audy.daud.alfian.prototype_lomba.adapter.ListSoalAdapter;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.Soal;
import com.example.handy.audy.daud.alfian.prototype_lomba.widget.AutofitRecyclerView;
import com.example.handy.audy.daud.alfian.prototype_lomba.widget.MarginDecoration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RiwayatVote extends BaseActivity implements ListRiwayatVoteAdapter.OnItemClickListener {

    Button btnKembali;
    ListView lvBelum, lvSudah;
    AutofitRecyclerView rcVoting;
    HashMap<String, String> listPertanyaan;
    ListRiwayatVoteAdapter adapter;

    List<Soal> Soal = new ArrayList<>();

    ProgressDialog pDialog;
    String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_vote);
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

        idUser = getIntent().getStringExtra("idUser");
        idUser = "1";

        rcVoting = (AutofitRecyclerView) findViewById(R.id.rcVoting);
        rcVoting.setHasFixedSize(true);
        rcVoting.addItemDecoration(new MarginDecoration(this));

        adapter = new ListRiwayatVoteAdapter(this);
        adapter.setOnItemClickListener(this);

        rcVoting.setAdapter(adapter);

        btnKembali = (Button) findViewById(R.id.btnKembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new LoadSoal().execute();
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent i = new Intent(getApplicationContext(), HasilVotingActivity.class);
        i.putExtra("idPertanyaan", adapter.getSoalID(position));
        i.putExtra("pertanyaanVoting", adapter.getSoalName(position));
        startActivity(i);
    }

    class LoadSoal extends AsyncTask<String,String,String> {
        int success2 = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RiwayatVote.this);
            pDialog.setMessage("Memuat riwayat vote....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("tag","get_riwayat_vote_user"));
            args.add(new BasicNameValuePair("idUser",idUser));
            JSONObject jsonObject2 = jsonParser.makeHttpRequest(urlWebService,"POST",args);

            try {
                success2 = jsonObject2.getInt(TAG_SUCCESS);

                if(success2 == 1) {
                    JSONArray arraySoal = jsonObject2.getJSONArray("items");

                    for(int i=0; i<arraySoal.length();i++) {
                        JSONObject c = arraySoal.getJSONObject(i);
                        Soal s = new Soal();
                        s.setIdSoal(c.getString("ID_SOAL"));
                        s.setJudul(c.getString("ISI_SOAL"));
                        s.setKategori(c.getString("KATEGORI"));
                        Soal.add(s);
                    }
                    //simpan data ke list soal

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
                adapter.setData(Soal);
                pDialog.dismiss();
            }
        }
    }

}