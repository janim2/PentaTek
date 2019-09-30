package com.uenr.pentatek;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Random;

public class User_Registration extends AppCompatActivity {

    private EditText first_name, last_name, email,
    phone_number, passeord, confirm_password;
    private String sfirst_name, slastname, semail, sphone_number,
    spassword, sconfirm_password;
    private Button done_button;
    private TextView success_message;
    private ProgressBar loading;
    private FirebaseAuth mauth;
    private DatabaseReference reference;
    private ImageView goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__registration);

        mauth = FirebaseAuth.getInstance();

        first_name = findViewById(R.id.f_name);
        last_name = findViewById(R.id.l_name);
        email = findViewById(R.id.email);
        phone_number = findViewById(R.id.phone_number);
        passeord = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        done_button = findViewById(R.id.done_button);
        success_message = findViewById(R.id.success_message);
        loading = findViewById(R.id.loading);
        goback = findViewById(R.id.goBack);

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    sfirst_name = first_name.getText().toString().trim();
                    slastname = last_name.getText().toString().trim();
                    semail = email.getText().toString().trim();
                    sphone_number = phone_number.getText().toString().trim();
                    spassword = passeord.getText().toString().trim();
                    sconfirm_password = confirm_password.getText().toString().trim();

                    if(!sfirst_name.equals("") && !slastname.equals("") && !semail.equals("")
                    && !sphone_number.equals("")){
                        if(spassword.equals(sconfirm_password)){
                            addToDatabase(sfirst_name,slastname,semail,sphone_number,spassword);
                        }else{
                            loading.setVisibility(View.GONE);
                            success_message.setText("Password mismatch");
                            success_message.setTextColor(getResources().getColor(R.color.red));
                            success_message.setVisibility(View.VISIBLE);
                        }

                    }else{
                        loading.setVisibility(View.GONE);
                        success_message.setText("Fields required");
                        success_message.setTextColor(getResources().getColor(R.color.red));
                        success_message.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void addToDatabase(final String sfirst_name, final String slastname, final String semail, final String sphone_number, final String spassword) {
        loading.setVisibility(View.VISIBLE);

        mauth.createUserWithEmailAndPassword(semail, spassword)
                .addOnCompleteListener(User_Registration.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                                Toast.makeText(RegisterActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
//                            Toast.makeText(RegisterSchool.this, "Authentication failed." + task.getException(),
//                                    Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            success_message.setVisibility(View.VISIBLE);
                            success_message.setTextColor(getResources().getColor(R.color.red));
                            success_message.setText("Registration failed");
                        } else {
                            reference = FirebaseDatabase.getInstance().getReference("users").child(mauth.getCurrentUser().getUid());
                            reference.child("first_name").setValue(sfirst_name);
                            reference.child("last_name").setValue(slastname);
                            reference.child("email").setValue(semail);
                            reference.child("telephone").setValue(sphone_number);
                            addToNotifications();
                            loading.setVisibility(View.GONE);
                            success_message.setVisibility(View.VISIBLE);
                            success_message.setTextColor(getResources().getColor(R.color.green));
                            success_message.setText("Registration successful");
                            FirebaseAuth.getInstance().signOut();
                            Intent goToLogin  = new Intent(User_Registration.this, Login.class);
                            goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(goToLogin);
                        }
                    }
                });
    }

    private void addToNotifications() {
        try {
            Random random = new Random();
            int a = random.nextInt(987654);
            String notificationID = "notification" + a+"";
            reference = FirebaseDatabase.getInstance().getReference("notifications").child(mauth.getCurrentUser().getUid()).child(notificationID);
            reference.child("image").setValue("WM");
            reference.child("message").setValue("Welcome to PentaTek. Customer care is our number one focus. Thats why we have made ourselves accessible in times of trouble. Try us out NOW!!!");
            reference.child("title").setValue("Welcome to PentaTek");
            reference.child("time").setValue(new Date().toString());
        }catch (NullPointerException e){

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
