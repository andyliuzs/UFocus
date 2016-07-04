package www.ufcus.com.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.ufcus.com.R;
import www.ufcus.com.beans.ClockBean;

/**
 * Created by andyliu on 16-7-4.
 */
public class ClockAdapter extends ArrayAdapter<ClockBean> {

    private Context context;
    private List<ClockBean> mClocks;


    public ClockAdapter(Context context, List<ClockBean> clocks) {
        super(context, 0, clocks);
        mClocks = clocks;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    static class ViewHolder {
        @BindView(R.id.clock_date)
        TextView clockDate;
        @BindView(R.id.clock_time)
        TextView clockTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
