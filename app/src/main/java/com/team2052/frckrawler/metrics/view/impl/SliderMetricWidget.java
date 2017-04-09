package com.team2052.frckrawler.metrics.view.impl;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jakewharton.rxbinding.widget.RxSeekBar;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.metric.MetricValue;
import com.team2052.frckrawler.metrics.view.MetricWidget;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.util.MetricHelper;

import rx.android.schedulers.AndroidSchedulers;

public class SliderMetricWidget extends MetricWidget {

    int value;
    private int min;
    private int max;
    private AppCompatSeekBar seekBar;
    private TextView valueText, nameText, minText, maxText;

    public SliderMetricWidget(Context context, MetricValue metricValue) {
        super(context, metricValue);
        setMetricValue(metricValue);
    }

    public SliderMetricWidget(Context context) {
        super(context);
    }

    @Override
    public void setMetricValue(MetricValue m) {
        min = 0;
        max = 1;
        nameText.setText(m.getMetric().getName());

        JsonObject range = JSON.getAsJsonObject(m.getMetric().getData());
        min = range.get("min").getAsInt();
        max = range.get("max").getAsInt();

        seekBar.setMax(max - min);

        minText.setText(Integer.toString(min));
        maxText.setText(Integer.toString(max));

        if (m.getValue() != null && !m.getValue().getAsJsonObject().get("value").isJsonNull())
            value = m.getValue().getAsJsonObject().get("value").getAsInt();
        else
            value = min;

        if (value < min || value > max)
            value = min;
        seekBar.setProgress(value - min);
        valueText.setText(Integer.toString(value));
    }

    @Override
    public void initViews() {
        inflater.inflate(R.layout.widget_metric_slider, this);
        seekBar = (AppCompatSeekBar) findViewById(R.id.sliderVal);
        valueText = (TextView) findViewById(R.id.value);
        nameText = ((TextView) findViewById(R.id.name));
        minText = ((TextView) findViewById(R.id.min));
        maxText = ((TextView) findViewById(R.id.max));

        RxSeekBar.userChanges(seekBar).subscribeOn(AndroidSchedulers.mainThread()).subscribe(seekValue -> {
            value = seekValue + min;
            valueText.setText(Integer.toString(value));
        });
    }

    @Override
    public JsonElement getData() {
        return MetricHelper.buildNumberMetricValue(value);
    }
}
