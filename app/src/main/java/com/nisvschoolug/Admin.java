package com.nisvschoolug;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Admin extends AppCompatActivity {
    BottomNavigationView btm_navigation;
    private RecyclerView recyclerView;
    private EditText searchEditText;
    private Adapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Model> courseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_admin);
        recyclerView = findViewById(R.id.rv_stories);
        searchEditText = findViewById(R.id.search_edit_text);
        btm_navigation = findViewById(R.id.btm_navigation);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseList = new ArrayList<>();
        adapter = new Adapter(this, courseList);
        recyclerView.setAdapter(adapter);

        btm_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.ic_home) {
                        startActivity(new Intent(Admin.this, Admin.class));
                        return true;
                    } else if (item.getItemId() == R.id.ic_add) {
                        startActivity(new Intent(Admin.this, AddCourse.class));
                        return true;
                    }else if (item.getItemId() == R.id.ic_feedback) {
                        startActivity(new Intent(Admin.this, FeedbackAdmin.class));
                        return true;
                    }
                return false;
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
        courseList.clear();
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
                    Toast.makeText(Admin.this, "No course found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Admin.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
