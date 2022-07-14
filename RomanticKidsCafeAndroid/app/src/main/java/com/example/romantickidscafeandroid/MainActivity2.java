package com.example.romantickidscafeandroid;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity2 extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private CafeFragment cafeFragment = new CafeFragment();
    private SearchFragment searchFragment = new SearchFragment();
    private MyInformationFragment myInformationFragment = new MyInformationFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, cafeFragment).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
    }
    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                case R.id.navi_main:
                    transaction.replace(R.id.frameLayout, cafeFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commitAllowingStateLoss();
                    break;
                case R.id.navi_search:
                    transaction.replace(R.id.frameLayout, searchFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commitAllowingStateLoss();
                    break;
                case R.id.navi_information:
                    transaction.replace(R.id.frameLayout, myInformationFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }
}