package www.ufcus.com.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.baidu.mapapi.SDKInitializer;

import butterknife.ButterKnife;
import www.ufcus.com.http.RequestManager;

/**
 *
 */
public abstract class BaseFragment extends Fragment {

    protected View rootView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutResource(), container, false);
            ButterKnife.bind(this, rootView);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }

        ButterKnife.bind(this, rootView);
        return rootView;
    }

    protected abstract int getLayoutResource();

    public String getName() {
        return BaseFragment.class.getName();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.unbind(this);
        //取消请求
        RequestManager.cancelRequest(getName());
    }
}
