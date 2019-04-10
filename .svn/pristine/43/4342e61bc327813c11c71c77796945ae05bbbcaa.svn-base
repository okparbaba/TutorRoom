package inc.osbay.android.tutorroom.adapter;

import android.content.Context;
import android.graphics.Typeface;
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
import inc.osbay.android.tutorroom.sdk.model.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notiList;
    private Context context;
    private OnItemClicked onClick;

    public NotificationAdapter(List<Notification> notiList, Context context, OnItemClicked listner) {
        this.notiList = notiList;
        this.context = context;
        this.onClick = listner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Notification notification = notiList.get(position);
        holder.notiCover.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_chat_support));
        holder.notiTitle.setText(notification.getTitle());
        if (notification.getStatus() == 1) {
            holder.newLb.setVisibility(View.VISIBLE);
            holder.notiTitle.setTypeface(null, Typeface.BOLD);
        } else {
            holder.newLb.setVisibility(View.GONE);
            holder.notiTitle.setTypeface(null, Typeface.NORMAL);
        }
        holder.newRL.setOnClickListener(view -> onClick.onItemClick(notification.getNotiId()));
    }

    public interface OnItemClicked {
        void onItemClick(String notiID);
    }

    @Override
    public int getItemCount() {
        return notiList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView notiTitle;
        TextView newLb;
        SimpleDraweeView notiCover;
        RelativeLayout newRL;

        ViewHolder(View itemView) {
            super(itemView);
            notiTitle = itemView.findViewById(R.id.noti_title);
            newLb = itemView.findViewById(R.id.new_lb);
            notiCover = itemView.findViewById(R.id.noti_img);
            newRL = itemView.findViewById(R.id.noti_rl);
        }
    }
}
