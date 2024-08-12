package com.nisvschoolug;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddCourse extends AppCompatActivity {
    EditText edt_title,edt_content;
    Button btn_add_car;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    CircleImageView car_image;
    final int PICK_IMAGE = 1;
    FirebaseStorage storage;
    StorageReference reference;
    BottomNavigationView btm_navigation;
    StorageReference carRef;
    Uri imageUri;
    String carUrl;
    boolean valid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        btm_navigation = findViewById(R.id.btm_navigation);
        edt_content = findViewById(R.id.edt_seats);
        edt_title = findViewById(R.id.edt_name);

        btn_add_car = findViewById(R.id.btn_add_car);
        btm_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.ic_home) {
                    startActivity(new Intent(AddCourse.this, Admin.class));
                    return true;
                } else if (item.getItemId() == R.id.ic_add) {
                    startActivity(new Intent(AddCourse.this, AddCourse.class));
                    return true;
                }else if (item.getItemId() == R.id.ic_feedback) {
                    startActivity(new Intent(AddCourse.this, FeedbackAdmin.class));
                    return true;
                }
                return false;
            }
        });

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        reference = storage.getReference();


        carRef = reference.child("course");

        car_image = findViewById(R.id.car_image);
        car_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_IMAGE);
            }
        });

        btn_add_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkField(edt_content);
                checkField(edt_title);
                if (valid) {
                    uploadcarImage();
                }

                }
        });

    }

    public boolean checkField(EditText textfield) {
        if (textfield.getText().toString().isEmpty()) {
            textfield.setError("Empty");
            Toast.makeText(AddCourse.this, "Please Fill", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            valid = true;
        }
        return valid;
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {

                imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                car_image.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(AddCourse.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(AddCourse.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    public void uploadcarImage() {


        carRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        carUrl = task.getResult().toString();
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveData(carUrl);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddCourse.this, "Failed not get URL", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddCourse.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveData(String carUrl) {


        String id = db.collection("courses").document().getId();


        String content = edt_content.getText().toString();
        String title = edt_title.getText().toString();


        Map<String, Object> car_data = new HashMap<>();

        car_data.put("id", id);
        car_data.put("image", carUrl);
        car_data.put("title", title);
        car_data.put("content", content);


        db.collection("courses").document(id).set(car_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddCourse.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddCourse.this, Create_Quiz.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddCourse.this, "Sorry " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}