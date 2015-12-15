package com.example.handy.audy.daud.alfian.prototype_lomba.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;
import com.example.handy.audy.daud.alfian.prototype_lomba.chart.BarChartFrag;
import com.example.handy.audy.daud.alfian.prototype_lomba.chart.MyValueFormatter;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class HasilVotingActivity extends BaseActivity {

    ProgressDialog pDialog;
    Button btnKembali;
    TextView tvSoal;
    //ViewPager pager;
    //PageAdapter a;
    BarChart barChart;
    PieChart pieChart;
    Typeface mTf;
    Typeface tf;
    RadioGroup rg;

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

//        pager = (ViewPager) findViewById(R.id.pager);
//        pager.setOffscreenPageLimit(3);
//
//        a = new PageAdapter(getSupportFragmentManager());

        rg = (RadioGroup) findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = rg.getCheckedRadioButtonId();

                RadioButton rb = (RadioButton) findViewById(selectedId);

                switch(selectedId) {
                    case R.id.rbBar :
                        pieChart.setVisibility(View.INVISIBLE);

                        barChart.setVisibility(View.VISIBLE);
                        barChart.invalidate();
                        barChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
                        break;
                    case R.id.rbPie :
                        barChart.setVisibility(View.INVISIBLE);

                        Random randPie = new Random();
                        int rPie = randPie.nextInt()%2;

                        pieChart.setVisibility(View.VISIBLE);
                        pieChart.invalidate();
                        switch(rPie) {
                            case 0:
                                pieChart.animateX(1400, Easing.EasingOption.EaseInOutQuad);
                                break;
                            default:
                                pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
                                break;
                        }
                        break;
                }
            }
        });

        barChart = (BarChart) findViewById(R.id.barChart);
        pieChart = (PieChart) findViewById(R.id.pieChart);

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

    private void initiateBarChart() {

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);

        barChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        barChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

        mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);

        YAxisValueFormatter custom = new MyYAxisValueFormatter();

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);

        Legend l = barChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
    }

    private void initiatePieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.setDescription("");
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        pieChart.setCenterText(generateCenterSpannableText());
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColorTransparent(true);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
    }

    private void setBarChartData() {
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals = new ArrayList<>();

        for(PilihanJawaban p : listPilihanJawaban) {
            xVals.add(p.getNamaPilihan());
            map.put(p.getIdPilihan(), 0);
        }

        for (int i = 0; i < listHasilVoting.size(); i++) {
            map.put(listHasilVoting.get(i).getIdJawaban(), Integer.parseInt(listHasilVoting.get(i).getTotal()));
        }

        int i = 0;
        for(PilihanJawaban p : listPilihanJawaban) {
            yVals.add(new BarEntry (Float.parseFloat(String.valueOf(map.get(p.getIdPilihan()))), i));
            i++;
        }

        BarDataSet set1 = new BarDataSet(yVals, "Total Pemilih");
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(mTf);
        data.setValueFormatter(new MyValueFormatter());

        barChart.setData(data);
    }

    private void setPieChartData() {
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals = new ArrayList<>();

        for(PilihanJawaban p : listPilihanJawaban) {
            xVals.add(p.getNamaPilihan());
            map.put(p.getIdPilihan(), 0);
        }

        for (int i = 0; i < listHasilVoting.size(); i++) {
            map.put(listHasilVoting.get(i).getIdJawaban(), Integer.parseInt(listHasilVoting.get(i).getTotal()));
        }

        for(PilihanJawaban p : listPilihanJawaban) {
            yVals.add(new Entry(Float.parseFloat(String.valueOf(map.get(p.getIdPilihan()))), map.size()));
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

        pieChart.setData(data);
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Hasil Pemilihan");
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(1.7f), 0, s.length(), 0);
        return s;
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

                RadioButton rbBar = (RadioButton) findViewById(R.id.rbBar);
                RadioButton rbPie = (RadioButton) findViewById(R.id.rbPie);

                rbBar.setVisibility(View.VISIBLE);
                rbPie.setVisibility(View.VISIBLE);

                initiateBarChart();
                initiatePieChart();
                setBarChartData();
                setPieChartData();

                barChart.setVisibility(View.VISIBLE);
                barChart.invalidate();
                barChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

            }
        }
    }

}
