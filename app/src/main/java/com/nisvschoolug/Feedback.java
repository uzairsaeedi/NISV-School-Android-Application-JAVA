package com.nisvschoolug;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Feedback extends AppCompatActivity {
    BottomNavigationView btm_navigation;
    private EditText edtEmail, edtMessage;
    private Button btnSubmit;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        edtEmail = findViewById(R.id.edt_email);
        edtMessage = findViewById(R.id.edt_message);
        btnSubmit = findViewById(R.id.btn_submit);
        btm_navigation = findViewById(R.id.btm_navigation);

        db = FirebaseFirestore.getInstance();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String message = edtMessage.getText().toString().trim();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(message)) {
                    saveFeedback(email, message);
                } else {
                    Toast.makeText(Feedback.this, "Please enter email and message", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btm_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.ic_home) {
                    startActivity(new Intent(Feedback.this, Courses.class));
                    return true;
                } else if (item.getItemId() == R.id.ic_feedback) {
                    startActivity(new Intent(Feedback.this, Feedback.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void saveFeedback(String email, String message) {
        FeedbackModel feedback = new FeedbackModel(email, message);
        db.collection("feedback").add(feedback)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(Feedback.this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                        // Clear input fields if needed
                        edtEmail.setText("");
                        edtMessage.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Feedback.this, "Failed to submit feedback", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}