package inc.osbay.android.tutorroom.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import inc.osbay.android.tutorroom.R;

public class CommonUtil {

    //Draw Notification Count on Canvas
    public static Drawable createImage(Context context, int count) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.notification);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        c.drawBitmap(bitmap, 0, 0, paint);

        Paint paint1 = new Paint();
        paint1.setColor(Color.WHITE);

        Paint paint2 = new Paint();
        paint2.setColor(ContextCompat.getColor(context, R.color.red));
        paint2.setAntiAlias(true);

        String text = String.valueOf(count);

        if (!text.equals("0")) {
            if (text.length() == 1) {
                paint1.setTextSize(w * 0.25f);
                c.drawCircle(w * 0.72f, h * 0.4f, w * 0.18f, paint2);
                c.drawText(text, w * 0.63f, h * 0.49f, paint1);
            }

            if (text.length() == 2) {
                paint1.setTextSize(w * 0.25f);
                c.drawCircle(w * 0.72f, h * 0.4f, w * 0.18f, paint2);
                c.drawText(text, w * 0.57f, h * 0.49f, paint1);
            }

            if (text.length() == 3) {
                paint1.setTextSize(w * 0.22f);
                c.drawCircle(w * 0.72f, h * 0.4f, w * 0.2f, paint2);
                c.drawText(text, w * 0.525f, h * 0.495f, paint1);
            }
        }
        return new BitmapDrawable(context.getResources(), b);
    }

    //Generate OnlineSupport RoomId
    public static String generateRoomId(String roomId) {
        String result;
        StringBuilder roomName = new StringBuilder("Engleezi-");
        int maxcount = 8;
        int len = roomId.length();
        int zerocount = maxcount - len;

        for (int i = 0; i < zerocount; i++) {
            roomName.append("0");
        }
        result = roomName + roomId;
        return result;
    }

    //Generate OnlineSupport RoomId
    public static String generateCallSupportRoomId(String roomId) {
        String result;
        StringBuilder roomName = new StringBuilder("Engleezi-Consultant");
        int maxcount = 8;
        int len = roomId.length();
        int zerocount = maxcount - len;

        for (int i = 0; i < zerocount; i++) {
            roomName.append("0");
        }
        result = roomName + roomId;
        return(result);
    }

    /**
     * validate your email address format. Ex-akhi@mani.com
     *
     * @param email Email
     * @return If email address is correct format, reutrn true.Otherwise, return
     * false.
     */
    public static boolean validateEmail(String email) {
        /*if (email.equals("")) {
            return false;
        } else {*/
            Pattern pattern;
            Matcher matcher;
            final String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*"
                    + "@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            pattern = Pattern.compile(emailPattern);
            matcher = pattern.matcher(email);
            return matcher.matches();
        //}
    }

    /**
     * validate your email address format. Ex-akhi@mani.com
     *
     * @param password Password
     * @return If email address is correct format, reutrn true.Otherwise, return
     * false.
     */
    public static boolean validatePassword(String password) {
        return (!TextUtils.isEmpty(password) && password.length() >= 6 && password.length() <= 10);
    }

    public static String getFormattedDate(Date d, String format, boolean isExt) {
        String endDateStr;
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        endDateStr = sdf.format(d);

        if (isExt) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd", Locale.getDefault());
            switch (Integer.parseInt(sdf1.format(d))) {
                case 1:
                    endDateStr += "st";
                    break;
                case 2:
                    endDateStr += "nd";
                    break;
                case 3:
                    endDateStr += "rd";
                    break;
                case 21:
                    endDateStr += "st";
                    break;
                case 22:
                    endDateStr += "nd";
                    break;
                case 23:
                    endDateStr += "rd";
                    break;
                default:
                    endDateStr += "th";
                    break;
            }
        }
        return endDateStr;
    }

    public static String getCustomFormattedDate(Date d, String format, boolean b) {
        String dateStr;

        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        dateStr = sdf.format(d);

        SimpleDateFormat sdf1 = new SimpleDateFormat("dd", Locale.getDefault());
        switch (Integer.parseInt(sdf1.format(d))) {
            case 1:
                dateStr += "st";
                break;
            case 2:
                dateStr += "nd";
                break;
            case 3:
                dateStr += "rd";
                break;
            case 21:
                dateStr += "st";
                break;
            case 22:
                dateStr += "nd";
                break;
            case 23:
                dateStr += "rd";
                break;
            default:
                dateStr += "th";
                break;
        }
        if (b) {
            SimpleDateFormat sdf2 = new SimpleDateFormat(" yyyy", Locale.getDefault());
            dateStr += sdf2.format(d);
        } else {
            SimpleDateFormat sdf2 = new SimpleDateFormat(", yyyy", Locale.getDefault());
            dateStr += sdf2.format(d);
        }

        return dateStr;
    }

    public static String getTimeStringResult(Date dateStr, String formatDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatDate, Locale.getDefault());
        return sdf.format(dateStr);
    }

    public static String getCustomDateResult(String dateStr, String requestDate, String formatDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(requestDate, Locale.getDefault());
        SimpleDateFormat formatSdf = new SimpleDateFormat(formatDate, Locale.getDefault());
        Date d = null;
        if (dateStr != null) {
            try {
                d = sdf.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return formatSdf.format(d);
    }

    public static Date convertStringToDate(String d, String formatDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatDate, Locale.getDefault());
        try {
            return sdf.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getTodayDateString(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(d);
    }

    public static String getDateStringFormat(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(d);
    }

    public static String changeLocalDateToUTC() throws ParseException {
        Date d = new Date();
        String bookedDateUTC = getTodayDateString(d);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        Date utcDate = dateFormat.parse(bookedDateUTC);

        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(utcDate);
    }

    public static void hideKeyBoard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
