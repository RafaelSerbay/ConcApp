package com.example.rafael.concapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends Activity {
    // Usernam, password edittext
    EditText txtUsername, txtPassword;

    // variables to store: username, password from EditText
    String username, password;

    // login button
    Button btnLogin;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);



        // Login button
        btnLogin = (Button) findViewById(R.id.btnLogin);


        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                username = txtUsername.getText().toString();
                password = txtPassword.getText().toString();
                new PostAsync(LoginActivity.this).execute(username, password);
            }
        });
    }

}