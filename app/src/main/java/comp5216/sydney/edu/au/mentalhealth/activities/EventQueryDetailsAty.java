package comp5216.sydney.edu.au.mentalhealth.activities;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.adapters.CommentAdapter;
import comp5216.sydney.edu.au.mentalhealth.entities.CurUserInfo;
import comp5216.sydney.edu.au.mentalhealth.entities.Event;
import comp5216.sydney.edu.au.mentalhealth.entities.PostComment;

public class EventQueryDetailsAty extends AppCompatActivity {

    private TextView etLoginUserName;
    private TextView regUserPwd;
    private TextView et_address;
    private TextView et_des;
    private TextView creatorName;
    private ImageView creatorImage;
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private Button joinEvent;
    private boolean isjoin = false;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_event_query_details);
        db = FirebaseFirestore.getInstance();

        etLoginUserName = findViewById(R.id.title);
        regUserPwd = findViewById(R.id.date);
        et_address = findViewById(R.id.address);
        et_des = findViewById(R.id.content);
        creatorName = findViewById(R.id.authorNameTextView);
        creatorImage = findViewById(R.id.authorAvatarImageView);



        etLoginUserName.setText(getIntent().getStringExtra("eventName"));
        regUserPwd.setText("Date: "+getIntent().getStringExtra("eventDate"));
        et_address.setText("Address: " + getIntent().getStringExtra("eventAddress"));
        et_des.setText(getIntent().getStringExtra("eventDes"));
        creatorName.setText(getIntent().getStringExtra("creator"));





        // load join records
        storage = FirebaseStorage.getInstance();

        PostDetailActivity.connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentAdapter = new CommentAdapter(new ArrayList<>());
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentAdapter);
        joinEvent = findViewById(R.id.joinButton);
        loadComments(getIntent().getStringExtra("eventId"));


        loadImage();



    }



    // Event joined records

    private void saveCommentToDatabase(String postId, String userId, String commentText) {
        CollectionReference commentsCollection = db.collection("joinEvent");

        PostComment newComment = new PostComment(null, postId, userId, commentText, Timestamp.now());

        if (CurUserInfo.isProfessional) {
            newComment.setProfessional(true);
        }

        commentsCollection.add(newComment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        newComment.setCommentId(documentReference.getId());
                        documentReference.set(newComment);
                        List<PostComment> currentComments = commentAdapter.getComments();
                        currentComments.add(newComment);
                        commentAdapter.setComments(currentComments);
                        commentAdapter.notifyDataSetChanged();
                        loadComments(postId);
                        Toast.makeText(EventQueryDetailsAty.this, "Joined the event successfully!", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EventQueryDetailsAty.this, "Error, failed to join the event!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadComments(String postId) {
        commentAdapter = new CommentAdapter(new ArrayList<>());
        commentsRecyclerView.setAdapter(commentAdapter);
        isjoin = false;
        db.collection("joinEvent")
                .whereEqualTo("postId", postId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<PostComment> commentsList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        PostComment comment = document.toObject(PostComment.class);
                        if(comment.getUserId().equals(CurUserInfo.userName)){
                            isjoin = true;
                        }
                        commentsList.add(comment);
                    }
                    if(isjoin){
                        joinEvent.setText("Cancel");
                    }else {
                        joinEvent.setText("Join");
                    }

                    Collections.sort(commentsList, (c1, c2) -> {
                        return c2.getTimestamp().compareTo(c1.getTimestamp());
                    });
                    if(commentsList.size() > 5){
                        commentsList = commentsList.subList(0,5);
                    }

                    commentAdapter.setComments(commentsList);
                    commentAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {

                });

    }



    private void deleteCommentsByPostId(String postId) {
        db.collection("joinEvent")
                .whereEqualTo("postId", postId).whereEqualTo("userId", CurUserInfo.userName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("joinEvent").document(document.getId()).delete();

                            }
                            Toast.makeText(EventQueryDetailsAty.this, "Canceled participation successfully!", Toast.LENGTH_SHORT).show();
                            loadComments(getIntent().getStringExtra("eventId"));
                        } else {
                        }
                    }
                });
    }


    public void joinButton(View v){
        CollectionReference eventsCollection = db.collection("event");
        String eventId = getIntent().getStringExtra("eventId");

        eventsCollection.whereEqualTo("eventId", eventId).limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        Event event = documentSnapshot.toObject(Event.class);
                        if (event != null) {
                            int currentCount = event.getParticipantsCount();
                            if (isjoin) {
                                currentCount--;
                                deleteCommentsByPostId(eventId);
                            } else {
                                currentCount++;
                                saveCommentToDatabase(eventId, CurUserInfo.userName, "");
                            }

                            documentSnapshot.getReference().update("participantsCount", currentCount)
                                    .addOnSuccessListener(aVoid -> {
                                        if (isjoin) {
                                            Toast.makeText(EventQueryDetailsAty.this, "Canceled participation successfully!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(EventQueryDetailsAty.this, "Joined the event successfully!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(EventQueryDetailsAty.this, "Error updating participants count!", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(EventQueryDetailsAty.this, "Event not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EventQueryDetailsAty.this, "Error accessing the event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    public void loadImage(){
        StorageReference image = storage.getReference().child(getIntent().getStringExtra("creator")+".JPEG");
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File finalLocalFile = localFile;
        image.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            creatorImage.setImageBitmap(EditUserProfile.cropCircle(BitmapFactory.
                    decodeFile(finalLocalFile.getAbsolutePath())));
        }).addOnFailureListener(exception -> {
        });
    }
    public void openUserProfile(View v){
        UserProfileActivity.UserProfileActivity(this,getIntent().getStringExtra("creator"));
    }


    public void openCommentUserProfile(View view) {
        String userIdFromTag = (String) view.getTag();
        if (userIdFromTag != null) {
            UserProfileActivity.UserProfileActivity(this, userIdFromTag);
        }
    }



}
