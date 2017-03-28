package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.AddMetricActivity;
import com.team2052.frckrawler.activities.ImportMetricsActivity;
import com.team2052.frckrawler.activities.MetricInfoActivity;
import com.team2052.frckrawler.binding.ListViewNoDataParams;
import com.team2052.frckrawler.binding.RecyclerViewBinder;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.listeners.FABButtonListener;
import com.team2052.frckrawler.listitems.smart.MetricItemView;
import com.team2052.frckrawler.listitems.smart.SmartAdapterInteractions;
import com.team2052.frckrawler.util.MetricHelper;

import java.util.List;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

/**
 * @author Adam
 * @since 10/15/2014
 */
public class MetricsFragment extends RecyclerViewFragment<List<Metric>, RecyclerViewBinder> implements FABButtonListener {
    private static final String CATEGORY_EXTRA = "CATEGORY_EXTRA";
    private static final String GAME_ID = "GAME_ID";
    private long mGame_id;
    private int mCategory;

    public static MetricsFragment newInstance(int category, long game_id) {
        MetricsFragment fragment = new MetricsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(GAME_ID, game_id);
        bundle.putInt(CATEGORY_EXTRA, category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGame_id = getArguments().getLong(GAME_ID, 0);
        mCategory = getArguments().getInt(CATEGORY_EXTRA);

        if (mCategory == MetricHelper.MATCH_PERF_METRICS) {
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.metric_import_firebase, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.import_metrics_menu) {
            startActivity(ImportMetricsActivity.newInstance(getContext(), mGame_id, mCategory));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends List<Metric>> getObservable() {
        return rxDbManager.metricsInGame(mGame_id, mCategory);
    }

    @Override
    public void onFABPressed() {
        startActivity(AddMetricActivity.newInstance(getActivity(), mGame_id, mCategory));
    }

    @Override
    protected ListViewNoDataParams getNoDataParams() {
        return new ListViewNoDataParams("No metrics found", R.drawable.ic_metric);
    }

    @Override
    public void provideAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(Metric.class, MetricItemView.class);
        creator.listener((actionId, item, position, view) -> {
            if (actionId == SmartAdapterInteractions.EVENT_CLICKED && item instanceof Metric) {
                Metric metric = (Metric) item;
                startActivity(MetricInfoActivity.newInstance(getActivity(), metric.getId()));
            }
        });
    }
}
