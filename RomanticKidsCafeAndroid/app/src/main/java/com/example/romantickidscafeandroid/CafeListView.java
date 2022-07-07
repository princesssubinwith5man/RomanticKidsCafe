package com.example.romantickidscafeandroid;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CafeListView extends AppCompatActivity {
    ListViewAdapter adapter = new ListViewAdapter();
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_list_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().getReference("cafe").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ListView listview = (ListView)findViewById(R.id.cafelist);
                ArrayList<String> cafeUrl = new ArrayList<String>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String CafeName = snapshot.getKey();
                    //Log.d(TAG, "cafe name is : "+ CafeName);
                    Cafe c = snapshot.getValue(Cafe.class);
                    assert c != null;
                    //Log.d(TAG, "onDataChange: "+c.address + " " + c.url + " " + CafeName);
                    cafeUrl.add(c.url);
                    adapter.addItem(CafeName, c.address);
                    listview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    //setListview();
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(CafeListView.this, HomeActivity.class);
                            intent.putExtra("url",cafeUrl.get(i));
                            startActivity(intent);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void logout(View view) {
        //firebaseAuth.removeAuthStateListener(firebaseAuthListener);


        firebaseAuth.signOut();
        finish();
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