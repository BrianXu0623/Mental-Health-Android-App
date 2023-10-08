package comp5216.sydney.edu.au.mentalhealth.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.entities.UserProfile;

public class EditUserProfile extends AppCompatActivity {


    ImageView userProfileImage;
    EditText userProfileName;
    ImageView docIcon;
    boolean doc;
    boolean hide;
    TextView userProfileId;
    EditText userProfileAge;
    EditText userProfileEmail;
    EditText userProfileHobbies;
    EditText getUserProfileMajor;

    EditText getUserProfileDes;

    Switch hiddenSwitch;

    private FirebaseFirestore db;
    private static final String TAG = "EditUserProfile";
    private static final int PHOTO_PICK_REQUEST_CODE = 101;

    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        userId = getIntent().getStringExtra("userId");

        userProfileId = findViewById(R.id.user_profile_id);
        userProfileImage = findViewById(R.id.user_profile_image);
        docIcon = findViewById(R.id.doctor_icon);
        userProfileName = findViewById(R.id.user_profile_name);
        userProfileAge = findViewById(R.id.user_age);
        userProfileEmail = findViewById(R.id.user_email);
        userProfileHobbies = findViewById(R.id.user_hobbies);
        getUserProfileMajor = findViewById(R.id.user_major);
        getUserProfileDes = findViewById(R.id.user_description);
        hiddenSwitch = findViewById(R.id.hidden_switch);

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
                            doc = true;
                        }else {
                            docIcon.setVisibility(View.GONE);
                            doc = false;
                        }
                        userProfileId.setText(userId);

                        userProfileAge.setText(document.getString("userBirth"));
                        userProfileEmail.setText(document.getString("userEmail"));
                        userProfileHobbies.setText(document.getString("userHobbies"));
                        getUserProfileMajor.setText(document.getString("userMajor"));
                        getUserProfileDes.setText(document.getString("userDes"));

                        if(document.getBoolean("hidden")){
                            hiddenSwitch.setChecked(true);

                        }else {
                            hiddenSwitch.setChecked(false);
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

    public void saveButton(View v){
        DocumentReference userRef = db.collection("UserProfiles").document(userId);
        UserProfile profile = new UserProfile(userId, userProfileName.getText().toString(),doc, hiddenSwitch.isChecked(), userProfileEmail.getText().toString(),
                userProfileHobbies.getText().toString(),
                getUserProfileMajor.getText().toString(),
                getUserProfileDes.getText().toString());
        userRef.set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Intent intent = new Intent(EditUserProfile.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }

    // onActivityResult() handles callbacks from the photo picker.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == PHOTO_PICK_REQUEST_CODE){
            Uri currentUri = data.getData();




        }
    }




    public void pickPic(View v){
        // Launches photo picker in single-select mode.
        // This means that the user can select one photo or video.
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_PICK_REQUEST_CODE);
    }


}