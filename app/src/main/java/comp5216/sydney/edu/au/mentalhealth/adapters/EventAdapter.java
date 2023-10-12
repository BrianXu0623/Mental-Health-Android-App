package comp5216.sydney.edu.au.mentalhealth.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.activities.EditEventDetailsAty;
import comp5216.sydney.edu.au.mentalhealth.activities.EventQueryDetailsAty;
import comp5216.sydney.edu.au.mentalhealth.entities.CurUserInfo;
import comp5216.sydney.edu.au.mentalhealth.entities.Event;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.PostViewHolder> {

    private List<Event> posts = new ArrayList<>();
    private Context context;


    public EventAdapter(Context context) {
        this.context = context;
    }

    public void setEvent(List<Event> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Event post = posts.get(position);
        holder.titleTextView.setText(post.getEventName());
        holder.contentTextView.setText(post.getEventDate());
        holder.participantCountTextView.setText(post.getParticipantsCount() + " participants");
        holder.itemView.setOnClickListener(view -> {
            if (CurUserInfo.isProfessional) {
                Intent intent = new Intent(context, EditEventDetailsAty.class);
                intent.putExtra("eventId", post.getEventId());
                intent.putExtra("eventName", post.getEventName());
                intent.putExtra("eventAddress", post.getEventAddress());
                intent.putExtra("eventDate", post.getEventDate());
                intent.putExtra("eventDes", post.getEventDes());
                context.startActivity(intent);
            }else {
                Intent intent = new Intent(context, EventQueryDetailsAty.class);
                intent.putExtra("eventId", post.getEventId());
                intent.putExtra("eventName", post.getEventName());
                intent.putExtra("eventAddress", post.getEventAddress());
                intent.putExtra("eventDate", post.getEventDate());
                intent.putExtra("eventDes", post.getEventDes());
                context.startActivity(intent);
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
        TextView participantCountTextView;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            participantCountTextView = itemView.findViewById(R.id.participantCountTextView);
        }
    }
}
