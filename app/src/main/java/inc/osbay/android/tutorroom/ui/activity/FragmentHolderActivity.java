package inc.osbay.android.tutorroom.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.ui.fragment.BackHandledFragment;
import inc.osbay.android.tutorroom.ui.fragment.ClassroomFAQFragment;
import inc.osbay.android.tutorroom.ui.fragment.ExistingLoginFragment;
import inc.osbay.android.tutorroom.ui.fragment.LeftMenuDrawerFragment;
import inc.osbay.android.tutorroom.ui.fragment.LessonFragment;
import inc.osbay.android.tutorroom.ui.fragment.OnlineSupportFragment;
import inc.osbay.android.tutorroom.ui.fragment.PackageFragment;
import inc.osbay.android.tutorroom.ui.fragment.ScheduleFragment;
import inc.osbay.android.tutorroom.ui.fragment.SignupFragment;
import inc.osbay.android.tutorroom.ui.fragment.StoreFragment;
import inc.osbay.android.tutorroom.ui.fragment.TutorListFragment;
import inc.osbay.android.tutorroom.ui.fragment.WebviewFragment;

public class FragmentHolderActivity extends AppCompatActivity implements BackHandledFragment.BackHandlerInterface {
    public static final String EXTRA_DISPLAY_FRAGMENT = "FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT";
    @BindView(R.id.framelayout)
    FrameLayout mFrameLayout;
    Fragment newFragment = null;
    private BackHandledFragment selectedFragment;
    private String fragmentName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_holder);
        ButterKnife.bind(this);

        fragmentName = getIntent().getStringExtra(EXTRA_DISPLAY_FRAGMENT);
        if (WelcomeActivity.class.getSimpleName().equals(fragmentName))
            newFragment = new SignupFragment();
        else if (LeftMenuDrawerFragment.class.getSimpleName().equals(fragmentName) ||
                SplashActivity.class.getSimpleName().equals(fragmentName))
            newFragment = new ExistingLoginFragment();
        else if (ClassroomFAQFragment.class.getSimpleName().equals(fragmentName)) {
            newFragment = new ClassroomFAQFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ClassroomFAQFragment.EXTRA_DISPLAY_FRAGMENT, getIntent().getStringExtra(ClassroomFAQFragment.EXTRA_DISPLAY_FRAGMENT));
            newFragment.setArguments(bundle);
        } else if (TutorListFragment.class.getSimpleName().equals(fragmentName))
            newFragment = new TutorListFragment();
        else if (ScheduleFragment.class.getSimpleName().equals(fragmentName))
            newFragment = new ScheduleFragment();
        else if (StoreFragment.class.getSimpleName().equals(fragmentName))
            newFragment = new StoreFragment();
        else if (PackageFragment.class.getSimpleName().equals(fragmentName))
            newFragment = new PackageFragment();
        else if (LessonFragment.class.getSimpleName().equals(fragmentName))
            newFragment = new LessonFragment();
        else if (OnlineSupportFragment.class.getSimpleName().equals(fragmentName))
            newFragment = new OnlineSupportFragment();
        else if (WebviewFragment.class.getSimpleName().equals(fragmentName)) {
            newFragment = new WebviewFragment();
            Bundle bundle = new Bundle();
            bundle.putString(WebviewFragment.WEBVIEW_EXTRA, getIntent().getStringExtra(WebviewFragment.WEBVIEW_EXTRA));
            bundle.putString(WebviewFragment.TITLE_EXTRA, getIntent().getStringExtra(WebviewFragment.TITLE_EXTRA));
            newFragment.setArguments(bundle);
        }


        if (newFragment != null) {
            FragmentManager fm = getFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.framelayout);
            if (fragment == null) {
                fm.beginTransaction()
                        .add(R.id.framelayout, newFragment).commit();
            } else {
                fm.beginTransaction()
                        .replace(R.id.framelayout, newFragment).commit();
            }
        } else {
            FragmentHolderActivity.this.finish();
        }
    }


    @Override
    public void setmSelectedFragment(BackHandledFragment backHandledFragment) {
        if (selectedFragment != null) {
        }
        selectedFragment = backHandledFragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (WebviewFragment.class.getSimpleName().equals(fragmentName)) {
            WebviewFragment.onMyKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }*/
}