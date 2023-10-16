package comp5216.sydney.edu.au.mentalhealth.entities;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Appointment {
    public String professionalName;
    public String professionalJob;
    public String date;
    public String time;
    public String avatarUrl;
    public String userName;
    private String AppointmentId;
    public Appointment() {
    }

    public Appointment(String professionalName, String professionalJob, String date, String time
            , String avatarUrl, String userName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsCollection = db.collection("appointments");
        DocumentReference newPostRef = postsCollection.document();
        this.AppointmentId = newPostRef.getId();
        this.professionalName = professionalName;
        this.professionalJob = professionalJob;
        this.date = date;
        this.time = time;
        this.avatarUrl = avatarUrl;
        this.userName = userName;
    }
    public String getAppointmentId() {
        return AppointmentId;
    }

}

