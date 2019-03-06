package inc.osbay.android.tutorroom.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.database.DBAdapter;
import inc.osbay.android.tutorroom.sdk.database.TutorAdapter;
import inc.osbay.android.tutorroom.sdk.model.Account;
import inc.osbay.android.tutorroom.sdk.model.Tutor;
import inc.osbay.android.tutorroom.sdk.util.FileDownloader;

public class TutorInfoFragment extends BackHandledFragment {
    public static final String EXTRA_TUTOR_ID = "TutorInfoFragment.EXTRA_TUTOR_ID";
    public static final String EXTRA_TUTOR_TIME = "TutorInfoFragment.EXTRA_TUTOR_TIME";
    public static final String EXTRA_BOOKING_TYPE = "TutorInfoFragment.EXTRA_BOOKING_TYPE";
    public static final String EXTRA_SOURCE = "TutorInfoFragment.EXTRA_SOURCE";
    public static final String EXTRA_LESSONID = "TutorInfoFragment.EXTRA_LESSONID";
    public static final String EXTRA_START_DATE = "TutorInfoFragment.EXTRA_START_DATE";
    public static final String EXTRA_END_DATE = "TutorInfoFragment.EXTRA_END_DATE";
    @BindView(R.id.pb_intro_voice)
    ProgressBar mMusicProgressBar;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.tv_course_title)
    TextView tvTutorName;
    @BindView(R.id.rb_tutor_rate)
    RatingBar rbRate;
    @BindView(R.id.self_intro_tv)
    TextView tvIntroText;
    @BindView(R.id.rate_tv)
    TextView tvCreditWeight;
    @BindView(R.id.speciality_label)
    TextView speciality;
    @BindView(R.id.teaching_exp_label)
    TextView tvExp;
    @BindView(R.id.location_label)
    TextView location;
    @BindView(R.id.sdv_tutor_photo)
    SimpleDraweeView sdvTutorPhoto;
    @BindView(R.id.imv_intro_voice)
    ImageView imvIntroVoice;
    @BindView(R.id.book_btn_tv)
    TextView bookTV;
    private MediaPlayer mMediaPlayer;
    private Timer mTimer;
    private boolean isPlaying;
    private String mTutorTime;
    private int mBookingType;
    private Account mAccount;
    private Tutor mTutor;
    private String tutorID;
    private String source;
    private String lessonID;
    private String startDate;
    private String endDate;
    private String lessonType;


    @Override
    public boolean onBackPressed() {
        getFragmentManager().popBackStack();
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TutorAdapter adapter = new TutorAdapter(getActivity());
        Bundle bundle = getArguments();
        if (bundle != null) {
            source = bundle.getString(EXTRA_SOURCE);
            if (source.equals(SingleBookingChooseTutorFragment.class.getSimpleName())) {
                lessonType = getArguments().getString("lesson_type");
                lessonID = getArguments().getString(EXTRA_LESSONID);
                startDate = getArguments().getString(EXTRA_START_DATE);
                endDate = getArguments().getString(EXTRA_END_DATE);
            }
            tutorID = bundle.getString(EXTRA_TUTOR_ID);
            mTutor = adapter.getTutorById(tutorID);
            mTutorTime = bundle.getString(EXTRA_TUTOR_TIME);
            mBookingType = bundle.getInt(EXTRA_BOOKING_TYPE);
        }

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int accountId = mPreferences.getInt("account_id", 0);
        if (accountId != 0) {
            DBAdapter DBAdapter = new DBAdapter(getActivity());
            mAccount = DBAdapter.getAccountById(String.valueOf(accountId));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tutor_info, container, false);
        ButterKnife.bind(this, rootView);

        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);

        tvTutorName.setText(mTutor.getName());
        rbRate.setRating(Float.parseFloat(mTutor.getRate()));
        tvIntroText.setText(mTutor.getIntroduction());
        tvCreditWeight.setText(mTutor.getCreditWeight());
        speciality.setText(getString(R.string.speciality, mTutor.getSpeciality()));
        tvExp.setText(getString(R.string.teach_experience, mTutor.getTeachingExp()));
        location.setText(getString(R.string.locationn, mTutor.getLocation()));
        if (mTutor.getAvatar() != null) {
            sdvTutorPhoto.setImageURI(Uri.parse(mTutor.getAvatar()));
        }
        if (source.equals(SingleBookingChooseTutorFragment.class.getSimpleName()))
            bookTV.setVisibility(View.VISIBLE);
        else bookTV.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (source.equals(SingleBookingChooseTutorFragment.class.getSimpleName()))
            setTitle(getString(R.string.single_book));
        else
            setTitle(getString(R.string.tutor));
        setDisplayHomeAsUpEnable(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMusicProgressBar.setMax(mMediaPlayer.getDuration() / 1000);
                    mMusicProgressBar.setProgress(mMediaPlayer.getCurrentPosition() / 1000);
                }
            }
        }, 1000, 1000);

        if (mMediaPlayer != null && isPlaying) {
            mMediaPlayer.start();
            isPlaying = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mTimer.cancel();

        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPlaying = true;
        }
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDestroy();
    }

    @OnClick(R.id.book_btn_tv)
    void bookTutor() {
        if (source.equals(SingleBookingChooseTutorFragment.class.getSimpleName())) {
            getFragmentManager().popBackStack();
            Fragment mainFragment = new SingleBookingChooseTutorFragment();
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_TUTOR_ID, tutorID);
            bundle.putString("source", TutorInfoFragment.class.getSimpleName());
            bundle.putString("lesson_id", lessonID);
            bundle.putString("start_date", startDate);
            bundle.putString("end_date", endDate);
            bundle.putString("lesson_type", lessonType);
            mainFragment.setArguments(bundle);

            FragmentManager fm = getFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.framelayout);
            if (fragment == null) {
                fm.beginTransaction()
                        .addToBackStack(null)
                        .add(R.id.framelayout, mainFragment).commit();
            } else {
                fm.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.framelayout, mainFragment).commit();
            }
        }
    }


    @OnClick(R.id.imv_intro_voice)
    void playIntroVoice() {
        if (!TextUtils.isEmpty(mTutor.getIntroVoice())) {
            String fileName = mTutor.getIntroVoice().substring(mTutor.getIntroVoice().lastIndexOf('/') + 1, mTutor.getIntroVoice().length());
            final File file = new File(CommonConstant.MEDIA_PATH, fileName);

            if (file.exists()) {
                if (mMediaPlayer == null) {
                    mMediaPlayer = new MediaPlayer();
                    mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                        mMediaPlayer.stop();
                        mMediaPlayer.reset();
                        mMediaPlayer = null;
                        imvIntroVoice.setImageResource(R.mipmap.play_64);
                        mMusicProgressBar.setProgress(0);
                    });
                    try {
                        mMediaPlayer.setDataSource(file.getAbsolutePath());
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                        imvIntroVoice.setImageResource(R.mipmap.pause_64);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                        imvIntroVoice.setImageResource(R.mipmap.play_64);
                    } else {
                        mMediaPlayer.start();
                        imvIntroVoice.setImageResource(R.mipmap.pause_64);
                    }
                }
            } else {

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();

                FileDownloader.downloadImage(mTutor.getIntroVoice(), new FileDownloader.OnDownloadFinishedListener() {
                    @Override
                    public void onSuccess() {
                        progressDialog.dismiss();

                        mMediaPlayer = new MediaPlayer();
                        mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                            mMediaPlayer.stop();
                            mMediaPlayer.reset();
                            mMediaPlayer = null;

                            imvIntroVoice.setImageResource(R.mipmap.play_64);
                            mMusicProgressBar.setProgress(0);
                        });
                        try {
                            mMediaPlayer.setDataSource(file.getAbsolutePath());
                            mMediaPlayer.prepare();
                            mMediaPlayer.start();

                            imvIntroVoice.setImageResource(R.mipmap.pause_64);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError() {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), getString(R.string.tu_lst_cant_download_video), Toast.LENGTH_SHORT).show();
                    }
                });
            }
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
