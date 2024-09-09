package com.example.cabbookingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class CabSel extends AppCompatActivity {
    DrawerLayout drawerLayout;
    Button mini,micro,prime;
    ImageView menu;
    LinearLayout home, about, logout;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_sel);
        mini=findViewById(R.id.mini);
        micro=findViewById(R.id.micro);
        prime=findViewById(R.id.prime);

        drawerLayout=findViewById(R.id.drawerLayout1);
        menu=findViewById(R.id.menu);
        home=findViewById(R.id.home);
        logout=findViewById(R.id.logout);

        mini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CabSel.this, "Cab Selected", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CabSel.this, BookCab.class));
            }
        });
        micro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CabSel.this, "Cab Selected", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CabSel.this, BookCab.class));
            }
        });
        prime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CabSel.this, "Cab Selected", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CabSel.this, BookCab.class));
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(CabSel.this, HomeActivity.class);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CabSel.this, "Logged Out Succesfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CabSel.this, LoginActivity.class));
            }
        });
    }
    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public static void closeDrawer(DrawerLayout drawerLayout){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity){
        Intent intent=new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
    @Override
    protected void onPause(){
        super.onPause();
        closeDrawer(drawerLayout);
    }
}