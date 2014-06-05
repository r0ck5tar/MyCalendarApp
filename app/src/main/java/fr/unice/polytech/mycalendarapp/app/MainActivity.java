package fr.unice.polytech.mycalendarapp.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import fr.unice.polytech.calendarmodule.FreeTimeCalendarService;

public class MainActivity extends ActionBarActivity {
    private FreeTimeCalendarService ftcService;

    private ServiceConnection ftcServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            ftcService = ((FreeTimeCalendarService.FreeTimeBinder)service).getService();

            Toast.makeText(getApplicationContext(), "FreeTimeCalendarService connected", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            ftcService = null;

            Toast.makeText(getApplicationContext(), "FreeTimeCalendarService disconnected", Toast.LENGTH_LONG).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindService(new Intent(getApplicationContext(), FreeTimeCalendarService.class), ftcServiceConnection, Context.BIND_AUTO_CREATE);

        Button readCalendarButton = (Button) findViewById( R.id.read_calendar_button);
        readCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Reading Calendar");
                ftcService.createCalendar();
                ftcService.getAllCalendars();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(ftcServiceConnection);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
