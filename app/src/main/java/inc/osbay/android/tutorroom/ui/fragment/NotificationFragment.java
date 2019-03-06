package inc.osbay.android.tutorroom.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.adapter.NotificationAdapter;
import inc.osbay.android.tutorroom.sdk.database.DBAdapter;
import inc.osbay.android.tutorroom.sdk.model.Notification;
import inc.osbay.android.tutorroom.utils.CommonUtil;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

public class NotificationFragment extends BackHandledFragment implements NotificationAdapter.OnItemClicked {


    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.noti_rv)
    RecyclerView notiRV;
    @BindView(R.id.no_noti)
    TextView noNotiTV;
    @BindView(R.id.noti_icon)
    ImageView notiIcon;
    private DBAdapter dbAdapter;
    private List<Notification> notificationList = new ArrayList<>();

    @Override
    public boolean onBackPressed() {
        getFragmentManager().popBackStack();
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbAdapter = new DBAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        ButterKnife.bind(this, view);

        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        int count = dbAdapter.getNotiCount();
        notiIcon.setImageDrawable(CommonUtil.createImage(getActivity(), count));
        notificationList.clear();
        notificationList = dbAdapter.getNotificationByDateDesc();
        if (notificationList.size() > 0) {
            notiRV.setVisibility(View.VISIBLE);
            noNotiTV.setVisibility(View.GONE);
            NotificationAdapter notificationAdapter = new NotificationAdapter(notificationList, getActivity(), this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            notiRV.setLayoutManager(mLayoutManager);
            notiRV.setItemAnimator(new DefaultItemAnimator());
            notiRV.setAdapter(notificationAdapter);
        } else {
            notiRV.setVisibility(View.GONE);
            noNotiTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        setTitle(getString(R.string.notification));
        setDisplayHomeAsUpEnable(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onItemClick(String notiID) {
        FragmentManager fm = getFragmentManager();
        Fragment frg = fm.findFragmentById(R.id.framelayout);
        Fragment fragment = new NotificationDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("noti_id", notiID);
        fragment.setArguments(bundle);
        if (frg == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, fragment)
                    .commit();
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.framelayout, fragment)
                    .commit();
        }
    }
}