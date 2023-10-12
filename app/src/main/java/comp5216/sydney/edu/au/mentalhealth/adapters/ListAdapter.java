package comp5216.sydney.edu.au.mentalhealth.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.entities.ListItem;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<ListItem> dataList;
    private OnBookButtonClickListener listener;
    public OnDelButtonClickListener dellistener;

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
        //holder.icon.setImageResource(item.getIconResId());
        holder.title.setText(item.getTitle());
        holder.subtitle.setText(item.getSubtitle());
        holder.button.setOnClickListener(v -> {
            // Handle button click event
            if (listener != null) {
                listener.onBookButtonClick(position);
            }
        });
        holder.delBtn.setOnClickListener(v -> {
            // Handle button click event
            if (dellistener != null) {
                dellistener.onDelButtonClick(position);
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
        Button button,delBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            button = itemView.findViewById(R.id.button);
            delBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }

    public interface OnBookButtonClickListener {
        void onBookButtonClick(int position);
    }
    public interface OnDelButtonClickListener {
        void onDelButtonClick(int position);
    }

    public void setOnBookButtonClickListener(OnBookButtonClickListener listener) {
        this.listener = listener;
    }
}

