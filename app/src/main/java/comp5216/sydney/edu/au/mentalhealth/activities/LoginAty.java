package comp5216.sydney.edu.au.mentalhealth.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.entities.CurUserInfo;
import comp5216.sydney.edu.au.mentalhealth.entities.Userinfo;

public class LoginAty extends AppCompatActivity {

    private EditText etLoginUserName;
    private EditText regUserPwd;

    private Button loginBtn;
    private TextView txtRegister;

    private FirebaseFirestore db;
    private CollectionReference userCollection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_login);
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("userinfo");

        etLoginUserName = findViewById(R.id.username);
        regUserPwd = findViewById(R.id.pwd);

        loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etLoginUserName.getText())
                    && !TextUtils.isEmpty(regUserPwd.getText())) {
                login();
            } else {
                Toast.makeText(this, "Please Enter Username", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        txtRegister = findViewById(R.id.txt_login_register);
        txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginAty.this, RegisterAty.class);
            startActivity(intent);
        });
    }

    private void login() {

        try {
            String password = MyUtils.encrypt(regUserPwd.getText().toString().trim());
            Query query = userCollection
                    .whereEqualTo("pwd", password)
                    .whereEqualTo("userName", etLoginUserName.getText().toString().trim());


            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    boolean isLogin = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Userinfo userinfo = document.toObject(Userinfo.class);
                        if (!TextUtils.isEmpty(userinfo.getUserName())) {
                            CurUserInfo.userName = userinfo.getUserName();
                            CurUserInfo.isProfessional = userinfo.getRole().equals("professional");
                            isLogin = true;
                        }
                    }

                    if (isLogin) {
                        Intent intent = new Intent(LoginAty.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginAty.this, "Login Failed",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.e("fk", "Error login.", task.getException());
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
