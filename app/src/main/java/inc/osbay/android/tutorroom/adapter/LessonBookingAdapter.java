package inc.osbay.android.tutorroom.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.model.Lesson;

public class LessonBookingAdapter extends RecyclerView.Adapter<LessonBookingAdapter.MyViewHolder> {

    private List<Lesson> lessonList;
    private Context context;
    private OnItemClicked onClick;
    private int row_index = -1;
    private int lessonID;

    public LessonBookingAdapter(List<Lesson> lessonList, int lessonID, Context context, OnItemClicked listner) {
        this.lessonList = lessonList;
        this.context = context;
        this.onClick = listner;
        this.lessonID = lessonID;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.lesson_booking_item, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Lesson itemLesson = lessonList.get(position);

        holder.lessonTitleTV.setText(itemLesson.getLessonName());
        holder.mRelativeLayout.setOnClickListener(view -> {
            lessonID = 0;
            row_index = position;
            notifyDataSetChanged();
            onClick.onItemClick(lessonList.get(position).getLessonId());
        });
        if (row_index == position || lessonID == Integer.parseInt(itemLesson.getLessonId())) {
            holder.mRelativeLayout.setBackgroundColor(context.getResources().getColor(R.color.pink));
            holder.lessonTitleTV.setTextColor(context.getResources().getColor(R.color.white));
        } else if (row_index != position) {
            holder.mRelativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.lessonTitleTV.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    public interface OnItemClicked {
        void onItemClick(String lessonID);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView lessonTitleTV;
        private RelativeLayout mRelativeLayout;

        MyViewHolder(View view) {
            super(view);
            mRelativeLayout = view.findViewById(R.id.lesson_prl);
            lessonTitleTV = view.findViewById(R.id.tv_title);
        }
    }
}

