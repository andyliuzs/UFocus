package www.ufcus.com.activity;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.baidu.mapapi.SDKInitializer;
import com.bumptech.glide.Glide;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.foundation_icons_typeface_library.FoundationIcons;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.xiaopan.android.preference.PreferencesUtils;
import me.xiaopan.android.view.ViewUtils;
import me.xiaopan.android.widget.ToastUtils;
import www.ufcus.com.R;
import www.ufcus.com.activity.base.BaseActivity;
import www.ufcus.com.beans.Aitem;
import www.ufcus.com.event.CanSlideEvent;
import www.ufcus.com.event.SkinChangeEvent;
import www.ufcus.com.fragment.AllFragment;
import www.ufcus.com.fragment.MapFragment;
import www.ufcus.com.http.RequestManager;
import www.ufcus.com.http.callback.CallBack;
import www.ufcus.com.theme.ColorUiUtil;
import www.ufcus.com.theme.ColorView;
import www.ufcus.com.theme.Theme;
import www.ufcus.com.utils.MyViewUtils;
import www.ufcus.com.utils.PreUtils;
import www.ufcus.com.utils.SystemUtils;
import www.ufcus.com.utils.ThemeUtils;
import www.ufcus.com.widget.ResideLayout;

public class MainActivity extends BaseActivity implements ColorChooserDialog.ColorCallback {

    @BindView(R.id.resideLayout)
    ResideLayout mResideLayout;

    //菜单初始化
    @BindView(R.id.menu)
    RelativeLayout mMenu;
    //原来的avatar
    @BindView(R.id.headImg)
    ImageView headImg;
    @BindView(R.id.desc)
    TextView mDesc;
    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    @BindView(R.id.all)
    TextView mAll;
    @BindView(R.id.map)
    TextView mMap;
    @BindView(R.id.fuli)
    TextView mFuli;
    @BindView(R.id.android)
    TextView mAndroid;
    @BindView(R.id.ios)
    TextView mIos;
    @BindView(R.id.video)
    TextView mVideo;
    @BindView(R.id.front)
    TextView mFront;
    @BindView(R.id.resource)
    TextView mResource;
    @BindView(R.id.about)
    TextView mAbout;
    @BindView(R.id.app)
    TextView mApp;
    @BindView(R.id.theme)
    TextView mTheme;
    //主页面初始化
    @BindView(R.id.status_bar)
    ColorView mStatusBar;
    @BindView(R.id.container)
    FrameLayout mContainer;
    //tool_bar_layout
    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.more)
    TextView mMore;
    private Fragment currentFragment;
    private static int TOOL_BAR_ICON_SIZE = 25;


    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //butterKnife注解生效，必须执行
        ButterKnife.bind(this);
        //状态栏沉浸状态
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mStatusBar.setVisibility(View.VISIBLE);
            mStatusBar.getLayoutParams().height = SystemUtils.getStatusHeight(this);
            mStatusBar.setLayoutParams(mStatusBar.getLayoutParams());

        } else {
            mStatusBar.setVisibility(View.GONE);
        }

        MyViewUtils.setIconDrawable(this, 16, 10, mAll, MaterialDesignIconic.Icon.gmi_view_comfy);
        MyViewUtils.setIconDrawable(this, 16, 10, mMap, MaterialDesignIconic.Icon.gmi_map);

        MyViewUtils.setIconDrawable(this, 16, 10, mFuli, MaterialDesignIconic.Icon.gmi_mood);
        MyViewUtils.setIconDrawable(this, 16, 10, mAndroid, MaterialDesignIconic.Icon.gmi_android);
        MyViewUtils.setIconDrawable(this, 16, 10, mIos, MaterialDesignIconic.Icon.gmi_apple);
        MyViewUtils.setIconDrawable(this, 16, 10, mVideo, MaterialDesignIconic.Icon.gmi_collection_video);
        MyViewUtils.setIconDrawable(this, 16, 10, mFront, MaterialDesignIconic.Icon.gmi_language_javascript);
        MyViewUtils.setIconDrawable(this, 16, 10, mResource, FontAwesome.Icon.faw_location_arrow);
        MyViewUtils.setIconDrawable(this, 16, 10, mApp, MaterialDesignIconic.Icon.gmi_apps);
        MyViewUtils.setIconDrawable(this, 16, 10, mAbout, MaterialDesignIconic.Icon.gmi_account);
