package com.example.handy.audy.daud.alfian.prototype_lomba.chart;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.handy.audy.daud.alfian.prototype_lomba.model.HasilVoting;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.PilihanJawaban;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Handy on 12/11/2015.
 */
public class SimpleFragment extends Fragment {

    Typeface tf;
    Typeface mTf;
    HashMap<String, Integer> map = new HashMap<>();

    public SimpleFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected BarData generateBarData(List<PilihanJawaban> listPilihanJawaban, List<HasilVoting> listHasilVoting) {

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
            yVals.add(new BarEntry(Float.parseFloat(String.valueOf(map.get(p.getIdPilihan()))), i));
            i++;
        }

        BarDataSet set1 = new BarDataSet(yVals, "Total Pemilih");
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(mTf);

        return data;
    }

    /**
     * generates less data (1 DataSet, 4 values)
     * @return
     */
    protected PieData generatePieData(List<PilihanJawaban> listPilihanJawaban, List<HasilVoting> listHasilVoting) {

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

        return data;
    }
}
