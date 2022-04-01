package com.example.romantickidscafeandroid;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
        FirebaseMessaging.getInstance().subscribeToTopic("falldown").addOnCompleteListener(new OnCompleteListener<Void>() {
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
        db = FirebaseFirestore.getInstance();
        FirebaseDatabase.getInstance().getReference("fall_down").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    FallDown s = snapshot.getValue(FallDown.class);
                    //String ss = snapshot.child("fall_down").getValue();
                    assert s != null;
                    Log.d(TAG, "onDataChange: "+s.name + " " + s.girlfriend);
                    if(s.girlfriend){
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
        MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.success);
        //MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.success);
        mediaPlayer.start();
    }
}