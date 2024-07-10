package com.example.momfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.momfood.R;
import com.example.momfood.adapter.ViewPagerAdapter;
import com.example.momfood.util.localstorage.LocalStorage;

import java.util.Timer;
import java.util.TimerTask;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check if user is logged in
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_welcome);
    }

    public void onLetsClicked(View view) {
        startActivity(new Intent(getApplicationContext(), LoginRegisterActivity.class));
        finish();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}
