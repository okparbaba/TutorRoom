package inc.osbay.android.tutorroom.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.model.Tutor;
import inc.osbay.android.tutorroom.sdk.util.FileDownloader;
import inc.osbay.android.tutorroom.ui.fragment.SingleBookingChooseTutorFragment;
import inc.osbay.android.tutorroom.ui.fragment.TutorInfoFragment;
import inc.osbay.android.tutorroom.utils.CommonUtil;

public class TutorListAdapter extends RecyclerView.Adapter<TutorListAdapter.ViewHolder> {

    private boolean isPlaying;
    private List<Tutor> mTutorList;
    private Context context;
    private MediaPlayer mMediaPlayer;
    private String mCurrentPlayingVoice;
    private String source;

    public TutorListAdapter(MediaPlayer mMediaPlayer, List<Tutor> tutorList, Context context, String source) {
        mTutorList = tutorList;
        this.context = context;
        this.source = source;
        this.mMediaPlayer = mMediaPlayer;
        this.mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tutor, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Tutor tutor = mTutorList.get(position);
        holder.mRatingBar.setRating(Float.parseFloat(tutor.getRate()));
        holder.tvTutorName.setText(tutor.getName());
        holder.tvTutorExp.setText(context.getString(R.string.years, tutor.getTeachingExp()));
        holder.tvCreditWeight.setText(/*String.format(Locale.getDefault(), "%.1f",*/ tutor.getCreditWeight());
        holder.tvTutorLocation.setText(tutor.getCountry());

        if (tutor.getAvatar() != null) {
            holder.sdvTutorPhoto.setImageURI(Uri.parse(tutor.getAvatar()));
        }

        holder.itemView.setOnClickListener(v -> {
            if (context != null)
                CommonUtil.hideKeyBoard(context, v);

            Fragment mainFragment = new TutorInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putString(TutorInfoFragment.EXTRA_TUTOR_ID, tutor.getTutorId());
            bundle.putString(TutorInfoFragment.EXTRA_SOURCE, source);
            mainFragment.setArguments(bundle);

            FragmentManager fm = ((Activity) context).getFragmentManager();
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
                }

                String fileName = tutor.getIntroVoice().substring(tutor.getIntroVoice().lastIndexOf('/') + 1, tutor.getIntroVoice().length());
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
                            notifyDataSetChanged();
                        });
                        try {
                            mMediaPlayer.setDataSource(file.getAbsolutePath());
                            mMediaPlayer.prepare();
                            mMediaPlayer.start();
                            mCurrentPlayingVoice = tutor.getTutorId();
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
                        }
                    }

                    notifyDataSetChanged();
                } else {

                    final ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage(context.getString(R.string.loading));
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    FileDownloader.downloadImage(tutor.getIntroVoice(), new FileDownloader.OnDownloadFinishedListener() {
                        @Override
                        public void onSuccess() {
                            progressDialog.dismiss();

                            if (context != null) {
                                mMediaPlayer = new MediaPlayer();
                                mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                                    mMediaPlayer.stop();
                                    mMediaPlayer.reset();
                                    mMediaPlayer = null;
                                    mCurrentPlayingVoice = null;
                                    holder.imvPlayVoice.setImageResource(R.mipmap.play_64);
                                    holder.playPB.setProgress(0);

                                    notifyDataSetChanged();
                                });

                                try {
                                    mMediaPlayer.setDataSource(file.getAbsolutePath());
                                    mMediaPlayer.prepare();
                                    mMediaPlayer.start();
                                    mCurrentPlayingVoice = tutor.getTutorId();
                                    notifyDataSetChanged();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onError() {
                            progressDialog.dismiss();

                            if (context != null) {
                                Toast.makeText(context, context.getString(R.string.tu_lst_cant_download_video), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            Timer mTimer = new Timer();
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