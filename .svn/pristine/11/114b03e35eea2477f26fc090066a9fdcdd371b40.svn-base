package inc.osbay.android.tutorroom.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.model.Packagee;
import inc.osbay.android.tutorroom.sdk.model.Tag;

public class PackageByAllTagAdapter extends RecyclerView.Adapter<PackageByAllTagAdapter.ViewHolder> {

    private List<Tag> tagList;
    private Context context;

    public PackageByAllTagAdapter(List<Tag> tagList, Context context) {
        this.tagList = tagList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_by_tag_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Tag tag = tagList.get(position);
        holder.tagName.setText(tag.getTagName());
        holder.tagDesc.setText(tag.getTagDescription());

        List<Packagee> packageList = new ArrayList<>();
        JSONArray packageJsonArray = tag.getPackageArray();
        try {
            for (int j = 0; j < packageJsonArray.length(); j++) {
                Packagee packageObj = new Packagee(packageJsonArray.getJSONObject(j));
                packageList.add(packageObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PackageAdapter packageAdapter = new PackageAdapter(packageList, context);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        holder.packageRV.setLayoutManager(mLayoutManager);
        holder.packageRV.setItemAnimator(new DefaultItemAnimator());
        holder.packageRV.setAdapter(packageAdapter);
        packageAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView packageRV;
        TextView tagDesc;
        TextView tagName;

        ViewHolder(View itemView) {
            super(itemView);
            packageRV = itemView.findViewById(R.id.package_rv);
            tagDesc = itemView.findViewById(R.id.tag_desc);
            tagName = itemView.findViewById(R.id.tag_name);
        }
    }
}