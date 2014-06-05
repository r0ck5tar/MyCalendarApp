package fr.unice.polytech.mycalendarapp.app;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button readCalendarButton = (Button) findViewById( R.id.read_calendar_button);
        readCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Reading Calendar");
                //createCalendar();
                getCalendars();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void getCalendars() {

        String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.NAME,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.ACCOUNT_TYPE};
        Cursor calCursor =
                getContentResolver().
                        query(CalendarContract.Calendars.CONTENT_URI,
                                projection,
                                CalendarContract.Calendars.VISIBLE + " = 1",
                                null,
                                CalendarContract.Calendars._ID + " ASC");
        if (calCursor.moveToFirst()) {
            do {
                // Get the field values
                long calID = calCursor.getLong(PROJECTION_ID_INDEX);
                String displayName = calCursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
                String accountName = calCursor.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                String ownerName = calCursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
                System.out.println(ownerName);

                /*
                System.out.println(calID);
                System.out.println(displayName);
                System.out.println(accountName);
                System.out.println(ownerName);
                */
                String[] proj = new String[]{
                        CalendarContract.Events._ID,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.DTEND,
                        CalendarContract.Events.RRULE,
                        CalendarContract.Events.TITLE};
                Cursor eventCursor =
                        getContentResolver().
                                query(
                                        CalendarContract.Events.CONTENT_URI,
                                        proj,
                                        CalendarContract.Events.CALENDAR_ID + " = ? ",
                                        new String[]{Long.toString(calID)},
                                        null);
                if (eventCursor.moveToFirst()) {
                    do{
                        String eventTitle = eventCursor.getString(4);
                        //System.out.println("event: " + eventTitle);
                    } while(eventCursor.moveToNext());
                }
            } while (calCursor.moveToNext());
        }
    }

    public Uri createCalendar(){
        ContentValues values = new ContentValues();
        values.put(
                CalendarContract.Calendars.ACCOUNT_NAME, "freetime");
        values.put(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(
                CalendarContract.Calendars.NAME,
                "FreeTime Calendar");
        values.put(
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                "FreeTime Calendar");
        values.put(
                CalendarContract.Calendars.CALENDAR_COLOR,
                0xffff0000);
        values.put(
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(
                CalendarContract.Calendars.OWNER_ACCOUNT,
                "freetime@gmail.com");
        values.put(
                CalendarContract.Calendars.CALENDAR_TIME_ZONE,
                "Europe/Paris");
        Uri.Builder builder =
                CalendarContract.Calendars.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_NAME,
                "com.freetime");
        builder.appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(
                CalendarContract.CALLER_IS_SYNCADAPTER,
                "true");
        Uri uri =
                getContentResolver().insert(builder.build(), values);

        return uri;
    }


}
