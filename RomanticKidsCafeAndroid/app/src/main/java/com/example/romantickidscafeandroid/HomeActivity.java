package com.example.romantickidscafeandroid;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kyleduo.switchbutton.SwitchButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.TimeZone;

public class HomeActivity extends AppCompatActivity {
    ListViewAdapter adapter = new ListViewAdapter();
    List<Object> Array = new ArrayList<Object>();
    static ArrayList<FallDown> fallDownArrayList = new ArrayList<FallDown>();
    FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
    String url;
    String cafename;
    String topic_name;
    Cafe c = new Cafe();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ListView listview = (ListView)findViewById(R.id.list);
        TextView textView = (TextView)findViewById(R.id.cafename);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        cafename = intent.getStringExtra("name");
        textView.setText(cafename);

        String DeviceID = getDeviceId(this);

        FirebaseDatabase.getInstance().getReference("cafe").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    c = snapshot.getValue(Cafe.class);
                    //Array.add(c);
                    if(snapshot.getKey().equals(cafename)) {
                        Log.d(TAG, "equals(cafename): "+ c.topic_name);
                        topic_name = c.topic_name;
                        Log.d(TAG, "topic_name: " + topic_name );


                        SwitchButton switchButton = (SwitchButton) findViewById(R.id.sb_use_listener);
                        FirebaseDatabase.getInstance().getReference("real_alarm").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                fallDownArrayList.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    if(snapshot.getKey().equals(DeviceID)) {
                                        Log.d(TAG, "topic_name: " + topic_name );
                                        if (snapshot.child(cafename).getValue() != null && snapshot.child(cafename).getValue().toString().equals("1")) {
                                            switchButton.setChecked(true);
                                            FirebaseMessaging.getInstance().subscribeToTopic(topic_name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    String msg = "성공 falldown7";
                                                    if(!task.isSuccessful()){
                                                        msg = "실패";
                                                    }
                                                    Log.d(TAG, msg);
                                                }
                                            });
                                        }
                                        else {
                                            switchbutton_ctrl(switchButton,DeviceID,topic_name);
                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic_name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    String msg = "성공falldown7";
                                                    if(!task.isSuccessful()){
                                                        msg = "실패";
                                                    }
                                                    Log.d(TAG, msg);
                                                }
                                            });
                                        }
                                    }
                                    else{
                                        switchbutton_ctrl(switchButton,DeviceID,topic_name);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        //Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
        //db = FirebaseFirestore.getInstance();
        /*for(int i = 0;i<fallDownArrayList.size();i++){
            adapter.addItem("[속보] "+fallDownArrayList.get(i).name+" 넘어짐", fallDownArrayList.get(i).date);
            listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(HomeActivity.this, CamActivity.class);
                    intent.putExtra("url",url);
                    startActivity(intent);
                }
            });
        }*/

        FirebaseDatabase.getInstance().getReference("fall_down").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fallDownArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //Log.d(TAG, "snapshot.getKey() = "+snapshot.getKey());
                    if(snapshot.getKey().equals(cafename)) {
                        Log.d(TAG, "snapshot.getKey() = "+snapshot.getKey());
                        FallDown s = snapshot.getValue(FallDown.class);
                        assert s != null;
                        if (s.fall_down) {
                            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy년 MM월 dd일");
                            Date now = new Date();
                            String nowTime1 = sdf1.format(now);

                            s.date = nowTime1;

                            fallDownArrayList.add(s);
                            adapter.addItem("[속보] " + s.name + " 넘어짐", nowTime1);
                            listview.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(HomeActivity.this, CamActivity.class);
                                    intent.putExtra("url", url);
                                    startActivity(intent);
                                }
                            });
                            FirebaseDatabase.getInstance().getReference("fall_down").child(cafename).child("fall_down").setValue(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void ViewCam(View view) {
        Intent intent = new Intent(HomeActivity.this, CamActivity.class);
        intent.putExtra("url",url);
        startActivity(intent);
    }


    public void switchbutton_ctrl(SwitchButton switchButton, String deviceID, String topic_name){
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                // 스위치 버튼이 체크되었는지 검사하여 텍스트뷰에 각 경우에 맞게 출력합니다.
                if (isChecked) {
                    FirebaseDatabase.getInstance().getReference("real_alarm").child(deviceID).child(cafename).setValue(1);
                    FirebaseMessaging.getInstance().subscribeToTopic(topic_name).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "성공 falldown7";
                            if(!task.isSuccessful()){
                                msg = "실패";
                            }
                            Log.d(TAG, msg);
                        }
                    });

                } else {
                    FirebaseDatabase.getInstance().getReference("real_alarm").child(deviceID).child(cafename).setValue(0);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic_name).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "성공falldown8";
                            if(!task.isSuccessful()){
                                msg = "실패";
                            }
                            Log.d(TAG, msg);
                        }
                    });

                }
            }
        });
    }

    public static String getDeviceId(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void logout(View view) {
        firebaseAuth.signOut();
        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

        Toast.makeText(getApplicationContext(), "로그아웃", Toast.LENGTH_SHORT).show();


    }
    /*public static String getTopicName(String cafename){
        final String[] topicname = new String[1];
        FirebaseDatabase.getInstance().getReference("cafe").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getKey().equals(cafename)) {
                        Log.d(TAG, "equals(cafename): "+ snapshot.child("topic_name").getValue().toString());
                        topicname[0] = snapshot.child("topic_name").getValue().toString();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //return topicname[0];
    }*/
}