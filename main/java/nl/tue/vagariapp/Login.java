package nl.tue.vagariapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

/**
 * Created by s157091 on 28-3-2017.
 */
public class Login extends AppCompatActivity {

    Button bLogin;
    Button bRegister;
    private DatabaseReference mDatabase;

    private EditText mEmailText;
    private EditText mPasswordText;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);


        bLogin = (Button) findViewById(R.id.loginButton);
        bRegister = (Button) findViewById(R.id.registerButton);

        bLogin.setEnabled(false);
        bRegister.setEnabled(false);

        mAuthList = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    startActivity(new Intent(Login.this, Tabs.class));

                }
            }
        };
        mAuth = FirebaseAuth.getInstance();


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String sUsername = mEmailText.getText().toString();
                String sPassword = mPasswordText.getText().toString();
                if (sUsername.matches("") || sPassword.matches("")) {
                    bLogin.setEnabled(false);
                    bRegister.setEnabled(false);
                } else {
                    bLogin.setEnabled(true);
                    bRegister.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mEmailText = (EditText) findViewById(R.id.userinput);
        mEmailText.addTextChangedListener(textWatcher);
        mPasswordText = (EditText) findViewById(R.id.passwordinput);
        mPasswordText.addTextChangedListener(textWatcher);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });


    }

    private void startRegister() {
        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(Login.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (task.isSuccessful()) {
                            startSignIn();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthList);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthList != null) {
            mAuth.removeAuthStateListener(mAuthList);
        }
    }

    private void startSignIn() {

        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Toast.makeText(Login.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Authentication succeeded.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        mAuth.signInWithEmailAndPassword(email, password);
    }
}