package com.nisvschoolug;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentInt extends AppCompatActivity {

    TextView txtId, txtAmount,txtStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_int);

        txtId = findViewById(R.id.txtId);
        txtAmount = findViewById(R.id.txtAmount);
        txtStatus = findViewById(R.id.txtStatus);

        Intent intent = getIntent();

        String paymentDetailsJson = intent.getStringExtra("PaymentDetails");

        if (paymentDetailsJson != null) {
            try {
                JSONObject jsonObject = new JSONObject(paymentDetailsJson);
                showDetails(jsonObject.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Payment Details are null", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDetails(JSONObject response, String paymentAmount) {
        try {
            txtId.setText(response.getString("id"));
            txtStatus.setText(response.getString("state"));
            txtAmount.setText("$" + paymentAmount);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}