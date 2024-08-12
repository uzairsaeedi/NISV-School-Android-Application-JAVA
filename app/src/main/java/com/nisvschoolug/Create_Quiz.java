package com.nisvschoolug;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Create_Quiz extends AppCompatActivity {
    EditText question;
    EditText aText;
    EditText bText;
    EditText cText;
    EditText dText;
    RadioButton aRadio;
    RadioButton bRadio;
    RadioButton cRadio;
    RadioButton dRadio;
    int currentQuestion = 1;
    int previousQuestion = 1;
    TextView questionNumber;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Question> ques;
    JSONArray jsonArray;
    String selectedOption = "";

    CardView fab, previousCard, saveButton;
    String id;
    String courseid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        jsonArray = new JSONArray();
        ques = new ArrayList<>();

        Intent intent =getIntent();
        if (intent!= null){
            courseid = intent.getStringExtra("id");
        }

        question = findViewById(R.id.questionView);
        aText = findViewById(R.id.aText);
        bText = findViewById(R.id.bText);
        cText = findViewById(R.id.cText);
        dText = findViewById(R.id.dText);
        questionNumber = findViewById(R.id.questionNumber);
        aRadio = findViewById(R.id.aRadio);
        bRadio = findViewById(R.id.bRadio);
        cRadio = findViewById(R.id.cRadio);
        dRadio = findViewById(R.id.dRadio);

        selectedOption = "";
        currentQuestion = 1;
        fab = findViewById(R.id.nextfab);
        previousCard = findViewById(R.id.update_card);
        saveButton = findViewById(R.id.fab2);


        setListeners();
        previousCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousQuestion > 1) {
                    previousQuestion--;
                    setAllData(previousQuestion);
                }
                if (previousQuestion == 1)
                    previousCard.setVisibility(View.INVISIBLE);
                Toast.makeText(Create_Quiz.this, String.valueOf(previousQuestion), Toast.LENGTH_SHORT).show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousQuestion != currentQuestion) {
                    previousQuestion++;
                    if (previousQuestion != currentQuestion)
                        setAllData(previousQuestion);
                    else {
                        clearAllData();
                        questionNumber.setText(String.valueOf(currentQuestion));
                    }
                    if (previousQuestion > 1)
                        previousCard.setVisibility(View.VISIBLE);
                }
                boolean cont = getEnteredQuestionsValue();
                if (cont) {
                    previousQuestion++;
                    currentQuestion++;
                    Toast.makeText(Create_Quiz.this, "QUESTION " + currentQuestion, Toast.LENGTH_SHORT).show();
                    questionNumber.setText(String.valueOf(currentQuestion));
                    clearAllData();
                    previousCard.setVisibility(View.VISIBLE);
                }
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEnteredQuestionsValue();
                saveQuizData();
                Intent intent = new Intent(Create_Quiz.this, Admin.class);
                startActivity(intent);
            }
        });

    }

    public void setAllData(int position) {
        clearAllData();
        Question question1 = ques.get(position - 1);
        questionNumber.setText(String.valueOf(question1.getId()));
        question.setText(question1.getQuestion());
        aText.setText(question1.getOpt_A());
        bText.setText(question1.getOpt_B());
        cText.setText(question1.getOpt_C());
        dText.setText(question1.getOpt_D());
        switch (question1.getAnswer()) {
            case "A":
                aRadio.setChecked(true);
                break;
            case "B":
                bRadio.setChecked(true);
                break;
            case "C":
                cRadio.setChecked(true);
                break;
            case "D":
                dRadio.setChecked(true);
                break;
        }
    }

    private void clearAllData() {
        aRadio.setChecked(false);
        bRadio.setChecked(false);
        cRadio.setChecked(false);
        dRadio.setChecked(false);
        aText.setText(null);
        bText.setText(null);
        cText.setText(null);
        dText.setText(null);
        question.setText(null);
        selectedOption = "";
    }

    private boolean getEnteredQuestionsValue() {
        id = db.collection("tests").document().getId();
        boolean cont = false;
        if (TextUtils.isEmpty(question.getText().toString().trim())) {
            question.setError("Please fill in a question");
        } else if (TextUtils.isEmpty(aText.getText().toString().trim())) {
            aText.setError("Please fill in option A");
        } else if (TextUtils.isEmpty(bText.getText().toString().trim())) {
            bText.setError("Please fill in option B");
        } else if (TextUtils.isEmpty(cText.getText().toString().trim())) {
            cText.setError("Please fill in option C");
        } else if (TextUtils.isEmpty(dText.getText().toString().trim())) {
            dText.setError("Please fill in option D");
        } else if (selectedOption.equals("")) {
            Toast.makeText(this, "Please select the correct answer", Toast.LENGTH_SHORT).show();
        } else {
            Question quest = new Question();
            quest.setId(currentQuestion);
            quest.setQuestion(question.getText().toString());
            quest.setOpt_A(aText.getText().toString());
            quest.setOpt_B(bText.getText().toString());
            quest.setOpt_C(cText.getText().toString());
            quest.setOpt_D(dText.getText().toString());
            quest.setAnswer(selectedOption);
            ques.add(quest);
            cont = true;

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("answer", selectedOption);
                jsonObject.put("id", id);
                jsonObject.put("courseID", courseid);
                jsonObject.put("opt_A", aText.getText().toString().trim());
                jsonObject.put("opt_B", bText.getText().toString().trim());
                jsonObject.put("opt_C", cText.getText().toString().trim());
                jsonObject.put("opt_D", dText.getText().toString().trim());
                jsonObject.put("question", question.getText().toString().trim());
                jsonObject.put("questionNumber", currentQuestion);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonArray.put(jsonObject);
        }
        return cont;
    }

    private void saveQuizData() {
        if (jsonArray.length() != 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    Map<String, Object> quizData = new HashMap<>();
                    quizData.put("answer", jsonObject.getString("answer"));
                    quizData.put("id", jsonObject.getString("id"));
                    quizData.put("opt_A", jsonObject.getString("opt_A"));
                    quizData.put("opt_B", jsonObject.getString("opt_B"));
                    quizData.put("opt_C", jsonObject.getString("opt_C"));
                    quizData.put("opt_D", jsonObject.getString("opt_D"));
                    quizData.put("courseID", jsonObject.getString("courseID"));
                    quizData.put("question", jsonObject.getString("question"));


                    db.collection("tests").document(courseid).collection("quiz")
                            .add(quizData)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Create_Quiz.this, "Quiz data saved successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Create_Quiz.this, "Error saving quiz data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(Create_Quiz.this, "Incomplete Quiz Format", Toast.LENGTH_SHORT).show();
        }
    }


    private List<Map<String, Object>> convertJsonArrayToList(JSONArray jsonArray) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
                Map<String, Object> map = new HashMap<>();
                map.put("answer", jsonObject.getString("answer"));
                map.put("id", jsonObject.getString("id"));
                map.put("courseID", jsonObject.getString("courseID"));
                map.put("opt_A", jsonObject.getString("opt_A"));
                map.put("opt_B", jsonObject.getString("opt_B"));
                map.put("opt_C", jsonObject.getString("opt_C"));
                map.put("opt_D", jsonObject.getString("opt_D"));
                map.put("question", jsonObject.getString("question"));
                list.add(map);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private void setListeners() {
        aRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOption = "A";
                bRadio.setChecked(false);
                cRadio.setChecked(false);
                dRadio.setChecked(false);
            }
        });
        bRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOption = "B";
                aRadio.setChecked(false);
                cRadio.setChecked(false);
                dRadio.setChecked(false);
            }
        });
        cRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOption = "C";
                bRadio.setChecked(false);
                aRadio.setChecked(false);
                dRadio.setChecked(false);
            }
        });
        dRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOption = "D";
                bRadio.setChecked(false);
                cRadio.setChecked(false);
                aRadio.setChecked(false);
            }
        });
    }
}
