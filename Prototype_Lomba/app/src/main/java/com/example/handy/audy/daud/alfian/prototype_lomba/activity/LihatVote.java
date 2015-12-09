package com.example.handy.audy.daud.alfian.prototype_lomba.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;
import com.example.handy.audy.daud.alfian.prototype_lomba.adapter.ListSoalAdapter;
import com.example.handy.audy.daud.alfian.prototype_lomba.jsonparser.JSONParser;
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

public class LihatVote extends BaseActivity  implements ListSoalAdapter.OnItemClickListener {

    ProgressDialog pDialog;
    AutofitRecyclerView rcVoting;
    Button btnKembali;
    List<String> pertanyaanVoting;
    List<String> idVoting;
    List<Soal> soal;
    String idKtp,idUser;
    ListSoalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_vote);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        soal = new ArrayList<Soal>();

        idUser = getIntent().getStringExtra("idUser");
        idKtp = getIntent().getStringExtra("idKtp");

        rcVoting = (AutofitRecyclerView) findViewById(R.id.rcVoting);
        rcVoting.setHasFixedSize(true);
        rcVoting.addItemDecoration(new MarginDecoration(this));

        adapter = new ListSoalAdapter(this);
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
        Intent i = new Intent(getApplicationContext(), Voting.class);
        i.putExtra("idKtp", idKtp);
        i.putExtra("idUser", idUser);
        i.putExtra("idPertanyaan", adapter.getSoalID(position));
        i.putExtra("pertanyaanVoting", adapter.getSoalName(position));
        startActivity(i);
        finish();
    }

    class LoadSoal extends AsyncTask<String,String,String> {
        int success2 = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LihatVote.this);
            pDialog.setMessage("Memuat daftar....");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("tag", "get_list_vote_by_id_user"));
            args.add(new BasicNameValuePair("idUser", idKtp));
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
                        soal.add(s);
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
                adapter.setData(soal);
                pDialog.dismiss();
            }
            else {
                pDialog.dismiss();
                TextView a = (TextView)findViewById(R.id.txtError);
                a.setTextSize(20);
                a.setText("Tidak ada pemilu untuk saat ini.");
                a.setVisibility(View.VISIBLE);
                rcVoting.setVisibility(View.GONE);
            }
        }
    }

}
