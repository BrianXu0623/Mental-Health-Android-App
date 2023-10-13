package comp5216.sydney.edu.au.mentalhealth.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

        dateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showDatePicker();
                    dateEditText.clearFocus();
                }

            }
        });

        timeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showTimePicker();
                    timeEditText.clearFocus();
                }

            }
        });

        bookButton = findViewById(R.id.button3);
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAppointmentToFirebase();
            }
        });

        ImageButton backButton = findViewById(R.id.AppointmentBackBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        String avatarUrl = getIntent().getStringExtra("avatarUrl");

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            ImageView professionalAvatar = findViewById(R.id.professionalAvatarImageView);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference().child(avatarUrl);

            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Uri downloadUrl = uri;
                Glide.with(MakeAnAppointment.this).load(downloadUrl).into(professionalAvatar);
            }).addOnFailureListener(exception -> {
            });
        }
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

    public boolean isValidDate(String dateInput) {
        if (dateInput.length() != 10) return false;
        if (dateInput.charAt(2) != '/' || dateInput.charAt(5) != '/') return false;

        try {
            int day = Integer.parseInt(dateInput.substring(0, 2));
            int month = Integer.parseInt(dateInput.substring(3, 5));
            int year = Integer.parseInt(dateInput.substring(6, 10));

            if (day < 1 || day > 31) return false;
            if (month < 1 || month > 12) return false;

        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public boolean isValidTime(String timeInput) {
        if (timeInput.length() != 5) return false;
        if (timeInput.charAt(2) != ':') return false;

        try {
            int hour = Integer.parseInt(timeInput.substring(0, 2));
            int minute = Integer.parseInt(timeInput.substring(3, 5));

            if (hour < 0 || hour > 23) return false;
            if (minute < 0 || minute > 59) return false;

        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }


    private void saveAppointmentToFirebase() {
        // 获取日期和时间的值
        String date = dateEditText.getText().toString();
        String time = timeEditText.getText().toString();
        String professionalName = professionalNameTextView.getText().toString();
        String professionalJob = professionalJobTextView.getText().toString();

        if (!isValidDate(date) || !isValidTime(time)) {
            Toast.makeText(this, "Please enter a valid date and time.", Toast.LENGTH_SHORT).show();
            return;
        }

        Appointment appointment = new Appointment(professionalName, professionalJob, date, time);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

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