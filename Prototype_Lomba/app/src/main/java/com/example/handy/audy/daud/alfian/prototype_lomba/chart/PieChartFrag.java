package com.example.handy.audy.daud.alfian.prototype_lomba.chart;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.handy.audy.daud.alfian.prototype_lomba.R;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.HasilVoting;
import com.example.handy.audy.daud.alfian.prototype_lomba.model.PilihanJawaban;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;

import java.util.List;

/**
 * Created by Handy on 12/11/2015.
 */
public class PieChartFrag extends SimpleFragment {

    static List<PilihanJawaban> listPilihanJawaban;
    static List<HasilVoting> listHasilVoting;
    Typeface tf;
    private PieChart mChart;

    public static Fragment newInstance(List<PilihanJawaban> lPilihanJawaban, List<HasilVoting> lHasilVoting) {
        listPilihanJawaban = lPilihanJawaban;
        listHasilVoting = lHasilVoting;

        return new PieChartFrag();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_pie_chart, container, false);

        mChart = (PieChart) v.findViewById(R.id.chart);
        mChart.setUsePercentValues(true);
        mChart.setDescription("");
        mChart.setExtraOffsets(5, 10, 5, 5);
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setCenterTextTypeface(Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf"));
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

        tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        mChart.setData(generatePieData(listPilihanJawaban, listHasilVoting));

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        return v;
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Hasil Pemilihan");
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(1.7f), 0, s.length(), 0);
        return s;
    }
}
