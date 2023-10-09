package comp5216.sydney.edu.au.mentalhealth.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.entities.CurUserInfo;
import comp5216.sydney.edu.au.mentalhealth.entities.Post;

public class CreatePostActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText contentEditText;
    private Button submitButton;
    private FirebaseFirestore db;
    private CollectionReference postsCollection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        submitButton = findViewById(R.id.submitButton);
        db = FirebaseFirestore.getInstance();
        postsCollection = db.collection("posts");
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewPost();
            }
        });

    }
    private void createNewPost() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        // 基本的验证
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Title and content cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        String currentUserId = CurUserInfo.userId;
        Post newPost = new Post(currentUserId, title, content);
        if(CurUserInfo.isProfessional) {
            newPost.setProfessional(true);
        }

        postsCollection.add(newPost)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CreatePostActivity.this, "Post created successfully!", Toast.LENGTH_SHORT).show();
                        finish();  // 关闭此Activity并返回到前一个Activity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreatePostActivity.this, "Error creating post!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}