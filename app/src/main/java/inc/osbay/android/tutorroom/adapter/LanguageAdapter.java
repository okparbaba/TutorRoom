package inc.osbay.android.tutorroom.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.model.Language;

public class LanguageAdapter extends BaseAdapter {
    private Context context;
    private List<Language> languageList;

    public LanguageAdapter(Context context, List<Language> languageList) {
        this.context = context;
        this.languageList = languageList;
    }

    @Override
    public int getCount() {
        return languageList.size();
    }

    @Override
    public String getItem(int position) {
        return languageList.get(position).getLanguageName();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Language language = languageList.get(position);

        TextView tv = new TextView(context);
        tv.setText( language.getLanguageName());
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

        Language language = languageList.get(position);

        TextView tvCountry = view.findViewById(R.id.tv_country);
        tvCountry.setText(language.getLanguageName());

        TextView tvCode = view.findViewById(R.id.tv_code);
        tvCode.setVisibility(View.GONE);
        //tvCode.setText(String.format(Locale.getDefault(), "+%d", language.getCode()));

        return view;
    }
}