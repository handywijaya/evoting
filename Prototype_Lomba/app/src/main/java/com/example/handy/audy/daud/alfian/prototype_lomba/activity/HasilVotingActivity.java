package com.example.handy.audy.daud.alfian.prototype_lomba.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;
import com.example.handy.audy.daud.alfian.prototype_lomba.chart.BarChartFrag;
import com.example.handy.audy.daud.alfian.prototype_lomba.chart.MyYAxisValueFormatter;
import com.example.handy.audy.daud.alfian.prototype_lomba.chart.PieChartFrag;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.HasilVoting;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.PilihanJawaban;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.Soal;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HasilVotingActivity extends BaseActivity {

    ProgressDialog pDialog;
    Button btnKembali;
    TextView tvSoal;
    ViewPager pager;
    PageAdapter a;

    String idPertanyaan;
    Soal soal;
    List<PilihanJawaban> listPilihanJawaban;
    List<HasilVoting> listHasilVoting;
    HashMap<String, Integer> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_voting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);

        a = new PageAdapter(getSupportFragmentManager());

        idPertanyaan = getIntent().getStringExtra("idPertanyaan");
        soal = new Soal();
        listPilihanJawaban = new ArrayList<>();
        listHasilVoting = new ArrayList<>();

        tvSoal = (TextView) findViewById(R.id.tvSoal);

        btnKembali = (Button) findViewById(R.id.btnKembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new LoadHasilVoting().execute();
    }

    private class PageAdapter extends FragmentPagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            Fragment f = null;

            switch(pos) {
                case 0:
                    f = BarChartFrag.newInstance(listPilihanJawaban, listHasilVoting);
                    break;
                case 1:
                    f = PieChartFrag.newInstance(listPilihanJawaban, listHasilVoting);
                    break;
            }

            return f;
        }

        @Override
        public int getCount() {
            return 2;
        }
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
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> argsPertanyaan = new ArrayList<>();
            argsPertanyaan.add(new BasicNameValuePair("tag", "get_pertanyaan"));
            argsPertanyaan.add(new BasicNameValuePair("idPertanyaan", idPertanyaan));
            JSONObject jsonObjectPertanyaan = jsonParser.makeHttpRequest(urlWebService,"POST",argsPertanyaan);

            List<NameValuePair> argsPilihanJawaban = new ArrayList<>();
            argsPilihanJawaban.add(new BasicNameValuePair("tag", "get_pilihan_jawaban"));
            argsPilihanJawaban.add(new BasicNameValuePair("idPertanyaan", idPertanyaan));
            JSONObject jsonObjectPilihanJawaban = jsonParser.makeHttpRequest(urlWebService,"POST",argsPilihanJawaban);

            List<NameValuePair> argsHasilVote = new ArrayList<>();
            argsHasilVote.add(new BasicNameValuePair("tag", "get_hasil_vote"));
            argsHasilVote.add(new BasicNameValuePair("idPertanyaan", idPertanyaan));
            JSONObject jsonObjectHasilVote = jsonParser.makeHttpRequest(urlWebService,"POST",argsHasilVote);

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
                new AlertDialog.Builder(HasilVotingActivity.this)
                        .setTitle("Informasi")
                        .setMessage("Geser layar ke kanan untuk melihat grafik dalam bentuk yang berbeda")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pager.setAdapter(a);
                            }
                        })
                        .show();

            }
        }
    }

}
