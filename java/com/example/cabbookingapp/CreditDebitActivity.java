package com.example.cabbookingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class CreditDebitActivity extends AppCompatActivity {
    EditText cardno;
    Button paid;

    float fare;
    EditText expdate, Cvv;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_debit);
        expdate=findViewById(R.id.expdate);
        Cvv=findViewById(R.id.Cvv);
        cardno = findViewById(R.id.cardno);
        cardno.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        cardno.setInputType(InputType.TYPE_CLASS_NUMBER);
        Cvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        Cvv.setInputType(InputType.TYPE_CLASS_NUMBER);
        expdate.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        paid=findViewById(R.id.paid);
        Intent intent=getIntent();
        fare=intent.getFloatExtra("fare",fare);
        cardno.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return keyCode==KeyEvent.KEYCODE_DEL;
            }
        });

        paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String card=Cvv.getText().toString();
                String expiry=expdate.getText().toString();
                String cardNumber=cardno.getText().toString();
                if(cardNumber.length()!=16){
                    Toast.makeText(CreditDebitActivity.this, "Please enter a valid 16-digit Card Number", Toast.LENGTH_SHORT).show();
                } else if (expiry.length()!=5) {
                    Toast.makeText(CreditDebitActivity.this, "Please enter the Expiry Date", Toast.LENGTH_SHORT).show();
                } else if (card.length()!=3) {
                    Toast.makeText(CreditDebitActivity.this, "Please enter a valid CVV", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(CreditDebitActivity.this, "Amount Paid", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreditDebitActivity.this, ReviewStar.class));
                }
            }
        });
    }
}
