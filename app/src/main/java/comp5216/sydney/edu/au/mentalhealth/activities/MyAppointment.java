package comp5216.sydney.edu.au.mentalhealth.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.adapters.ListAdapter;
import comp5216.sydney.edu.au.mentalhealth.entities.ListItem;

public class MyAppointment extends AppCompatActivity implements ListAdapter.OnBookButtonClickListener,ListAdapter.OnDelButtonClickListener{
    RecyclerView myList;
    private List<ListItem> dataList = new ArrayList<>();
    private ListAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference professionalsCollection;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointment);
        db = FirebaseFirestore.getInstance();
        professionalsCollection = db.collection("posts");
        myList =  findViewById(R.id.myrecyclerView);

        myList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListAdapter(dataList);
        adapter.setOnBookButtonClickListener(this);
        myList.setAdapter(adapter);
        adapter.setOnBookButtonClickListener(this);
        adapter.dellistener = this;
        loadProfessionals();
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


    @Override
    public void onDelButtonClick(int position) {
        dataList.remove(position);
        adapter.notifyDataSetChanged();

    }
}