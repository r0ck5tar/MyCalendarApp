package fr.unice.polytech.calendarmodule;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.provider.CalendarContract.*;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Hakim on 5/6/2014.
 */
public class FreeTimeCalendarService extends Service {
    public class FreeTimeBinder extends Binder {
        public FreeTimeCalendarService getService() {
            return FreeTimeCalendarService.this;
        }
    }

    private final IBinder binder = new FreeTimeBinder();

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void getAllCalendars() {

        String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.NAME,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.ACCOUNT_TYPE};

        Cursor calCursor = getContentResolver().query(CalendarContract.Calendars.CONTENT_URI,
                                                      projection, null, null,
                                                      CalendarContract.Calendars._ID + " ASC");
        if (calCursor.moveToFirst()) {
            do {
                // Get the field values
                long calID = calCursor.getLong(PROJECTION_ID_INDEX);
                String displayName = calCursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
                String accountName = calCursor.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                String ownerName = calCursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX);


                System.out.println(calID);
                System.out.println(displayName);
                System.out.println(accountName);
                System.out.println(ownerName);

            } while (calCursor.moveToNext());
        }
    }

    public Uri createCalendar(){
        ContentValues values = new ContentValues();
        values.put(Calendars.ACCOUNT_NAME, "freetime");
        values.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(Calendars.NAME, "FreeTime Calendar");
        values.put(Calendars.CALENDAR_DISPLAY_NAME, "FreeTime Calendar");
        values.put(Calendars.CALENDAR_COLOR, 0xffff0000);
        values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        values.put(Calendars.OWNER_ACCOUNT, "freetime@gmail.com");
        values.put(Calendars.CALENDAR_TIME_ZONE, "Europe/Paris");
        Uri.Builder builder = Calendars.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(Calendars.ACCOUNT_NAME, "com.freetime");
        builder.appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER,"true");
        Uri uri = getContentResolver().insert(builder.build(), values);

        System.out.println(uri);
        Toast.makeText(getApplicationContext(), "Created FreeTime Calendar" , Toast.LENGTH_LONG).show();
        return uri;
    }

    public long getFreeTimeCalendarId() {
        String[] projection = new String[]{Calendars._ID};
        String selection = Calendars.ACCOUNT_NAME + " = ? AND "
                         + Calendars.ACCOUNT_TYPE +  " = ? ";
        // use the same values as above:
        String[] selArgs =
                new String[]{"freetime", CalendarContract.ACCOUNT_TYPE_LOCAL};
        Cursor cursor = getContentResolver().query(Calendars.CONTENT_URI,
                projection, selection, selArgs, null);
        if (cursor.moveToFirst()) {
            return cursor.getLong(0);
        }
        else{
            Uri uri = createCalendar();
            cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
            return 0;
        }
    }

    public long createEvent(String title, int startDay, int startMonth, int startYear,
                            int endDay, int endMonth, int endYear) {
        long calId = getFreeTimeCalendarId();

        Calendar cal = new GregorianCalendar(startYear, startMonth, startDay);
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long start = cal.getTimeInMillis();

        cal = new GregorianCalendar(endYear, endMonth, endDay);
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long end = cal.getTimeInMillis();

        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, start);
        values.put(Events.DTEND, end);
        //values.put(Events.RRULE, "FREQ=DAILY;COUNT=20;BYDAY=MO,TU,WE,TH,FR;WKST=MO");
        values.put(Events.TITLE, title);
        values.put(Events.EVENT_LOCATION, "Münster");
        values.put(Events.CALENDAR_ID, calId);
        values.put(Events.EVENT_TIMEZONE, "Europe/Paris");
        values.put(Events.DESCRIPTION, "The agenda or some description of the event");
        // reasonable defaults exist:
        values.put(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
        values.put(Events.SELF_ATTENDEE_STATUS, Events.STATUS_CONFIRMED);
        values.put(Events.ALL_DAY, 1);
        values.put(Events.ORGANIZER, "demo@freetime.com");
        values.put(Events.GUESTS_CAN_INVITE_OTHERS, 1);
        values.put(Events.GUESTS_CAN_MODIFY, 1);
        values.put(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
        Uri uri = getContentResolver().insert(Events.CONTENT_URI, values);
        long eventId = new Long(uri.getLastPathSegment());

        return eventId;
    }
}
