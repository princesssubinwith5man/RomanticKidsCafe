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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

public class HomeActivity extends AppCompatActivity {

    FirebaseFirestore db;
    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
    static ProgressBar pb;
    String temp;
    static int cnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        pb = (ProgressBar) findViewById(R.id.progressbar);
        pb.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        db = FirebaseFirestore.getInstance();
        FirebaseDatabase.getInstance().getReference("alarm").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String e = user.getUid();
                    String key = snapshot.child(e).getKey();
                    if(key.equals(e)) {
                        String alarm = snapshot.getValue(String.class);
                        Log.d(TAG, "onDataChddddange: " + alarm);
                        if(alarm.equals("-1")){
                            AlarmDialogue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference("fall_down").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //if(alarm == "-1"){

                    //}
                    FallDown s = snapshot.getValue(FallDown.class);
                    //String ss = snapshot.child("fall_down").getValue();
                    assert s != null;
                    Log.d(TAG, "onDataChange: "+s.name + " " + s.girlfriend);
                    if(s.girlfriend){
                        //createNotification();
                        HashMap<String, String> item = new HashMap<String, String>();
                        item.put("item1", "[속보] "+s.name+" 여자친구 생김");
                        item.put("item2", s.date);
                        list.add(item);
                        setListview();
                        FirebaseDatabase.getInstance().getReference("fall_down").child("test").child("girlfriend").setValue(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void setListview(){
        ListView listView =(ListView)findViewById(R.id.list);
        SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,new String[]{"item1","item2"}, new int[] {android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);
        //MySoundPlayer.initSounds(getApplicationContext());
        //MySoundPlayer.play(MySoundPlayer.SUCCESS);
    }

    public void ViewCam(View view) {
        Intent intent = new Intent(HomeActivity.this, CamActivity.class);
        startActivity(intent);
    }

    private void createNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("속보");
        builder.setContentText("홍이삭 여친 생김");

        builder.setColor(Color.RED);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationManager.notify(1, builder.build());
    }
    private void AlarmDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("알람허용").setMessage("아이가 넘어졌을때 알람을 받으시겠어요?\n(허용 안할시 아이가 넘어지는 것을 실시간으로 확인하지 않을시 알 수 없음)");

        builder.setPositiveButton("허용", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                FirebaseMessaging.getInstance().subscribeToTopic("falldown2").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "성공";
                        if(!task.isSuccessful()){
                            msg = "실패";
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
                FirebaseDatabase.getInstance().getReference("alarm").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String e = user.getUid();
                            FirebaseDatabase.getInstance().getReference("alarm").child(e).setValue("1");
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
                Toast.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_SHORT).show();
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void logout(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        Toast.makeText(getApplicationContext(), "로그아웃", Toast.LENGTH_SHORT).show();

        finish();
    }
}