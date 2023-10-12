package comp5216.sydney.edu.au.mentalhealth.adapters;

import static comp5216.sydney.edu.au.mentalhealth.activities.PostDetailActivity.connectivityManager;
import static comp5216.sydney.edu.au.mentalhealth.activities.PostDetailActivity.getAuthorAvatar;

import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.activities.EditUserProfile;
import comp5216.sydney.edu.au.mentalhealth.entities.PostComment;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<PostComment> postComments;
    private FirebaseStorage storage;
    public CommentAdapter(List<PostComment> postComments) {
        this.postComments = postComments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        storage = FirebaseStorage.getInstance();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent,
                false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        PostComment postComment = postComments.get(position);
        holder.authorNameTextView.setText(postComment.getUserId());
        holder.commentContentTextView.setText(postComment.getText());
        if (postComment.isProfessional()) {
            holder.commentDoctorIcon.setVisibility(View.VISIBLE);
        } else {
            holder.commentDoctorIcon.setVisibility(View.GONE);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String formattedTimestamp = dateFormat.format(postComment.getTimestamp().toDate());
        holder.commentTimestampTextView.setText(formattedTimestamp);


        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() &&
                networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            StorageReference image = storage.getReference().child(postComment.getUserId() + ".JPEG");
            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            File finalLocalFile = localFile;
            image.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                holder.authorAvatarImageView.setImageBitmap(EditUserProfile.cropCircle(BitmapFactory.
                        decodeFile(finalLocalFile.getAbsolutePath())));
            }).addOnFailureListener(exception -> {
            });
        }
        else {
            int authorAvatarResId = getAuthorAvatar(postComment.getUserId());
            holder.authorAvatarImageView.setImageResource(authorAvatarResId);
        }
    }

    @Override
    public int getItemCount() {
        return postComments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView authorNameTextView;
        TextView commentContentTextView;
        TextView commentTimestampTextView;
        ImageView authorAvatarImageView;
        ImageView commentDoctorIcon;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            authorNameTextView = itemView.findViewById(R.id.commentAuthorNameTextView);
            commentContentTextView = itemView.findViewById(R.id.commentContentTextView);
            commentTimestampTextView = itemView.findViewById(R.id.commentTimestampTextView);
            authorAvatarImageView = itemView.findViewById(R.id.commentAuthorAvatarImageView);
            commentDoctorIcon = itemView.findViewById(R.id.commentDoctorIcon);
        }
    }
    public List<PostComment> getComments() {
        return postComments;
    }

    public void setComments(List<PostComment> postComments) {
        this.postComments = postComments;
    }
}
