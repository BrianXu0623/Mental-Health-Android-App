package comp5216.sydney.edu.au.mentalhealth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.mentalhealth.adapters.PostAdapter;
import comp5216.sydney.edu.au.mentalhealth.entities.Post;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference postsCollection;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter();
        recyclerView.setAdapter(adapter);
        searchEditText = findViewById(R.id.searchEditText);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_forum) {
                loadPosts();
                return true;
            }
            return false;
        });
        postsCollection = db.collection("posts");
//        createSamplePosts();
        loadPosts();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPosts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void createSamplePosts() {
        List<Post> postsToCreate = generateSamplePosts();

        WriteBatch batch = db.batch();

        for (Post post : postsToCreate) {
            batch.set(postsCollection.document(), post);
        }
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });
    }

    private void filterPosts(String query) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference postsRef = db.collection("posts");

        String queryLowerCase = query.toLowerCase();

        Query searchQuery = postsRef
                .orderBy("title")
                .startAt(queryLowerCase)
                .endAt(queryLowerCase + "\uf8ff");

        searchQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Post> filteredList = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();

                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Post post = document.toObject(Post.class);
                        filteredList.add(post);
                    }
                }

                adapter.setPosts(filteredList);
                adapter.notifyDataSetChanged();
            } else {
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }

    private List<Post> generateSamplePosts() {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post("user1", "title1", "content1"));
        posts.add(new Post("user2", "title2", "content2"));
        posts.add(new Post("user3", "title3", "content3"));
        posts.add(new Post("user1", "title1", "content1"));
        posts.add(new Post("user2", "title2", "content2"));
        posts.add(new Post("user3", "title3", "content3"));
        posts.add(new Post("user1", "title1", "content1"));
        posts.add(new Post("user2", "title2", "content2"));
        posts.add(new Post("user3", "title3", "content3"));
        posts.add(new Post("user1", "title1", "content1"));
        posts.add(new Post("user2", "title2", "content2"));
        posts.add(new Post("user3", "title3", "content3"));
        return posts;
    }

    private void loadPosts() {
        db.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Post> posts = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
                        posts.add(post);
                    }

                    adapter.setPosts(posts);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                });
    }
}
