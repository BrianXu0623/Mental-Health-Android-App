package comp5216.sydney.edu.au.mentalhealth.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private BottomNavigationView bottomNavigationView;
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
            } else if(itemId == R.id.nav_profile) {
                Intent intent = new Intent(this, EditUserProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if(itemId == R.id.nav_forum) {
                Intent intent = new Intent(this, MainActivity.class);  // Changed to EventAty
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);  // Disable transition animations
                return true;
            }

            return false;
        });

        bottomNavigationView.getMenu().findItem(R.id.nav_appointment).setChecked(true);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProfessionals(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProfessionals(newText);
                return false;
            }
        });

        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggleButton);
        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.button1) {
                        loadProfessionalsByName();
                    } else if (checkedId == R.id.button2) {
                        loadProfessionalsBySkill();
                    }
                }
            }
        });


    }

    private void loadProfessionals() {
        professionalsCollection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ListItem professional = document.toObject(ListItem.class);
                            String avatarUrl = document.getString("avatarUrl");
                            professional.setAvatarUrl(avatarUrl);
                            dataList.add(professional);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                    }
                });
    }


    @Override
    public void onBookButtonClick(int position) {
        ListItem item = dataList.get(position);
        Intent intent = new Intent(this, MakeAnAppointment.class);
        intent.putExtra("PROFESSIONAL_NAME", item.getTitle());
        intent.putExtra("PROFESSIONAL_JOB", item.getSubtitle());

        intent.putExtra("avatarUrl", item.getAvatarUrl());

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        bottomNavigationView.getMenu().findItem(R.id.nav_appointment).setChecked(true);

        super.onResume();
    }

    private void filterProfessionals(String query) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference professionalsRef = db.collection("professionals");

        String queryLowerCase = query.toLowerCase();

        Query searchQuery = professionalsRef
                .orderBy("title")
                .startAt(query)
                .endAt(query + "\uf8ff");

        searchQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ListItem> filteredList = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();

                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        ListItem professional = document.toObject(ListItem.class);
                        filteredList.add(professional);
                    }
                }

                adapter.setDataList(filteredList);
                adapter.notifyDataSetChanged();
            } else {
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadProfessionalsByName() {
        professionalsCollection
                .orderBy("title")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ListItem professional = document.toObject(ListItem.class);
                            dataList.add(professional);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                    }
                });
    }


    private void loadProfessionalsBySkill() {
        professionalsCollection
                .orderBy("subtitle")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ListItem professional = document.toObject(ListItem.class);
                            dataList.add(professional);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                    }
                });
    }


}