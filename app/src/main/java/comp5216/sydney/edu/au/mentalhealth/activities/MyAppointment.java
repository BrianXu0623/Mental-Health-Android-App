package comp5216.sydney.edu.au.mentalhealth.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.adapters.MyAppointmentAdapter;
import comp5216.sydney.edu.au.mentalhealth.entities.MyAppMent;

public class MyAppointment extends AppCompatActivity {
    RecyclerView myList;
    private List<MyAppMent> dataList = new ArrayList<>();
    private MyAppointmentAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference professionalsCollection;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointment);
        db = FirebaseFirestore.getInstance();
        professionalsCollection = db.collection("appointments");
        myList =  findViewById(R.id.myrecyclerView);
        findViewById(R.id.BackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.AppointmentHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MyAppointment.this, AppointmentHistory.class));

            }
        });
        myList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyAppointmentAdapter(this,dataList);

        myList.setAdapter(adapter);
        adapter.mOnButtonClick = new MyAppointmentAdapter.OnButtonClick() {
            @Override
            public void onCancelBtnClick(int position) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference("appointments");
                String date = "date";
                usersRef.child(date).removeValue();
                dataList.remove(position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void oncompleteBtnClick(int position) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference("appointments");
                String professionalJob = "professionalJob";
                usersRef.child(professionalJob).setValue("professionalJob:finish");
            }
        };

        loadData();
    }
    private void loadData() {
        professionalsCollection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    dataList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        MyAppMent bean = document.toObject(MyAppMent.class);
                        dataList.add(bean);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }


}