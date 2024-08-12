package com.nisvschoolug;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Quiz extends AppCompatActivity {
    TextView questionView, aText, bText, cText, dText, questionNumberTextView;
    RadioButton aRadio, bRadio, cRadio, dRadio;
    Integer currentQuestionIndex = 0;
    String courseId;
    CardView fab, previousCard, saveButton;
    int correct = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<DocumentSnapshot> questionsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionView = findViewById(R.id.questionView);
        aText = findViewById(R.id.aText);
        bText = findViewById(R.id.bText);
        cText = findViewById(R.id.cText);
        dText = findViewById(R.id.dText);
        questionNumberTextView = findViewById(R.id.questionNumber);
        aRadio = findViewById(R.id.aRadio);
        bRadio = findViewById(R.id.bRadio);
        cRadio = findViewById(R.id.cRadio);
        dRadio = findViewById(R.id.dRadio);
        fab = findViewById(R.id.nextfab);
        previousCard = findViewById(R.id.update_card);
        saveButton = findViewById(R.id.submit);

        Intent intent = getIntent();
        if (intent != null) {
            courseId = intent.getStringExtra("id");
        }

        fetchQuizQuestions();

        previousCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex--;
                    displayQuestion();
                } else {
                    Toast.makeText(Quiz.this, "No previous questions found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionIndex < questionsList.size() - 1) {
                    currentQuestionIndex++;
                    displayQuestion();
                } else {
                    Toast.makeText(Quiz.this, "No more questions available", Toast.LENGTH_SHORT).show();
                }
                aRadio.setChecked(false);
                bRadio.setChecked(false);
                cRadio.setChecked(false);
                dRadio.setChecked(false);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Quiz.this, ScoreActivity.class);
                intent.putExtra("correct", correct);
                intent.putExtra("totalQuestionsAttempted", currentQuestionIndex + 1);
                startActivity(intent);
            }
        });
    }
    private void fetchQuizQuestions() {
        if (courseId != null) {
            db.collection("tests").document(courseId).collection("quiz").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                    questionsList.addAll(querySnapshot.getDocuments());
                                    displayQuestion();
                                } else {
                                    Toast.makeText(Quiz.this, "No quiz data found for this course", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Quiz.this, "Failed to fetch quiz data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Quiz.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Course ID is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayQuestion() {
        if (!questionsList.isEmpty() && currentQuestionIndex < questionsList.size()) {
            DocumentSnapshot document = questionsList.get(currentQuestionIndex);
            questionView.setText(document.getString("question"));
            aText.setText(document.getString("opt_A"));
            bText.setText(document.getString("opt_B"));
            cText.setText(document.getString("opt_C"));
            dText.setText(document.getString("opt_D"));
            questionNumberTextView.setText(String.valueOf(currentQuestionIndex + 1));

            setRadioListeners(document.getString("answer"));
        }
    }

    private void setRadioListeners(final String correctAnswer) {
        aRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bRadio.setChecked(false);
                cRadio.setChecked(false);
                dRadio.setChecked(false);
                checkAnswer("A", correctAnswer);
            }
        });
        bRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aRadio.setChecked(false);
                cRadio.setChecked(false);
                dRadio.setChecked(false);
                checkAnswer("B", correctAnswer);
            }
        });
        cRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bRadio.setChecked(false);
                aRadio.setChecked(false);
                dRadio.setChecked(false);
                checkAnswer("C", correctAnswer);
            }
        });
        dRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bRadio.setChecked(false);
                cRadio.setChecked(false);
                aRadio.setChecked(false);
                checkAnswer("D", correctAnswer);
            }
        });
    }

    private void checkAnswer(String selectedAnswer, String correctAnswer) {
        if (selectedAnswer.equals(correctAnswer)) {
            Toast.makeText(Quiz.this, "Correct Answer!", Toast.LENGTH_SHORT).show();
            correct++;
        } else {
            Toast.makeText(Quiz.this, "Wrong Answer. Try again!", Toast.LENGTH_SHORT).show();
        }
    }
}
