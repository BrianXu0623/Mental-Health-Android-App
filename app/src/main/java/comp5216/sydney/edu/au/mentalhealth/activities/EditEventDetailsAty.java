package comp5216.sydney.edu.au.mentalhealth.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.adapters.CommentAdapter;
import comp5216.sydney.edu.au.mentalhealth.entities.CurUserInfo;
import comp5216.sydney.edu.au.mentalhealth.entities.PostComment;

public class EditEventDetailsAty extends AppCompatActivity {

    private EditText etLoginUserName;// 活动名称
    private EditText regUserPwd;// 活动时间
    private EditText et_address;// 活动地点
    private EditText et_des;// 活动简介
    private Button loginBtn;// 发布活动

    private RecyclerView commentsRecyclerView;

    private CommentAdapter commentAdapter;

    private Button joinEvent;

    private boolean isjoin = false;


    private FirebaseFirestore db;
    private CollectionReference userCollection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_edit_event_details);
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("event");


        etLoginUserName = findViewById(R.id.et_login_username);
        regUserPwd = findViewById(R.id.et_login_pwd);
        et_address = findViewById(R.id.et_address);
        et_des = findViewById(R.id.et_des);


        etLoginUserName.setText(getIntent().getStringExtra("eventName"));
        regUserPwd.setText(getIntent().getStringExtra("eventDate"));
        et_address.setText(getIntent().getStringExtra("eventAddress"));
        et_des.setText(getIntent().getStringExtra("eventDes"));

        loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etLoginUserName.getText())
                    && !TextUtils.isEmpty(regUserPwd.getText())) {
                post();
            } else {
                Toast.makeText(this, "请输入活动相关数据", Toast.LENGTH_SHORT).show();
            }
        });

        // load join records
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentAdapter = new CommentAdapter(new ArrayList<>());
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentAdapter);
        joinEvent = findViewById(R.id.joinButton);
        loadComments(getIntent().getStringExtra("eventId"));

    }

    private void post() {
        String username = etLoginUserName.getText().toString().trim();
        String pwd = regUserPwd.getText().toString().trim();
        String et_desRef = et_des.getText().toString().trim();
        String et_addressR = et_address.getText().toString().trim();

        userCollection
                .whereEqualTo("eventId", getIntent().getStringExtra("eventId"))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        Map<String,Object> map =new HashMap<>();
                        map.put("eventName",username);
                        map.put("eventDes",et_desRef);
                        map.put("eventDate",pwd);
                        map.put("eventAddress",et_addressR);
                        userCollection.document(document.getId()).update(map);
                        finish();
                    }

                })
                .addOnFailureListener(e -> {
                });
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
                        Toast.makeText(EditEventDetailsAty.this, "Joined the event successfully!", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditEventDetailsAty.this, "Error, failed to join the event!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadComments(String postId) {
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
                        if (c1.isProfessional() && !c2.isProfessional()) return -1;
                        if (!c1.isProfessional() && c2.isProfessional()) return 1;
                        return c2.getTimestamp().compareTo(c1.getTimestamp());
                    });

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
                            Toast.makeText(EditEventDetailsAty.this, "Canceled participation successfully!", Toast.LENGTH_SHORT).show();
                            loadComments(getIntent().getStringExtra("eventId"));
                        } else {
                        }
                    }
                });
    }


    public void joinButton(View v){
        if(isjoin){
            deleteCommentsByPostId(getIntent().getStringExtra("eventId"));
        }else {
            saveCommentToDatabase(getIntent().getStringExtra("eventId"), CurUserInfo.userName, "");
        }



    }









}