//        MyViewUtils.setIconDrawable(this,16,10,mSetting, MaterialDesignIconic.Icon.gmi_settings);
        MyViewUtils.setIconDrawable(this, 16, 10, mTheme, MaterialDesignIconic.Icon.gmi_palette);
        MyViewUtils.setIconDrawable(this, 16, 10, mMore, MaterialDesignIconic.Icon.gmi_more);

        //平滑图像加载
        // For a simple image list:
        Glide.with(this)
                .load(R.drawable.head_img)
                .placeholder(new IconicsDrawable(this)
                                .icon(FoundationIcons.Icon.fou_photo)
                                .color(Color.GRAY)
                                .backgroundColor(Color.WHITE)
                                .roundedCornersDp(40)
                                .paddingDp(15)

                )
                        //圆形
                .bitmapTransform(new CropCircleTransformation(this))
                .dontAnimate()
                .into(headImg);

        //初始化描述信息
        RequestManager.getVideo(true, getName(), true, new CallBack<List<Aitem>>() {
            @Override
            public void onSuccess(List<Aitem> result) {
                mDesc.setText(result.get(0).getDesc());
            }
        });

        //初始化头像
        RequestManager.getFuLi(true, getName(), true, new CallBack<List<Aitem>>() {
            @Override
            public void onSuccess(List<Aitem> result) {
                Glide.with(MainActivity.this)
                        .load(result.get(0).getUrl())
                                //设置占位图
                        .placeholder(new IconicsDrawable(MainActivity.this)
                                .icon(FoundationIcons.Icon.fou_photo)
                                .color(Color.GRAY)
                                .backgroundColor(Color.WHITE)
                                .roundedCornersDp(40)
                                .paddingDp(15)
                                .sizeDp(75))
                        .bitmapTransform(new CropCircleTransformation(MainActivity.this))
                        .dontAnimate()
                        .into(headImg);
            }
        });

        if (PreferencesUtils.getBoolean(this, "isFirst", true)) {
            mResideLayout.openPane();
            PreferencesUtils.putBoolean(this, "isFirst", false);
        }

        mIcon.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE)
                .icon(MaterialDesignIconic.Icon.gmi_view_comfy).sizeDp((int) TOOL_BAR_ICON_SIZE));
        mTitle.setText(R.string.app_name);
        MyViewUtils.switchFragment(this, currentFragment, new AllFragment());
        testData();
    }

    /**
     * 测试数据,如果有服务端可删除本方法
     */
    private void testData() {

        PreUtils.putString(this, "phone_number", "18301214392");
        PreUtils.putString(this, "attend_wifi_ssid", "office");
        PreUtils.putInt(this, "work_time", 8);
    }


    @OnClick({R.id.headImg, R.id.all, R.id.map, R.id.fuli, R.id.android, R.id.ios, R.id.video, R.id.front,
            R.id.resource, R.id.about,
            R.id.app, R.id.theme, R.id.icon, R.id.more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.headImg:
                MyViewUtils.showCenterToast(this, "再点吃掉你丫的");
                break;
            case R.id.all:
                mResideLayout.closePane();
                mIcon.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(MaterialDesignIconic.Icon.gmi_view_comfy).sizeDp((int) TOOL_BAR_ICON_SIZE));
                mTitle.setText(R.string.app_name);
                currentFragment = MyViewUtils.switchFragment(this, currentFragment, new AllFragment());
                break;
            case R.id.map:
                mResideLayout.closePane();
                mIcon.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(MaterialDesignIconic.Icon.gmi_map).sizeDp((int) TOOL_BAR_ICON_SIZE));
                mTitle.setText(R.string.map);
                currentFragment = MyViewUtils.switchFragment(this, currentFragment, new MapFragment());
                break;
            case R.id.fuli:
