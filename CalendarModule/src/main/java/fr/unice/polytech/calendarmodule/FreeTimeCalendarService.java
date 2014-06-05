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
        values.put(CalendarContract.Calendars.ACCOUNT_NAME, "freetime");
        values.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(CalendarContract.Calendars.NAME, "FreeTime Calendar");
        values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "FreeTime Calendar");
        values.put( CalendarContract.Calendars.CALENDAR_COLOR, 0xffff0000);
        values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(CalendarContract.Calendars.OWNER_ACCOUNT, "freetime@gmail.com");
        values.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, "Europe/Paris");
        Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "com.freetime");
        builder.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER,"true");
        Uri uri = getContentResolver().insert(builder.build(), values);

        System.out.println(uri);
        return uri;
    }
}
