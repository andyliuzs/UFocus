package www.ufcus.com.adapter;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.xiaopan.android.content.res.DimenUtils;
import www.ufcus.com.R;
import www.ufcus.com.beans.Aitem;
import www.ufcus.com.utils.MyViewUtils;
import www.ufcus.com.utils.ThemeUtils;

/**
 * Created by dongjunkun on 2016/2/15.
 */
public class AllAdapter extends BaseAdapter {
    private Context context;
    private List<Aitem> aItems;

    public AllAdapter(Context context, List<Aitem> aItems) {
        this.context = context;
        this.aItems = aItems;
    }

    @Override
    public int getCount() {
        return aItems.size();
    }

    @Override
    public Object getItem(int position) {
        return aItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Aitem aItem = aItems.get(position);
        View view1;
        View view2;
        if (aItem.getType().equals("福利")) {
            ImageViewHolder viewHolder1 = null;
            if (convertView != null && convertView.getTag() instanceof ImageViewHolder) {
                viewHolder1 = (ImageViewHolder) convertView.getTag();
            } else {
                view1 = LayoutInflater.from(context).inflate(R.layout.item_fuli, null);
                viewHolder1 = new ImageViewHolder(view1);
                view1.setTag(viewHolder1);
                convertView = view1;
            }
            Glide.with(context).load(aItem.getUrl())
                    .placeholder(R.drawable.head_img).into(viewHolder1.mImage);
        } else {
            ViewHolder viewHolder2 = null;
            if (convertView != null && convertView.getTag() instanceof ViewHolder) {
                viewHolder2 = (ViewHolder) convertView.getTag();
            } else {
                view2 = LayoutInflater.from(context).inflate(R.layout.item_android, null);
                viewHolder2 = new ViewHolder(view2);
                view2.setTag(viewHolder2);
                convertView = view2;
            }
            viewHolder2.mText.setText(Html.fromHtml("<a href=\""
                    + aItem.getUrl() + "\">"
                    + aItem.getDesc() + "</a>"
                    + "[" + aItem.getWho() + "]"));
            viewHolder2.mText.setMovementMethod(LinkMovementMethod.getInstance());
            switch (aItem.getType()) {
                case "Android":
                    MyViewUtils.setIconDrawable(context, viewHolder2.mText, MaterialDesignIconic.Icon.gmi_android);
                    break;
                case "iOS":
                    MyViewUtils.setIconDrawable(context, viewHolder2.mText, MaterialDesignIconic.Icon.gmi_apple);
                    break;
                case "休息视频":
                    MyViewUtils.setIconDrawable(context, viewHolder2.mText, MaterialDesignIconic.Icon.gmi_collection_video);
                    break;
                case "前端":
                    MyViewUtils.setIconDrawable(context, viewHolder2.mText, MaterialDesignIconic.Icon.gmi_language_javascript);
                    break;
                case "拓展资源":
                    MyViewUtils.setIconDrawable(context, viewHolder2.mText, FontAwesome.Icon.faw_location_arrow);
                    break;
                case "App":
                    MyViewUtils.setIconDrawable(context, viewHolder2.mText, MaterialDesignIconic.Icon.gmi_apps);
                    break;
                case "瞎推荐":
                    MyViewUtils.setIconDrawable(context, viewHolder2.mText, MaterialDesignIconic.Icon.gmi_more);
                    break;

            }
        }
        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.text)
        TextView mText;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ImageViewHolder {
        @BindView(R.id.image)
        ImageView mImage;

        ImageViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
