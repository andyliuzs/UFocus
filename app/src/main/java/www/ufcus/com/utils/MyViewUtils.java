package www.ufcus.com.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;

import me.xiaopan.android.content.res.DimenUtils;
import www.ufcus.com.R;
import www.ufcus.com.activity.MainActivity;

/**
 * Created by andyliu on 16-6-29.
 */
public class MyViewUtils {

    /**
     * TextView设置图标
     * MainActivity
     *
     * @param context
     * @param view
     * @param icon
     */
    public static void setIconDrawable(Context context, int sizeDp, int cdPadding, TextView view, IIcon icon) {

        //可以在上、下、左、右设置图标，如果不想在某个地方显示，则设置为null。图标的宽高将会设置为固有宽高，既自动通过getIntrinsicWidth和getIntrinsicHeight获取
        view.setCompoundDrawablesWithIntrinsicBounds(new IconicsDrawable(context)
                        .icon(icon)
                        .color(Color.WHITE)
                        .sizeDp(sizeDp),
                null, null, null);
        view.setCompoundDrawablePadding(DimenUtils.dp2px(context, cdPadding));
    }


    /***
     * Adapter Item
     *
     * @param context
     * @param view
     * @param icon
     */
    public static void setIconDrawable(Context context, TextView view, IIcon icon) {
        view.setCompoundDrawablesWithIntrinsicBounds(new IconicsDrawable(context)
                        .icon(icon)
                        .color(ThemeUtils.getThemeColor(context, R.attr.colorPrimary))
                        .sizeDp(14),
                null, null, null);
        view.setCompoundDrawablePadding(DimenUtils.dp2px(context, 5));
    }

    /**
     * 中央显示的toast
     *
     * @param context
     * @param message
     */
    public static void showCenterToast(Context context, CharSequence message) {
        Toast mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }


    /**
     * 切换fragment
     *
     * @param activity
     * @param currentFragment
     * @param fragment
     * @return
     */
    public static Fragment switchFragment(MainActivity activity, Fragment currentFragment, Fragment fragment) {
        if (currentFragment == null || !fragment.getClass().getName().equals(currentFragment.getClass().getName())) {
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            return fragment;
        } else {
            return currentFragment;
        }
    }


}
