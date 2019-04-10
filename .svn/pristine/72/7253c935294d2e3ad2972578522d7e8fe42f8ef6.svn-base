package inc.osbay.android.tutorroom.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.model.Packagee;
import inc.osbay.android.tutorroom.sdk.model.Tag;

public class TagAdapter extends BaseAdapter {
    private List<Tag> tagList;
    private Context context;

    public TagAdapter(Context context, List<Tag> tagList) {
        this.context = context;
        this.tagList = tagList;
    }

    @Override
    public int getCount() {
        return tagList.size();
    }

    @Override
    public Object getItem(int position) {
        return tagList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Tag aPackage = tagList.get(position);

        TextView tv = new TextView(context);
        tv.setText(aPackage.getTagName());
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(0, 0, 10, 0);
        tv.setTextColor(Color.BLACK);

        return tv;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_spinner_country_code,
                    viewGroup, false);
        }

        Tag aPackage = tagList.get(position);

        TextView tvCountry = view.findViewById(R.id.tv_country);
        tvCountry.setText(aPackage.getTagName());

        TextView tvCode = view.findViewById(R.id.tv_code);
        tvCode.setVisibility(View.GONE);

        return view;
    }
}
