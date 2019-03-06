package inc.osbay.android.tutorroom.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

public class SingleBookingChooseDateFragment extends BackHandledFragment
        implements OnDateSelectedListener {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.calendarView)
    MaterialCalendarView widget;
    @BindView(R.id.schedule_tv)
    TextView scheduleTV;
    private String startHrSt;
    private String startMinSt;
    private long currentDateInMili;
    private long minBookTimeInMili;
    private long classMinuteInMili;
    private int minBookTime;
    private int maxBookDay;
    private int classMinute;
    private String lessonID;
    private String selectedDate = null;
    private int startHour;
    private int startMin;
    private String[] mHours = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
    private String[] mMinutes = {"00", "30"};
    private String lessonType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lessonID = getArguments().getString("lesson_id");
        lessonType = getArguments().getString("lesson_type");
        SharedPreferenceData sharedPreferenceData = new SharedPreferenceData(Objects.requireNonNull(getActivity()));
        minBookTime = sharedPreferenceData.getInt("min_book_time");
        minBookTimeInMili = minBookTime * 60 * 1000;
        maxBookDay = sharedPreferenceData.getInt("max_book_time");
        classMinute = sharedPreferenceData.getInt("class_minute");
        classMinuteInMili = classMinute * 60 * 1000;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_booking_choose_date, container, false);
        ButterKnife.bind(this, view);

        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Date date = new Date();
        currentDateInMili = date.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMin = calendar.get(Calendar.MINUTE);

        calendar.add(Calendar.DAY_OF_YEAR, maxBookDay);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        widget.state().edit()
                .setMinimumDate(CalendarDay.today())
                .setMaximumDate(CalendarDay.from(year, month
                        , day))
                .commit();
        widget.setOnDateChangedListener(this);

        // Adding Minimum book time
        int startHrInt;
        int startMinInt;
        if (currentMin + minBookTime >= 60) {
            startHrInt = currentHour + (currentMin + minBookTime) / 60;
            startMinInt = (currentMin + minBookTime) % 60;
        } else {
            startHrInt = currentHour;
            startMinInt = currentMin + minBookTime;
        }

        // Adding Book time block (30 mins) **/
        if (startMinInt <= 30) {
            startMinInt = 30;
        } else {
            startHrInt = startHrInt + 1;
            startMinInt = 0;
        }

        int endHrInt;
        int endMinInt;
        if (startMinInt + classMinute >= 60) {
            endHrInt = startHrInt + (startMinInt + classMinute) / 60;
            endMinInt = (startMinInt + classMinute) % 60;
        } else {
            endHrInt = startHrInt;
            endMinInt = startMinInt + classMinute;
        }
        showTimeSpinner(startHrInt, startMinInt, endHrInt, endMinInt);
    }

    @SuppressLint("SetTextI18n")
    public void showTimeSpinner(int startHour, int startMin, int endHour, int endMin) {
        if (startHour < 10)
            startHrSt = "0" + startHour;
        else if (startHour >= 10 && startHour < 24) startHrSt = String.valueOf(startHour);
        else startHrSt = "0" + (startHour % 24);

        String endHrSt;
        if (endHour < 10)
            endHrSt = "0" + endHour;
        else if (endHour >= 10 && endHour < 24) endHrSt = String.valueOf(endHour);
        else endHrSt = "0" + (endHour % 24);

        if (startMin < 10)
            startMinSt = "0" + startMin;
        else startMinSt = String.valueOf(startMin);

        String endMinSt;
        if (endMin < 10)
            endMinSt = "0" + endMin;
        else endMinSt = String.valueOf(endMin);

        this.startHour = startHour;
        this.startMin = startMin;
        scheduleTV.setText(startHrSt + ":" + startMinSt + " - " + endHrSt + ":" + endMinSt);
    }

    @Override
    public boolean onBackPressed() {
        getFragmentManager().popBackStack();
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        setTitle(getString(R.string.single_book));
        setDisplayHomeAsUpEnable(true);
    }

    @OnClick(R.id.time_spinner_ll)
    void showSchedule() {
        /*TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                (timePicker, startHour, startMinutes) -> {
                    int endHrInt;
                    int endMinInt;
                    if (startMinutes + classMinute >= 60) {
                        endHrInt = startHour + (startMinutes + classMinute) / 60;
                        endMinInt = (startMinutes + classMinute) % 60;
                    } else {
                        endHrInt = startHour;
                        endMinInt = startMinutes + classMinute;
                    }
                    showTimeSpinner(startHour, startMinutes, endHrInt, endMinInt);
                }, startHour, startMin, false);
        timePickerDialog.show();*/

        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.timepicker_dialog_layout);
        dialog.setCancelable(false);

        NumberPicker hourNP = dialog.findViewById(R.id.np_book_hour);
        hourNP.setMinValue(0);
        hourNP.setMaxValue(23);
        hourNP.setDisplayedValues(mHours);

        NumberPicker minuteNP = dialog.findViewById(R.id.np_book_minute);
        minuteNP.setDisplayedValues(null);
        minuteNP.setMinValue(0);
        minuteNP.setMaxValue(1);
        minuteNP.setDisplayedValues(mMinutes);

        setDividerColor(hourNP, getActivity().getResources().getColor(R.color.colorPrimary));
        setDividerColor(minuteNP, getActivity().getResources().getColor(R.color.colorPrimary));

        TextView okTV = dialog.findViewById(R.id.ok_tv);
        TextView cancelTV = dialog.findViewById(R.id.cancel_tv);
        okTV.setOnClickListener(view -> {
            dialog.dismiss();
            int startHour;
            int startMinutes;
            int endHrInt;
            int endMinInt;
            startHour = Integer.parseInt(mHours[hourNP.getValue()]);
            startMinutes = Integer.parseInt(mMinutes[minuteNP.getValue()]);

            if (startMinutes + classMinute >= 60) {
                endHrInt = startHour + (startMinutes + classMinute) / 60;
                endMinInt = (startMinutes + classMinute) % 60;
            } else {
                endHrInt = startHour;
                endMinInt = startMinutes + classMinute;
            }
            showTimeSpinner(startHour, startMinutes, endHrInt, endMinInt);
        });

        cancelTV.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    /*private String getStartTime() {
        return mHours[hourNP.getValue()] + ":" + mMinutes[minuteNP.getValue()];
    }

    private String getEndTime() {
        if (mBookingType == Booking.Type.TOPIC) {
            if (minuteNP.getValue() == 0) {
                return mHours[hourNP.getValue()] + ":25";
            } else {
                return mHours[hourNP.getValue()] + ":55";
            }
        } else {
            if (minuteNP.getValue() == 0) {
                return mHours[hourNP.getValue()] + ":50";
            } else {
                return mHours[(hourNP.getValue() + 1) % mHours.length] + ":20";
            }
        }
    }*/

    private void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException | IllegalAccessException
                        | Resources.NotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    @OnClick(R.id.next_tv)
    void clickNext() {
        if (selectedDate == null)
            Toast.makeText(getActivity(), getString(R.string.select_date_empty), Toast.LENGTH_LONG)
                    .show();
        else {
            String startDateSt = selectedDate + " " + startHrSt + ":" + startMinSt + ":00";
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = sdf.parse(startDateSt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long selectedDateInMili = date.getTime();

            if (selectedDateInMili >= currentDateInMili + minBookTimeInMili) {
                Date endDate = new Date(selectedDateInMili + classMinuteInMili);

                String endDateSt = sdf.format(endDate);

                FragmentManager fm = getFragmentManager();
                Fragment frg = fm.findFragmentById(R.id.framelayout);
                Fragment fragment = new SingleBookingChooseTutorFragment();

                Bundle bundle = new Bundle();
                bundle.putString("source", SingleBookingChooseDateFragment.class.getSimpleName());
                bundle.putString("lesson_id", String.valueOf(lessonID));
                bundle.putString("lesson_type", lessonType);
                bundle.putString("start_date", startDateSt);
                bundle.putString("end_date", endDateSt);
                fragment.setArguments(bundle);
                if (frg == null) {
                    fm.beginTransaction()
                            .add(R.id.framelayout, fragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    fm.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.framelayout, fragment)
                            .commit();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.select_date_err, String.valueOf(minBookTime)), Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onDateSelected(
            @NonNull MaterialCalendarView widget,
            @NonNull CalendarDay date,
            boolean selected) {
        selectedDate = selected ? FORMATTER.format(date.getDate()) : null;
    }
}