//                mResideLayout.closePane();
//                mIcon.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(MaterialDesignIconic.Icon.gmi_mood).sizeDp((int) TOOL_BAR_ICON_SIZE));
//                mTitle.setText(R.string.fuli);
//                switchFragment(new FuLiFragment());
                break;
            case R.id.android:
//                mResideLayout.closePane();
//                mIcon.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(MaterialDesignIconic.Icon.gmi_android).sizeDp((int) TOOL_BAR_ICON_SIZE));
//                mTitle.setText(R.string.android);
//                switchFragment(new AndroidFragment());
                break;
            case R.id.ios:
//                mResideLayout.closePane();
//                mIcon.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(MaterialDesignIconic.Icon.gmi_apple).sizeDp((int) TOOL_BAR_ICON_SIZE));
//                mTitle.setText(R.string.ios);
//                switchFragment(new IOSFragment());
                break;

            case R.id.video:
//                mResideLayout.closePane();
//                mIcon.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(MaterialDesignIconic.Icon.gmi_collection_video).sizeDp((int) TOOL_BAR_ICON_SIZE));
//                mTitle.setText(R.string.video);
//                switchFragment(new VideoFragment());
                break;
            case R.id.front:
//                mResideLayout.closePane();
//                mIcon.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(MaterialDesignIconic.Icon.gmi_language_javascript).sizeDp((int) TOOL_BAR_ICON_SIZE));
//                mTitle.setText(R.string.front);
//                switchFragment(new FrontFragment());
                break;
            case R.id.resource:
//                mResideLayout.closePane();
//                mIcon.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(FontAwesome.Icon.faw_location_arrow).sizeDp((int) TOOL_BAR_ICON_SIZE));
//                mTitle.setText(R.string.resource);
//                switchFragment(new ResourceFragment());
                break;
            case R.id.app:
//                mResideLayout.closePane();
//                mIcon.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(MaterialDesignIconic.Icon.gmi_apps).sizeDp((int) TOOL_BAR_ICON_SIZE));
//                mTitle.setText(R.string.app);
//                switchFragment(new AppFragment());
                break;
            case R.id.more:
//                mResideLayout.closePane();
//                mIcon.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(MaterialDesignIconic.Icon.gmi_more).sizeDp((int) TOOL_BAR_ICON_SIZE));
//                mTitle.setText(R.string.more);
//                switchFragment(new MoreFragment());
                break;
