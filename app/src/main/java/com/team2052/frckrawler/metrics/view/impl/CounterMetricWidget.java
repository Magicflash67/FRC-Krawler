package com.team2052.frckrawler.metrics.view.impl;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.metric.MetricValue;
import com.team2052.frckrawler.metrics.view.MetricWidget;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.util.MetricHelper;


public class CounterMetricWidget extends MetricWidget implements OnClickListener, View.OnLongClickListener {
    int value;
    private int max;
    private int min;
    private int increment;
    private TextView nameText, valueText;

    public CounterMetricWidget(Context context, MetricValue metricValue) {
        super(context, metricValue);
        setMetricValue(metricValue);
    }

    public CounterMetricWidget(Context context) {
        super(context);
    }

    @Override
    public void setMetricValue(MetricValue m) {
        ((TextView) findViewById(R.id.title)).setText(m.getMetric().getName());

        JsonObject o = JSON.getAsJsonObject(m.getMetric().getData());

        max = o.get("max").getAsInt();
        min = o.get("min").getAsInt();
        increment = o.get("inc").getAsInt();

        if (m.getValue() != null)
            value = m.getValue().getAsJsonObject().get("value").getAsInt();
        else
            value = min;

        valueText.setText(Integer.toString(value));
    }

    @Override
    public void initViews() {
        inflater.inflate(R.layout.widget_metric_counter, this);
        findViewById(R.id.plus).setOnClickListener(this);
        findViewById(R.id.plus).setOnLongClickListener(this);
        findViewById(R.id.minus).setOnClickListener(this);
        findViewById(R.id.minus).setOnLongClickListener(this);
        nameText = (TextView) findViewById(R.id.title);
        valueText = (TextView) findViewById(R.id.value);
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.plus) {

            value += increment;

            if (value > max)
                value = max;

        } else if (v.getId() == R.id.minus) {

            value -= increment;

            if (value < min)
                value = min;
        }
        updateValueText();
    }

    private void updateValueText() {
        ((TextView) findViewById(R.id.value)).setText(Integer.toString(value));
    }

    @Override
    public JsonElement getData() {
        return MetricHelper.buildNumberMetricValue(value);
    }

    @Override
    public boolean onLongClick(View v) {
        new MaterialDialog.Builder(getContext())
                .title(nameText.getText().toString())
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input("Enter Value", Integer.toString(value), false, (dialog, input) -> {
                    int i = Integer.parseInt(input.toString());
                    if (i > max) {
                        value = max;
                    } else if (i < min) {
                        value = min;
                    } else {
                        value = i;
                    }
                    updateValueText();
                }).show();
        return false;
    }
}
