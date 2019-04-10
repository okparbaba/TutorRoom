package inc.osbay.android.tutorroom.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.database.DBAdapter;
import inc.osbay.android.tutorroom.sdk.model.Booking;
import inc.osbay.android.tutorroom.sdk.util.LGCUtil;
import inc.osbay.android.tutorroom.utils.EventDecorator;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

public class ScheduleFragment extends BackHandledFragment
        implements OnDateSelectedListener {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.calendarView)
    MaterialCalendarView calendarView;
    @BindView(R.id.book_new_tv)
    TextView bookTV;
    private int maxBookDay;
    private ServerRequestManager mServerRequestManager;
    private String accountID;
    private List<Booking> mBookingList = new ArrayList<>();
    private List<CalendarDay> scheduledLessonDays = new ArrayList<>();
    private List<CalendarDay> missedLessonDays = new ArrayList<>();
    private List<CalendarDay> scheduledTrialDays = new ArrayList<>();
    private List<CalendarDay> scheduledMixedDays = new ArrayList<>();
    private Fragment newFragment = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(Objects.requireNonNull(getActivity()));
        maxBookDay = sharedPreferenceData.getInt("max_book_time");
        accountID = String.valueOf(sharedPreferenceData.getInt("account_id"));
        mServerRequestManager = new ServerRequestManager(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar_schedule, container, false);
        ButterKnife.bind(this, rootView);
        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Setting minimum and maximum date of calendar
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
        scheduledLessonDays.clear();
        scheduledTrialDays.clear();
        scheduledMixedDays.clear();

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, maxBookDay);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendarView.state().edit()
                .setMinimumDate(CalendarDay.today())
                .setMaximumDate(CalendarDay.from(year, month
                        , day))
                .commit();

        mServerRequestManager.getBookingList(accountID, new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse result) {
                if (result.getCode() == 1) //For Success Situation
                {
                    final DBAdapter dbAdapter = new DBAdapter(getActivity());
                    try {
                        JSONArray classArray = new JSONArray(result.getDataSt());
                        for (int i = 0; i < classArray.length(); i++) {
                            Booking booking = new Booking(classArray.getJSONObject(i));
                            booking.setStartDate(LGCUtil.convertUTCToLocale(classArray.getJSONObject(i).getString("start_date"), LGCUtil.FORMAT_NORMAL, LGCUtil.FORMAT_NORMAL));
                            booking.setEndDate(LGCUtil.convertUTCToLocale(classArray.getJSONObject(i).getString("end_date"), LGCUtil.FORMAT_NORMAL, LGCUtil.FORMAT_NORMAL));
                            mBookingList.add(booking);
                        }
                        dbAdapter.insertBookedClass(mBookingList);
                    } catch (JSONException | ParseException e) {
                        Log.e(CommonConstant.TAG, "Cannot parse Booking Object", e);
                    }

                    //putting background mark on the calendar
                    for (int i = 0; i < mBookingList.size(); i++) {
                        try {
                            long currentDate = LGCUtil.dateToMilisecond(LGCUtil.getCurrentTimeString(), LGCUtil.FORMAT_NOTIME);
                            long scheduledDateLong = LGCUtil.dateToMilisecond
                                    (LGCUtil.convertToNoTime(mBookingList.get(i).getStartDate()), LGCUtil.FORMAT_NOTIME);

                            if (scheduledDateLong > currentDate || scheduledDateLong == currentDate) {
                                LocalDate scheduledDate = LocalDate.parse(LGCUtil.convertToNoTime(mBookingList.get(i).getStartDate()));
                                final CalendarDay scheduledCalendarDay = CalendarDay.from(scheduledDate);
                                if (mBookingList.get(i).getBookingType() == CommonConstant.Single)//1 = Single Booking
                                    scheduledLessonDays.add(scheduledCalendarDay);
                                else if (mBookingList.get(i).getBookingType() == CommonConstant.Trial)//4 = Trial booking
                                    scheduledTrialDays.add(scheduledCalendarDay);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    calendarView.addDecorator(new EventDecorator(CommonConstant.Single, Color.RED, scheduledLessonDays, getActivity()));
                    calendarView.addDecorator(new EventDecorator(CommonConstant.Trial, Color.RED, scheduledTrialDays, getActivity()));

                    for (int i = 0; i < scheduledLessonDays.size(); i++) {
                        for (int j = 0; j < scheduledTrialDays.size(); j++) {
                            if (scheduledTrialDays.get(j).equals(scheduledLessonDays.get(i)))
                                scheduledMixedDays.add(scheduledTrialDays.get(j));
                        }
                    }

                    calendarView.addDecorator(new EventDecorator(CommonConstant.Single_Trial, Color.RED, scheduledMixedDays, getActivity()));
                }
            }

            @Override
            public void onError(ServerError err) {
                Log.i("Get Booking List Error", err.getMessage());
            }
        });
        calendarView.setOnDateChangedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @OnClick(R.id.book_new_tv)
    void bookNewLesson() {
        Fragment newFragment = new SingleBookingChooseLessonFragment();
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.framelayout);

        Bundle bundle = new Bundle();
        bundle.putString(SingleBookingChooseLessonFragment.Booking_EXTRA, ScheduleFragment.class.getSimpleName());
        bundle.putString("lesson_type", String.valueOf(CommonConstant.singleLessonType));
        newFragment.setArguments(bundle);

        if (fragment == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, newFragment)
                    .commit();
        } else {
            fm.beginTransaction()
                    .replace(R.id.framelayout, newFragment)
                    .commit();
        }
    }

    @Override
    public boolean onBackPressed() {
        getActivity().finish();
        return false;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView,
                               @NonNull CalendarDay calendarDay, boolean b) {
        String selectedDate = b ? FORMATTER.format(calendarDay.getDate()) : null;
        if (mBookingList != null) {
            for (int i = 0; i < mBookingList.size(); i++) {
                try {
                    assert selectedDate != null;
                    if (selectedDate.equals(LGCUtil.convertToNoTime(mBookingList.get(i).getStartDate()))) {
                        FragmentManager fm = getFragmentManager();
                        Fragment fragment = fm.findFragmentById(R.id.framelayout);
                        Fragment newFragment = new ClassScheduleFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("selected_date", selectedDate);
                        newFragment.setArguments(bundle);
                        if (fragment == null) {
                            fm.beginTransaction()
                                    .add(R.id.framelayout, newFragment)
                                    .commit();
                        } else {
                            fm.beginTransaction()
                                    .replace(R.id.framelayout, newFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                        break;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
