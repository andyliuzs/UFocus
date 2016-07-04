package www.ufcus.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.xiaopan.java.util.DateTimeUtils;
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

    public void setList(ArrayList<ClockBean> clocks) {
        this.mClocks = clocks;
    }

    @Override
    public int getCount() {
        return mClocks.size();
    }

    @Override
    public ClockBean getItem(int position) {
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
        ClockBean bean = mClocks.get(position);
        ViewHolder viewHolder = null;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_total_clock_bean, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
            convertView = view;
        }
        viewHolder.phoneNumber = bean.getPhoneNumber();
        long clockTime = bean.getClockTime();
        String clockDateStr = DateTimeUtils.getYear(clockTime) + "年" + DateTimeUtils.getMonth(clockTime) + "月" + DateTimeUtils.getDay(clockTime) + "日";
        String clockTimeStr = "打卡时间:" + DateTimeUtils.getHour(clockTime) + ":" + DateTimeUtils.getMinute(clockTime) + ":" + DateTimeUtils.getSecond(clockTime);
        viewHolder.clockDate.setText(clockDateStr);
        viewHolder.clockTime.setText(clockTimeStr);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.clock_date)
        TextView clockDate;
        @BindView(R.id.clock_time)
        TextView clockTime;
        String phoneNumber;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
