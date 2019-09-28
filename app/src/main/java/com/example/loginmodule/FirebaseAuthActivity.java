package com.example.loginmodule;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseAuthActivity extends AppCompatActivity {

    EditText etLoginemail, etLoginpass;

    FirebaseAuth auth;
    DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_auth);

        etLoginemail = findViewById(R.id.etloginemail);
        etLoginpass = findViewById(R.id.etloginpassword);

        auth = FirebaseAuth.getInstance();
        root = FirebaseDatabase.getInstance().getReference();

    }


    public void doLogin(View view)
    {
        String email = etLoginemail.getText().toString();
        String pass = etLoginpass.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            etLoginemail.setError("Email is required");
            etLoginemail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            etLoginemail.setError("Not Valid Email Required");
            etLoginemail.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(pass))
        {
            etLoginpass.setError("Pass is required");
            etLoginpass.requestFocus();
            return;
        }
        if(pass.length() <6)
        {
            etLoginemail.setError("Minimum 6 character requird");
            etLoginpass.requestFocus();
            return;
        }


        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    Toast.makeText(FirebaseAuthActivity.this,"Login Sucessful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FirebaseAuthActivity.this,UserActivity.class));
                    finish();
                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthInvalidUserException)
                    {
                        Toast.makeText(FirebaseAuthActivity.this,"User is not register", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(FirebaseAuthActivity.this,RegisterActivity.class));

                    }
                }}
        });
    }

    public void doRegister(View view)
    {
        Intent i = new Intent(this,RegisterActivity.class);
        startActivity(i);
    }
}
