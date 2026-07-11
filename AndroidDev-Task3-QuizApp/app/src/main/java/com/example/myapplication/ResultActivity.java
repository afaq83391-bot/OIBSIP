package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private TextView tvFinalScore, tvCorrect, tvWrong, tvTotal, tvMessage, tvGradeIcon;
    private Button btnRestart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvGradeIcon = findViewById(R.id.tv_grade_icon);
        tvFinalScore = findViewById(R.id.tv_final_score);
        tvCorrect = findViewById(R.id.tv_correct);
        tvWrong = findViewById(R.id.tv_wrong);
        tvTotal = findViewById(R.id.tv_total);
        tvMessage = findViewById(R.id.tv_message);
        btnRestart = findViewById(R.id.btn_restart);

        int score = getIntent().getIntExtra("SCORE", 0);
        int correct = getIntent().getIntExtra("CORRECT", 0);
        int wrong = getIntent().getIntExtra("WRONG", 0);
        int total = getIntent().getIntExtra("TOTAL", 0);

        int percentage = (int) ((double) correct / total * 100);

        tvFinalScore.setText(score + " / " + (total * 10));
        tvCorrect.setText(String.valueOf(correct));
        tvWrong.setText(String.valueOf(wrong));
        tvTotal.setText(String.valueOf(total));

        if (percentage >= 80) {
            tvGradeIcon.setText("🏆");
            tvMessage.setText("Outstanding! You're a genius!");
        } else if (percentage >= 60) {
            tvGradeIcon.setText("⭐");
            tvMessage.setText("Great job! Keep it up!");
        } else if (percentage >= 40) {
            tvGradeIcon.setText("📚");
            tvMessage.setText("Not bad, but room to improve.");
        } else {
            tvGradeIcon.setText("💪");
            tvMessage.setText("Don't give up! Try again!");
        }

        Animation scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        tvGradeIcon.startAnimation(scaleIn);
        tvFinalScore.startAnimation(slideUp);
        tvMessage.startAnimation(slideUp);

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}