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
import java.util.Locale;

import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.model.CountryCode;

public class CountryCodeAdapter extends BaseAdapter {
    private Context context;
    private List<CountryCode> mCountryCodes;

    public CountryCodeAdapter(Context context, List<CountryCode> mCountryCodes) {
        this.context = context;
        this.mCountryCodes = mCountryCodes;
    }

    @Override
    public int getCount() {
        return mCountryCodes.size();
    }

    @Override
    public Object getItem(int position) {
        return mCountryCodes.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        CountryCode code = mCountryCodes.get(position);

        TextView tv = new TextView(context);
        tv.setText(String.format(Locale.getDefault(), "+%d", code.getCode()));
        tv.setGravity(Gravity.END);
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

        CountryCode code = mCountryCodes.get(position);

        TextView tvCountry = view.findViewById(R.id.tv_country);
        tvCountry.setText(code.getCountry());

        TextView tvCode = view.findViewById(R.id.tv_code);
        tvCode.setText(String.format(Locale.getDefault(), "+%d", code.getCode()));

        return view;
    }
}
