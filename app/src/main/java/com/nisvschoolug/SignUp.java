package com.nisvschoolug;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    EditText edt_email, edt_username, edt_name, edt_password, edt_phone, edt_repassword, edt_city, edt_country, edt_age, edt_address;
    Button btn_signup, btn_login;
    RadioButton btn_admin, btn_user;
    boolean valid = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edt_password = findViewById(R.id.edt_password);
        edt_email = findViewById(R.id.edt_email);
        edt_username = findViewById(R.id.edt_username);
        edt_phone = findViewById(R.id.edt_phone);
        edt_name = findViewById(R.id.edt_name);
        edt_age = findViewById(R.id.edt_age);
        edt_city = findViewById(R.id.edt_city);
        edt_country = findViewById(R.id.edt_country);
        edt_address = findViewById(R.id.edt_address);
        edt_repassword = findViewById(R.id.edt_re_password);

        btn_admin = findViewById(R.id.btn_admin);
        btn_user = findViewById(R.id.btn_user);

        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);
        mAuth = FirebaseAuth.getInstance();

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkField(edt_address);
                checkField(edt_age);
                checkField(edt_city);
                checkField(edt_country);
                checkField(edt_email);
                checkField(edt_address);
                checkField(edt_name);
                checkField(edt_password);
                checkField(edt_repassword);
                checkField(edt_phone);
                checkField(edt_username);

                String email = edt_email.getText().toString().trim();
                String password = edt_password.getText().toString().trim();
                if (!(btn_admin.isChecked() || btn_user.isChecked())) {
                    Toast.makeText(SignUp.this, "Select Account Role", Toast.LENGTH_SHORT).show();
                } else {
                    if (valid) {
                        if (password.equals(edt_repassword.getText().toString())) {
                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String userID = mAuth.getCurrentUser().getUid();
                                        Toast.makeText(SignUp.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                        saveData(userID);
                                        Intent intent = new Intent(SignUp.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUp.this, "Sorry" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            edt_password.setError("Password do not match");
                        }

                    }
                }
            }


        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean checkField(EditText textfield) {
        if (textfield.getText().toString().isEmpty()) {
            textfield.setError("Empty");
            valid = false;
        } else {
            valid = true;
        }
        return valid;
    }
    public void saveData(String userID) {
        RadioGroup rg = findViewById(R.id.radio);

        String gender = ((RadioButton)findViewById(rg.getCheckedRadioButtonId())).getText().toString();

        String name_value = edt_name.getText().toString();
        String email = edt_email.getText().toString();
        String username = edt_username.getText().toString();
        String phone_number_value = edt_phone.getText().toString();
        String city_value = edt_city.getText().toString();
        String country_value = edt_country.getText().toString();
        String address_value = edt_address.getText().toString();
        String age_value = edt_age.getText().toString();


        Map<String, Object> user_data = new HashMap<>();
        if (btn_admin.isChecked()){
            user_data.put("isAdmin","1");
        }
        if (btn_user.isChecked()){
            user_data.put("isUser","1");
        }

        user_data.put("name", name_value);
        user_data.put("email", email);
        user_data.put("username", username);
        user_data.put("phone_number", phone_number_value);
        user_data.put("city", city_value);
        user_data.put("country", country_value);
        user_data.put("address", address_value);
        user_data.put("age", age_value);
        user_data.put("gender", gender);
        user_data.put("user_id", userID);
        db.collection("users").document(userID).set(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignUp.this, "Done", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUp.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
