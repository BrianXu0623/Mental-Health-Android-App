package comp5216.sydney.edu.au.mentalhealth.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import comp5216.sydney.edu.au.mentalhealth.R;

public class EditEventDetailsAty extends AppCompatActivity {

    private TextInputEditText etLoginUserName;
    private TextInputEditText regUserPwd;
    private TextInputEditText et_address;
    private EditText et_des;
    private Button loginBtn;

    private FirebaseFirestore db;
    private CollectionReference userCollection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_edit_event_details);
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("event");
        etLoginUserName = findViewById(R.id.username);
        regUserPwd = findViewById(R.id.date);
        et_address = findViewById(R.id.address);
        et_des = findViewById(R.id.des);

        etLoginUserName.setText(getIntent().getStringExtra("eventName"));
        regUserPwd.setText(getIntent().getStringExtra("eventDate"));
        et_address.setText(getIntent().getStringExtra("eventAddress"));
        et_des.setText(getIntent().getStringExtra("eventDes"));

        loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etLoginUserName.getText())
                    && !TextUtils.isEmpty(regUserPwd.getText())) {
                post();
            } else {
                Toast.makeText(this, "Please Enter Event Relative Information",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post() {
        String username = etLoginUserName.getText().toString().trim();
        String pwd = regUserPwd.getText().toString().trim();
        String et_desRef = et_des.getText().toString().trim();
        String et_addressR = et_address.getText().toString().trim();

        userCollection
                .whereEqualTo("eventId", getIntent().getStringExtra("eventId"))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        Map<String,Object> map =new HashMap<>();
                        map.put("eventName",username);
                        map.put("eventDes",et_desRef);
                        map.put("eventDate",pwd);
                        map.put("eventAddress",et_addressR);
                        userCollection.document(document.getId()).update(map);
                        finish();
                    }

                })
                .addOnFailureListener(e -> {
                });
    }
}
