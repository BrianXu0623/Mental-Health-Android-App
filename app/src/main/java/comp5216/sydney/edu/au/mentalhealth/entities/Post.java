package comp5216.sydney.edu.au.mentalhealth.entities;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Post {
    private String postId;
    private String userId;
    private String title;
    private String content;
    private Timestamp timestamp;

    public Post() {
    }

    public Post(String userId, String title, String content) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsCollection = db.collection("posts");
        DocumentReference newPostRef = postsCollection.document();
        this.postId = newPostRef.getId();
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.timestamp = Timestamp.now();
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
