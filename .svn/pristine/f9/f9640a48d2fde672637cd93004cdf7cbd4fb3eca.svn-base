package inc.osbay.android.tutorroom.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;

public class WelcomeActivity extends AppCompatActivity {
    @BindView(R.id.start)
    TextView startBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.start)
    void clickStart() {
        Intent intent = new Intent(this, FragmentHolderActivity.class);
        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, WelcomeActivity.class.getSimpleName());
        startActivity(intent);
        finish();

        ServerRequestManager requestManager = new ServerRequestManager(this);

       /* requestManager.getTutorList(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(Object result) {
                Log.i("Profile", "Received");
            }

            @Override
            public void onError(ServerError err) {
                Log.i("Update", "Failed");
            }
        });*/
        /*requestManager.getTutorDetailByID("5", new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(Object result) {
                Log.i("Profile", "Received");
            }

            @Override
            public void onError(ServerError err) {
                Log.i("Update", "Failed");
            }
        });*/

        /*requestManager.getLanguageList(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(Object result) {
                Log.i("Profile", "Received");
            }

            @Override
            public void onError(ServerError err) {
                Log.i("Update", "Failed");
            }
        });*/

        /*requestManager.getCountryList(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(Object result) {
                Log.i("Profile", "Received");
            }

            @Override
            public void onError(ServerError err) {
                Log.i("Update", "Failed");
            }
        });*/

        /*requestManager.getPackageListByAllTag(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(Object result) {
                Log.i("Profile", "Received");
            }

            @Override
            public void onError(ServerError err) {
                Log.i("Update", "Failed");
            }
        });*/

        /*requestManager.getLessonListByAllTag(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(Object result) {
                Log.i("Profile", "Received");
            }

            @Override
            public void onError(ServerError err) {
                Log.i("Update", "Failed");
            }
        });*/

        /*requestManager.getLessonListByPackageID(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(Object result) {
                Log.i("Profile", "Received");
            }

            @Override
            public void onError(ServerError err) {
                Log.i("Update", "Failed");
            }
        });*/
    }
}