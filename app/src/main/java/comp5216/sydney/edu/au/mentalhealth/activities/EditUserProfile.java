package comp5216.sydney.edu.au.mentalhealth.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;


import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.entities.CurUserInfo;
import comp5216.sydney.edu.au.mentalhealth.entities.UserProfile;

public class EditUserProfile extends AppCompatActivity {


    ImageView userImage;
    String userName;
    TextView userNameText;
    ImageView docIcon;
    boolean doc;
    boolean hide;
    TextInputEditText phone;
    TextInputEditText email;
    TextInputEditText hobbies;
    TextInputEditText info;

    Switch hiddenSwitch;
    BottomNavigationView bottomNavigationView;
    FirebaseFirestore db;
    FirebaseStorage storage;
    private static final String TAG = "EditUserProfile";
    private static final int PHOTO_PICK_REQUEST_CODE = 101;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        userName = CurUserInfo.userName;
        userImage = findViewById(R.id.user_image);
        userNameText = findViewById(R.id.user_name);
        docIcon = findViewById(R.id.doctor_icon);
        phone = findViewById(R.id.phoneText);
        email = findViewById(R.id.emailText);
        hobbies = findViewById(R.id.hobbiesText);
        info = findViewById(R.id.infoText);

        hiddenSwitch = findViewById(R.id.hidden_switch);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_appointment) {
                Intent intent = new Intent(this, ProfessionalList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_event) {
                Intent intent = new Intent(this, EventAty.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if(itemId == R.id.nav_profile) {
                Intent intent = new Intent(this, EditUserProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if(itemId == R.id.nav_forum) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            bottomNavigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
            return false;
        });


        loaduser();
        loadImage();



    }

    public void saveButton(View v){
        DocumentReference userRef = db.collection("UserProfiles").document(userName);
        UserProfile profile = new UserProfile(userName, doc, hiddenSwitch.isChecked(),phone
                .getText().toString(),
                email.getText().toString(),hobbies.getText().toString(),info.getText().toString());
        userRef.set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast toast = Toast.makeText(EditUserProfile.this, "Update success", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
        loadImage();

    }

    private Bitmap getBitmapFromImageUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
    }

    // onActivityResult() handles callbacks from the photo picker.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == PHOTO_PICK_REQUEST_CODE){
            Uri file = data.getData();
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child(CurUserInfo.userName+".JPEG");
            Bitmap bitmap = getBitmapFromImageUri(file);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            byte[] bytes = stream.toByteArray();
            UploadTask uploadTask = imageRef.putBytes(bytes);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(EditUserProfile.this,
                            "Upload successful!", Toast.LENGTH_SHORT).show();

                    loadImage();
                }
            });

            loadImage();

        }
    }

    public void pickPic(View v){
        // Launches photo picker in single-select mode.
        // This means that the user can select one photo or video.
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_PICK_REQUEST_CODE);
    }

    public void loaduser(){
        DocumentReference userRef = db.collection("UserProfiles").document(userName);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userNameText.setText(userName);
                        if(CurUserInfo.isProfessional){
                            docIcon.setVisibility(View.VISIBLE);
                            doc = true;
                        }else {
                            docIcon.setVisibility(View.GONE);
                            doc = false;
                        }
                        phone.setText(document.getString("phone"));
                        email.setText(document.getString("email"));
                        hobbies.setText(document.getString("hobbies"));
                        info.setText(document.getString("info"));

                        if(document.getBoolean("hidden")){
                            hiddenSwitch.setChecked(true);

                        }else {
                            hiddenSwitch.setChecked(false);
                        }

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                        Log.d(TAG, userName);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

    public void loadImage(){
        StorageReference image = storage.getReference().child(userName+".JPEG");
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File finalLocalFile = localFile;
        image.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            // Local temp file has been created
            userImage.setImageBitmap(cropCircle(BitmapFactory.decodeFile(
                    finalLocalFile.getAbsolutePath())));
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


    }

    public static Bitmap cropCircle(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = Math.min(width, height);
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        float radius = size / 2f;
        canvas.drawCircle(radius, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Rect rectSrc = new Rect((width - size) / 2, (height - size) / 2,
                (width + size) / 2, (height + size) / 2);
        Rect rectDst = new Rect(0, 0, size, size);
        canvas.drawBitmap(bitmap, rectSrc, rectDst, paint);
        return output;
    }

    public static void createUserprofile(String userName){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("UserProfiles").document(userName);
        UserProfile profile = new UserProfile(userName, false, false,"",
                "", "","");
        userRef.set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}