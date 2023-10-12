package comp5216.sydney.edu.au.mentalhealth.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.adapters.CommentAdapter;
import comp5216.sydney.edu.au.mentalhealth.entities.CurUserInfo;
import comp5216.sydney.edu.au.mentalhealth.entities.PostComment;
import comp5216.sydney.edu.au.mentalhealth.entities.UserProfile;

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

        // 设置个空的适配器
        commentAdapter = new CommentAdapter(new ArrayList<>());
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentAdapter);

        // 获取传递的数据
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String title = extras.getString("postTitle");
            String content = extras.getString("postContent");
            String postId = extras.getString("postId");
            userId = extras.getString("userId");
            String timestamp = extras.getString("timestamp");
            boolean isProfessional = extras.getBoolean("isProfessional");
            // 设置帖子详细信息
            titleTextView.setText(title);
            contentTextView.setText(content);
            // 判断是否显示专家图标
            if (isProfessional) {
                authorDoctorIcon.setVisibility(View.VISIBLE);
            } else {
                authorDoctorIcon.setVisibility(View.GONE);
            }
            // 从数据库中获取作者名字和头像（示例，实际中需要替换为真实的数据库查询）
            String authorName = userId; // 替换为实际的查询用户名字的方法
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

                    // 对评论列表进行排序：先按isProfessional降序排列，然后按timestamp降序排列
                    Collections.sort(commentsList, (c1, c2) -> {
                        if (c1.isProfessional() && !c2.isProfessional()) return -1;
                        if (!c1.isProfessional() && c2.isProfessional()) return 1;
                        return c2.getTimestamp().compareTo(c1.getTimestamp());
                    });

                    // 更新RecyclerView的Adapter数据
                    commentAdapter.setComments(commentsList);
                    commentAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.w("PostDetailActivity", "Error getting comments.", e);
                });
    }



    private void deletePostByField(String postId) {
        // 先找到帖子
        db.collection("posts")
                .whereEqualTo("postId", postId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // 删除找到的帖子
                                db.collection("posts").document(document.getId()).delete();
                                // 同时删除与该帖子关联的所有评论
                                deleteCommentsByPostId(postId);
                                setResult(Activity.RESULT_OK);
                                finish();
                            }
                        } else {
                            Log.w("PostDetailActivity", "Error deleting post.", task.getException());
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
                                db.collection("comments").document(document.getId()).delete();
                            }
                        } else {
                            Log.w("PostDetailActivity", "Error deleting comments.", task.getException());
                        }
                    }
                });
    }


    private void saveCommentToDatabase(String postId, String userId, String commentText) {
        CollectionReference commentsCollection = db.collection("comments");

        PostComment newComment = new PostComment(null, postId, userId, commentText, Timestamp.now());

        // 设置评论者是否是专家
        if (CurUserInfo.isProfessional) {
            newComment.setProfessional(true);
        }

        commentsCollection.add(newComment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        commentEditText.setText("");  // 清除EditText的内容
                        newComment.setCommentId(documentReference.getId());
                        documentReference.set(newComment);
                        // 添加新评论到评论列表并更新UI
                        List<PostComment> currentComments = commentAdapter.getComments(); // 获取当前评论列表
                        currentComments.add(newComment);
                        commentAdapter.setComments(currentComments);  // 更新评论列表
                        commentAdapter.notifyDataSetChanged();  // 通知数据已改变，从而更新UI
                        loadComments(postId);
                        Toast.makeText(PostDetailActivity.this, "Comment added successfully", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostDetailActivity.this, "Error adding comment!", Toast.LENGTH_SHORT).show();
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
