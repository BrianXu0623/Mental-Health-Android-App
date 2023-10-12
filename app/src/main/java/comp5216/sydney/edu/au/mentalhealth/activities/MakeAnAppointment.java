package comp5216.sydney.edu.au.mentalhealth.activities;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import comp5216.sydney.edu.au.mentalhealth.R;



public class MakeAnAppointment extends AppCompatActivity {

    private TextView professionalNameTextView;
    private TextView professionalJobTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_an_appointment);

        professionalNameTextView = findViewById(R.id.professionalNameTextView);
        professionalJobTextView = findViewById(R.id.professionalJobTextView);

        String professionalName = getIntent().getStringExtra("PROFESSIONAL_NAME");
        String professionalJob = getIntent().getStringExtra("PROFESSIONAL_JOB");

        professionalNameTextView.setText(professionalName);
        professionalJobTextView.setText(professionalJob);

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                String selStr = ""+year+(month+1)+dayOfMonth;
                Toast.makeText(MakeAnAppointment.this, "时间:"+selStr, Toast.LENGTH_SHORT).show();
                String otherPersonTime = "2023-10-11 14:20";
                if(selStr==otherPersonTime){//对比其他人时间
                    Toast.makeText(MakeAnAppointment.this, "此时间已有预约不可重复", Toast.LENGTH_SHORT).show();

                }

            }
        });



    }
}