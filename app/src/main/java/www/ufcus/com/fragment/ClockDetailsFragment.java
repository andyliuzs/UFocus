package www.ufcus.com.fragment;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.baidu.platform.comapi.map.C;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import www.ufcus.com.R;
import www.ufcus.com.activity.MainActivity;
import www.ufcus.com.adapter.AllAdapter;
import www.ufcus.com.adapter.ClockDetailsAdapter;
import www.ufcus.com.beans.Aitem;
import www.ufcus.com.beans.ClockBean;
import www.ufcus.com.beans.ClockDetailResult;
import www.ufcus.com.event.SkinChangeEvent;
import www.ufcus.com.fragment.base.BaseFragment;
import www.ufcus.com.http.RequestManager;
import www.ufcus.com.http.callback.CallBack;
import www.ufcus.com.utils.MyViewUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClockDetailsFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener, LoaderManager.LoaderCallbacks<ArrayList<ClockDetailResult>>, MainActivity.MainCallBack {

    @BindView(R.id.list)
    ListView mListView;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    private ClockDetailsAdapter adapter;
    private ArrayList<ClockDetailResult> clockDetails = new ArrayList<ClockDetailResult>();

    private int page = 1;
    private boolean isRefresh = true;

    @Override
    protected int getLayoutResource() {
        return R.layout.clock_detail_main;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        ((MainActivity) getActivity()).setMainCallBack(this);
        initView();
        onRefresh();
    }

    @Subscribe
    public void onEvent(SkinChangeEvent event) {
        adapter.notifyDataSetChanged();

    }

    private void getData(final boolean isRefresh) {
        this.isRefresh = isRefresh;
        getLoaderManager().restartLoader(0, null, this);

    }

    private void initView() {
        mSwipeToLoadLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeToLoadLayout.setRefreshing(true);
            }
        });
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mSwipeToLoadLayout.setOnLoadMoreListener(this);
        adapter = new ClockDetailsAdapter(getActivity(), clockDetails);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        page = 1;
        getData(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onLoadMore() {
        getData(false);
    }

    @Override
    public Loader<ArrayList<ClockDetailResult>> onCreateLoader(int id, Bundle args) {
        Logger.v("onCreateLoader");
        DataAsyncLoader dataAsyncLoader = new DataAsyncLoader(getActivity());
        return dataAsyncLoader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ClockDetailResult>> loader, ArrayList<ClockDetailResult> data) {
        Logger.v("onLoadFinished");

        if (isRefresh) {
            clockDetails.clear();
            page = 2;
        } else {
            page++;
        }
        clockDetails.addAll(data);
        adapter.setList(clockDetails);
        adapter.notifyDataSetChanged();
        if (mSwipeToLoadLayout != null) {
            mSwipeToLoadLayout.setRefreshing(false);
            mSwipeToLoadLayout.setLoadingMore(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ClockDetailResult>> loader) {
        loader = null;
        adapter.notifyDataSetChanged();
    }


    class DataAsyncLoader extends AsyncTaskLoader<ArrayList<ClockDetailResult>> {

        private Context context;
        private int pageSize = 15;

        public DataAsyncLoader(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        public ArrayList<ClockDetailResult> loadInBackground() {

            Logger.v("DataAsyncTaskLoader--》loadInBackground");
            int limit = pageSize * page;
            String order = ClockBean.CLOCK_TIME + " DESC limit 0," + limit;
            Cursor c = context.getContentResolver().query(ClockBean.ITEMS_URI, new String[]{ClockBean._ID, ClockBean.PHONE_NUMBER, ClockBean.GROUP_BY, "MAX(" + ClockBean.CLOCK_TIME + ") as " + ClockDetailResult.AS_LAST_TIME, "MIN(" + ClockBean.CLOCK_TIME + ") as " + ClockDetailResult.AS_FIRST_TIME}, " 1=1" + " ) " + " group by (" + ClockBean.GROUP_BY, null, null);
            ArrayList<ClockDetailResult> cdrs = ClockDetailResult.getBeans(c);
            return cdrs;
        }


        @Override
        protected void onStartLoading() {
            Logger.v("DataAsyncTaskLoader--》onStartLoading");
            forceLoad();    //强制加载
        }

        @Override
        protected void onStopLoading() {
            Logger.v("DataAsyncTaskLoader--》onStopLoading");
            super.onStopLoading();
        }
    }


    @Override
    public void onBackPressed() {

        MyViewUtils.switchFragment(((MainActivity) getActivity()), ((MainActivity) getActivity()).getCurrentFragment(), new MapFragment());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        clockDetails.clear();
        clockDetails = null;
        mListView = null;
        adapter.clear();
        adapter = null;
        ((MainActivity) getActivity()).setMainCallBack(null);

    }
}
