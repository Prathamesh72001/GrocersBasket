package com.example.grocerbasket;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class NewPassword extends AppCompatActivity {

    TextInputLayout newpassword, confirmpassword;
    RelativeLayout updatebtn;
    float v = 0;
    ImageView backbtn;
    String phoneno,fullname,forWhom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        newpassword = findViewById(R.id.newpassword);
        confirmpassword = findViewById(R.id.confirmpassword);
        updatebtn = findViewById(R.id.Updatebtn);
        backbtn = findViewById(R.id.backBtn);

        phoneno=getIntent().getStringExtra("phoneno");
        fullname=getIntent().getStringExtra("firstname");
        forWhom=getIntent().getStringExtra("forWhom");

        //Listeners
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPassword.this, ForgetPassword.class);
                startActivity(intent);
                finish();
            }
        });

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewPassword();
            }
        });


        //Animation
        newpassword.setTranslationX(800);
        confirmpassword.setTranslationX(800);
        updatebtn.setTranslationX(800);

        newpassword.setAlpha(v);
        confirmpassword.setAlpha(v);
        updatebtn.setAlpha(v);

        newpassword.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(100).start();
        confirmpassword.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        updatebtn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
    }



    private void setNewPassword() {
       /* //Check internet
        CheckInternet checkInternet=new CheckInternet();
        if(!checkInternet.isConnected(this)){
            showCustomDialog();
            return;
        }*/

        //Validation
        if(!validateNewPassword() | !validateConfirmPassword()){
            return;
        }

        //Get Data from User
        String _newpassword=newpassword.getEditText().getText().toString().trim();

        //UpdateData
        if("user".equalsIgnoreCase(forWhom)) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(phoneno).child("password").setValue(_newpassword);
            Toast.makeText(NewPassword.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }else{
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Sellers");
            databaseReference.child(phoneno).child("password").setValue(_newpassword);
            Toast.makeText(NewPassword.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
    }

    //Validation
    private boolean validateNewPassword() {
        String val = Objects.requireNonNull(newpassword.getEditText()).getText().toString().trim();

        if (val.isEmpty()) {
            newpassword.setError("Empty");
            return false;
        } else {
            newpassword.setError(null);
            newpassword.requestFocus();
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        String val = Objects.requireNonNull(confirmpassword.getEditText()).getText().toString().trim();
        String val2 = Objects.requireNonNull(newpassword.getEditText()).getText().toString().trim();

        if (val.isEmpty()) {
            confirmpassword.setError("Empty");
            return false;
        } else if (!val2.equalsIgnoreCase(val)) {
            confirmpassword.setError("Incorrect");
            return false;
        } else {
            confirmpassword.setError(null);
            confirmpassword.requestFocus();
            return true;
        }
    }


}