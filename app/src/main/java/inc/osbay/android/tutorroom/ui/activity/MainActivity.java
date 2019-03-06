package inc.osbay.android.tutorroom.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.TRApplication;
import inc.osbay.android.tutorroom.service.MessengerService;
import inc.osbay.android.tutorroom.ui.fragment.BackHandledFragment;
import inc.osbay.android.tutorroom.ui.fragment.MainFragment;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;
import inc.osbay.android.tutorroom.utils.WSMessageClient;

public class MainActivity extends AppCompatActivity implements BackHandledFragment.BackHandlerInterface {

    private BackHandledFragment selectedFragment;
    private WSMessageClient mWSMessageClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_holder);
        ButterKnife.bind(this);

        SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(this);
        mWSMessageClient = ((TRApplication) getApplication()).getWSMessageClient();
        Message message = Message.obtain(null, MessengerService.MSG_WS_LOGIN);
        Bundle bundle = new Bundle();
        bundle.putString("user_id", "S_" + sharedPreferenceData.getInt("account_id"));
        message.setData(bundle);
        try {
            mWSMessageClient.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.framelayout);
        MainFragment mainFragment = new MainFragment();
        if (fragment == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, mainFragment)
                    .commit();
        } else {
            fm.beginTransaction()
                    .replace(R.id.framelayout, mainFragment)
                    .commit();
        }
    }

    @Override
    public void setmSelectedFragment(BackHandledFragment backHandledFragment) {
        if (selectedFragment != null) {
        }
        selectedFragment = backHandledFragment;
    }
}
