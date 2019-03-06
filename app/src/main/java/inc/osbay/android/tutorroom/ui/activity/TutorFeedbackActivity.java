package inc.osbay.android.tutorroom.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

/**
 * A simple {@link Fragment} subclass.
 */
public class TutorFeedbackActivity extends Activity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    public static final String CLASSROOM_ID = "TutorFeedbackActivity.CLASSROOM_ID";

    String[] st;
    //private FeedbackAdapter mFeedbackAdapter;

    //private LinearLayout mLlPagerCount;
    private RelativeLayout mRlTutorSubmit;
    private RelativeLayout mRlTutorFeedback;
    //private TextView mTvFeedbackNotice;
    private TextView mTvRatingFeedback;
    private EditText mEtStudentMsg;
    private TextView mBtnFeedbackSubmit;
    private RatingBar mRbOtherFeedbackRating;
    private ImageView[] dots;
    private int dotsCount;
    //private float[] mRatingValues;
    //private String[] mCommentValues;
    private float mRatingValue;
    private String mCommentValue;
    private int mPosition = 0;
    private String mTutorFeedback = "";
    private String mStClassroomId;
    private RatingBar rbFeedbackRating;

    public TutorFeedbackActivity() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_feedback);

        st = getResources().getStringArray(R.array.tutor_feedback_type);

        mStClassroomId = getIntent().getStringExtra(CLASSROOM_ID);

        //mTvFeedbackNotice = findViewById(R.id.tv_feedback_notice);
        mTvRatingFeedback = findViewById(R.id.tv_rating_feedback);
        mEtStudentMsg = findViewById(R.id.et_student_msg);
        mRbOtherFeedbackRating = findViewById(R.id.rb_other_feedback_rating);
        RatingBar rbTutorRating = findViewById(R.id.rb_tutor_rating);
        mRlTutorSubmit = findViewById(R.id.rl_tutor_submit);
        mRlTutorFeedback = findViewById(R.id.rl_tutor_feedback);
        //mLlPagerCount = findViewById(R.id.ll_pager_counts);
        mBtnFeedbackSubmit = findViewById(R.id.btn_feedback_submit);
        LinearLayout vwFeedback = findViewById(R.id.vw_feedback);
        TextView tvTutorFeedback = findViewById(R.id.tv_tutor_feedback);
        rbFeedbackRating = findViewById(R.id.rb_feedback_rating);

        RelativeLayout rlMainTutorFeedback = findViewById(R.id.rl_main_tutor_feedback);
        rlMainTutorFeedback.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard();
            return false;
        });

        vwFeedback.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard();
            return false;
        });
        mBtnFeedbackSubmit.setOnClickListener(this);

        tvTutorFeedback.setText(getString(R.string.tu_tutor_feedback));

        /*mRatingValues = new float[st.length];
        mCommentValues = new String[st.length];*/
        rbTutorRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            mRlTutorFeedback.setVisibility(View.GONE);
            mRlTutorSubmit.setVisibility(View.VISIBLE);
            mRatingValue = rating;
            rbFeedbackRating.setRating(mRatingValue);

            if (rating >= 2.5f) {
                mTutorFeedback = getString(R.string.tu_star_3_5);
                mTvRatingFeedback.setText(getString(R.string.tu_star_3_5));
            } else {
                mTutorFeedback = getString(R.string.tu_star_1_2);
                mTvRatingFeedback.setText(getString(R.string.tu_star_1_2));
            }
        });

        /*mFeedbackAdapter = new FeedbackAdapter();
        vwFeedback.setAdapter(mFeedbackAdapter);
        vwFeedback.addOnPageChangeListener(this);*/
        mRbOtherFeedbackRating.setOnRatingBarChangeListener(this);
        //setUiPageViewController();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
    }

    /*private void setUiPageViewController() {
        dotsCount = mFeedbackAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            mLlPagerCount.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
    }*/

    /*@Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mPosition = position;
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));

        if (position == 0) {
            mEtStudentMsg.setText(mCommentValues[position]);
            mTvRatingFeedback.setText(mTutorFeedback);
            mRbOtherFeedbackRating.setVisibility(View.GONE);
            mTvFeedbackNotice.setVisibility(View.VISIBLE);
            mBtnFeedbackSubmit.setVisibility(View.VISIBLE);
        } else if (position == 1) {
            mEtStudentMsg.setText(mCommentValues[position]);
            mRbOtherFeedbackRating.setRating(mRatingValues[position]);
            mRbOtherFeedbackRating.setVisibility(View.VISIBLE);
            mTvFeedbackNotice.setVisibility(View.GONE);
            mTvRatingFeedback.setText(getString(R.string.tu_call_quality));
            mBtnFeedbackSubmit.setVisibility(View.GONE);
        } else if (position == 2) {
            mEtStudentMsg.setText(mCommentValues[position]);
            mRbOtherFeedbackRating.setRating(mRatingValues[position]);
            mRbOtherFeedbackRating.setVisibility(View.VISIBLE);
            mTvFeedbackNotice.setVisibility(View.GONE);
            mTvRatingFeedback.setText(getString(R.string.tu_feedback_content));
            mBtnFeedbackSubmit.setVisibility(View.GONE);
        } else {
            mEtStudentMsg.setText(mCommentValues[position]);
            mRbOtherFeedbackRating.setVisibility(View.VISIBLE);
            mRbOtherFeedbackRating.setVisibility(View.GONE);
            mTvFeedbackNotice.setVisibility(View.GONE);
            mTvRatingFeedback.setText(getString(R.string.tu_feedback_others));
            mBtnFeedbackSubmit.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mCommentValues[mPosition] = mEtStudentMsg.getText().toString();
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_feedback_submit:
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();
                mCommentValue = mEtStudentMsg.getText().toString();
                ServerRequestManager serverRequestManager = new ServerRequestManager(this);
                if (mStClassroomId != null) {
                    SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(TutorFeedbackActivity.this);
                    String accountID = String.valueOf(sharedPreferenceData.getInt("account_id"));
                    serverRequestManager.sendFeedback(accountID, mCommentValue, mStClassroomId, String.valueOf(mRatingValue), new ServerRequestManager.OnRequestFinishedListener() {
                        @Override
                        public void onSuccess(ServerResponse result) {
                            if (TutorFeedbackActivity.this != null) {
                                progressDialog.dismiss();

                                Intent mainActivity = new Intent(TutorFeedbackActivity.this, MainActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("class_type", "normal");
                                mainActivity.putExtras(bundle);
                                mainActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(mainActivity);

                                finish();
                            }
                        }

                        @Override
                        public void onError(ServerError err) {
                            if (TutorFeedbackActivity.this != null) {
                                progressDialog.dismiss();
                                Toast.makeText(TutorFeedbackActivity.this, err.getMessage(), Toast.LENGTH_SHORT).show();

                                Intent mainActivity = new Intent(TutorFeedbackActivity.this, MainActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("class_type", "normal");
                                mainActivity.putExtras(bundle);
                                mainActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(mainActivity);

                                finish();
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    finish();
                }
                break;
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        mRatingValue = rating;
        rbFeedbackRating.setRating(mRatingValue);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEtStudentMsg.getWindowToken(), 0);
    }

    /*private class FeedbackAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return st.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_vp_feedback, null);

            TextView tvFeedbackTitle = v.findViewById(R.id.tv_feedback_title);
            tvFeedbackTitle.setText(st[position]);
            SimpleDraweeView sdvFeedback = v.findViewById(R.id.sdv_feedback_tutor_photo);
            RatingBar rbFeedbackRating = v.findViewById(R.id.rb_feedback_rating);
            ImageView imvOtherFeedback = v.findViewById(R.id.imv_other_feedback);
            if (position == 0) {
                sdvFeedback.setVisibility(View.VISIBLE);
                imvOtherFeedback.setVisibility(View.GONE);
                rbFeedbackRating.setVisibility(View.VISIBLE);
                rbFeedbackRating.setRating(mRatingValues[0]);
            } else if (position == 1) {
                sdvFeedback.setVisibility(View.GONE);
                imvOtherFeedback.setVisibility(View.VISIBLE);
                imvOtherFeedback.setImageResource(R.mipmap.ic_call_quality);
                rbFeedbackRating.setVisibility(View.GONE);
            } else if (position == 2) {
                imvOtherFeedback.setVisibility(View.VISIBLE);
                sdvFeedback.setVisibility(View.GONE);
                imvOtherFeedback.setImageResource(R.mipmap.ic_content);
                rbFeedbackRating.setVisibility(View.GONE);
            } else {
                imvOtherFeedback.setVisibility(View.VISIBLE);
                sdvFeedback.setVisibility(View.GONE);
                imvOtherFeedback.setImageResource(R.mipmap.ic_others);
                rbFeedbackRating.setVisibility(View.GONE);
            }

            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }*/
}