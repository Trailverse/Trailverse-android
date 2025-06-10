package com.trailverse.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;

import com.trailverse.app.ExploreFragment;
import com.trailverse.app.StartHikingFragment;
import com.trailverse.app.RankingFragment;
import com.trailverse.app.MyPageFragment;

import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();

            if (itemId == R.id.nav_explore) {
                selectedFragment = new ExploreFragment();
            } else if (itemId == R.id.nav_start) {
                selectedFragment = new StartHikingFragment();
            } else if (itemId == R.id.nav_ranking) {
                selectedFragment = new RankingFragment();
            } else if (itemId == R.id.nav_mypage) {
                selectedFragment = new MyPageFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });


        // 초기 화면
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_explore);
        }
    }
}
