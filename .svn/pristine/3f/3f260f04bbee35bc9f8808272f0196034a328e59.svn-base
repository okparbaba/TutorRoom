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

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.MyViewHolder> {

    private List<Lesson> lessonList;
    private Context context;
    private OnItemClicked onClick;

    public LessonAdapter(List<Lesson> lessonList, Context context, OnItemClicked listner) {
        this.lessonList = lessonList;
        this.context = context;
        this.onClick = listner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.package_item, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Lesson itemLesson = lessonList.get(position);

        String cover = itemLesson.getLessonCover();
        holder.packageCover.setImageURI(Uri.parse(cover));
        holder.packageName.setText(itemLesson.getLessonName());
        holder.packageDesc.setText(itemLesson.getLessonDescription());
        holder.packagePrice.setText(String.valueOf(itemLesson.getLessonPrice()) + " Credits");
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
        TextView packagePrice;
        TextView packageDesc;
        TextView packageName;
        SimpleDraweeView packageCover;
        RelativeLayout mRelativeLayout;

        public MyViewHolder(View view) {
            super(view);
            packagePrice = itemView.findViewById(R.id.package_price);
            packageDesc = itemView.findViewById(R.id.package_desc);
            packageName = itemView.findViewById(R.id.package_name);
            packageCover = itemView.findViewById(R.id.package_cover);
            mRelativeLayout = itemView.findViewById(R.id.relative_layout);
        }
    }
}

