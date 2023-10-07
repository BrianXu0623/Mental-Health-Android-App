package comp5216.sydney.edu.au.mentalhealth.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.entities.PostComment;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<PostComment> postComments;

    public CommentAdapter(List<PostComment> postComments) {
        this.postComments = postComments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        PostComment postComment = postComments.get(position);
        holder.authorNameTextView.setText(postComment.getUserId());
        holder.commentContentTextView.setText(postComment.getText());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String formattedTimestamp = dateFormat.format(postComment.getTimestamp().toDate());
        holder.commentTimestampTextView.setText(formattedTimestamp);
    }

    @Override
    public int getItemCount() {
        return postComments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView authorNameTextView;
        TextView commentContentTextView;
        TextView commentTimestampTextView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            authorNameTextView = itemView.findViewById(R.id.authorNameTextView);
            commentContentTextView = itemView.findViewById(R.id.commentContentTextView);
            commentTimestampTextView = itemView.findViewById(R.id.commentTimestampTextView);
        }
    }
    public List<PostComment> getComments() {
        return postComments;
    }

    public void setComments(List<PostComment> postComments) {
        this.postComments = postComments;
    }

}