package inc.osbay.android.tutorroom.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.database.DBAdapter;
import inc.osbay.android.tutorroom.sdk.model.Account;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

public class ProfileFragment extends BackHandledFragment {
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.email_tv)
    TextView emailTv;
    @BindView(R.id.phone_tv)
    TextView phoneTV;
    @BindView(R.id.country_tv)
    TextView countryTv;
    @BindView(R.id.address_tv)
    TextView addressTv;
    @BindView(R.id.native_lang_tv)
    TextView nativeLangTv;
    @BindView(R.id.change_pwd_tv)
    TextView changePwdTv;
    @BindView(R.id.edit_tv)
    TextView editTv;
    @BindView(R.id.sdv_profile_photo)
    SimpleDraweeView profilePic;
    /*@BindView(R.id.noti_img)
    ImageView notiImg;*/
    private Account mAccount;
    private DBAdapter mDBAdapter;
    private String accountId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDBAdapter = new DBAdapter(getActivity());
        SharedPreferenceData mPreferences = new SharedPreferenceData(getActivity());
        accountId = String.valueOf(mPreferences.getInt("account_id"));
        mAccount = mDBAdapter.getAccountById(accountId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);
        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profilePic.setImageURI(Uri.parse(mAccount.getAvatar()));
        nameTv.setText(mAccount.getName());
        emailTv.setText(mAccount.getEmail());
        phoneTV.setText(mAccount.getPhoneCode() + "" + mAccount.getPhoneNumber());
        if (!mAccount.getCountry().equals("null") && !mAccount.getCountry().equals(""))
            countryTv.setText(mAccount.getCountry());
        else
            countryTv.setText("");
        if (!mAccount.getAddress().equals("null") && !mAccount.getAddress().equals(""))
            addressTv.setText(mAccount.getAddress());
        else
            addressTv.setText("");
        nativeLangTv.setText(mAccount.getSpeakingLang());
        //notiImg.setImageDrawable(CommonUtil.createImage(getActivity(), count));
    }

    @Override
    public boolean onBackPressed() {
        getFragmentManager().popBackStack();
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        setTitle(getString(R.string.profile));
        setDisplayHomeAsUpEnable(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        //notiImg.setImageDrawable(CommonUtil.createImage(getActivity(), count));
        mAccount = mDBAdapter.getAccountById(accountId);
        profilePic.setImageURI(Uri.parse(mAccount.getAvatar()));
        nameTv.setText(mAccount.getName());
        emailTv.setText(mAccount.getEmail());
        phoneTV.setText(mAccount.getPhoneCode() + mAccount.getPhoneNumber());
        /*countryTv.setText(mAccount.getCountry());
        addressTv.setText(mAccount.getAddress());*/
        if (!mAccount.getCountry().equals("null") && !mAccount.getCountry().equals(""))
            countryTv.setText(mAccount.getCountry());
        else
            countryTv.setText("");
        if (!mAccount.getAddress().equals("null") && !mAccount.getAddress().equals(""))
            addressTv.setText(mAccount.getAddress());
        else
            addressTv.setText("");
        nativeLangTv.setText(mAccount.getSpeakingLang());
    }

    /*@OnClick(R.id.noti_img)
    void readNotification() {
        FragmentManager fm = getFragmentManager();
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
    }*/

    @OnClick(R.id.edit_tv)
    void clickEdit() {
        FragmentManager fm = getFragmentManager();
        Fragment frg = fm.findFragmentById(R.id.framelayout);
        Fragment fragment = new EditProfileFragment();
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
    }

    @OnClick(R.id.change_pwd_tv)
    void changePassword() {
        FragmentManager fm = getFragmentManager();
        Fragment frg = fm.findFragmentById(R.id.framelayout);
        Fragment fragment = new PasswordFragment();
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
