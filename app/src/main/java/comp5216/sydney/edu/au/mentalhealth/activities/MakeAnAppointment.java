package comp5216.sydney.edu.au.mentalhealth.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.entities.Appointment;

public class MakeAnAppointment extends AppCompatActivity {

    private TextView professionalNameTextView;
    private TextView professionalJobTextView;

    private TextView dateEditText;
    private TextView timeEditText;

    private Button bookButton;


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

        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        bookButton = findViewById(R.id.button3);
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAppointmentToFirebase();
            }
        });


    }

    private void showDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(MakeAnAppointment.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateEditText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, 2023, 10, 13); // Default date set to current date
        datePicker.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePicker = new TimePickerDialog(MakeAnAppointment.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeEditText.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        }, 12, 0, true); // Default time set to 12:00 in 24-hour format
        timePicker.show();
    }

    private void saveAppointmentToFirebase() {
        // 获取日期和时间的值
        String date = dateEditText.getText().toString();
        String time = timeEditText.getText().toString();
        String professionalName = professionalNameTextView.getText().toString();
        String professionalJob = professionalJobTextView.getText().toString();

        // 创建一个新的预约对象
        Appointment appointment = new Appointment(professionalName, professionalJob, date, time);

        // 获取 Firestore 实例
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 保存到 Firestore
        db.collection("appointments").add(appointment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MakeAnAppointment.this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MakeAnAppointment.this, "Failed to book appointment. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}