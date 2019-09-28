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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText etname, etemail,etpass,etrepass;
    DatabaseReference root ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etname = findViewById(R.id.etname);
        etemail = findViewById(R.id.etemail);
        etpass = findViewById(R.id.etpassword);
        etrepass = findViewById(R.id.etrepassword);

        auth = FirebaseAuth.getInstance();
        root = FirebaseDatabase.getInstance().getReference();

    }

    public void doRegister(View view)
    {
        final String name = etname.getText().toString();
        String email = etemail.getText().toString();
        String pass = etpass.getText().toString();

        if(TextUtils.isEmpty(email))
        {
             etemail.setError("Email is required");
             etemail.requestFocus();
             return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            etemail.setError("Valid Email is required");
            etemail.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(pass))
        {
            etpass.setError("Pass is required");
            etpass.requestFocus();
            return;
        }
        if(pass.length() <6)
        {
            etpass.setError("Minimum 6 character requird");
            etpass.requestFocus();
            return;
        }


        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
              if(task.isSuccessful())
              {
                  String uid = auth.getCurrentUser().getUid();
                  root.child(uid).setValue(name);
                  Toast.makeText(RegisterActivity.this,"User Account created", Toast.LENGTH_SHORT).show();
                  finish();
                  startActivity(new Intent(RegisterActivity.this,UserActivity.class));
              }
              else
              {
                  if(task.getException() instanceof FirebaseAuthUserCollisionException)
                  {
                      Toast.makeText(RegisterActivity.this,"User Already Existes", Toast.LENGTH_SHORT).show();
                      finish();
                      startActivity(new Intent(RegisterActivity.this,FirebaseAuthActivity.class));

                  }
                  else
                  {
                      Toast.makeText(RegisterActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                  }
              }
            }
        });
    }
}
