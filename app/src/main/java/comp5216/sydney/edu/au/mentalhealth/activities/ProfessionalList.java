package comp5216.sydney.edu.au.mentalhealth.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.adapters.ListAdapter;
import comp5216.sydney.edu.au.mentalhealth.entities.ListItem;

public class ProfessionalList extends AppCompatActivity implements ListAdapter.OnBookButtonClickListener {

    private FirebaseFirestore db;
    private CollectionReference professionalsCollection;
    private List<ListItem> dataList = new ArrayList<>();
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_list);

        db = FirebaseFirestore.getInstance();
        professionalsCollection = db.collection("professionals");

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListAdapter(dataList);
        adapter.setOnBookButtonClickListener(this);
        recyclerView.setAdapter(adapter);

        loadProfessionals();

        // Add some test contents
//        dataList.add(new ListItem(R.drawable._6e63e353c3f2e238ed59039c5a2db31, "Dr. Michael Anderson", "Licensed Therapist"));
//        dataList.add(new ListItem(R.drawable._df6796f1d2361ccbed5eb7d9b697095, "Dr. Sarah Mitchell", "Clinical Psychologist"));
//        dataList.add(new ListItem(R.drawable.ellipse_15, "Dr. Emily Collins", "Mental Health Counselor"));
//        dataList.add(new ListItem(R.drawable.ellipse_15__1_, "Dr. David Morgan", "Sleep Specialist"));
//        dataList.add(new ListItem(R.drawable.ellipse_15__2_, "Dr. Jessica Turner", "Licensed Therapist"));
//        dataList.add(new ListItem(R.drawable.ellipse_15__3_, "Dr. Christopher Bennett", "Licensed Therapist"));
//        dataList.add(new ListItem(R.drawable._6e63e353c3f2e238ed59039c5a2db31, "Dr. Michael Anderson", "Licensed Therapist"));
//        dataList.add(new ListItem(R.drawable._df6796f1d2361ccbed5eb7d9b697095, "Dr. Sarah Mitchell", "Clinical Psychologist"));
//        dataList.add(new ListItem(R.drawable.ellipse_15, "Dr. Emily Collins", "Mental Health Counselor"));
//        dataList.add(new ListItem(R.drawable.ellipse_15__1_, "Dr. David Morgan", "Sleep Specialist"));
//        dataList.add(new ListItem(R.drawable.ellipse_15__2_, "Dr. Jessica Turner", "Licensed Therapist"));
//        dataList.add(new ListItem(R.drawable.ellipse_15__3_, "Dr. Christopher Bennett", "Licensed Therapist"));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_appointment) {
                bottomNavigationView.getMenu().findItem(R.id.nav_appointment).setChecked(true);
                Intent intent = new Intent(this, ProfessionalList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);  // Disable transition animations
                return true;
            } else if (itemId == R.id.nav_event) {
                bottomNavigationView.getMenu().findItem(R.id.nav_event).setChecked(true);
                Intent intent = new Intent(this, EventAty.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if(itemId == R.id.nav_profile) {
                bottomNavigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
                Intent intent = new Intent(this, EditUserProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                // get current user id
                intent.putExtra("userId", "user3");
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if(itemId == R.id.nav_forum) {
                bottomNavigationView.getMenu().findItem(R.id.nav_forum).setChecked(true);
                Intent intent = new Intent(this, MainActivity.class);  // Changed to EventAty
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);  // Disable transition animations
                return true;
            }

            return false;
        });


    }

    private void loadProfessionals() {
        professionalsCollection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    dataList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ListItem professional = document.toObject(ListItem.class);
                        dataList.add(professional);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }

    @Override
    public void onBookButtonClick(int position) {
        ListItem item = dataList.get(position);
        Intent intent = new Intent(this, MakeAnAppointment.class);
        intent.putExtra("PROFESSIONAL_NAME", item.getTitle());
        intent.putExtra("PROFESSIONAL_JOB", item.getSubtitle());

        startActivity(intent);
    }


}