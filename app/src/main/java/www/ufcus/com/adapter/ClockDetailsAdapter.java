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
            View view = LayoutInflater.from(context).inflate(R.layout.item_clock_bean, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
            convertView = view;
        }
        viewHolder.phoneNumber = bean.getPhoneNumber();
        long clockTime = bean.
        String clockDateStr = bean.getGroupBy();
        String clockTimeStr =Utils.getTime(clockTime);
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
