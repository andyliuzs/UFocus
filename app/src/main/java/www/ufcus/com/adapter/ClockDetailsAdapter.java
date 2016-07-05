package www.ufcus.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.ufcus.com.R;
import www.ufcus.com.beans.ClockDetailResult;
import www.ufcus.com.utils.Utils;

/**
 * Created by andyliu on 16-7-4.
 */
public class ClockDetailsAdapter extends ArrayAdapter<ClockDetailResult> {

    private Context context;
    private List<ClockDetailResult> mClocks;


    public ClockDetailsAdapter(Context context, List<ClockDetailResult> clocks) {
        super(context, 0, clocks);
        mClocks = clocks;
        this.context = context;
    }

    public void setList(ArrayList<ClockDetailResult> clocks) {
        this.mClocks = clocks;
    }

    @Override
    public int getCount() {
        return mClocks.size();
    }

    @Override
    public ClockDetailResult getItem(int position) {
        return mClocks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mClocks.get(position).getId();
        /*
        return position;*/
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ClockDetailResult bean = mClocks.get(position);
        ViewHolder viewHolder = null;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_detail_clock_bean, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
            convertView = view;
        }
        viewHolder.day.setText(bean.getDay());
        viewHolder.week.setText(bean.getWeek());
        viewHolder.firstClockTime.setText(bean.getFirstTime());
        viewHolder.lastClockTime.setText(bean.getLastTime());
        viewHolder.workTime.setText(bean.getWorkTime());
        viewHolder.workStatus.setText(bean.getWorkStatus());

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.day)
        TextView day;
        @BindView(R.id.week)
        TextView week;
        @BindView(R.id.first_clock_time)
        TextView firstClockTime;
        @BindView(R.id.last_clock_time)
        TextView lastClockTime;
        @BindView(R.id.work_time)
        TextView workTime;
        @BindView(R.id.work_status)
        TextView workStatus;

        String phoneNumber;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
