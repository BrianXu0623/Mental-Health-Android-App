package comp5216.sydney.edu.au.mentalhealth.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.adapters.MyAppointmentAdapter;
import comp5216.sydney.edu.au.mentalhealth.entities.MyAppMent;

public class AppointmentHistory extends AppCompatActivity {
    RecyclerView rv;
    private FirebaseFirestore db;
    private CollectionReference mCollection;
    private List<MyAppMent> dataList = new ArrayList<>();
    private MyAppointmentAdapter adapter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_history);
        db = FirebaseFirestore.getInstance();
        mCollection = db.collection("appointments");
        rv = findViewById(R.id.hist_rv);
        findViewById(R.id.AppointmentBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loadData();
    }

    private void loadData() {

        mCollection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    dataList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        MyAppMent bean = document.toObject(MyAppMent.class);
                        if(bean.getProfessionalJob().endsWith(":finish")){
                            dataList.add(bean);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }
}