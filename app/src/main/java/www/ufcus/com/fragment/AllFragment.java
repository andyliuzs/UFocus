package www.ufcus.com.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import www.ufcus.com.R;
import www.ufcus.com.adapter.AllAdapter;
import www.ufcus.com.beans.Aitem;
import www.ufcus.com.event.SkinChangeEvent;
import www.ufcus.com.fragment.base.BaseFragment;
import www.ufcus.com.http.RequestManager;
import www.ufcus.com.http.callback.CallBack;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {

    @BindView(R.id.swipe_target)
    ListView mListView;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;
    private AllAdapter adapter;
    private List<Aitem> aItems = new ArrayList<>();

    private int page = 1;


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_android;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        initView();
        onRefresh();
    }

    @Subscribe
    public void onEvent(SkinChangeEvent event) {
        adapter.notifyDataSetChanged();

    }

    private void getData(final boolean isRefresh) {
        int pageSize = 30;
        RequestManager.getAll(getName(), pageSize, page, isRefresh,
                new CallBack<List<Aitem>>() {
                    @Override
                    public void onSuccess(List<Aitem> result) {
                        if (isRefresh) {
                            aItems.clear();
                            page = 2;
                        } else {
                            page++;
                        }
                        aItems.addAll(result);
                        adapter.notifyDataSetChanged();
                        if (mSwipeToLoadLayout != null) {
                            mSwipeToLoadLayout.setRefreshing(false);
                            mSwipeToLoadLayout.setLoadingMore(false);
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        super.onFailure(message);
                        if (mSwipeToLoadLayout != null) {
                            mSwipeToLoadLayout.setRefreshing(false);
                            mSwipeToLoadLayout.setLoadingMore(false);
                        }
                    }
                });
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
        adapter = new AllAdapter(getActivity(), aItems);
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
}
