package comp5216.sydney.edu.au.mentalhealth.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.adapters.CommentAdapter;
import comp5216.sydney.edu.au.mentalhealth.entities.CurUserInfo;
import comp5216.sydney.edu.au.mentalhealth.entities.PostComment;

public class PostDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView contentTextView;
    private TextView authorNameTextView;
    private ImageView authorAvatarImageView;
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private FirebaseFirestore db;
    private EditText commentEditText;
    private Button submitCommentButton;
    private TextView timestampTextView;
    private TextView deleteTextView;
    private ImageView authorDoctorIcon;
    private FirebaseStorage storage;
    String userId;
    public static ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        authorDoctorIcon = findViewById(R.id.authorDoctorIcon);
        titleTextView = findViewById(R.id.titleTextView);
        contentTextView = findViewById(R.id.contentTextView);
        authorNameTextView = findViewById(R.id.authorNameTextView);
        authorAvatarImageView = findViewById(R.id.authorAvatarImageView);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        db = FirebaseFirestore.getInstance();
        commentEditText = findViewById(R.id.commentEditText);
        submitCommentButton = findViewById(R.id.submitCommentButton);
        String currentUserId = CurUserInfo.userName;
        timestampTextView = findViewById(R.id.timestampPostDetailTextView);
        storage = FirebaseStorage.getInstance();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        commentAdapter = new CommentAdapter(new ArrayList<>());
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentAdapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String title = extras.getString("postTitle");
            String content = extras.getString("postContent");
            String postId = extras.getString("postId");
            userId = extras.getString("userId");
            String timestamp = extras.getString("timestamp");
            boolean isProfessional = extras.getBoolean("isProfessional");
            titleTextView.setText(title);
            contentTextView.setText(content);
            if (isProfessional) {
                authorDoctorIcon.setVisibility(View.VISIBLE);
            } else {
                authorDoctorIcon.setVisibility(View.GONE);
            }
            String authorName = userId;
            authorNameTextView.setText(authorName);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected() &&
                    networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                StorageReference image = storage.getReference().child(userId+".JPEG");
                File localFile = null;
                try {
                    localFile = File.createTempFile("images", "jpg");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                File finalLocalFile = localFile;
                image.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                    authorAvatarImageView.setImageBitmap(EditUserProfile.cropCircle(BitmapFactory.
                            decodeFile(finalLocalFile.getAbsolutePath())));
                }).addOnFailureListener(exception -> {
                });
            }
            else {
                int authorAvatarResId = getAuthorAvatar(userId);
                authorAvatarImageView.setImageResource(authorAvatarResId);
            }

            timestampTextView.setText(timestamp);
            loadComments(postId);
            deleteTextView = findViewById(R.id.deleteTextView);


            if (currentUserId != null && currentUserId.equals(userId)) {
                deleteTextView.setVisibility(View.VISIBLE);
                deleteTextView.setOnClickListener(new View.OnClickListener() {
                     @Override
                    public void onClick(View v) {
                         deletePostByField(postId);
                    }
                });
            } else {
                deleteTextView.setVisibility(View.GONE);
            }
            submitCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String commentText = commentEditText.getText().toString();
                    if (!commentText.isEmpty()) {
                        saveCommentToDatabase(postId, currentUserId, commentText);
                    }
                }
            });
        }
    }

    public static int getAuthorAvatar(String userId) {
        return R.drawable.default_avatar;
    }
    private void loadComments(String postId) {
        db.collection("comments")
                .whereEqualTo("postId", postId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<PostComment> commentsList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        PostComment comment = document.toObject(PostComment.class);
                        commentsList.add(comment);
                    }

                    Collections.sort(commentsList, (c1, c2) -> {
                        if (c1.isProfessional() && !c2.isProfessional()) return -1;
                        if (!c1.isProfessional() && c2.isProfessional()) return 1;
                        return c2.getTimestamp().compareTo(c1.getTimestamp());
                    });

                    commentAdapter.setComments(commentsList);
                    commentAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.w("PostDetailActivity", "Error getting comments.", e);
                });
    }



    private void deletePostByField(String postId) {
        db.collection("posts")
                .whereEqualTo("postId", postId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("posts").document(document.getId())
                                        .delete();
                                deleteCommentsByPostId(postId);
                                setResult(Activity.RESULT_OK);
                                finish();
                            }
                        } else {
                            Log.w("PostDetailActivity", "Error deleting post.",
                                    task.getException());
                        }
                    }
                });
    }

    private void deleteCommentsByPostId(String postId) {
        db.collection("comments")
                .whereEqualTo("postId", postId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("comments").document(document.getId())
                                        .delete();
                            }
                        } else {
                            Log.w("PostDetailActivity", "Error deleting comments.",
                                    task.getException());
                        }
                    }
                });
    }


    private void saveCommentToDatabase(String postId, String userId, String commentText) {
        CollectionReference commentsCollection = db.collection("comments");

        PostComment newComment = new PostComment(null, postId, userId, commentText,
                Timestamp.now());

        if (CurUserInfo.isProfessional) {
            newComment.setProfessional(true);
        }

        commentsCollection.add(newComment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        commentEditText.setText("");
                        newComment.setCommentId(documentReference.getId());
                        documentReference.set(newComment);
                        List<PostComment> currentComments = commentAdapter.getComments();
                        currentComments.add(newComment);
                        commentAdapter.setComments(currentComments);
                        commentAdapter.notifyDataSetChanged();
                        loadComments(postId);
                        Toast.makeText(PostDetailActivity.this,
                                "Comment added successfully", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostDetailActivity.this,
                                "Error adding comment!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void openUserProfile(View v){
        UserProfileActivity.UserProfileActivity(this,userId);
    }

    public void openCommentUserProfile(View view) {
        String userIdFromTag = (String) view.getTag();
        if (userIdFromTag != null) {
            UserProfileActivity.UserProfileActivity(this, userIdFromTag);
        }
    }

}
