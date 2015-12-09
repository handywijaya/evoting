package com.example.handy.audy.daud.alfian.prototype_lomba.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.HasilVoting;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.PilihanJawaban;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.Soal;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

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
    TextView tvSoal;
    PieChart mChart;
    Typeface tf;
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

        idPertanyaan = getIntent().getStringExtra("idPertanyaan");
        soal = new Soal();
        listPilihanJawaban = new ArrayList<>();
        listHasilVoting = new ArrayList<>();

        tvSoal = (TextView) findViewById(R.id.tvSoal);

        //inisialisasi chart
        mChart = (PieChart) findViewById(R.id.chart);
        mChart.setUsePercentValues(true);
        mChart.setDescription("");
        mChart.setExtraOffsets(5, 10, 5, 5);
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        mChart.setCenterText(generateCenterSpannableText());
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);
        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);
        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);
        mChart.setDrawCenterText(true);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        //end of inisialisasi chart

        btnKembali = (Button) findViewById(R.id.btnKembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new LoadHasilVoting().execute();
    }

    private void setChartData() {

        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals = new ArrayList<>();

        for(PilihanJawaban p : listPilihanJawaban) {
            xVals.add(p.getNamaPilihan());
        }

        for (int i = 0; i < listHasilVoting.size(); i++) {
            yVals.add(new Entry(Float.parseFloat(listHasilVoting.get(i).getTotal()), i));
        }

        PieDataSet dataSet = new PieDataSet(yVals, "");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);

        //inisialisasi warna
        ArrayList<Integer> colors = new ArrayList<Integer>();

        colors.add(ColorTemplate.getHoloBlue());

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(tf);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
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
                setChartData();
                mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
            }
        }
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Hasil Pemilihan");
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(1.7f), 0, s.length(), 0);
        return s;
    }

}
