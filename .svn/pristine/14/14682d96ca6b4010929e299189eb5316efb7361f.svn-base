package inc.osbay.android.tutorroom.utils;

import android.content.Context;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;

/**
 * Decorate several days with a dot
 */
public class EventDecorator implements DayViewDecorator {

    private int color;
    private HashSet<CalendarDay> dates;
    private Context context;
    private int bookType;

    public EventDecorator(int bookType, int color, Collection<CalendarDay> dates, Context context) {
        this.color = color;
        this.dates = new HashSet<>(dates);
        this.context = context;
        this.bookType = bookType;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        /*view.addSpan(new DotSpan(5, color));*/
        if (bookType == CommonConstant.Single)
            view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.calendar_bg_yellow));
        else if (bookType == CommonConstant.Trial)
            view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.calendar_bg_red));
        else if (bookType == CommonConstant.Single_Trial)
            view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.calendar_bg_red_yellow));
    }
}
