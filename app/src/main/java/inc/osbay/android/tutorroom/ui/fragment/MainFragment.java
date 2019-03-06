package inc.osbay.android.tutorroom.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.database.DBAdapter;
import inc.osbay.android.tutorroom.sdk.database.TutorAdapter;
import inc.osbay.android.tutorroom.sdk.model.Banner;
import inc.osbay.android.tutorroom.sdk.model.Notification;
import inc.osbay.android.tutorroom.sdk.model.Tutor;
import inc.osbay.android.tutorroom.ui.activity.FragmentHolderActivity;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

public class MainFragment extends BackHandledFragment {

    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.rl_main_content)
    RelativeLayout frame;
    @BindView(R.id.vp_main_slider)
    ViewPager bannerPager;

    private ActionBarDrawerToggle mDrawerToggle;
    private ServerRequestManager requestManager;
    private String accountID;
    private List<Banner> bannerList = new ArrayList<>();
    private int mCurrentPage;
    private int mPageNo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(getActivity());
        accountID = String.valueOf(sharedPreferenceData.getInt("account_id"));
        requestManager = new ServerRequestManager(getActivity());
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        requestManager.getLessonListByAllTag(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse result) {
                getTutorList(progressDialog);
            }

            @Override
            public void onError(ServerError err) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getResources().getString(R.string.get_lesson_err), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getTutorList(ProgressDialog progressDialog) {
        requestManager.getTutorList(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse result) {
                if (result.getCode() == 1) {//For Success Response
                    final TutorAdapter dbAdapter = new TutorAdapter(getActivity());
                    try {
                        JSONArray jsonArray = new JSONArray(result.getDataSt());
                        List<Tutor> tutors = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Tutor tutor = new Tutor(jsonArray.getJSONObject(i));
                            tutors.add(tutor);
                        }
                        dbAdapter.insertTutors(tutors);
                    } catch (JSONException je) {
                        Log.e(CommonConstant.TAG, "Cannot parse Tutor Object", je);
                    }
                    getNotification(progressDialog);
                }
            }

            @Override
            public void onError(ServerError err) {
                progressDialog.dismiss();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), getString(R.string.tu_lst_refresh_failed), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    void getNotification(ProgressDialog progressDialog) {
        requestManager.getNoti(accountID, new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse response) {
                progressDialog.dismiss();
                if (response.getCode() == 1) {
                    List<Notification> notificationList = new ArrayList<>();
                    try {
                        JSONArray dataArray = new JSONArray(response.getDataSt());
                        for (int i = 0; i < dataArray.length(); i++) {
                            Notification notification = new Notification(dataArray.getJSONObject(i));
                            notificationList.add(notification);
                        }
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                    DBAdapter dbAdapter = new DBAdapter(getActivity());
                    dbAdapter.insertNotifications(notificationList);
                }
            }

            @Override
            public void onError(ServerError err) {
                progressDialog.dismiss();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        toolBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setTitle(getResources().getString(R.string.app_name));

        // Implement Left Menu Drawer
        LeftMenuDrawerFragment leftMenuDrawerFragment = new LeftMenuDrawerFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment oldDrawer = fragmentManager.findFragmentById(R.id.left_menu_drawer);
        if (oldDrawer == null) {
            fragmentManager.beginTransaction().add(R.id.left_menu_drawer, leftMenuDrawerFragment)
                    .commitAllowingStateLoss();
        } else {
            fragmentManager.beginTransaction().replace(R.id.left_menu_drawer, leftMenuDrawerFragment)
                    .commitAllowingStateLoss();
        }
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                mDrawerLayout,
                toolBar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float moveFactor = (getResources().getDimension(R.dimen.dp250) * slideOffset);
                frame.setTranslationX(moveFactor);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                ServerRequestManager requestManager = new ServerRequestManager(
                        getActivity().getApplicationContext());
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(false);
        toolBar.setNavigationIcon(createImage());
        toolBar.setNavigationOnClickListener(view -> {
            if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START, false);
                mDrawerLayout.addDrawerListener(mDrawerToggle);
                mDrawerToggle.setDrawerSlideAnimationEnabled(true);
            } else {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });
        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(() -> mDrawerToggle.syncState());
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestManager.getBanner(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse response) {
                if (response.getCode() == 1) {
                    try {
                        JSONArray dataArray = new JSONArray(response.getDataSt());
                        for (int i = 0; i < dataArray.length(); i++) {
                            Banner banner = new Banner(dataArray.getJSONObject(i));
                            if (banner.getBannerType().equalsIgnoreCase(String.valueOf(CommonConstant.MB_HomeBanner))) {
                                bannerList.add(banner);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    bannerPager.setAdapter(new MainImageSlideAdapter(getActivity()));
                    mPageNo = bannerList.size();
                    mCurrentPage = 0;

                    final Handler handler = new Handler();
                    final Runnable runnable = () -> {
                        if (mCurrentPage == mPageNo) {
                            mCurrentPage = 0;
                        }

                        bannerPager.setCurrentItem(mCurrentPage++, true);
                    };

                    Timer swipeTimer = new Timer();
                    swipeTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(runnable);
                        }
                    }, 3000, 3000);

                    bannerPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            mCurrentPage = position;
                        }

                        @Override
                        public void onPageSelected(int position) {

                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                }
            }

            @Override
            public void onError(ServerError err) {

            }
        });
    }

    public void closeDrawer() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START, false);
    }

    @OnClick(R.id.package_imv)
    void clickPackage() {
        Intent intent = new Intent(getActivity(), FragmentHolderActivity.class);
        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, PackageFragment.class.getSimpleName());
        startActivity(intent);
    }

    @OnClick(R.id.lesson_imv)
    void clickSingleBooking() {
        Intent intent = new Intent(getActivity(), FragmentHolderActivity.class);
        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, LessonFragment.class.getSimpleName());
        startActivity(intent);
    }

    @OnClick(R.id.tutor_imv)
    void clickTutor() {
        Intent intent = new Intent(getActivity(), FragmentHolderActivity.class);
        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, TutorListFragment.class.getSimpleName());
        startActivity(intent);
    }

    @OnClick(R.id.schedule_img)
    void clickSchedule() {
        Intent intent = new Intent(getActivity(), FragmentHolderActivity.class);
        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, ScheduleFragment.class.getSimpleName());
        startActivity(intent);
    }

    @OnClick(R.id.store_imv)
    void clickStore() {
        Intent intent = new Intent(getActivity(), FragmentHolderActivity.class);
        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, StoreFragment.class.getSimpleName());
        startActivity(intent);
    }

    @OnClick(R.id.online_support_img)
    void clickOnlineSupport() {
        Intent intent = new Intent(getActivity(), FragmentHolderActivity.class);
        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, OnlineSupportFragment.class.getSimpleName());
        startActivity(intent);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public void setTitle(String title) {
        if (toolBar != null) {
            TextView tvTitle = toolBar.findViewById(R.id.tv_toolbar_title);
            //tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/microsoft_jhenghei.ttf"));
            tvTitle.setText(title);
            tvTitle.setCompoundDrawables(null, null, null, null);
            tvTitle.setOnClickListener(null);
        }
    }

    public Drawable createImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.menu_56);
        return new BitmapDrawable(getResources(), bitmap);
    }

    private class MainImageSlideAdapter extends PagerAdapter {
        private Context mContext;

        private MainImageSlideAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return bannerList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            SimpleDraweeView sdvMainImage = new SimpleDraweeView(mContext);
            sdvMainImage.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
            Uri uri = Uri.parse(bannerList.get(position).getImage());
            sdvMainImage.setImageURI(uri);
            container.addView(sdvMainImage);
            return sdvMainImage;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((SimpleDraweeView) object);
        }
    }
}