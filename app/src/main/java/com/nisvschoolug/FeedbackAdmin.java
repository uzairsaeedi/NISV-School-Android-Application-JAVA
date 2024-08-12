package com.nisvschoolug;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FeedbackAdmin extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private List<FeedbackModel> feedbackList;
    private FeedbackAdapter adapter;
    BottomNavigationView btm_navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_admin);

        recyclerView = findViewById(R.id.recycler_view_feedback);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedbackList = new ArrayList<>();
        adapter = new FeedbackAdapter(feedbackList);
        recyclerView.setAdapter(adapter);
        btm_navigation = findViewById(R.id.btm_navigation);

        btm_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.ic_home) {
                    startActivity(new Intent(FeedbackAdmin.this, Admin.class));
                    return true;
                } else if (item.getItemId() == R.id.ic_add) {
                    startActivity(new Intent(FeedbackAdmin.this, AddCourse.class));
                    return true;
                }else if (item.getItemId() == R.id.ic_feedback) {
                    startActivity(new Intent(FeedbackAdmin.this, FeedbackAdmin.class));
                    return true;
                }
                return false;
            }
        });
        db = FirebaseFirestore.getInstance();

        loadFeedback();
    }
    private void loadFeedback() {
        db.collection("feedback").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        feedbackList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            FeedbackModel feedback = document.toObject(FeedbackModel.class);
                            feedbackList.add(feedback);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FeedbackAdmin.this, "Failed to load feedback", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}