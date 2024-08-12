package com.nisvschoolug;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ScoreActivity extends AppCompatActivity {
    TextView scoreTextView,questionTextView;
    BottomNavigationView btm_navigation;
    Button btn_payment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        scoreTextView = findViewById(R.id.scoreTextView);
        questionTextView = findViewById(R.id.questionTextView);
        btm_navigation = findViewById(R.id.btm_navigation);
        btn_payment = findViewById(R.id.btn_payment);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int totalQuestionsAttempted = extras.getInt("totalQuestionsAttempted", 0);
            int Score = extras.getInt("correct", 0);


            questionTextView.setText("Total Questions " + totalQuestionsAttempted);
            scoreTextView.setText("Your Score: " + Score);
        }

        btm_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.ic_home) {
                    startActivity(new Intent(ScoreActivity.this, Courses.class));
                    return true;
                } else if (item.getItemId() == R.id.ic_feedback) {
                    startActivity(new Intent(ScoreActivity.this, Feedback.class));
                    return true;
                }
                return false;
            }
        });
        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreActivity.this,Payment.class);
                startActivity(intent);
            }
        });
    }
}