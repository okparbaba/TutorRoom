package inc.osbay.android.tutorroom.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.database.TutorAdapter;
import inc.osbay.android.tutorroom.sdk.model.Tutor;
import inc.osbay.android.tutorroom.sdk.util.FileDownloader;
import inc.osbay.android.tutorroom.utils.CommonUtil;

public class TutorListFragment extends BackHandledFragment {

    @BindView(R.id.rv_tutor_list)
    RecyclerView tutorListRV;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.no_data)
    TextView noDataTV;
    SharedPreferences sharedPreferences;
    boolean isPlaying;
    private MediaPlayer mMediaPlayer;
    private ServerRequestManager mServerRequestManager;
    private TutorAdapter mTutorDbAdapter;
    private TutorListAdapter mTutorListAdapter;
    private List<Tutor> mTutorList;
    private RelativeLayout mSearchBarRelativeLayout;
    private EditText mSearchEditText;
    private String mCurrentPlayingVoice;
    private DrawerLayout mDrawerLayout;
    private Timer mTimer;

    @Override
    public boolean onBackPressed() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        getActivity().finish();
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mServerRequestManager = new ServerRequestManager(getActivity().getApplicationContext());
        mTutorDbAdapter = new TutorAdapter(getActivity());
        mTutorList = mTutorDbAdapter.getAllTutor();
        mMediaPlayer = new MediaPlayer();
        /*mTutorListAdapter = new TutorListAdapter(mMediaPlayer, mTutorList, getActivity(),
                TutorListFragment.class.getSimpleName());*/
        mTutorListAdapter = new TutorListAdapter();
        mTimer = new Timer();
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        /*setTitle(getString(R.string.tu_lst_title));
        setDisplayHomeAsUpEnable(true);*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutor_list, container, false);
        ButterKnife.bind(this, view);

        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);

        ImageView searchCancelImageView = view.findViewById(R.id.imv_search_cancel);
        searchCancelImageView.setOnClickListener(view1 -> {
            mSearchBarRelativeLayout.setVisibility(View.INVISIBLE);
            mSearchEditText.setEnabled(false);

            if (getActivity() != null)
                CommonUtil.hideKeyBoard(getActivity(), view1);
        });

        mSearchEditText = view.findViewById(R.id.edt_search_text);
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String tutorName = mSearchEditText.getText().toString();
                if (!TextUtils.isEmpty(tutorName)) {
                    mTutorList = mTutorDbAdapter.searchTutorByName(tutorName);
                } else {
                    mTutorList = mTutorDbAdapter.getAllTutor();
                }
                mTutorListAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        tutorListRV.setLayoutManager(mLayoutManager);
        tutorListRV.setItemAnimator(new DefaultItemAnimator());
        tutorListRV.setAdapter(mTutorListAdapter);
        noDataTV.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onPause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPlaying = true;
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        setDisplayHomeAsUpEnable(true);
        setTitle(getString(R.string.tu_lst_title));

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));

        if (mTutorList.size() == 0) {
            progressDialog.show();
        }

        mServerRequestManager.getTutorList(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse result) {
                progressDialog.dismiss();
                if (result.getCode() == 1) //For Success Situation
                {
                    tutorListRV.setVisibility(View.VISIBLE);
                    noDataTV.setVisibility(View.GONE);
                    if (getActivity() != null) {
                        mTutorList.clear();

                        try {
                            JSONArray jsonArray = new JSONArray(result.getDataSt());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Tutor tutor = new Tutor(jsonArray.getJSONObject(i));
                                mTutorList.add(tutor);
                            }
                        } catch (JSONException je) {
                            Log.e(CommonConstant.TAG, "Cannot parse Tutor Object", je);
                        }
                    }
                    mTutorListAdapter.notifyDataSetChanged();
                } else //For No Data Situation
                {
                    noDataTV.setVisibility(View.VISIBLE);
                    noDataTV.setText(getString(R.string.no_tutor));
                    tutorListRV.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(ServerError err) {
                progressDialog.dismiss();

                if (getActivity() != null) {
                    Toast.makeText(getActivity(), getString(R.string.tu_lst_refresh_failed), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tutorlist_menu, menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            /*case R.id.opt_search:
                mSearchBarRelativeLayout.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mSearchEditText.setEnabled(true);
                mSearchEditText.requestFocus();
                imm.toggleSoftInputFromWindow(mSearchEditText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

                //hideActionBar();
                return true;*/
        }
        return true;
    }

    public class TutorListAdapter extends RecyclerView.Adapter<TutorListAdapter.ViewHolder> {

        private boolean isPlaying;
        private String mCurrentPlayingVoice;

        TutorListAdapter() {
        }

        @NonNull
        @Override
        public TutorListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tutor, parent, false);
            return new TutorListAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final TutorListAdapter.ViewHolder holder, final int position) {
            final Tutor tutor = mTutorList.get(position);
            holder.mRatingBar.setRating(Float.parseFloat(tutor.getRate()));
            holder.tvTutorName.setText(tutor.getName());
            holder.tvTutorExp.setText(getActivity().getString(R.string.years, tutor.getTeachingExp()));
            holder.tvCreditWeight.setText(/*String.format(Locale.getDefault(), "%.1f",*/ tutor.getCreditWeight());
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
                bundle.putString(TutorInfoFragment.EXTRA_SOURCE, TutorListFragment.class.getSimpleName());
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
            return mTutorList.size();
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