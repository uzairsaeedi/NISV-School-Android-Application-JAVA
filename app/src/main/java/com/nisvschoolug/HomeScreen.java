package com.nisvschoolug;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

public class HomeScreen extends AppCompatActivity implements TextToSpeech.OnInitListener {
    public static String courseID;
    private Model story;
    private TextToSpeech tts;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button btn_speak, btn_stop, btn_quiz;
    ImageView btn_back,iv_layout;
    TextView tv_title, tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        tts = new TextToSpeech(this, this);
        btn_speak = findViewById(R.id.btn_speak);
        btn_stop = findViewById(R.id.btn_stop);
        btn_back = findViewById(R.id.btn_back);
        iv_layout = findViewById(R.id.iv_layout);
        btn_quiz = findViewById(R.id.btn_quiz);
        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        if(courseID != null) {
            DocumentReference documentReference = db.collection("courses").document(courseID);
            System.out.println("courseID: " + courseID);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            story = document.toObject(Model.class);

                            tv_title.setText(story.getTitle());
                            tv_content.setText(story.getContent());
                            String imgUrl = story.getImage();
                            Glide.with(HomeScreen.this).load(imgUrl).into(iv_layout);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HomeScreen.this, "Error loading story", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(HomeScreen.this, "Course ID is null", Toast.LENGTH_SHORT).show();
        }
        int storyId = getIntent().getIntExtra("story_id",-1);
        btn_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this,Quiz.class);
                intent.putExtra("id",courseID);
                startActivity(intent);
            }
        });
        if (storyId != -1) {


            tv_title = findViewById(R.id.tv_title);
            tv_content = findViewById(R.id.tv_content);

            tv_title.setText(story.getTitle());
            tv_content.setText(story.getContent());
        }

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.stop();
            }
        });
        btn_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textToSpeak = story.getContent();

                if (tts != null) {
                    tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}