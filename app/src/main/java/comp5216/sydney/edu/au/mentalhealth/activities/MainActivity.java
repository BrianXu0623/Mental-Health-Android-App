package comp5216.sydney.edu.au.mentalhealth.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
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
import comp5216.sydney.edu.au.mentalhealth.entities.UserProfile;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_POST_DETAIL = 1234;
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference postsCollection;
    private SearchView searchView;
    private FloatingActionButton floatingActionButton;
    private MaterialButtonToggleGroup toggleButton;
    private Button buttonAll;
    private Button buttonProfessional;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        toggleButton = findViewById(R.id.toggleButton);
        buttonAll = findViewById(R.id.button1);
        buttonProfessional = findViewById(R.id.button2);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(this);
        recyclerView.setAdapter(adapter);
        searchView = findViewById(R.id.postSearchView);
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
                loadPosts(false);
                return true;
            } else if (item.getItemId() == R.id.nav_event) {
                Intent intent = new Intent(MainActivity.this, EventAty.class);
                startActivity(intent);
            }else if(item.getItemId() == R.id.nav_profile) {
                Intent intent = new Intent(this, EditUserProfile.class);
                // get current user id
                intent.putExtra("userId", "user3");
                startActivity(intent);
            }
            return false;
        });
        postsCollection = db.collection("posts");
//        createSamplePosts();
//        generateSampleUsers();
        loadPosts(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterPosts(s.toString());
                return true;
            }
        });

        toggleButton.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.button1) {
                    loadPosts(false);
                } else if (checkedId == R.id.button2) {
                    loadPosts(true);
                }
            } else {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPosts(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_POST_DETAIL && resultCode == Activity.RESULT_OK) {
            // 如果在PostDetailActivity中有更改（例如删除帖子），则重新加载帖子
            loadPosts(false);
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
                .startAt(query)
                .endAt(query + "\uf8ff");

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
        return posts;
    }

    private void loadPosts(boolean onlyProfessional) {
        db.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Post> posts = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
                        if(onlyProfessional) {
                            if(post.isProfessional()) {
                                posts.add(post);
                            }
                        }
                        else {
                            posts.add(post);
                        }
                    }

                    adapter.setPosts(posts);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                });
    }


    private void generateSampleUsers(){
        List<UserProfile> profiles = new ArrayList<>();
        profiles.add(new UserProfile("sampleUserId","user1", false, false, "test1@mentalHealth.com", "test hobby1", "test major1", "1111"));
        profiles.add(new UserProfile("user1","user1", false, false, "test1@mentalHealth.com", "test hobby1", "test major1", "1111"));
        profiles.add(new UserProfile("user2","user2", true, false, "test2@mentalHealth.com", "test hobby2", "test major2", "2222"));
        profiles.add(new UserProfile("user3","user3", true, true, "test3@mentalHealth.com", "test hobby3", "test major3", "3333"));
        CollectionReference userProfiles = db.collection("UserProfiles");
        WriteBatch batch = db.batch();
        for(UserProfile profile: profiles){
            batch.set(userProfiles.document(profile.getUserId()), profile);

        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("UserProfiles", "generate success");
            }
        });

    }

}
