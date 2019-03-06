package inc.osbay.android.tutorroom.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.sdk.database.DBAdapter;
import inc.osbay.android.tutorroom.sdk.model.Account;
import inc.osbay.android.tutorroom.ui.activity.MainActivity;
import inc.osbay.android.tutorroom.utils.CommonUtil;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

public class ExistingLoginFragment extends BackHandledFragment {

    @BindView(R.id.password)
    EditText passET;
    @BindView(R.id.name)
    TextView nameTV;
    @BindView(R.id.profile_pic_img)
    SimpleDraweeView profilePic;
    @BindView(R.id.password_error_tv)
    TextView passErrorTV;

    private Account mAccount;
    private SharedPreferenceData mPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_existing_login, container, false);
        ButterKnife.bind(this, view);

        mPreferences = new SharedPreferenceData(getActivity());
        DBAdapter DBAdapter = new DBAdapter(getActivity());
        String accountId = String.valueOf(mPreferences.getInt("account_id"));
        mAccount = DBAdapter.getAccountById(accountId);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mAccount.getAvatar() != null)
            profilePic.setImageURI(Uri.parse(mAccount.getAvatar()));
        nameTV.setText(mAccount.getName());
    }

    @OnClick(R.id.login)
    void clickLogin() {
        String passwordSt = passET.getText().toString();
        String emailSt = mAccount.getEmail();

        //*** Password Validation ***/
        if (passwordSt.equals("")) {
            passErrorTV.setVisibility(View.VISIBLE);
            passErrorTV.setText(getString(R.string.pass_error));
        } else {
            if (passwordSt.length() < 6) {
                passErrorTV.setVisibility(View.VISIBLE);
                passErrorTV.setText(getString(R.string.pass_min_length));
            }
            if (passwordSt.length() >= 6 && passwordSt.length() <= 10) {
                passErrorTV.setVisibility(View.GONE);
            }
        }

        if (!passwordSt.equals("") && passwordSt.length() >= 6 && passwordSt.length() <= 10) {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
            ServerRequestManager requestManager = new ServerRequestManager(getActivity());
            requestManager.loginStudent(passwordSt, emailSt, new ServerRequestManager.OnRequestFinishedListener() {
                @Override
                public void onSuccess(ServerResponse result) {
                    progressDialog.dismiss();
                    Log.i("Login", "Successful");
                    mPreferences.addBoolean("login", true);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }

                @Override
                public void onError(ServerError err) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), err.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @OnClick(R.id.signup)
    void clickHaveNoAccount() {
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.framelayout);
        if (fragment == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, new SignupFragment()).commit();
        } else {
            fm.beginTransaction()
                    .replace(R.id.framelayout, new SignupFragment()).commit();
        }
    }

    @OnClick(R.id.change_user)
    void changeUser() {
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.framelayout);
        if (fragment == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, new NewLoginFragment()).commit();
        } else {
            fm.beginTransaction()
                    .replace(R.id.framelayout, new NewLoginFragment()).commit();
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
