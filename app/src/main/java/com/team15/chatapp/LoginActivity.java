package com.team15.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class LoginActivity extends AppCompatActivity {
    private EditText editTextPhone;
    private Button btnSendCode;
    public String number, phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextPhone = findViewById(R.id.editText_phone);
        btnSendCode = findViewById(R.id.btn_SendCode);
        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


    }
//
//    private void pickNumber() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Dexter.withActivity(this)
//                    .withPermission(Manifest.permission.READ_PHONE_NUMBERS)
//                    .withListener(new PermissionListener() {
//                        @Override
//                        public void onPermissionGranted(PermissionGrantedResponse response) {
//                            TelephonyManager tMgr = (TelephonyManager) LoginActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
//                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                                    this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                                            this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                                return;
//                            }
//                            String mPhoneNumber = tMgr.getLine1Number();
//                        }
//
//                        @Override
//                        public void onPermissionDenied(PermissionDeniedResponse response) {
//
//                        }
//
//                        @Override
//                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//
//                        }
//                    }
//        };
//    }


    public void login() {

        phoneNum=editTextPhone.getText().toString();

        if(phoneNum.isEmpty()){
            editTextPhone.setError("Phone number is required");
            editTextPhone.requestFocus();
            return;
        }

        if(phoneNum.length() < 11 ){
            editTextPhone.setError("Please enter a valid phone");
            editTextPhone.requestFocus();
            return;
        }
        number="+88"+ phoneNum;


        Intent intent= new Intent(this, VerifyCodeActivity.class);
        intent.putExtra("number",number);
        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent= new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}