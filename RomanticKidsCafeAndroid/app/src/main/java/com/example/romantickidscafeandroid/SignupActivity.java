package com.example.romantickidscafeandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.romantickidscafeandroid.Name;
import com.example.romantickidscafeandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private Button buttonJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT); }
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.editText_email);
        editTextPassword = (EditText) findViewById(R.id.editText_passWord);
        editTextName = (EditText) findViewById(R.id.editText_name);

        buttonJoin = (Button) findViewById(R.id.btn_join);
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                    // ???????????? ??????????????? ????????? ?????? ??????
                    createUser(editTextEmail.getText().toString(), editTextPassword.getText().toString(), editTextName.getText().toString());
                } else {
                    // ???????????? ??????????????? ????????? ??????
                    Toast.makeText(SignupActivity.this, "????????? ??????????????? ???????????????.", Toast.LENGTH_LONG).show();
                    Log.d("asdfsadf", "onClick: ????????? ??????????????? ???????????????");
                }
            }
        });
    }
    private void createUser(String email, String password, String name) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // ???????????? ?????????
                            Toast.makeText(SignupActivity.this, "???????????? ??????", Toast.LENGTH_SHORT).show();
                            Log.d("asdfsadf", "onClick: ???????????? ??????");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String e = user.getUid();
                            Log.d("dafsadf", "onComplete: "+e);
                            Name name = new Name(editTextName.getText().toString());
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("name");
                            mDatabase.child(e).setValue(name);
                            DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference("alarm");
                            nDatabase.child(e).setValue("-1");
                            finish();
                        } else {
                            // ????????? ????????? ??????
                            Toast.makeText(SignupActivity.this, "?????? ???????????? ??????????????? ????????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
                            Log.d("asdfsadf", "onClick: ?????? ???????????? ??????????????? ????????? ????????? ????????????.");
                        }
                    }
                });
    }
}