package inc.osbay.android.tutorroom.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
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

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.ui.activity.MainActivity;
import inc.osbay.android.tutorroom.utils.CommonUtil;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

public class NewLoginFragment extends BackHandledFragment {
    @BindView(R.id.password)
    EditText passET;
    @BindView(R.id.email)
    EditText emailET;
    @BindView(R.id.email_error_tv)
    TextView emailErrorTV;
    @BindView(R.id.password_error_tv)
    TextView passErrorTV;

    SharedPreferenceData sharedPreferenceData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_login, container, false);
        ButterKnife.bind(this, view);

        sharedPreferenceData = new SharedPreferenceData(Objects.requireNonNull(getActivity()));
        return view;
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

    @OnClick(R.id.login)
    void clickLogin() {
        String passwordSt = passET.getText().toString();
        String emailSt = emailET.getText().toString();

        //*** Email Validation ***/
        if (emailSt.equals("")) {
            emailErrorTV.setVisibility(View.VISIBLE);
            emailErrorTV.setText(getString(R.string.email_error));
        } else if (!emailSt.equals("") && !CommonUtil.validateEmail(emailSt)) {
            emailErrorTV.setVisibility(View.VISIBLE);
            emailErrorTV.setText(getString(R.string.invalid_email));
        } else if (!emailSt.equals("") && CommonUtil.validateEmail(emailSt)) {
            emailErrorTV.setVisibility(View.GONE);
        }

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

        if (!emailSt.equals("") && CommonUtil.validateEmail(emailSt) && !passwordSt.equals("") &&
                passwordSt.length() >= 6 && passwordSt.length() <= 10) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();

            ServerRequestManager requestManager = new ServerRequestManager(getActivity());
            requestManager.loginStudent(passwordSt, emailSt, new ServerRequestManager.OnRequestFinishedListener() {
                @Override
                public void onSuccess(ServerResponse result) {
                    progressDialog.dismiss();
                    Log.i("Login", "Successful");
                    sharedPreferenceData.addBoolean("login", true);
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

    @Override
    public boolean onBackPressed() {
        return false;
    }
}