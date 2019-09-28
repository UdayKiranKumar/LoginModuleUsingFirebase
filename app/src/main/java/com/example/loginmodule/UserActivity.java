package com.example.loginmodule;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class UserActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference root;
    FirebaseUser user;
    ImageView iv;
    TextView tv;

    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        iv = findViewById(R.id.ivuser);
        tv = findViewById(R.id.tvuser);

        auth = FirebaseAuth.getInstance();
        root = FirebaseDatabase.getInstance().getReference();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        user = auth.getCurrentUser();

        if(user!= null)
        {
            displayDetails();
        }

        iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

    }

    public void openImage()
     {
         Intent i = new Intent();
         i.setType("image/*");
         i.setAction(Intent.ACTION_GET_CONTENT);
         startActivityForResult(i,1);
     }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==1 && resultCode == RESULT_OK)
        {
            if(data.getData() != null)
            {
                Uri uri = data.getData();
                iv.setImageURI(uri);
                upLoadFile(uri);
            }
        }
    }

    public void upLoadFile(Uri uri)
    {
         StorageReference sr = storageReference.child(auth.getCurrentUser().getUid());

         sr.putFile(uri);
    }

    public void displayDetails()
    {
       root.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

               final long ONE_MEGABYTE = 1024 * 1024;
               DataSnapshot ds = dataSnapshot.child(auth.getCurrentUser().getUid());
               String name = (String) ds.getValue();

               tv.setText("\n"+name);

               StorageReference sr = storageReference.child(auth.getCurrentUser().getUid());

               sr.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                   @Override
                   public void onSuccess(byte[] bytes) {

                       Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                       iv.setImageBitmap(bitmap);
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {

                   }
               });
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
    }

    public void doAdd(View view)
    {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.mylayout,null);

        final EditText type = v.findViewById(R.id.tvmylayout);

        ad.setView(v);

        ad.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String info = type.getText().toString();

                String userid = auth.getCurrentUser().getUid();

                root.child(userid).setValue(tv.getText().toString()+"\n"+ info);
                dialogInterface.cancel();
            }
        });

        ad.show();
    }
}
