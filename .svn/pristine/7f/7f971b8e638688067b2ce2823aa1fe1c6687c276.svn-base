package inc.osbay.android.tutorroom.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.model.Lesson;

public class LessonByPackageDetailAdapter extends RecyclerView.Adapter<LessonByPackageDetailAdapter.MyViewHolder> {

    private List<Lesson> lessonList;
    private Context context;
    private OnItemClicked onClick;

    public LessonByPackageDetailAdapter(List<Lesson> lessonList, Context context, OnItemClicked listner) {
        this.lessonList = lessonList;
        this.context = context;
        this.onClick = listner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.lesson_package_detail_item, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Lesson itemLesson = lessonList.get(position);
        String cover = itemLesson.getLessonCover();
        holder.lessonCover.setImageURI(Uri.parse(cover));
        holder.lessonName.setText(itemLesson.getLessonName());
        holder.lessonDesc.setText(itemLesson.getLessonDescription());
        holder.lessonDuration.setText(String.valueOf(itemLesson.getClassMin()));
        holder.lessonCredit.setText(String.valueOf(itemLesson.getLessonPrice()));
        holder.mRelativeLayout.setOnClickListener(view -> onClick.onItemClick(itemLesson));
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    public interface OnItemClicked {
        void onItemClick(Lesson lesson);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView lessonCredit;
        TextView lessonDesc;
        TextView lessonName;
        TextView lessonDuration;
        SimpleDraweeView lessonCover;
        RelativeLayout mRelativeLayout;

        public MyViewHolder(View view) {
            super(view);
            lessonCredit = itemView.findViewById(R.id.credit);
            lessonDesc = itemView.findViewById(R.id.lesson_desc);
            lessonName = itemView.findViewById(R.id.lesson_name);
            lessonCover = itemView.findViewById(R.id.lesson_cover);
            mRelativeLayout = itemView.findViewById(R.id.relative_layout);
            lessonDuration = itemView.findViewById(R.id.lesson_duration);
        }
    }
}