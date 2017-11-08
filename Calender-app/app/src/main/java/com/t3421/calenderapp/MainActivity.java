package com.t3421.calenderapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    Button button;
int startYear = 0;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {



            String eventTitle = data.getStringExtra("eventTitle");
            startYear = data.getIntExtra("startYear", 0);
            int startMonth = data.getIntExtra("startMonth" , 0);
            int startDay = data.getIntExtra("startDay", 0);
            int startHour = data.getIntExtra("startHour", 0);
            int startMinute = data.getIntExtra("startMinute", 0);
            int endHour = data.getIntExtra("endHour", 0);
            int endMinute = data.getIntExtra("endMinute", 0);
            int occurrence = data.getIntExtra("occurrence" , 0);
            String extraComments = data.getStringExtra("extraComments");
            String colorSelected = data.getStringExtra("colorSelected");

            Toast.makeText(getBaseContext(),eventTitle + " Starts on " + startDay + "/" + startMonth + "/" + startYear + " at " + startHour + ":" + startMinute + " ending " + endHour + ":" + endMinute + " with comments '" + extraComments + "'" + " using color " + colorSelected, Toast.LENGTH_LONG).show();
        }
    }

            @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from activity_main.xml
        setContentView(R.layout.activity_main);

        // Locate the button in activity_main.xml
        Button eventAdd = (Button) findViewById(R.id.add_event);
        eventAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, AddEvent.class);
                startActivityForResult(intent, 1);
            }
        });
    }}