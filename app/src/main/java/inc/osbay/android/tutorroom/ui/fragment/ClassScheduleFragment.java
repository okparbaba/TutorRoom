package inc.osbay.android.tutorroom.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.adapter.BookingScheduleAdapter;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.database.DBAdapter;
import inc.osbay.android.tutorroom.sdk.model.Booking;
import inc.osbay.android.tutorroom.sdk.model.Lesson;
import inc.osbay.android.tutorroom.sdk.util.LGCUtil;

public class ClassScheduleFragment extends BackHandledFragment implements BookingScheduleAdapter.OnItemClicked {

    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.schedule_rv)
    RecyclerView classRV;
    /*@BindView(R.id.previous_date)
    ImageView previousDateImg;
    @BindView(R.id.next_date)
    ImageView nextDateImg;*/
    @BindView(R.id.scheduled_date_tv)
    TextView scheduledDateTV;
    private String selectedDate;
    private List<Booking> mBookingList = new ArrayList<>();
    private ServerRequestManager mServerRequestManager;
    private List<Lesson> lessonList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBAdapter dbAdapter = new DBAdapter(getActivity());
        selectedDate = getArguments().getString("selected_date");
        mBookingList.clear();
        mBookingList = dbAdapter.getFilteredBookings(selectedDate);
        mServerRequestManager = new ServerRequestManager(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_class_schedule, container, false);
        ButterKnife.bind(this, rootView);

        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setTitle(getString(R.string.schedule));
        setDisplayHomeAsUpEnable(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            scheduledDateTV.setText(LGCUtil.changeDateFormat(selectedDate, LGCUtil.FORMAT_NOTIME, LGCUtil.FORMAT_LONG_WEEKDAY));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        BookingScheduleAdapter bookingScheduleAdapter = new BookingScheduleAdapter(mBookingList, mServerRequestManager, getActivity(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        classRV.setLayoutManager(mLayoutManager);
        classRV.setItemAnimator(new DefaultItemAnimator());
        classRV.setAdapter(bookingScheduleAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        getFragmentManager().popBackStack();
        return false;
    }

    @Override
    public void onItemClick(String lessonID) {

    }
}
