package comp5216.sydney.edu.au.mentalhealth.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.mentalhealth.R;
import comp5216.sydney.edu.au.mentalhealth.adapters.PostAdapter;
import comp5216.sydney.edu.au.mentalhealth.entities.Post;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_POST_DETAIL = 1234;
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference postsCollection;
    private EditText searchEditText;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(this);
        recyclerView.setAdapter(adapter);
        searchEditText = findViewById(R.id.searchEditText);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        CreatePostActivity.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_forum) {
                loadPosts();
                return true;
            } else if (item.getItemId() == R.id.nav_event) {
                Intent intent = new Intent(MainActivity.this, EventAty.class);
                startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        loadPosts();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_POST_DETAIL && resultCode == Activity.RESULT_OK) {
            // 如果在PostDetailActivity中有更改（例如删除帖子），则重新加载帖子
            loadPosts();
        }
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
