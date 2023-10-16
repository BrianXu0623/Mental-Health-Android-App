package comp5216.sydney.edu.au.mentalhealth.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.adapters.MyAppointmentAdapter;
import comp5216.sydney.edu.au.mentalhealth.entities.CurUserInfo;
import comp5216.sydney.edu.au.mentalhealth.entities.MyAppointmentListItem;

public class MyAppointment extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference professionalsCollection;
    private List<MyAppointmentListItem> dataList = new ArrayList<>();
    private MyAppointmentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointment);

        db = FirebaseFirestore.getInstance();
        professionalsCollection = db.collection("appointments");

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyAppointmentAdapter(dataList);
        recyclerView.setAdapter(adapter);

        loadProfessionals();
    }

    private void loadProfessionals() {
        professionalsCollection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MyAppointmentListItem professional = document.toObject(
                                    MyAppointmentListItem.class);
                            if(professional.getUserName() == null ||
                                    ! professional.getUserName().equals(CurUserInfo.userName)) {
                                continue;
                            }
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
    protected void onResume() {
        super.onResume();
    }
}