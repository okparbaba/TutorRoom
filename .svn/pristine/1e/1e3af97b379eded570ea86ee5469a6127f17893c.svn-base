package inc.osbay.android.tutorroom.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.sdk.database.DBAdapter;
import inc.osbay.android.tutorroom.sdk.model.Account;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

public class SplashActivity extends AppCompatActivity {
    SharedPreferenceData sharedPreferenceData;
    int SPLASH_TIME_OUT = 2000;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.error)
    TextView errorTV;
    private boolean login;
    private Account mAccount;
    private ServerRequestManager requestManager;
    private int PERMISSION_REQUEST_STORAGE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!checkPermissionForSDCard()) {
                requestPermissionForSDCard();
            } else {
                getCompanyConfig();
            }
        } else {
            getCompanyConfig();
        }
    }

    private boolean checkPermissionForSDCard() {
        int resultReadSD = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int resultWriteSD = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return (resultReadSD == PackageManager.PERMISSION_GRANTED) && (resultWriteSD == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissionForSDCard() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Toast.makeText(this, getString(R.string.storage_perm), Toast.LENGTH_LONG).show();
            errorTV.setVisibility(View.VISIBLE);
            errorTV.setText(getString(R.string.storage_perm));
            mProgressBar.setVisibility(View.GONE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start to show other layouts.
                getCompanyConfig();
            } else {
                // Permission request was denied.
                errorTV.setVisibility(View.VISIBLE);
                errorTV.setText(getString(R.string.storage_perm));
                mProgressBar.setVisibility(View.GONE);

            }
        }
    }

    public void getCompanyConfig() {
        mProgressBar.setVisibility(View.VISIBLE);
        sharedPreferenceData = new SharedPreferenceData(SplashActivity.this);
        requestManager = new ServerRequestManager(SplashActivity.this);
        new Handler().postDelayed(() -> {
            login = sharedPreferenceData.getBoolean("login");
            requestManager.getCompanyConfiguration(new ServerRequestManager.OnRequestFinishedListener() {
                @Override
                public void onSuccess(ServerResponse result) {
                    getThirdPartConfig();
                }

                @Override
                public void onError(ServerError err) {
                    mProgressBar.setVisibility(View.GONE);
                    errorTV.setVisibility(View.VISIBLE);
                    errorTV.setText(getString(R.string.check_internet));
                    Log.i("Get Config Failed", err.getMessage());
                }
            });
        }, SPLASH_TIME_OUT);
    }

    public void getThirdPartConfig() {
        requestManager.getThirdPartConfig(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse result) {
                if (login) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    DBAdapter DBAdapter = new DBAdapter(SplashActivity.this);
                    SharedPreferenceData mPreferences = new SharedPreferenceData(SplashActivity.this);
                    String accountId = String.valueOf(mPreferences.getInt("account_id"));
                    mAccount = DBAdapter.getAccountById(accountId);

                    if (mAccount != null) {
                        Intent intent = new Intent(SplashActivity.this, FragmentHolderActivity.class);
                        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, SplashActivity.class.getSimpleName());
                        startActivity(intent);
                    } else {
                        Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
                        startActivity(i);
                    }
                    finish();
                }
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(ServerError err) {
                mProgressBar.setVisibility(View.GONE);
                errorTV.setVisibility(View.VISIBLE);
                errorTV.setText(getString(R.string.check_internet));
            }
        });
    }
}