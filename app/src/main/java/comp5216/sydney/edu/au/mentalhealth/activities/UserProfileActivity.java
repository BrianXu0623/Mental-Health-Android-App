package comp5216.sydney.edu.au.mentalhealth.activities;

import static androidx.core.content.ContextCompat.startActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.Document;

import comp5216.sydney.edu.au.mentalhealth.R;

public class UserProfileActivity extends AppCompatActivity {

    ImageView userProfileImage;
    TextView userProfileName;
    ImageView docIcon;

    TextView userProfileAge;
    TextView userProfileEmail;
    TextView userProfileHobbies;
    TextView getUserProfileMajor;

    TextView getUserProfileDes;

    private FirebaseFirestore db;
    private static final String TAG = "UserProfile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // 获取传入的用户ID
        String userId = getIntent().getStringExtra("userId");

        // 初始化UI组件
        userProfileImage = findViewById(R.id.user_profile_image);
        docIcon = findViewById(R.id.doctor_icon);
        userProfileName = findViewById(R.id.user_profile_name);
        userProfileAge = findViewById(R.id.user_age);
        userProfileEmail = findViewById(R.id.user_email);
        userProfileHobbies = findViewById(R.id.user_hobbies);
        getUserProfileMajor = findViewById(R.id.user_major);
        getUserProfileDes = findViewById(R.id.user_description);

        db = FirebaseFirestore.getInstance();

        DocumentReference userRef = db.collection("UserProfiles").document(userId);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userProfileName.setText(document.getString("userName"));
                        if(document.getBoolean("doc")){
                            docIcon.setVisibility(View.VISIBLE);
                        }else {
                            docIcon.setVisibility(View.GONE);
                        }

                        if(document.getBoolean("hidden")){
                            userProfileAge.setVisibility(View.GONE);
                            userProfileEmail.setVisibility(View.GONE);
                            userProfileHobbies.setVisibility(View.GONE);
                            getUserProfileMajor.setVisibility(View.GONE);
                            getUserProfileDes.setVisibility(View.GONE);
                        }else{
                            userProfileAge.setVisibility(View.VISIBLE);
                            userProfileEmail.setVisibility(View.VISIBLE);
                            userProfileHobbies.setVisibility(View.VISIBLE);
                            getUserProfileMajor.setVisibility(View.VISIBLE);
                            getUserProfileDes.setVisibility(View.VISIBLE);
                            userProfileAge.setText(document.getString("userBirth"));
                            userProfileEmail.setText(document.getString("userEmail"));
                            userProfileHobbies.setText(document.getString("userHobbies"));
                            getUserProfileMajor.setText(document.getString("userMajor"));
                            getUserProfileDes.setText(document.getString("userDes"));
                        }



                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                        Log.d(TAG, userId);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });





    }

    public static void UserProfileActivity(AppCompatActivity activity, String userId){
        Intent userProfileIntent = new Intent(activity, UserProfileActivity.class);
        userProfileIntent.putExtra("userId", userId);
        activity.startActivity(userProfileIntent);

    }





}