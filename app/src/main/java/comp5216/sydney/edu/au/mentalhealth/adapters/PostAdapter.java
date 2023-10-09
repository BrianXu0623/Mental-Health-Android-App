package comp5216.sydney.edu.au.mentalhealth.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.activities.PostDetailActivity;
import comp5216.sydney.edu.au.mentalhealth.entities.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts = new ArrayList<>();
    private Context context;
    private static final int REQUEST_CODE_POST_DETAIL = 1234;


    public PostAdapter(Context context) {
        this.context = context;
    }

    public void setPosts(List<Post> posts) {
        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post post1, Post post2) {
                return post2.getTimestamp().compareTo(post1.getTimestamp());
            }
        });
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        // 如果帖子是专业用户发布的，显示医生图标
        if (post.isProfessional()) {
            holder.doctorIcon.setVisibility(View.VISIBLE);
        } else {
            holder.doctorIcon.setVisibility(View.GONE);
        }
        holder.titleTextView.setText(post.getTitle());
        holder.contentTextView.setText(post.getContent());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String formattedTimestamp = dateFormat.format(post.getTimestamp().toDate());
        holder.timestampTextView.setText(formattedTimestamp);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postTitle", post.getTitle());
                intent.putExtra("postContent", post.getContent());
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("userId", post.getUserId());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String formattedTimestamp = dateFormat.format(post.getTimestamp().toDate());
                intent.putExtra("timestamp", formattedTimestamp);
                intent.putExtra("isProfessional",post.isProfessional());

                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, REQUEST_CODE_POST_DETAIL);
                } else {
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        TextView timestampTextView;
        ImageView doctorIcon;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            doctorIcon = itemView.findViewById(R.id.doctor_picture);
        }
    }
}
