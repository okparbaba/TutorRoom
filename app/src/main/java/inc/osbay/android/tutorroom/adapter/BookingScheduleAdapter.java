package inc.osbay.android.tutorroom.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;

import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.database.DBAdapter;
import inc.osbay.android.tutorroom.sdk.database.TutorAdapter;
import inc.osbay.android.tutorroom.sdk.model.Booking;
import inc.osbay.android.tutorroom.sdk.model.Lesson;
import inc.osbay.android.tutorroom.sdk.model.Tutor;
import inc.osbay.android.tutorroom.sdk.util.LGCUtil;
import inc.osbay.android.tutorroom.ui.activity.ClassRoomActivity;


public class BookingScheduleAdapter extends RecyclerView.Adapter<BookingScheduleAdapter.MyViewHolder> {
    private List<Booking> mSelectedBookingList;
    private Context context;
    private OnItemClicked onClick;
    private TutorAdapter tutorAdapter;
    private DBAdapter mDBAdapter;
    private Booking mSelectedBooking;
    private ServerRequestManager mServerRequestManager;

    public BookingScheduleAdapter(List<Booking> mSelectedBookingList, ServerRequestManager mServerRequestManager,
                                  Context context, OnItemClicked listner) {
        this.mSelectedBookingList = mSelectedBookingList;
        this.context = context;
        this.onClick = listner;
        tutorAdapter = new TutorAdapter(context);
        mDBAdapter = new DBAdapter(context);
        this.mServerRequestManager = mServerRequestManager;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.booked_class_item,
                parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Booking booking = mSelectedBookingList.get(i);
        if (booking.getBookingType() == CommonConstant.singleLessonType)
            holder.horiView.setBackground(context.getResources().getDrawable(R.drawable.single_class_view_bg));
        else if (booking.getBookingType() == CommonConstant.Trial)
            holder.horiView.setBackground(context.getResources().getDrawable(R.drawable.trial_view_bg));
        try {
            holder.stateDateTV.setText(LGCUtil.changeDateFormat(booking.getStartDate(), LGCUtil.FORMAT_NORMAL, LGCUtil.FORMAT_TIME_NO_SEC));
            holder.endDateTV.setText(LGCUtil.changeDateFormat(booking.getEndDate(), LGCUtil.FORMAT_NORMAL, LGCUtil.FORMAT_TIME_NO_SEC));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Tutor mTutor = tutorAdapter.getTutorById(booking.getTutorId());
        holder.tutorNameTV.setText(mTutor.getName());
        holder.tutorImg.setImageURI(Uri.parse(mTutor.getAvatar()));

        int lessonCount = mDBAdapter.getLessonCountByID(booking.getLessonId());
        if (lessonCount == 0) {
            holder.lessonTV.setText(booking.getLessonId());
        } else {
            Lesson lesson = mDBAdapter.getLessonByID(booking.getLessonId());
            holder.lessonTV.setText(lesson.getLessonName());
            holder.lessonImg.setImageURI(Uri.parse(lesson.getLessonCover()));
        }

        holder.startTV.setOnClickListener(view -> {
                    mSelectedBooking = booking;
                    System.gc();
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.sd_network_notice))
                            .setMessage(context.getString(R.string.sd_network_msg))
                            .setPositiveButton(context.getString(R.string.sd_network_continue), (dialogInterface, i1) -> {
                                Intent intent = new Intent(context, ClassRoomActivity.class);
                                intent.putExtra(ClassRoomActivity.EXTRA_BOOKING, mSelectedBooking);
                                context.startActivity(intent);
                            })
                            .setNegativeButton(context.getString(R.string.cr_leave_room_cancel), null)
                            .create()
                            .show();
                }
        );

        mServerRequestManager.checkStartClass(booking.getBookingId(), new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse response) {
                if (response.getCode() == 1) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.getDataSt());
                        String isStart = jsonObject.getString("is_start");
                        if (isStart.equalsIgnoreCase("false")) {
                            holder.startTV.setClickable(false);
                            holder.startTV.setBackground(context.getResources().getDrawable(R.drawable.btn_bg_rounded_green_disable));
                        } else {
                            holder.startTV.setClickable(true);
                            holder.startTV.setBackground(context.getResources().getDrawable(R.drawable.btn_bg_rounded_green));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(ServerError err) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSelectedBookingList.size();
    }

    public interface OnItemClicked {
        void onItemClick(String lessonID);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView stateDateTV;
        private TextView endDateTV;
        private TextView tutorNameTV;
        private TextView lessonTV;
        private TextView startTV;
        private SimpleDraweeView tutorImg;
        private SimpleDraweeView lessonImg;
        private View horiView;

        MyViewHolder(View view) {
            super(view);
            stateDateTV = view.findViewById(R.id.start_time);
            endDateTV = view.findViewById(R.id.end_time);
            tutorNameTV = view.findViewById(R.id.tutor_name_tv);
            lessonTV = view.findViewById(R.id.lesson_tv);
            startTV = view.findViewById(R.id.start_class);
            tutorImg = view.findViewById(R.id.sdv_tutor_photo);
            lessonImg = view.findViewById(R.id.sdv_lesson_photo);
            horiView = view.findViewById(R.id.view_pink);
        }
    }
}
