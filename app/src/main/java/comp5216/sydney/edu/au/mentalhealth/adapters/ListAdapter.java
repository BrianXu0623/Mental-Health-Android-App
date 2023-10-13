package comp5216.sydney.edu.au.mentalhealth.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.entities.ListItem;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<ListItem> dataList;
    private OnBookButtonClickListener listener;

    public ListAdapter(List<ListItem> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListItem item = dataList.get(position);

        // Reference to Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child(item.getAvatarUrl()); // 使用新的 avatarUrl 属性

        // Load the avatar into the ImageView
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Uri downloadUrl = uri;
            Glide.with(holder.icon.getContext()).load(downloadUrl).into(holder.icon);
        }).addOnFailureListener(exception -> {
        });

        holder.title.setText(item.getTitle());
        holder.subtitle.setText(item.getSubtitle());
        holder.button.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookButtonClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setDataList(List<ListItem> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView subtitle;
        Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            button = itemView.findViewById(R.id.button);
        }
    }

    public interface OnBookButtonClickListener {
        void onBookButtonClick(int position);
    }

    public void setOnBookButtonClickListener(OnBookButtonClickListener listener) {
        this.listener = listener;
    }
}

