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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.adapter.CountryCodeAdapter;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.database.DBAdapter;
import inc.osbay.android.tutorroom.sdk.model.Account;
import inc.osbay.android.tutorroom.sdk.model.CountryCode;
import inc.osbay.android.tutorroom.ui.activity.MainActivity;
import inc.osbay.android.tutorroom.utils.CommonUtil;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

public class SignupFragment extends BackHandledFragment {

    @BindView(R.id.country_code_spinner)
    Spinner mSpnAccountPhCodes;
    @BindView(R.id.name)
    EditText nameET;
    @BindView(R.id.password)
    EditText passET;
    @BindView(R.id.re_password)
    EditText rePassET;
    @BindView(R.id.phone_et)
    EditText phoneET;
    @BindView(R.id.email)
    EditText emailET;
    @BindView(R.id.name_error_tv)
    TextView nameErrorTV;
    @BindView(R.id.password_error_tv)
    TextView passErrorTV;
    @BindView(R.id.re_password_error_tv)
    TextView repassErrorTV;
    @BindView(R.id.email_error_tv)
    TextView emailErrorTV;
    @BindView(R.id.phone_error_tv)
    TextView phoneErrorTV;
    private SharedPreferenceData sharedPreferenceData;
    private ServerRequestManager requestManager;
    private String nameSt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        ButterKnife.bind(this, view);

        sharedPreferenceData = new SharedPreferenceData(getActivity());
        DBAdapter DBAdapter = new DBAdapter(getActivity());
        List<CountryCode> countryCodes = DBAdapter.getCountryCodes();

        CountryCodeAdapter adapter = new CountryCodeAdapter(getActivity(), countryCodes);
        mSpnAccountPhCodes.setAdapter(adapter);
        return view;
    }

    @OnClick(R.id.login)
    void clickLogin() {
        DBAdapter DBAdapter = new DBAdapter(getActivity());
        SharedPreferenceData mPreferences = new SharedPreferenceData(getActivity());
        String accountId = String.valueOf(mPreferences.getInt("account_id"));
        Account account = DBAdapter.getAccountById(accountId);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.framelayout);
        if (account != null) {
            if (fragment == null) {
                fm.beginTransaction()
                        .add(R.id.framelayout, new ExistingLoginFragment()).commit();
            } else {
                fm.beginTransaction()
                        .replace(R.id.framelayout, new ExistingLoginFragment()).commit();
            }
        } else {
            if (fragment == null) {
                fm.beginTransaction()
                        .add(R.id.framelayout, new NewLoginFragment()).commit();
            } else {
                fm.beginTransaction()
                        .replace(R.id.framelayout, new NewLoginFragment()).commit();
            }
        }
    }

    @OnClick(R.id.signup)
    void clickSignup() {
        nameSt = nameET.getText().toString();
        String passwordSt = passET.getText().toString();
        String rePasswordSt = rePassET.getText().toString();
        String emailSt = emailET.getText().toString();
        String countryCodeSt = mSpnAccountPhCodes.getSelectedItem().toString();
        String phoneSt = phoneET.getText().toString();
        requestManager = new ServerRequestManager(getActivity());

        //*** Name Validation ***/
        if (nameSt.equals("")) {
            nameErrorTV.setVisibility(View.VISIBLE);
        } else {
            nameErrorTV.setVisibility(View.GONE);
        }

        //*** Password Validation ***/
        if (!passwordSt.equals(rePasswordSt)) {
            passErrorTV.setVisibility(View.VISIBLE);
            repassErrorTV.setVisibility(View.VISIBLE);
            passErrorTV.setText(getString(R.string.pass_not_matched));
            repassErrorTV.setText(getString(R.string.pass_not_matched));
        } else if (passwordSt.equals(rePasswordSt)) {
            passErrorTV.setVisibility(View.GONE);
            repassErrorTV.setVisibility(View.GONE);
        }

        if (passwordSt.equals("")) {
            passErrorTV.setVisibility(View.VISIBLE);
            passErrorTV.setText(getString(R.string.pass_error));
        } else {
            if (passwordSt.length() < 6) {
                passErrorTV.setVisibility(View.VISIBLE);
                passErrorTV.setText(getString(R.string.pass_min_length));
            }
            if (passwordSt.length() >= 6 && passwordSt.length() <= 10 && passwordSt.equals(rePasswordSt)) {
                passErrorTV.setVisibility(View.GONE);
            }
        }
        if (rePasswordSt.equals("")) {
            repassErrorTV.setVisibility(View.VISIBLE);
            repassErrorTV.setText(getString(R.string.repass_error));
        } else {
            if (rePasswordSt.length() < 6) {
                repassErrorTV.setVisibility(View.VISIBLE);
                repassErrorTV.setText(getString(R.string.pass_min_length));
            }
            if (rePasswordSt.length() >= 6 && rePasswordSt.length() <= 10 && passwordSt.equals(rePasswordSt)) {
                repassErrorTV.setVisibility(View.GONE);
            }
        }

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

        //*** Country Validation ***/
        if (countryCodeSt.equals("") || phoneSt.equals("")) {
            phoneErrorTV.setVisibility(View.VISIBLE);
            phoneErrorTV.setText(getString(R.string.phone_error));
        } else {
            phoneErrorTV.setVisibility(View.GONE);
        }

        if (!nameSt.equals("") && passwordSt.equals(rePasswordSt) && !passwordSt.equals("") &&
                passwordSt.length() >= 6 && passwordSt.length() <= 10 && !rePasswordSt.equals("") &&
                rePasswordSt.length() >= 6 && rePasswordSt.length() <= 10 && !emailSt.equals("") &&
                CommonUtil.validateEmail(emailSt) & !countryCodeSt.equals("") && !phoneSt.equals("")) {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
            requestManager.registerStudent(nameSt, passwordSt, emailSt, countryCodeSt, phoneSt,
                    CommonConstant.emailRegisterType, new ServerRequestManager.OnRequestFinishedListener() {
                        @Override
                        public void onSuccess(ServerResponse result) {
                            if (result.getCode() == ServerResponse.Status.SUCCESS) {
                                try {
                                    JSONObject object = new JSONObject(result.getDataSt());
                                    String studentID = object.getString("account_id");
                                    sharedPreferenceData.addInt("account_id", Integer.parseInt(studentID));
                                    sharedPreferenceData.addString("account_name", nameSt);
                                    getStudentInfo(studentID, progressDialog);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (result.getCode() == ServerResponse.Status.Account_EmailAlreadyExit) {
                                progressDialog.dismiss();
                                emailErrorTV.setVisibility(View.VISIBLE);
                                emailErrorTV.setText(result.getMessage());
                            }
                        }

                        @Override
                        public void onError(ServerError err) {
                            progressDialog.dismiss();
                            Log.i("Register Failed", err.getMessage());
                        }
                    });
        }
    }

    private void getStudentInfo(String studentID, ProgressDialog progressDialog) {
        requestManager.getProfileInfo(studentID, new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse result) {
                progressDialog.dismiss();
                Log.i("Profile", "Received");
                sharedPreferenceData.addBoolean("login", true);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void onError(ServerError err) {
                progressDialog.dismiss();
                Log.i("Profile Failed", err.getMessage());
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}