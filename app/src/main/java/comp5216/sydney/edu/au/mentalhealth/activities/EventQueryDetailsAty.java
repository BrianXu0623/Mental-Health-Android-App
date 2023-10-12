package comp5216.sydney.edu.au.mentalhealth.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.entities.Event;

public class EventQueryDetailsAty extends AppCompatActivity {

    private EditText etLoginUserName;// 活动名称
    private EditText regUserPwd;// 活动时间
    private EditText et_address;// 活动地点
    private EditText et_des;// 活动简介

    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_event_query_details);
        db = FirebaseFirestore.getInstance();

        etLoginUserName = findViewById(R.id.username);
        regUserPwd = findViewById(R.id.date);
        et_address = findViewById(R.id.address);
        et_des = findViewById(R.id.des);

        etLoginUserName.setText(getIntent().getStringExtra("eventName"));
        regUserPwd.setText(getIntent().getStringExtra("eventDate"));
        et_address.setText(getIntent().getStringExtra("eventAddress"));
        et_des.setText(getIntent().getStringExtra("eventDes"));


    }
}
