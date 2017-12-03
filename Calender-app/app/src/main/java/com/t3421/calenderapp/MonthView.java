package com.t3421.calenderapp;

import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.*;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;



public class MonthView extends AppCompatActivity {

    int startYear = 0, startMonth = 0, startDay = 0, startHour = 0, startMinute = 0, endHour = 0, endMinute = 0;
    String eventTitle, eventComments, colorSelected, selection;
    EventsDb db = new EventsDb(this);

    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private CompactCalendarView calendar;
    private EventsDb database;
    List<com.t3421.calenderapp.Event> events;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if(requestCode == 2 && requestCode == RESULT_OK)
        //Toast.makeText(getBaseContext(),startMinute + endMinute +startHour + endHour + startDay + startYear, Toast.LENGTH_LONG).show();


        if (requestCode == 1 && resultCode == RESULT_OK) {

            String eventTitle = data.getStringExtra("eventTitle");
            startYear = data.getIntExtra("startYear", 0);
            startMonth = data.getIntExtra("startMonth" , 0);
            startDay = data.getIntExtra("startDay", 0);
            startHour = data.getIntExtra("startHour", 0);
            startMinute = data.getIntExtra("startMinute", 0);
            endHour = data.getIntExtra("endHour", 0);
            endMinute = data.getIntExtra("endMinute", 0);
            String occurrence = data.getStringExtra("occurrence");
            String extraComments = data.getStringExtra("extraComments");
            String colorSelected = data.getStringExtra("colorSelected");


            com.t3421.calenderapp.Event event =  new com.t3421.calenderapp.Event(startMinute, endMinute, startHour, endHour, startDay, startYear, startMonth, eventTitle, extraComments, occurrence, colorSelected);
            if(db.checkForConflict(event)){
                Toast.makeText(getBaseContext(), "Conflict", Toast.LENGTH_LONG).show();
            }
            else {
                db.insertEvent(event);
                db.eventOccurance(event);
                Toast.makeText(getBaseContext(), eventTitle + " added", Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == 1  && resultCode == RESULT_CANCELED){
            Toast.makeText(getBaseContext(), "Canceled by user", Toast.LENGTH_LONG).show();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_view);

        final TextView title = (TextView) findViewById(R.id.month_title_text);
        title.setText(dateFormatMonth.format(new Date()));

        calendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setUseThreeLetterAbbreviation(true);

        database = new EventsDb(this);
        addEventsToCalendar();
//        events = database.getAllEvents();
//        for(int i = 0; i < events.size(); i++)
//        {
//            com.t3421.calenderapp.Event eve = events.get(i);
//            calendar.addEvent(new Event(getColor(getColorInt(eve.getColor())), toEpoch(eve.getDay(),eve.getMonth(),eve.getYear())), true);
//        }

        calendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                long epochForDayClicked = dateClicked.getTime();
                String day = getDayFromEpoch(epochForDayClicked);
                String month = getMonthFromEpoch(epochForDayClicked);
                String year = getYearFromEpoch(epochForDayClicked);

                Intent intent = new Intent(MonthView.this, DayView.class);
                intent.putExtra("day", day);
                intent.putExtra("month", month);
                intent.putExtra("year", year);
                intent.putExtra("dateEpoch", epochForDayClicked);
                intent.putExtra("default", false);
                startActivityForResult(intent, 2);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                title.setText(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });

        Button agendaView = (Button) findViewById(R.id.month_view_To_agenda);
        agendaView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MonthView.this, AgendaView.class);
                startActivity(intent);
            }
        });

        Button eventAdd = (Button) findViewById(R.id.month_view_add);
        eventAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MonthView.this, AddEvent.class);
                intent.putExtra("startMinute" , 0);
                intent.putExtra("startHour" , 0);
                intent.putExtra("endHour" , 0);
                intent.putExtra("endMinute" , 0);
                intent.putExtra("eventTitle" , "");
                intent.putExtra("extraComments" , "");
                intent.putExtra("occurrence" , "");
                intent.putExtra("startDay" , 0);
                intent.putExtra("startMonth" , 0);
                intent.putExtra("startYear" , 0);
                intent.putExtra("colorSelected" , "");
                intent.putExtra("default" , true);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        calendar.removeAllEvents();
        addEventsToCalendar();

    }

    private void addEventsToCalendar() {
        events = database.getAllEvents();
        for(int i = 0; i < events.size(); i++)
        {
            com.t3421.calenderapp.Event eve = events.get(i);
            calendar.addEvent(new Event(getColor(getColorInt(eve.getColor())), toEpoch(eve.getDay(),eve.getMonth(),eve.getYear())), true);
        }
    }

    public long toEpoch(int day, int month, int year) {
        long epoch = 0;
        try {
            epoch = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(month + "/" + day + "/" + year + " 01:00:00").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return epoch;
    }

    public String getDayFromEpoch(long epoch) {
        String date = new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date (epoch));
        return date.substring(3,5);
    }

    public String getMonthFromEpoch(long epoch) {
        String date = new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date (epoch));
        return date.substring(0,2);
    }

    public String getYearFromEpoch(long epoch) {
        String date = new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date (epoch));
        return date.substring(6);
    }

    private int getColorInt(String colorName) {
        switch(colorName.toLowerCase()) {
            case "yellow":
                return R.color.yellow;
            case "fuchsia":
                return R.color.fuchsia;
            case "red":
                return R.color.red;
            case "green":
                return R.color.green;
            case "purple":
                return R.color.purple;
            case "blue":
                return  R.color.blue;
            case "maroon":
                return R.color.maroon;
            case "teal":
                return R.color.teal;
            default:
                return R.color.red;
        }
    }
}
