package com.example.handy.audy.daud.alfian.prototype_lomba.chart;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.*;

import java.text.DecimalFormat;

/**
 * Created by Handy on 12/11/2015.
 */
public class MyYAxisValueFormatter implements YAxisValueFormatter  {

    private DecimalFormat mFormat;

    public MyYAxisValueFormatter() {
        mFormat = new DecimalFormat("###,###,###,###");
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return mFormat.format(value);
    }
}
