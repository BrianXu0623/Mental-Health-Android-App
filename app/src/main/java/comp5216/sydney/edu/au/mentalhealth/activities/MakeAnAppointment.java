package comp5216.sydney.edu.au.mentalhealth.activities;

import android.os.Bundle;
import android.widget.TextView;

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
    }
}