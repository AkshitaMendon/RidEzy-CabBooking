package com.example.cabbookingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;


public class PaymentActivity extends AppCompatActivity {
    TextView cash, debit, rate;
    float fare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        rate=findViewById(R.id.rate);
        Intent intent=getIntent();
        fare=intent.getFloatExtra("fare", 0.0f);
        rate.setText(String.format(Locale.getDefault(), "To Pay: %.2f Rs", fare));
        cash=findViewById(R.id.cash);
        debit=findViewById(R.id.debit);
        debit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PaymentActivity.this, CreditDebitActivity.class));
            }
        });

        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PaymentActivity.this, ReviewStar.class));

            }
        });

    }
}