package com.team15.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.team15.chatapp.Model.User;

import java.util.concurrent.TimeUnit;

public class VerifyCodeActivity extends AppCompatActivity {
    private EditText editTextCode;
    private Button btnLogin;
    private TextView viewPhone;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    String codeSent;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        viewPhone=findViewById(R.id.view_phone);
        editTextCode= findViewById(R.id.editText_code);
        btnLogin=findViewById(R.id.btn_Login);
        progressBar=findViewById(R.id.progressBar2);

        mAuth = FirebaseAuth.getInstance();

        String phoneNumber=getIntent().getStringExtra("number");

        sendVerificationCode(phoneNumber);
    }

    public void signUp(View view) {
        String code= editTextCode.getText().toString();

        if(code.isEmpty() || code.length()<6){
            editTextCode.setError("Enter Code....");
            editTextCode.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        verifySignInCode(code);
    }

    private void verifySignInCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String userId = firebaseUser.getUid();
                            User user = new User(userId,"default","default","offline","user","default");
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                            reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(VerifyCodeActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Toast.makeText(VerifyCodeActivity.this, "Create Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            progressBar.setVisibility(View.INVISIBLE);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(), "যাচাই কোড ভুল", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });
    }


    private void sendVerificationCode(String number){

        viewPhone.setText(number);


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }



    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();

            if(code !=null){
                editTextCode.setText(code);
                progressBar.setVisibility(View.VISIBLE);
                verifySignInCode(code);

            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyCodeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            codeSent = s;
        }
    };


}