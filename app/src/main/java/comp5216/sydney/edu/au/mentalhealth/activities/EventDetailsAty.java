package comp5216.sydney.edu.au.mentalhealth.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.entities.Event;

public class EventDetailsAty extends AppCompatActivity {

    private EditText etLoginUserName;
    private EditText regUserPwd;
    private EditText et_address;
    private EditText et_des;
    private Button loginBtn;

    private FirebaseFirestore db;
    private CollectionReference userCollection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_event_details);
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("event");


        etLoginUserName = findViewById(R.id.username);
        regUserPwd = findViewById(R.id.date);
        et_address = findViewById(R.id.address);
        et_des = findViewById(R.id.des);

        loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etLoginUserName.getText())
                    && !TextUtils.isEmpty(regUserPwd.getText())) {
                post();
            } else {
                Toast.makeText(this, "Enter Event's Relative Data",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post() {
        String username = etLoginUserName.getText().toString().trim();
        String pwd = regUserPwd.getText().toString().trim();
        String et_desRef = et_des.getText().toString().trim();
        String et_addressR = et_address.getText().toString().trim();

        Event userinfo = new Event();
        userinfo.setEventAddress(et_addressR);
        userinfo.setEventDate(pwd);
        userinfo.setEventName(username);
        userinfo.setEventDes(et_desRef);
        DocumentReference newPostRef = userCollection.document();
        userinfo.setEventId(newPostRef.getId());


        userCollection.add(userinfo)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(EventDetailsAty.this, "Post created successfully!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(EventDetailsAty.this,
                        "Error creating Post!", Toast.LENGTH_SHORT).show());
    }
}
