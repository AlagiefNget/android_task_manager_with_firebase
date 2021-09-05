package com.example.taskmanagementapp_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class RegistrationActivity extends AppCompatActivity {

    private EditText registerEmail;
    private EditText registerPassword;
    private TextView signInTxt;
    private Button registerBtn;

    private FirebaseAuth myAuth;
    private ProgressDialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerBtn = findViewById(R.id.registerBtn);

        myAuth = FirebaseAuth.getInstance();
        myDialog = new ProgressDialog(this);

        signInTxt = findViewById(R.id.signInTxt);
        signInTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = registerEmail.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    registerEmail.setError("Required field...");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    registerPassword.setError("Required field...");
                    return;
                }

                myDialog.setMessage("Registering....");
                myDialog.show();

                myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Registration successful, please log in.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(getApplicationContext(), "Registration failed, please try again", Toast.LENGTH_SHORT).show();
                        }
                        myDialog.dismiss();
                    }
                });

            }
        });
    }
}