//            case R.id.setting:
//                mResideLayout.closePane();
//                mIcon.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(MaterialDesignIconic.Icon.gmi_settings).sizeDp(20));
//                mTitle.setText(R.string.setting);
//                switchFragment(new SettingFragment());
//                break;
            case R.id.about:
                new MaterialDialog.Builder(this)
                        .title(R.string.about)
                        .icon(new IconicsDrawable(this)
                                .color(ThemeUtils.getThemeColor(this, R.attr.colorPrimary))
                                .icon(MaterialDesignIconic.Icon.gmi_account)
                                .sizeDp(20))
                        .content(R.string.about_me)
                        .positiveText(R.string.close)
                        .show();
                break;
            case R.id.theme:
                new ColorChooserDialog.Builder(this, R.string.theme)
                        .customColors(R.array.colors, null)
                        .doneButton(R.string.done)
                        .cancelButton(R.string.cancel)
                        .allowUserColorInput(false)
                        .allowUserColorInputAlpha(false)
                        .show();
                break;
            case R.id.icon:
                mResideLayout.openPane();
                break;
        }
    }

    @Override
    public void onColorSelection(ColorChooserDialog dialog, int selectedColor) {
        if (selectedColor == ThemeUtils.getThemeColor(this, R.attr.colorPrimary)) {
            return;
        }
        EventBus.getDefault().post(new SkinChangeEvent());

        if (selectedColor == getResources().getColor(R.color.colorBluePrimary)) {
            setTheme(R.style.BlueTheme);
            PreUtils.setCurrentTheme(this, Theme.Blue);

        } else if (selectedColor == getResources().getColor(R.color.colorRedPrimary)) {
            setTheme(R.style.RedTheme);
            PreUtils.setCurrentTheme(this, Theme.Red);

        } else if (selectedColor == getResources().getColor(R.color.colorBrownPrimary)) {
            setTheme(R.style.BrownTheme);
            PreUtils.setCurrentTheme(this, Theme.Brown);

        } else if (selectedColor == getResources().getColor(R.color.colorGreenPrimary)) {
            setTheme(R.style.GreenTheme);
            PreUtils.setCurrentTheme(this, Theme.Green);

        } else if (selectedColor == getResources().getColor(R.color.colorPurplePrimary)) {
            setTheme(R.style.PurpleTheme);
            PreUtils.setCurrentTheme(this, Theme.Purple);

        } else if (selectedColor == getResources().getColor(R.color.colorTealPrimary)) {
            setTheme(R.style.TealTheme);
            PreUtils.setCurrentTheme(this, Theme.Teal);

        } else if (selectedColor == getResources().getColor(R.color.colorPinkPrimary)) {
            setTheme(R.style.PinkTheme);
            PreUtils.setCurrentTheme(this, Theme.Pink);

        } else if (selectedColor == getResources().getColor(R.color.colorDeepPurplePrimary)) {
            setTheme(R.style.DeepPurpleTheme);
            PreUtils.setCurrentTheme(this, Theme.DeepPurple);

        } else if (selectedColor == getResources().getColor(R.color.colorOrangePrimary)) {
            setTheme(R.style.OrangeTheme);
            PreUtils.setCurrentTheme(this, Theme.Orange);

        } else if (selectedColor == getResources().getColor(R.color.colorIndigoPrimary)) {
            setTheme(R.style.IndigoTheme);
            PreUtils.setCurrentTheme(this, Theme.Indigo);

        } else if (selectedColor == getResources().getColor(R.color.colorLightGreenPrimary)) {
            setTheme(R.style.LightGreenTheme);
            PreUtils.setCurrentTheme(this, Theme.LightGreen);

        } else if (selectedColor == getResources().getColor(R.color.colorDeepOrangePrimary)) {
            setTheme(R.style.DeepOrangeTheme);
            PreUtils.setCurrentTheme(this, Theme.DeepOrange);

        } else if (selectedColor == getResources().getColor(R.color.colorLimePrimary)) {
            setTheme(R.style.LimeTheme);
            PreUtils.setCurrentTheme(this, Theme.Lime);

        } else if (selectedColor == getResources().getColor(R.color.colorBlueGreyPrimary)) {
            setTheme(R.style.BlueGreyTheme);
            PreUtils.setCurrentTheme(this, Theme.BlueGrey);

        } else if (selectedColor == getResources().getColor(R.color.colorCyanPrimary)) {
            setTheme(R.style.CyanTheme);
            PreUtils.setCurrentTheme(this, Theme.Cyan);

        }

        final View rootView = getWindow().getDecorView();
        rootView.setDrawingCacheEnabled(true);
        rootView.buildDrawingCache(true);

        final Bitmap localBitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);
        if (null != localBitmap && rootView instanceof ViewGroup) {
            final View tmpView = new View(getApplicationContext());
            tmpView.setBackgroundDrawable(new BitmapDrawable(getResources(), localBitmap));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ((ViewGroup) rootView).addView(tmpView, params);
            tmpView.animate().alpha(0).setDuration(400).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ColorUiUtil.changeTheme(rootView, getTheme());
                    System.gc();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ((ViewGroup) rootView).removeView(tmpView);
                    localBitmap.recycle();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
        }
    }


    @Subscribe
    public void onEvent(CanSlideEvent event) {
        mResideLayout.setChildWantMove(event.getB());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mResideLayout.isOpen()) {
                mResideLayout.closePane();
            } else {
                exitBy2Click(); //调用双击退出函数
            }

        }
        return false;
    }


    /***
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            System.exit(0);
        }
    }
}
