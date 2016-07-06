package www.ufcus.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.xiaopan.android.content.res.DimenUtils;
import me.xiaopan.java.util.DateTimeUtils;
import www.ufcus.com.R;
import www.ufcus.com.beans.ClockBean;
import www.ufcus.com.utils.MyViewUtils;
import www.ufcus.com.utils.ThemeUtils;
import www.ufcus.com.utils.Utils;

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
            View view = LayoutInflater.from(context).inflate(R.layout.item_clock_bean, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
            convertView = view;
        }
        viewHolder.phoneNumber = bean.getPhoneNumber();
        long clockTime = bean.getClockTime();
        String clockDateStr = bean.getGroupBy();
        String clockTimeStr = "打卡时间:" + Utils.getTime(clockTime);
        viewHolder.clockDate.setText(clockDateStr);
        viewHolder.clockTime.setText(clockTimeStr);
        viewHolder.imageView.setCompoundDrawablesWithIntrinsicBounds(new IconicsDrawable(context)
                        .icon(MaterialDesignIconic.Icon.gmi_time_countdown)
                        .color(ThemeUtils.getThemeColor(context, R.attr.colorPrimary))
                        .sizeDp(20),
                null, null, null);
        viewHolder.imageView.setCompoundDrawablePadding(DimenUtils.dp2px(context, 5));
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.clock_date)
        TextView clockDate;
        @BindView(R.id.clock_time)
        TextView clockTime;
        @BindView(R.id.clock_r_img)
        TextView imageView;
        String phoneNumber;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
