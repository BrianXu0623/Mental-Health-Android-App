package comp5216.sydney.edu.au.mentalhealth.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.adapters.EventAdapter;
import comp5216.sydney.edu.au.mentalhealth.adapters.PostAdapter;
import comp5216.sydney.edu.au.mentalhealth.entities.CurUserInfo;
import comp5216.sydney.edu.au.mentalhealth.entities.Event;
import comp5216.sydney.edu.au.mentalhealth.entities.Post;

public class EventAty extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference userCollection;

    private RecyclerView rv_event;
    private EventAdapter adapter;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_event);

        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("event");

        rv_event = findViewById(R.id.rv_event);
        rv_event.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(this);
        rv_event.setAdapter(adapter);

        if (CurUserInfo.isProfessional) {
            findViewById(R.id.floatingActionButton).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.floatingActionButton).setVisibility(View.GONE);
        }

        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventAty.this, EventDetailsAty.class);
                startActivity(intent);
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_appointment) {
                Intent intent = new Intent(this, ProfessionalList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);  // Disable transition animations
                return true;
            } else if (itemId == R.id.nav_event) {
                Intent intent = new Intent(this, EventAty.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(this, EditUserProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_forum) {
                Intent intent = new Intent(this, MainActivity.class);  // Changed to EventAty
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);  // Disable transition animations
                return true;
            }

            return false;
        });

        bottomNavigationView.getMenu().findItem(R.id.nav_event).setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        userCollection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> posts = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Event post = document.toObject(Event.class);
                        posts.add(post);
                    }

                    adapter.setEvent(posts);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                });
    }
}
