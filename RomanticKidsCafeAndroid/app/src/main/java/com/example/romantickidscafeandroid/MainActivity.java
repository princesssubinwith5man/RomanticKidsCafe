package com.example.romantickidscafeandroid;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    RelativeLayout rellay1, rellay2;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogIn;
    private Button buttonSignUp;
    private Context mContext;
    String loginId,loginPwd;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT); }

        rellay1 = (RelativeLayout)findViewById(R.id.rellay1);
        rellay2 = (RelativeLayout) findViewById(R.id.rellay2);
        handler.postDelayed(runnable, 2000);
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.edittext_email);
        editTextPassword = (EditText) findViewById(R.id.edittext_password);
        buttonLogIn = (Button) findViewById(R.id.btn_login);


        editTextEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    EditText editText = (EditText) findViewById(R.id.edittext_password);
                    editText.requestFocus();

                    return true;
                }
                return false;
            }
        });
        editTextPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    buttonLogIn.performClick();
                    return true;
                }
                return false;
            }
        });
        //자동 로그인===================================================================================================
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                }
            }
        };
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE);
        loginId = sharedPreferences.getString("inputId", null);
        loginPwd = sharedPreferences.getString("inputPwd", null);
        if(loginId != null && loginPwd != null) {
            loginUser(loginId,loginPwd);
            Log.d(TAG, "onClick: "+ loginId + " "+ loginPwd);
        }
        else {
            buttonLogIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE);

                    SharedPreferences.Editor autoLogin = sharedPreferences.edit();

                    autoLogin.putString("inputId", editTextEmail.getText().toString());
                    autoLogin.putString("inputPwd", editTextPassword.getText().toString());

                    autoLogin.commit();
                    Toast.makeText(getApplicationContext(), editTextEmail.getText().toString() + "님 환영합니다.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: "+ loginId + " "+ loginPwd);
                    loginUser(editTextEmail.getText().toString(),editTextPassword.getText().toString());
                }
            });
        }
        //=============================================================================================================
        //로그인 정보 저장 체크박스
        CheckBox save_checkBox = (CheckBox) findViewById(R.id.save_checkbox) ;
        mContext = this;
        if (save_checkBox.isChecked()) {
            editTextEmail.setText(PreferenceManager.getString(mContext, "id"));
            editTextPassword.setText(PreferenceManager.getString(mContext, "pw"));
            save_checkBox.setChecked(true);
        }




        /*buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                    loginUser(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                    // 로그인 정보 저장
                    if (save_checkBox.isChecked()) {
                        PreferenceManager.setString(mContext, "id", editTextEmail.getText().toString());//id 키값으로 저장
                        PreferenceManager.setString(mContext, "pw", editTextPassword.getText().toString());//pw 키값으로 저장
                        PreferenceManager.setBoolean(mContext, "check", save_checkBox.isChecked()); //현재 체크박스 상태 값 저장

                    }
                    else {
                        PreferenceManager.setBoolean(mContext, "check", save_checkBox.isChecked()); //현재 체크박스 상태 값 저장
                        PreferenceManager.clear(mContext); //로그인 정보 삭제
                    }

                } else {
                    Toast.makeText(MainActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                    Log.d("asdfsadf", "onClick: 계정과 비밀번호를 입력하세요");
                }
            }
        });
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                }
            }
        };*/
    }
    public void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            Log.d("asdfsadf", "onClick: 로그인 성공");
                            firebaseAuth.addAuthStateListener(firebaseAuthListener);
                        } else {
                            // 로그인 실패
                            Toast.makeText(MainActivity.this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            Log.d("asdfsadf", "onClick: 아이디 또는 비밀번호가 일치하지 않습니다.");
                        }
                    }
                });
    }

    public void signup(View view) {
        Intent intent = new Intent(MainActivity.this, SignupActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }
}
