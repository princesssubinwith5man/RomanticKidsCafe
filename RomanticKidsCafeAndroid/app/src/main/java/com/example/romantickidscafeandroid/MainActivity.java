package com.example.romantickidscafeandroid;

import static android.content.ContentValues.TAG;
import static android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.provider.Settings.Secure;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    RelativeLayout rellay1;
    LinearLayout rellay2;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogIn;
    private Button buttonSignUp;
    private Context mContext;
    String loginId,loginPwd;
    int check = 0;
    String androidId;

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
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.TRANSPARENT); }
        androidId = getDeviceId(this);
        Log.d(TAG, "Android ID is "+ androidId);
        FirebaseDatabase.getInstance().getReference("alarm").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String key = snapshot.getKey();
                    if(key.equals(androidId)) {
                        Log.d(TAG, "key: " + key);
                        String alarm = snapshot.getValue(String.class);
                        Log.d(TAG, "alarm: " + alarm);
                        check = 1;
                        Log.d(TAG, "check: " + check);
                    }
                }
                Log.d(TAG, "check: "+ check);
                if(check == 0){
                    AlarmDialogue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        rellay1 = (RelativeLayout)findViewById(R.id.rellay1);
        rellay2 = (LinearLayout) findViewById(R.id.rellay2);
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
        /*editTextPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    buttonLogIn.performClick();
                    return true;
                }
                return false;
            }
        });*/
        //자동 로그인===================================================================================================
        CheckBox save_checkBox = (CheckBox) findViewById(R.id.save_checkbox) ;
        mContext = this;
        if (save_checkBox.isChecked()) {
            editTextEmail.setText(PreferenceManager.getString(mContext, "id"));
            editTextPassword.setText(PreferenceManager.getString(mContext, "pw"));
            save_checkBox.setChecked(true);
        }
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    startActivity(intent);
                    finish();
                } else {
                }
            }
        };
        //SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE);
        //loginId = sharedPreferences.getString("inputId", null);
        //loginPwd = sharedPreferences.getString("inputPwd", null);
        if(loginId != "" && loginPwd != ""&&loginId != null && loginPwd != null) {
            Log.d(TAG, "onClick: "+ loginId + " "+ loginPwd);
            //loginUser(loginId,loginPwd);
        }
        else {
            buttonLogIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE);

                    //SharedPreferences.Editor autoLogin = sharedPreferences.edit();

                    if(!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                        //Log.d(TAG, "뭐가 문젠데 " +"\"" +editTextEmail.getText().toString() + "\""+" " +editTextPassword.getText().toString());
                        //autoLogin.putString("inputId", editTextEmail.getText().toString());
                        //autoLogin.putString("inputPwd", editTextPassword.getText().toString());

                        //autoLogin.commit();
                        loginUser(editTextEmail.getText().toString(),editTextPassword.getText().toString());
                        if (save_checkBox.isChecked()) {
                            PreferenceManager.setString(mContext, "id", editTextEmail.getText().toString());//id 키값으로 저장
                            PreferenceManager.setString(mContext, "pw", editTextPassword.getText().toString());//pw 키값으로 저장
                            PreferenceManager.setBoolean(mContext, "check", save_checkBox.isChecked()); //현재 체크박스 상태 값 저장

                        }
                        else {
                            PreferenceManager.setBoolean(mContext, "check", save_checkBox.isChecked()); //현재 체크박스 상태 값 저장
                            PreferenceManager.clear(mContext); //로그인 정보 삭제
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                        Log.d("asdfsadf", "onClick: 계정과 비밀번호를 입력하세요");
                    }
                    Log.d(TAG, "onClick: "+ loginId + " "+ loginPwd);

                    //Toast.makeText(getApplicationContext(), editTextEmail.getText().toString() + "님 환영합니다.", Toast.LENGTH_SHORT).show();
                }
            });

        }
        //=============================================================================================================
        //로그인 정보 저장 체크박스





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
        });*/
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
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
        //FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        //while(!currentUser.getEmail().equals(null))
        //Log.d(TAG, "currentUser: "+currentUser.getEmail());
    }

    @Override
    protected void onStop() {
        super.onStop();
        //if (firebaseAuthListener != null) {
        firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        //}
    }
    public static String getDeviceId(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }
    private void AlarmDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("알람허용").setMessage("아이가 넘어졌을때 알람을 받으시겠어요?\n(허용 안할시 아이가 넘어지는 것을 실시간으로 확인하지 않을시 알 수 없음)");

        builder.setPositiveButton("허용", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                FirebaseMessaging.getInstance().subscribeToTopic("falldown7").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "성공";
                        if(!task.isSuccessful()){
                            msg = "실패";
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
                FirebaseDatabase.getInstance().getReference("alarm").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            //String androidId = getDeviceId(this);
                            FirebaseDatabase.getInstance().getReference("alarm").child(androidId).setValue("1");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Toast.makeText(getApplicationContext(), "알람이 허용되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                FirebaseDatabase.getInstance().getReference("alarm").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            //String androidId = getDeviceId(this);
                            FirebaseDatabase.getInstance().getReference("alarm").child(androidId).setValue("0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                alarmDialogue();
                Toast.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_SHORT).show();
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void alarmDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("알림").setMessage("알람을 취소하였습니다.\n알람을 받고 싶으면 좌측하단에서 알람허용을 하실 수 있습니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Toast.makeText(getApplicationContext(), "테스트", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void exit(View view) {
        moveTaskToBack(true); // 태스크를 백그라운드로 이동
        finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기

        System.exit(0);
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
