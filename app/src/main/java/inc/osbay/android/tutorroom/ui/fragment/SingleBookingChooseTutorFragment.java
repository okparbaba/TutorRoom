package inc.osbay.android.tutorroom.ui.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.database.TutorAdapter;
import inc.osbay.android.tutorroom.sdk.model.AvailableTutor;
import inc.osbay.android.tutorroom.sdk.model.Tutor;
import inc.osbay.android.tutorroom.sdk.util.FileDownloader;
import inc.osbay.android.tutorroom.sdk.util.LGCUtil;
import inc.osbay.android.tutorroom.ui.activity.FragmentHolderActivity;
import inc.osbay.android.tutorroom.ui.activity.MainActivity;
import inc.osbay.android.tutorroom.utils.CommonUtil;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

public class SingleBookingChooseTutorFragment extends BackHandledFragment {

    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.tutor_rv)
    RecyclerView tutorRV;
    @BindView(R.id.no_data)
    TextView noDataTV;
    private ServerRequestManager mServerRequestManager;
    private AvailableTutorListAdapter mTutorListAdapter;
    private List<AvailableTutor> availableTutorList = new ArrayList<>();
    private String lessonID;
    private String startDate;
    private String endDate;
    private String accountID;
    private String source;
    private String tutorID = "0";
    private String lessonType;
    private Timer mTimer;
    private List<Tutor> mTutorList;
    private List<Tutor> avaiTutorList = new ArrayList<>();
    private boolean isPlaying;
    private MediaPlayer mMediaPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        source = getArguments().getString("source");
        lessonID = getArguments().getString("lesson_id");
        startDate = getArguments().getString("start_date");
        endDate = getArguments().getString("end_date");
        lessonType = getArguments().getString("lesson_type");
        if (source.equals(TutorInfoFragment.class.getSimpleName())) {
            tutorID = getArguments().getString(TutorInfoFragment.EXTRA_TUTOR_ID);
        }
        SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(Objects.requireNonNull(getActivity()));
        accountID = String.valueOf(sharedPreferenceData.getInt("account_id"));
        mServerRequestManager = new ServerRequestManager(getActivity().getApplicationContext());
        mTimer = new Timer();
        TutorAdapter tutorDbAdapter = new TutorAdapter(getActivity());
        mTutorList = tutorDbAdapter.getAllTutor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_booking_choose_tutor, container, false);
        ButterKnife.bind(this, view);

        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        try {
            mServerRequestManager.getAvailableTutorByTime(LGCUtil.convertToUTC(startDate), new ServerRequestManager.OnRequestFinishedListener() {
                @Override
                public void onSuccess(ServerResponse result) {
                    progressDialog.dismiss();
                    if (result.getCode() == 1) //For Success Situation
                    {
                        if (getActivity() != null) {
                            availableTutorList.clear();
                            avaiTutorList.clear();
                            try {
                                JSONObject dataObject = new JSONObject(result.getDataSt());
                                JSONArray fullTimeTutorJsonArray = dataObject.getJSONArray("tutor_fulltime");
                                JSONArray partTimeTutorJsonArray = dataObject.getJSONArray("tutor_parttime");
                                for (int i = 0; i < fullTimeTutorJsonArray.length(); i++) {
                                    AvailableTutor tutor = new AvailableTutor(fullTimeTutorJsonArray.getJSONObject(i));
                                    availableTutorList.add(tutor);
                                }
                                for (int j = 0; j < partTimeTutorJsonArray.length(); j++) {
                                    AvailableTutor tutor = new AvailableTutor(partTimeTutorJsonArray.getJSONObject(j));
                                    availableTutorList.add(tutor);
                                }

                            } catch (JSONException je) {
                                Log.e(CommonConstant.TAG, "Cannot parse Tutor Object", je);
                            }
                        }

                        for (int i = 0; i < mTutorList.size(); i++) {
                            for (int j = 0; j < availableTutorList.size(); j++) {
                                if (availableTutorList.get(j).getTutorId().equalsIgnoreCase(mTutorList.get(i).getTutorId())) {
                                    avaiTutorList.add(mTutorList.get(i));
                                }
                            }
                        }
                        /*mTutorListAdapter = new AvailableTutorListAdapter(lessonID, startDate, endDate, tutorID, lessonType,
                                availableTutorList, getActivity(), SingleBookingChooseTutorFragment.class.getSimpleName());*/
                        mTutorListAdapter = new AvailableTutorListAdapter();
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        tutorRV.setLayoutManager(mLayoutManager);
                        tutorRV.setItemAnimator(new DefaultItemAnimator());
                        tutorRV.setAdapter(mTutorListAdapter);
                        mTutorListAdapter.notifyDataSetChanged();
                        tutorRV.setVisibility(View.VISIBLE);
                        noDataTV.setVisibility(View.GONE);
                    } else //For No Data Situation
                    {
                        noDataTV.setVisibility(View.VISIBLE);
                        tutorRV.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onError(ServerError err) {
                    progressDialog.dismiss();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), err.getMessage(), Toast.LENGTH_SHORT)
                                .show();

                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onBackPressed() {
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.framelayout);
        Fragment newFragment = new SingleBookingChooseDateFragment();

        Bundle bundle = new Bundle();
        bundle.putString("lesson_id", lessonID);
        bundle.putString("lesson_type", lessonType);
        newFragment.setArguments(bundle);
        if (fragment == null) {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.framelayout, newFragment).commit();
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.framelayout, newFragment).commit();
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        setTitle(getString(R.string.single_book));
        setDisplayHomeAsUpEnable(true);
    }

    @Override
    public void onPause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPlaying = true;
        }

        super.onPause();
    }

    @OnClick(R.id.confirm_tv)
    void confirmBooking() {
        if (Integer.parseInt(tutorID) == 0) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.select_tutor), Toast.LENGTH_LONG)
                    .show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();

            ServerRequestManager serverRequestManager = new ServerRequestManager(getActivity());
            serverRequestManager.bookSingleClass(accountID, lessonID, tutorID, startDate, endDate,
                    String.valueOf(CommonConstant.lessonBookingType), lessonType, new ServerRequestManager.OnRequestFinishedListener() {
                        @Override
                        public void onSuccess(ServerResponse result) {
                            progressDialog.dismiss();
                            if (result.getCode() == ServerResponse.Status.SUCCESS) {
                                showCustomDialog();
                            } else if (result.getCode() == ServerResponse.Status.INSUFFICIENT_CREDIT) {
                                Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(ServerError err) {
                            progressDialog.dismiss();
                            Log.i("Booking Failed", err.getMessage());
                        }
                    });
        }
    }

    private void showCustomDialog() {
        Dialog successDialog = new Dialog(getActivity());
        successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successDialog.setContentView(R.layout.single_booking_success_layout);
        successDialog.setCancelable(false);

        TextView viewSchedule = successDialog.findViewById(R.id.tv_my_schedule);
        TextView bookLesson = successDialog.findViewById(R.id.tv_book_lesson);
        TextView backDashboard = successDialog.findViewById(R.id.tv_back_to_main_menu);
        viewSchedule.setOnClickListener(view -> {
            successDialog.dismiss();
            getActivity().finish();
            Intent intent = new Intent(getActivity(), FragmentHolderActivity.class);
            intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, ScheduleFragment.class.getSimpleName());
            startActivity(intent);
        });

        bookLesson.setOnClickListener(view -> {
            successDialog.dismiss();
            getFragmentManager().beginTransaction()
                    .remove(SingleBookingChooseTutorFragment.this)
                    .commit();
            getFragmentManager().popBackStack(getFragmentManager().getBackStackEntryAt(0).getId(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });

        backDashboard.setOnClickListener(view -> {
            successDialog.dismiss();
            getActivity().finish();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });
        successDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public class AvailableTutorListAdapter extends RecyclerView.Adapter<AvailableTutorListAdapter.ViewHolder> {

        private String mCurrentPlayingVoice;

        AvailableTutorListAdapter() {
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tutor, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            final Tutor tutor = avaiTutorList.get(position);
            if (tutor.getTutorId().equals(tutorID))
                holder.checkImg.setVisibility(View.VISIBLE);
            holder.mRatingBar.setRating(Float.parseFloat(tutor.getRate()));
            holder.tvTutorName.setText(tutor.getName());
            holder.tvTutorExp.setText(getActivity().getString(R.string.years, tutor.getTeachingExp()));
            holder.tvCreditWeight.setText(tutor.getCreditWeight());
            holder.tvTutorLocation.setText(tutor.getCountry());
            if (tutor.getAvatar() != null) {
                holder.sdvTutorPhoto.setImageURI(Uri.parse(tutor.getAvatar()));
            }

            holder.itemView.setOnClickListener(v -> {
                if (getActivity() != null)
                    CommonUtil.hideKeyBoard(getActivity(), v);

                Fragment mainFragment = new TutorInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putString(TutorInfoFragment.EXTRA_TUTOR_ID, tutor.getTutorId());
                bundle.putString(TutorInfoFragment.EXTRA_SOURCE, SingleBookingChooseTutorFragment.class.getSimpleName());
                bundle.putString(TutorInfoFragment.EXTRA_LESSONID, lessonID);
                bundle.putString(TutorInfoFragment.EXTRA_START_DATE, startDate);
                bundle.putString(TutorInfoFragment.EXTRA_END_DATE, endDate);
                bundle.putString("lesson_type", lessonType);
                mainFragment.setArguments(bundle);

                FragmentManager fm = getActivity().getFragmentManager();
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
            });

            if (mMediaPlayer != null && mMediaPlayer.isPlaying() && tutor.getTutorId().equals(mCurrentPlayingVoice)) {
                holder.imvPlayVoice.setImageResource(R.mipmap.pause_64);
            } else {
                holder.imvPlayVoice.setImageResource(R.mipmap.play_64);
            }
            holder.imvPlayVoice.setOnClickListener(view -> {
                if (!TextUtils.isEmpty(tutor.getIntroVoice())) {
                    if (mMediaPlayer != null &&
                            !tutor.getTutorId().equals(mCurrentPlayingVoice)) {
                        if (mMediaPlayer.isPlaying())
                            mMediaPlayer.stop();
                        mMediaPlayer.reset();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                        mCurrentPlayingVoice = null;
                        mTimer.cancel();
                        mTimer = null;
                        holder.playPB.setProgress(0);
                    }

                    String fileName = tutor.getIntroVoice().substring(tutor.getIntroVoice().lastIndexOf('/') + 1);
                    final File file = new File(CommonConstant.MEDIA_PATH, fileName);

                    if (file.exists()) {
                        if (mMediaPlayer == null) {
                            mMediaPlayer = new MediaPlayer();
                            mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                                mMediaPlayer.stop();
                                mMediaPlayer.reset();
                                mMediaPlayer = null;
                                mCurrentPlayingVoice = null;
                                holder.imvPlayVoice.setImageResource(R.mipmap.play_64);
                                holder.playPB.setProgress(0);
                                mTimer.cancel();
                                mTimer = null;
                                holder.playPB.setProgress(0);
                                notifyDataSetChanged();
                            });
                            try {
                                mMediaPlayer.setDataSource(file.getAbsolutePath());
                                mMediaPlayer.prepare();
                                mMediaPlayer.start();
                                mCurrentPlayingVoice = tutor.getTutorId();
                                mTimer = new Timer();
                                mTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                                            holder.playPB.setMax(mMediaPlayer.getDuration() / 1000);
                                            holder.playPB.setProgress(mMediaPlayer.getCurrentPosition() / 1000);
                                        }
                                    }
                                }, 1000, 1000);

                                if (mMediaPlayer != null && isPlaying) {
                                    mMediaPlayer.start();
                                    isPlaying = false;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (mMediaPlayer.isPlaying()) {
                                mMediaPlayer.pause();
                                holder.imvPlayVoice.setImageResource(R.mipmap.play_64);
                            } else {
                                mMediaPlayer.start();
                                mCurrentPlayingVoice = tutor.getTutorId();
                                mTimer = new Timer();
                                mTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                                            holder.playPB.setMax(mMediaPlayer.getDuration() / 1000);
                                            holder.playPB.setProgress(mMediaPlayer.getCurrentPosition() / 1000);
                                        }
                                    }
                                }, 1000, 1000);

                                if (mMediaPlayer != null && isPlaying) {
                                    mMediaPlayer.start();
                                    isPlaying = false;
                                }
                            }
                        }

                        notifyDataSetChanged();
                    } else {
                        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage(getActivity().getString(R.string.loading));
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        FileDownloader.downloadImage(tutor.getIntroVoice(), new FileDownloader.OnDownloadFinishedListener() {
                            @Override
                            public void onSuccess() {
                                progressDialog.dismiss();

                                if (getActivity() != null) {
                                    mMediaPlayer = new MediaPlayer();
                                    mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                                        mMediaPlayer.stop();
                                        mMediaPlayer.reset();
                                        mMediaPlayer = null;
                                        mCurrentPlayingVoice = null;
                                        holder.imvPlayVoice.setImageResource(R.mipmap.play_64);
                                        holder.playPB.setProgress(0);
                                        mTimer.cancel();
                                        mTimer = null;
                                        holder.playPB.setProgress(0);
                                        notifyDataSetChanged();
                                    });

                                    try {
                                        mMediaPlayer.setDataSource(file.getAbsolutePath());
                                        mMediaPlayer.prepare();
                                        mMediaPlayer.start();
                                        mCurrentPlayingVoice = tutor.getTutorId();
                                        mTimer = new Timer();
                                        mTimer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                                                    holder.playPB.setMax(mMediaPlayer.getDuration() / 1000);
                                                    holder.playPB.setProgress(mMediaPlayer.getCurrentPosition() / 1000);
                                                }
                                            }
                                        }, 1000, 1000);

                                        if (mMediaPlayer != null && isPlaying) {
                                            mMediaPlayer.start();
                                            isPlaying = false;
                                        }
                                        notifyDataSetChanged();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onError() {
                                progressDialog.dismiss();

                                if (getActivity() != null) {
                                    Toast.makeText(getActivity(), getActivity().getString(R.string.tu_lst_cant_download_video), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return avaiTutorList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTutorName;
            TextView tvTutorExp;
            TextView tvCreditWeight;
            TextView tvTutorLocation;
            SimpleDraweeView sdvTutorPhoto;
            RatingBar mRatingBar;
            View vwSpeaker;
            ImageView imvPlayVoice;
            ImageView checkImg;
            ProgressBar playPB;

            ViewHolder(View itemView) {
                super(itemView);
                mRatingBar = itemView.findViewById(R.id.rb_tutor_rate);
                tvTutorName = itemView.findViewById(R.id.tv_course_title);
                tvTutorExp = itemView.findViewById(R.id.tv_tutor_exp);
                tvCreditWeight = itemView.findViewById(R.id.tv_credit_weight);
                tvTutorLocation = itemView.findViewById(R.id.tv_tutor_location);
                vwSpeaker = itemView.findViewById(R.id.vw_speaker);
                sdvTutorPhoto = itemView.findViewById(R.id.sdv_tutor_photo);
                imvPlayVoice = itemView.findViewById(R.id.imv_intro_voice);
                checkImg = itemView.findViewById(R.id.check_img);
                playPB = itemView.findViewById(R.id.pb_intro_voice);
            }
        }
    }
}
