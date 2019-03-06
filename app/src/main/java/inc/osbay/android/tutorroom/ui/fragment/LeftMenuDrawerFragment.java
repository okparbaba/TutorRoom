package inc.osbay.android.tutorroom.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.database.DBAdapter;
import inc.osbay.android.tutorroom.sdk.model.Account;
import inc.osbay.android.tutorroom.ui.activity.FragmentHolderActivity;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

public class LeftMenuDrawerFragment extends BackHandledFragment {

    @BindView(R.id.sdv_profile_photo)
    ImageView profileImg;
    @BindView(R.id.noti_count)
    TextView notiCount;
    SharedPreferenceData sharedPreferenceData;
    private String mLocale;
    private Account mAccount;
    private int count;
    private DBAdapter DBAdapter;
    private String accountId;

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocale = Locale.getDefault().getLanguage();
        sharedPreferenceData = new SharedPreferenceData(Objects.requireNonNull(getActivity()));
        DBAdapter = new DBAdapter(getActivity());
        accountId = String.valueOf(sharedPreferenceData.getInt("account_id"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_left_menu_drawer, container, false);
        ButterKnife.bind(this, rootView);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAccount = DBAdapter.getAccountById(accountId);
        count = DBAdapter.getNotiCount();
        profileImg.setImageURI(Uri.parse(mAccount.getAvatar()));
        if (count > 0) {
            notiCount.setText(String.valueOf(count));
            notiCount.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.sdv_profile_photo)
    void clickProfileImg() {
        FragmentManager fm = getParentFragment().getFragmentManager();
        Fragment frg = fm.findFragmentById(R.id.framelayout);
        Fragment fragment = new ProfileFragment();
        if (frg == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, fragment)
                    .commit();
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.framelayout, fragment)
                    .commit();
        }
        ((MainFragment) getParentFragment()).closeDrawer();
    }

    @OnClick(R.id.noti_ll)
    void clickNoti() {
        FragmentManager fm = getParentFragment().getFragmentManager();
        Fragment frg = fm.findFragmentById(R.id.framelayout);
        Fragment fragment = new NotificationFragment();
        if (frg == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, fragment)
                    .commit();
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.framelayout, fragment)
                    .commit();
        }
        ((MainFragment) getParentFragment()).closeDrawer();
    }

    @OnClick(R.id.setting_tv)
    void clickSetting() {
        FragmentManager fm = getParentFragment().getFragmentManager();
        Fragment frg = fm.findFragmentById(R.id.framelayout);
        Fragment fragment = new SettingFragment();
        if (frg == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, fragment)
                    .commit();
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.framelayout, fragment)
                    .commit();
        }
        ((MainFragment) getParentFragment()).closeDrawer();
    }

    @OnClick(R.id.faq_tv)
    void clickFAQ() {
        FragmentManager fm = getParentFragment().getFragmentManager();
        Fragment frg = fm.findFragmentById(R.id.framelayout);
        Fragment fragment = new ClassroomFAQFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ClassroomFAQFragment.EXTRA_DISPLAY_FRAGMENT, LeftMenuDrawerFragment.class.getSimpleName());
        fragment.setArguments(bundle);
        if (frg == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, fragment)
                    .commit();
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.framelayout, fragment)
                    .commit();
        }
        ((MainFragment) getParentFragment()).closeDrawer();
    }

    @OnClick(R.id.signout_tv)
    void clickLogout() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.signout))
                .setMessage(getString(R.string.lmd_sign_out_msg))
                .setNegativeButton(getString(R.string.cr_leave_room_cancel), null)
                .setPositiveButton(getString(R.string.signout), (dialogInterface, i) -> {
                    sharedPreferenceData.addBoolean("login", false);

                    Intent intent = new Intent(getActivity(), FragmentHolderActivity.class);
                    intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, LeftMenuDrawerFragment.class.getSimpleName());
                    startActivity(intent);
                    getActivity().finish();
                })
                .show();
    }
}