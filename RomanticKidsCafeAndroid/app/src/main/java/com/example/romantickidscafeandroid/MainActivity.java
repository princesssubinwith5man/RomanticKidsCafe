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
        //?????? ?????????===================================================================================================
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
                        //Log.d(TAG, "?????? ????????? " +"\"" +editTextEmail.getText().toString() + "\""+" " +editTextPassword.getText().toString());
                        //autoLogin.putString("inputId", editTextEmail.getText().toString());
                        //autoLogin.putString("inputPwd", editTextPassword.getText().toString());

                        //autoLogin.commit();
                        loginUser(editTextEmail.getText().toString(),editTextPassword.getText().toString());
                        if (save_checkBox.isChecked()) {
                            PreferenceManager.setString(mContext, "id", editTextEmail.getText().toString());//id ???????????? ??????
                            PreferenceManager.setString(mContext, "pw", editTextPassword.getText().toString());//pw ???????????? ??????
                            PreferenceManager.setBoolean(mContext, "check", save_checkBox.isChecked()); //?????? ???????????? ?????? ??? ??????

                        }
                        else {
                            PreferenceManager.setBoolean(mContext, "check", save_checkBox.isChecked()); //?????? ???????????? ?????? ??? ??????
                            PreferenceManager.clear(mContext); //????????? ?????? ??????
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "????????? ??????????????? ???????????????.", Toast.LENGTH_LONG).show();
                        Log.d("asdfsadf", "onClick: ????????? ??????????????? ???????????????");
                    }
                    Log.d(TAG, "onClick: "+ loginId + " "+ loginPwd);

                    //Toast.makeText(getApplicationContext(), editTextEmail.getText().toString() + "??? ???????????????.", Toast.LENGTH_SHORT).show();
                }
            });

        }
        //=============================================================================================================
        //????????? ?????? ?????? ????????????





        /*buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                    loginUser(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                    // ????????? ?????? ??????
                    if (save_checkBox.isChecked()) {
                        PreferenceManager.setString(mContext, "id", editTextEmail.getText().toString());//id ???????????? ??????
                        PreferenceManager.setString(mContext, "pw", editTextPassword.getText().toString());//pw ???????????? ??????
                        PreferenceManager.setBoolean(mContext, "check", save_checkBox.isChecked()); //?????? ???????????? ?????? ??? ??????

                    }
                    else {
                        PreferenceManager.setBoolean(mContext, "check", save_checkBox.isChecked()); //?????? ???????????? ?????? ??? ??????
                        PreferenceManager.clear(mContext); //????????? ?????? ??????
                    }

                } else {
                    Toast.makeText(MainActivity.this, "????????? ??????????????? ???????????????.", Toast.LENGTH_LONG).show();
                    Log.d("asdfsadf", "onClick: ????????? ??????????????? ???????????????");
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
                            // ????????? ??????
                            Toast.makeText(MainActivity.this, "????????? ??????", Toast.LENGTH_SHORT).show();
                            Log.d("asdfsadf", "onClick: ????????? ??????");
                            firebaseAuth.addAuthStateListener(firebaseAuthListener);
                        } else {
                            // ????????? ??????
                            Toast.makeText(MainActivity.this, "????????? ?????? ??????????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                            Log.d("asdfsadf", "onClick: ????????? ?????? ??????????????? ???????????? ????????????.");
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

        builder.setTitle("????????????").setMessage("????????? ??????????????? ????????? ???????????????????\n(?????? ????????? ????????? ???????????? ?????? ??????????????? ???????????? ????????? ??? ??? ??????)");

        builder.setPositiveButton("??????", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                FirebaseMessaging.getInstance().subscribeToTopic("falldown7").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "??????";
                        if(!task.isSuccessful()){
                            msg = "??????";
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
                Toast.makeText(getApplicationContext(), "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("??????", new DialogInterface.OnClickListener(){
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

        builder.setTitle("??????").setMessage("????????? ?????????????????????.\n????????? ?????? ????????? ?????????????????? ??????????????? ?????? ??? ????????????.");
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Toast.makeText(getApplicationContext(), "?????????", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void exit(View view) {
        moveTaskToBack(true); // ???????????? ?????????????????? ??????
        finishAndRemoveTask(); // ???????????? ?????? + ????????? ??????????????? ?????????

        System.exit(0);
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
