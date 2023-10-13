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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.entities.CurUserInfo;
import comp5216.sydney.edu.au.mentalhealth.entities.Post;
import comp5216.sydney.edu.au.mentalhealth.entities.PostComment;
import comp5216.sydney.edu.au.mentalhealth.entities.Userinfo;

public class RegisterAty extends AppCompatActivity {

    private TextInputEditText regUserName;
    private TextInputEditText regUserPwd;
    private TextInputEditText regUserPwdAgain;
    private Button btnRegister;

    private FirebaseFirestore db;
    private CollectionReference userCollection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_register);
        regUserName = findViewById(R.id.username);
        regUserPwd = findViewById(R.id.pwd);
        regUserPwdAgain = findViewById(R.id.pwd_again);

        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("userinfo");

        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(regUserName.getText())
                    && !TextUtils.isEmpty(regUserName.getText())) {
                checkName();
            } else {
                Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void register() {
        String username = regUserName.getText().toString().trim();
        String pwd = regUserPwd.getText().toString().trim();
        String pwdAgain = regUserPwdAgain.getText().toString().trim();

        if(! pwdAgain.equals(pwd)) {
            Toast.makeText(RegisterAty.this,
                    "Two passwords are not consistent!", Toast.LENGTH_SHORT).show();
            return;
        }
        Userinfo userinfo = new Userinfo();
        userinfo.setUserName(username);

        if(! MyUtils.validatePassword(pwd)) {
            Toast toast = Toast.makeText(RegisterAty.this,
                    "Incorrect Password Format", Toast.LENGTH_LONG);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        try {
            userinfo.setPwd(MyUtils.encrypt(pwd));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DocumentReference newPostRef = userCollection.document();
        userinfo.setUserId(newPostRef.getId());

        userCollection.add(userinfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(RegisterAty.this,
                                "Register created successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterAty.this,
                                "Error creating Register!", Toast.LENGTH_SHORT).show();
                    }
                });


        // create new user profile
        EditUserProfile.createUserprofile(username);
    }


    public void checkName(){
        String name = regUserName.getText().toString().trim();
        userCollection.whereEqualTo("userName", name).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(queryDocumentSnapshots.isEmpty()){
                register();
            }else {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            }
            });
    }
}
