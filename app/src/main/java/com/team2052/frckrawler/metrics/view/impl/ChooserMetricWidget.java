package com.team2052.frckrawler.metrics.view.impl;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.metric.MetricValue;
import com.team2052.frckrawler.metrics.view.ListIndexMetricWidget;
import com.team2052.frckrawler.util.MetricHelper;
import com.team2052.frckrawler.util.Tuple2;

import java.util.List;

public class ChooserMetricWidget extends ListIndexMetricWidget implements OnItemSelectedListener {
    int value;
    private Spinner chooserSpinner;

    public ChooserMetricWidget(Context context, MetricValue metricValue) {
        super(context, metricValue);
        setMetricValue(metricValue);
    }

    public ChooserMetricWidget(Context context) {
        super(context);
    }

    @Override
    public void setMetricValue(MetricValue m) {
        ((TextView) findViewById(R.id.name)).setText(m.getMetric().getName());
        final Optional<List<String>> optionalValues = MetricHelper.getListItemIndexRange(m.getMetric());
        if (!optionalValues.isPresent())
            throw new IllegalStateException("Couldn't parse values, cannot proceed");

        ArrayAdapter<Object> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        for (String value : optionalValues.get()) adapter.add(value);
        chooserSpinner.setAdapter(adapter);

        int selectedPos = 0;
        final Tuple2<List<Integer>, MetricHelper.ReturnResult> preloadedValuesResult = MetricHelper.getListIndexMetricValue(m);
        if (!preloadedValuesResult.t2.isError)
            if (!preloadedValuesResult.t1.isEmpty())
                selectedPos = preloadedValuesResult.t1.get(0);

        if (!adapter.isEmpty())
            chooserSpinner.setSelection(selectedPos);
    }

    @Override
    public void initViews() {
        inflater.inflate(R.layout.widget_metric_chooser, this);
        chooserSpinner = findViewById(R.id.choooserList);
        chooserSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> a, View arg1, int pos, long arg3) {
        value = pos;
    }

    @Override
    public void onNothingSelected(AdapterView<?> a) {
        a.setSelection(0);
    }

    @Override
    public List<Integer> getIndexValues() {
        if (chooserSpinner.getAdapter().getCount() > 0) {
            return Lists.newArrayList(value);
        }
        return Lists.newArrayList();
    }
}
