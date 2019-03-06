package inc.osbay.android.tutorroom.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

public class PasswordFragment extends BackHandledFragment {
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.old_pass)
    EditText oldPassET;
    @BindView(R.id.confirm_new_pass)
    EditText confirmNewPassET;
    @BindView(R.id.new_pass)
    EditText newPassET;
    @BindView(R.id.old_pass_error_tv)
    TextView oldPassErrorTV;
    @BindView(R.id.pass_error_tv)
    TextView passErrorTV;
    @BindView(R.id.confirm_pass_error_tv)
    TextView confirmPassErrorTV;
    SharedPreferenceData sharedPreferenceData;
    private ServerRequestManager mRequestManager;
    private String accountId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferenceData = new SharedPreferenceData(Objects.requireNonNull(getActivity()));
        mRequestManager = new ServerRequestManager(getActivity());
        accountId = String.valueOf(sharedPreferenceData.getInt("account_id"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        ButterKnife.bind(this, view);

        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setTitle(getString(R.string.change_pass));
        setDisplayHomeAsUpEnable(true);
        setHasOptionsMenu(true);
    }

    @OnClick(R.id.change_pass)
    void changePass() {
        String oldPass = oldPassET.getText().toString();
        String newPass = newPassET.getText().toString();
        String confirmNewPass = confirmNewPassET.getText().toString();

        if (oldPass.equals("")) {
            oldPassErrorTV.setVisibility(View.VISIBLE);
            oldPassErrorTV.setText(getString(R.string.old_pass_error));
        } else {
            if (oldPass.length() < 6) {
                oldPassErrorTV.setVisibility(View.VISIBLE);
                oldPassErrorTV.setText(getString(R.string.pass_min_length));
            }
            if (oldPass.length() >= 6 && oldPass.length() <= 10) {
                oldPassErrorTV.setVisibility(View.GONE);
            }
        }

        if (!newPass.equals(confirmNewPass)) {
            passErrorTV.setVisibility(View.VISIBLE);
            confirmPassErrorTV.setVisibility(View.VISIBLE);
            passErrorTV.setText(getString(R.string.pass_not_matched));
            confirmPassErrorTV.setText(getString(R.string.pass_not_matched));
        } else if (newPass.equals(confirmNewPass)) {
            passErrorTV.setVisibility(View.GONE);
            confirmPassErrorTV.setVisibility(View.GONE);
        }

        if (newPass.equals("")) {
            passErrorTV.setVisibility(View.VISIBLE);
            passErrorTV.setText(getString(R.string.new_pass_error));
        } else {
            if (newPass.length() < 6) {
                passErrorTV.setVisibility(View.VISIBLE);
                passErrorTV.setText(getString(R.string.pass_min_length));
            }
            if (newPass.length() >= 6 && newPass.length() <= 10 && newPass.equals(confirmNewPass)) {
                passErrorTV.setVisibility(View.GONE);
            }
        }
        if (confirmNewPass.equals("")) {
            confirmPassErrorTV.setVisibility(View.VISIBLE);
            confirmPassErrorTV.setText(getString(R.string.conf_new_pass_error));
        } else {
            if (confirmNewPass.length() < 6) {
                confirmPassErrorTV.setVisibility(View.VISIBLE);
                confirmPassErrorTV.setText(getString(R.string.pass_min_length));
            }
            if (confirmNewPass.length() >= 6 && confirmNewPass.length() <= 10 && newPass.equals(confirmNewPass)) {
                confirmPassErrorTV.setVisibility(View.GONE);
            }
        }

        if (!oldPass.equals("") && oldPass.length() >= 6 && oldPass.length() <= 10 &&
                newPass.equals(confirmNewPass) && !newPass.equals("") && newPass.length() >= 6 &&
                newPass.length() <= 10 && !confirmNewPass.equals("") &&
                confirmNewPass.length() >= 6 && confirmNewPass.length() <= 10) {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();

            mRequestManager.changePassword(accountId, oldPass, newPass,
                    new ServerRequestManager.OnRequestFinishedListener() {
                        @Override
                        public void onSuccess(ServerResponse result) {
                            progressDialog.dismiss();
                            if (result.getCode() == 1) {
                                Toast.makeText(getActivity(),
                                        getResources().getString(R.string.pwd_changed), Toast.LENGTH_LONG).show();
                                getFragmentManager().popBackStack();
                            } else {
                                oldPassErrorTV.setVisibility(View.VISIBLE);
                                oldPassErrorTV.setText(result.getMessage());
                            }
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
        getFragmentManager().popBackStack();
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}