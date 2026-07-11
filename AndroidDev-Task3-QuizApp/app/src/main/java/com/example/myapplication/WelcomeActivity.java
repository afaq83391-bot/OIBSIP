package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Button btnStart;
    private TextView tvTitle, tvSubtitle, tvIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        tvIcon = findViewById(R.id.tv_icon);
        tvTitle = findViewById(R.id.tv_title);
        tvSubtitle = findViewById(R.id.tv_subtitle);
        btnStart = findViewById(R.id.btn_start);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Animation scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in);

        tvIcon.startAnimation(scaleIn);
        tvTitle.startAnimation(fadeIn);
        tvSubtitle.startAnimation(slideUp);
        btnStart.startAnimation(slideUp);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, QuestionActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}