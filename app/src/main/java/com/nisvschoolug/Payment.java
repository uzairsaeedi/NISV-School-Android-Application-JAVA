package com.nisvschoolug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class Payment extends AppCompatActivity {

    String ClientID = "AcG9vSm6v_kVtyTPcZDIHBSSld2dt8jAuPBMOt6mGX3KVZYkUe0jnUTSzpREsQyeMbXzd4fbTrBlzxaB";
    Button btn_payment;
    EditText edt_amount;
    int Paypal_RequestCode = 7171;
    String amoount;
    public static PayPalConfiguration configuration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

         configuration = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(ClientID);

        Intent intent = new Intent(this,PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,configuration);
        startService(intent);

        btn_payment = findViewById(R.id.btn_payment);
        edt_amount = findViewById(R.id.edt_amount);

        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPayment();
            }
        });
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,PayPalService.class));
        super.onDestroy();
    }

    private void getPayment(){
        amoount = edt_amount.getText().toString();
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amoount)),"USD","Uzair",PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(Payment.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,configuration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,Paypal_RequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Paypal_RequestCode){
            if (resultCode == RESULT_OK){
                PaymentConfirmation con = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (con != null){
                    try{
                        String PaymentDetails = con.toJSONObject().toString();
                        Log.d("PaymentDetails", "PaymentDetails: " + PaymentDetails);
                        startActivity(new Intent(this, PaymentInt.class)
                                .putExtra("PaymentDetails",PaymentDetails)
                                .putExtra("PaymentAmount",amoount));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        }else if(resultCode ==PaymentActivity.RESULT_EXTRAS_INVALID){
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
        }

    }
}