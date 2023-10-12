package comp5216.sydney.edu.au.mentalhealth.activities;

import static androidx.core.content.ContextCompat.startActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firestore.v1.Document;

import java.io.File;
import java.io.IOException;

import comp5216.sydney.edu.au.mentalhealth.R;

public class UserProfileActivity extends AppCompatActivity {

    ImageView userImage;
    TextView userName;
    ImageView docIcon;

    TextView phone;

    TextView email;
    TextView hobbies;
    TextView info;
    TextView hideInfo;

    String username;


    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private static final String TAG = "UserProfile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        username = getIntent().getStringExtra("userId");

        userImage = findViewById(R.id.user_image);
        docIcon = findViewById(R.id.doctor_icon);
        userName = findViewById(R.id.user_name);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        hobbies = findViewById(R.id.hobbies);
        info = findViewById(R.id.info);
        hideInfo = findViewById(R.id.hide_info);


        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        DocumentReference userRef = db.collection("UserProfiles").document(username);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userName.setText(document.getString("userName"));
                        if(document.getBoolean("doc")){
                            docIcon.setVisibility(View.VISIBLE);
                        }else {
                            docIcon.setVisibility(View.GONE);
                        }


                        if(document.getBoolean("hidden")){
                            phone.setVisibility(View.GONE);
                            email.setVisibility(View.GONE);
                            hobbies.setVisibility(View.GONE);
                            info.setVisibility(View.GONE);
                            hideInfo.setVisibility(View.VISIBLE);

                        }else{
                            hideInfo.setVisibility(View.GONE);
                            phone.setVisibility(View.VISIBLE);
                            email.setVisibility(View.VISIBLE);
                            hobbies.setVisibility(View.VISIBLE);
                            info.setVisibility(View.VISIBLE);
                            phone.setText(document.getString("phone"));
                            email.setText(document.getString("email"));
                            hobbies.setText(document.getString("hobbies"));
                            info.setText(document.getString("info"));

                        }



                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                        Log.d(TAG, username);
                        EditUserProfile.createUserprofile(username);

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        loadUser();

        loadImage();





    }

    public void loadImage(){
        StorageReference image = storage.getReference().child(username+".JPEG");
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File finalLocalFile = localFile;
        image.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            userImage.setImageBitmap(EditUserProfile.cropCircle(BitmapFactory.
                    decodeFile(finalLocalFile.getAbsolutePath())));
        }).addOnFailureListener(exception -> {
        });
    }

    public static void UserProfileActivity(AppCompatActivity activity, String userId){
        Intent userProfileIntent = new Intent(activity, UserProfileActivity.class);
        userProfileIntent.putExtra("userId", userId);
        activity.startActivity(userProfileIntent);

    }

    public void loadUser(){
        DocumentReference userRef = db.collection("UserProfiles").document(username);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userName.setText(document.getString("userName"));
                        if(document.getBoolean("doc")){
                            docIcon.setVisibility(View.VISIBLE);
                        }else {
                            docIcon.setVisibility(View.GONE);
                        }


                        if(document.getBoolean("hidden")){
                            phone.setVisibility(View.GONE);
                            email.setVisibility(View.GONE);
                            hobbies.setVisibility(View.GONE);
                            info.setVisibility(View.GONE);
                            hideInfo.setVisibility(View.VISIBLE);

                        }else{
                            hideInfo.setVisibility(View.GONE);
                            phone.setVisibility(View.VISIBLE);
                            email.setVisibility(View.VISIBLE);
                            hobbies.setVisibility(View.VISIBLE);
                            info.setVisibility(View.VISIBLE);
                            phone.setText(document.getString("phone"));
                            email.setText(document.getString("email"));
                            hobbies.setText(document.getString("hobbies"));
                            info.setText(document.getString("info"));

                        }



                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                        Log.d(TAG, username);
                        EditUserProfile.createUserprofile(username);
                        loadUser();

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        loadImage();
    }





}