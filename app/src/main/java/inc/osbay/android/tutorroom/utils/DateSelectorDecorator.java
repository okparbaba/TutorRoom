package inc.osbay.android.tutorroom.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import inc.osbay.android.tutorroom.R;

/**
 * Use a custom selector
 */
public class DateSelectorDecorator implements DayViewDecorator {

    private Drawable drawable;
    public DateSelectorDecorator(Activity context, int classType) {
        if (classType == 1)//Lesson Booking
            drawable = context.getResources().getDrawable(R.drawable.date_selector_red);
        else if (classType == 2)//Package Booking
            drawable = context.getResources().getDrawable(R.drawable.date_selector_yellow);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }
}
