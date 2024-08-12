package com.nisvschoolug;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Courses extends AppCompatActivity {
    BottomNavigationView btm_navigation;
    private RecyclerView recyclerView;
    private EditText searchEditText;
    TextView txt_name;
    private Adapter adapter;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mAuth.getCurrentUser();
    String user = firebaseUser.getUid().toString();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("courses");
    private ArrayList<Model> courseList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        recyclerView = findViewById(R.id.rv_courses);
        searchEditText = findViewById(R.id.search_edit_text);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseList = new ArrayList<>();
        adapter = new Adapter(this, courseList);
        recyclerView.setAdapter(adapter);
        btm_navigation = findViewById(R.id.btm_navigation);
        txt_name = findViewById(R.id.txt_name);

        btm_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.ic_home) {
                    startActivity(new Intent(Courses.this, Courses.class));
                    return true;
                } else if (item.getItemId() == R.id.ic_feedback) {
                    startActivity(new Intent(Courses.this, Feedback.class));
                    return true;
                }
                return false;
            }
        });

        DocumentReference documentReference = db.collection("users").document(user);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        txt_name.setText(document.getString("name"));
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Courses.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        loadCourses();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    loadCourses();
                } else {
                    searchCourses(charSequence.toString().toLowerCase());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

    }

    private void loadCourses() {
        courseList.clear(); // Clear existing courses
        db.collection("courses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshots : task.getResult()) {
                        Model obj = documentSnapshots.toObject(Model.class);
                        courseList.add(obj);
                    }
                    adapter.notifyDataSetChanged(); // Notify adapter after adding all courses
                } else {
                    Toast.makeText(Courses.this, "No course found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Courses.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void searchCourses(String searchText) {
        List<Model> searchResults = new ArrayList<>();
        for (Model course : courseList) {
            if (course.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                searchResults.add(course);
            }
        }
        adapter.filterList(searchResults);
    }
}