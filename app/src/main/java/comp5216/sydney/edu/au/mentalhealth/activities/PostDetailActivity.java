package comp5216.sydney.edu.au.mentalhealth.activities;

import android.app.Activity;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.adapters.CommentAdapter;
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
    private Timestamp timestamp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        titleTextView = findViewById(R.id.titleTextView);
        contentTextView = findViewById(R.id.contentTextView);
        authorNameTextView = findViewById(R.id.authorNameTextView);
        authorAvatarImageView = findViewById(R.id.authorAvatarImageView);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        db = FirebaseFirestore.getInstance();
        commentEditText = findViewById(R.id.commentEditText);
        submitCommentButton = findViewById(R.id.submitCommentButton);
        String currentUserId = getCurrentUserId();

        // 获取传递的数据
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String title = extras.getString("postTitle");
            String content = extras.getString("postContent");
            String postId = extras.getString("postId");
            String userId = extras.getString("userId");
            String timestamp = extras.getString("timestamp");

            // 设置帖子详细信息
            titleTextView.setText(title);
            contentTextView.setText(content);

            // 从数据库中获取作者名字和头像（示例，实际中需要替换为真实的数据库查询）
            String authorName = getAuthorName(userId); // 替换为实际的查询用户名字的方法
            int authorAvatarResId = getAuthorAvatar(userId); // 替换为实际的查询用户头像的方法

            authorNameTextView.setText(authorName);
            authorAvatarImageView.setImageResource(authorAvatarResId);

            // 从数据库中获取该帖子的所有评论（示例，实际中需要替换为真实的数据库查询）
            List<PostComment> comments = getCommentsForPost(postId); // 替换为实际的查询评论的方法
            commentAdapter = new CommentAdapter(comments);
            commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            commentsRecyclerView.setAdapter(commentAdapter);

            Button deleteButton = findViewById(R.id.deleteButton); // 先确保你的activity_post_detail.xml中有一个ID为deleteButton的Button，并默认设置为不可见（android:visibility="gone"）

            if (currentUserId != null && currentUserId.equals(userId)) {
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletePost(postId);
                    }
                });
            } else {
                deleteButton.setVisibility(View.GONE);
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

    // 从数据库中获取作者名字（示例，实际中需要替换为真实的数据库查询）
    private String getAuthorName(String userId) {
        // 查询数据库获取作者名字的逻辑
        return "Author Name"; // 示例数据
    }

    // 从数据库中获取作者头像（示例，实际中需要替换为真实的数据库查询）
    private int getAuthorAvatar(String userId) {
        // 查询数据库获取作者头像的逻辑
        return R.drawable.default_avatar; // 示例数据
    }

    // 从数据库中获取该帖子的所有评论（示例，实际中需要替换为真实的数据库查询）
    private List<PostComment> getCommentsForPost(String postId) {
        // 查询数据库获取帖子评论的逻辑
        List<PostComment> commentsList = new ArrayList<>();

        CollectionReference commentsRef = db.collection("posts").document(postId).collection("comments");

        // Order the comments by timestamp
        Query query = commentsRef.orderBy("timestamp", Query.Direction.ASCENDING);
        try {
            QuerySnapshot querySnapshot = Tasks.await(query.get());
            for (QueryDocumentSnapshot document : querySnapshot) {
                PostComment comment = document.toObject(PostComment.class);
                commentsList.add(comment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commentsList;
    }
    // 得到查看帖子当前用户的id
    private String getCurrentUserId() {
        //Todo:根据用户身份验证系统来实现
        return "sampleUserId";
    }
    private void deletePost(String postId) {
        db.collection("posts").document(postId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setResult(Activity.RESULT_OK);
                        finish();  // 关闭此Activity并返回到前一个Activity
                        Log.d("PostDetailActivity", "Post with ID: " + postId + " deleted successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostDetailActivity.this, "Error deleting post!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void saveCommentToDatabase(String postId, String userId, String commentText) {
        CollectionReference commentsCollection = db.collection("posts").document(postId).collection("comments");

        PostComment newComment = new PostComment(null, postId, userId, commentText, Timestamp.now());

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

}
