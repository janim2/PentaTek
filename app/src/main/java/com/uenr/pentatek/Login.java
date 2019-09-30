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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private EditText emailedit_Text, passwordedit_Text;
    private String semail, spassword, isSelected, auto_mobile_email;
    private RadioButton company_radiobutton, user_radiobutton;
    private Button login_button;
    private TextView success_message,forgot_password, signup_as_user;
    private ProgressBar loading;
    private Accessories login_accessor;
    private FirebaseAuth mauth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_accessor = new Accessories(Login.this);
        mauth = FirebaseAuth.getInstance();
        emailedit_Text = findViewById(R.id.email_editText);
        passwordedit_Text = findViewById(R.id.password_editText);
        user_radiobutton = findViewById(R.id.user_radio_button);
        company_radiobutton = findViewById(R.id.company_radio_button);
        login_button = findViewById(R.id.login_button);
        loading = findViewById(R.id.loading);
        success_message = findViewById(R.id.success_message);
        forgot_password = findViewById(R.id.forgot_password_text);
        signup_as_user = findViewById(R.id.signup_text);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                semail = emailedit_Text.getText().toString().trim();
                spassword = passwordedit_Text.getText().toString().trim();
                if(isNetworkAvailable()){
                   if(!semail.equals("") && !spassword.equals("") ){
                       if(user_radiobutton.isChecked() || company_radiobutton.isChecked()){
                           if(user_radiobutton.isChecked()){
                               isSelected = "User";
                               login_accessor.put("user_type", isSelected);
                               LoginAsuser(semail,spassword);
                           }else{
                               isSelected = "Company";
                               CheckLogin_Validity();
                           }
                       }else{
                           success_message.setVisibility(View.VISIBLE);
                           success_message.setTextColor(getResources().getColor(R.color.red));
                           success_message.setText("User selection required");
                       }
                   }else{
                       success_message.setVisibility(View.VISIBLE);
                       success_message.setTextColor(getResources().getColor(R.color.red));
                       success_message.setText("No internet connection");
                   }
                }else{
                    success_message.setVisibility(View.VISIBLE);
                    success_message.setTextColor(getResources().getColor(R.color.red));
                    success_message.setText("No internet connection");
                }
            }
        });

        signup_as_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,User_Registration.class));
            }
        });


    }

    private void CheckLogin_Validity() {
        success_message.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        try{
            reference = FirebaseDatabase.getInstance().getReference("company_emails");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            Fetch_Registered_Emails(child.getKey());
                        }
                    }else{
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Login.this,"Cancelled",Toast.LENGTH_LONG).show();
                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Fetch_Registered_Emails(String key) {
        DatabaseReference get_registered_Emails = FirebaseDatabase.getInstance().getReference("company_emails").child(key);
        get_registered_Emails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("auto_mobile_email")){
                            auto_mobile_email = child.getValue().toString();
                            if(auto_mobile_email.equals(semail)){
                                login_accessor.put("user_type", isSelected);
                                LoginAsCompany();
                            }else{
                                loading.setVisibility(View.GONE);
                                success_message.setVisibility(View.VISIBLE);
                                success_message.setTextColor(getResources().getColor(R.color.red));
                                success_message.setText("You are not An Automobile Company");
                            }
                        }
                        else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Login.this,"Cancelled",Toast.LENGTH_LONG).show();

            }
        });
    }

    private void LoginAsCompany() {
        mauth.signInWithEmailAndPassword(semail,spassword).addOnCompleteListener(Login.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    loading.setVisibility(View.GONE);
                    success_message.setVisibility(View.VISIBLE);
                    success_message.setTextColor(getResources().getColor(R.color.red));
                    success_message.setText("Login failed");
                }else{
                    loading.setVisibility(View.GONE);
                    success_message.setVisibility(View.VISIBLE);
                    success_message.setTextColor(getResources().getColor(R.color.green));
                    success_message.setText("Login successful");
                    Intent goto_one_last_step = new Intent(Login.this,Company_mainActivity.class);
                    goto_one_last_step.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(goto_one_last_step);
                }
            }
        });
    }

    private void LoginAsuser(String email, String password) {
        loading.setVisibility(View.VISIBLE);
        mauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(Login.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    loading.setVisibility(View.GONE);
                    success_message.setVisibility(View.VISIBLE);
                    success_message.setTextColor(getResources().getColor(R.color.red));
                    success_message.setText("Login failed");
                }else{
                    loading.setVisibility(View.GONE);
                    success_message.setVisibility(View.VISIBLE);
                    success_message.setTextColor(getResources().getColor(R.color.green));
                    success_message.setText("Login successful");
                    Intent goto_one_last_step = new Intent(Login.this,One_Last_Step_User.class);
                    goto_one_last_step.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(goto_one_last_step);
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
