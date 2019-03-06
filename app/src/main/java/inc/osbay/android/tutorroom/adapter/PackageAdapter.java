package inc.osbay.android.tutorroom.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import inc.osbay.android.tutorroom.sdk.model.Packagee;
import inc.osbay.android.tutorroom.ui.fragment.PackageDetailFragment;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {

    private List<Packagee> packageList;
    private Context context;

    PackageAdapter(List<Packagee> tutorList, Context context) {
        packageList = tutorList;
        this.context = context;
    }

    @NonNull
    @Override
    public PackageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_item, parent, false);
        return new PackageAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PackageAdapter.ViewHolder holder, final int position) {
        Packagee packagee = packageList.get(position);
        String cover = packagee.getCoverImg();
        holder.packageCover.setImageURI(Uri.parse(cover));
        holder.packageName.setText(packagee.getPackageName());
        holder.packageDesc.setText(packagee.getPackageDescription());
        holder.packagePrice.setText(context.getString(R.string._credits, String.valueOf(packagee.getPackagePrice())));
        holder.packageRL.setOnClickListener(view -> {
            Fragment mainFragment = new PackageDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString(PackageDetailFragment.PackageDetailFragment_EXTRA, packagee.getPackageID());
            mainFragment.setArguments(bundle);

            FragmentManager fm = ((Activity) context).getFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.framelayout);
            if (fragment == null) {
                fm.beginTransaction()
                        .add(R.id.framelayout, mainFragment)
                        .commit();
            } else {
                fm.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.framelayout, mainFragment)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView packagePrice;
        TextView packageDesc;
        TextView packageName;
        SimpleDraweeView packageCover;
        RelativeLayout packageRL;

        ViewHolder(View itemView) {
            super(itemView);
            packagePrice = itemView.findViewById(R.id.package_price);
            packageDesc = itemView.findViewById(R.id.package_desc);
            packageName = itemView.findViewById(R.id.package_name);
            packageCover = itemView.findViewById(R.id.package_cover);
            packageRL = itemView.findViewById(R.id.relative_layout);
        }
    }
}
