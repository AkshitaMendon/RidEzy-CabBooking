package com.example.cabbookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText signupName,signupEmail, signupPassword, signupCnfpass;
    private Button signupButton;
    private TextView loginRedirectText;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth= FirebaseAuth.getInstance();
        signupName=findViewById(R.id.signup_username);
        signupEmail=findViewById(R.id.signup_email);
        signupPassword= findViewById(R.id.signup_password);
        signupCnfpass=findViewById(R.id.signup_cnfpassword);
        signupButton= findViewById(R.id.signup_button);
        loginRedirectText= findViewById(R.id.loginRedirectText);
        usersRef= FirebaseDatabase.getInstance().getReference().child("users");
        Pattern lowercase=Pattern.compile("^.*[a-z].*$");

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=signupName.getText().toString().trim();
                String user= signupEmail.getText().toString().trim();
                String pass= signupPassword.getText().toString().trim();
                String cnfpass=signupCnfpass.getText().toString().trim();

                if(user.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                }
                if(pass.isEmpty()){
                    signupPassword.setError("Password cannot be empty");
                } else if (!isValidPassword(pass)) {
                    // Custom password validation method to check password criteria
                    signupPassword.setError("Invalid password. Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.");
                }
                else if (!pass.equals(cnfpass)) {
                    Toast.makeText(SignUpActivity.this, "Passwords are not matching", Toast.LENGTH_SHORT).show();
                } else{
                    auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                                saveUserData(name, user, pass);
                            }else{
                                Toast.makeText(SignUpActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

    }
    private boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
        return pattern.matcher(password).matches();
    }
    private void saveUserData(String name, String email, String password){
        FirebaseUser currentUser= auth.getCurrentUser();
        if(currentUser!=null){
            String userId=currentUser.getUid();
            DatabaseReference userRef=usersRef.child(userId);
            userRef.child("name").setValue(name);
            userRef.child("email").setValue(email);
            userRef.child("password").setValue(password);
        }
    }
}