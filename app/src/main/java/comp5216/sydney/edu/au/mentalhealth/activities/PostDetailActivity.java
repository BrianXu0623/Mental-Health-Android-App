package comp5216.sydney.edu.au.mentalhealth.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        titleTextView = findViewById(R.id.titleTextView);
        contentTextView = findViewById(R.id.contentTextView);
        authorNameTextView = findViewById(R.id.authorNameTextView);
        authorAvatarImageView = findViewById(R.id.authorAvatarImageView);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);

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
        return new ArrayList<>(); // 示例数据
    }
}
