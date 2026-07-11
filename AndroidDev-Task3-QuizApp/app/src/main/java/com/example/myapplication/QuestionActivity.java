package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    private TextView tvQuestionNumber, tvQuestionText, tvScore;
    private Button btnOptionA, btnOptionB, btnOptionC, btnOptionD, btnNext;
    private ProgressBar progressBar;
    private LinearLayout optionsContainer;

    private List<Question> questionList;
    private int currentIndex = 0;
    private int score = 0;
    private int correctCount = 0;
    private int wrongCount = 0;
    private boolean answered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        tvQuestionNumber = findViewById(R.id.tv_question_number);
        tvQuestionText = findViewById(R.id.tv_question_text);
        tvScore = findViewById(R.id.tv_score);
        btnOptionA = findViewById(R.id.btn_option_a);
        btnOptionB = findViewById(R.id.btn_option_b);
        btnOptionC = findViewById(R.id.btn_option_c);
        btnOptionD = findViewById(R.id.btn_option_d);
        btnNext = findViewById(R.id.btn_next);
        progressBar = findViewById(R.id.progress_bar);
        optionsContainer = findViewById(R.id.options_container);

        questionList = createQuestionList();
        Collections.shuffle(questionList);

        View.OnClickListener optionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answered) return;
                answered = true;

                Button clickedBtn = (Button) v;
                int selectedOption = getOptionNumber(clickedBtn);
                int correctOption = questionList.get(currentIndex).getCorrectAnswer();

                disableAllOptions();
                highlightAnswer(clickedBtn, selectedOption, correctOption);

                if (selectedOption == correctOption) {
                    score += 10;
                    correctCount++;
                } else {
                    wrongCount++;
                }

                tvScore.setText("Score: " + score);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnNext.setVisibility(View.VISIBLE);
                        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
                        fadeIn.setDuration(300);
                        btnNext.startAnimation(fadeIn);
                    }
                }, 400);
            }
        };

        btnOptionA.setOnClickListener(optionClickListener);
        btnOptionB.setOnClickListener(optionClickListener);
        btnOptionC.setOnClickListener(optionClickListener);
        btnOptionD.setOnClickListener(optionClickListener);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNext.setVisibility(View.GONE);
                answered = false;
                currentIndex++;
                if (currentIndex < questionList.size()) {
                    loadQuestion();
                } else {
                    Intent intent = new Intent(QuestionActivity.this, ResultActivity.class);
                    intent.putExtra("SCORE", score);
                    intent.putExtra("CORRECT", correctCount);
                    intent.putExtra("WRONG", wrongCount);
                    intent.putExtra("TOTAL", questionList.size());
                    startActivity(intent);
                    finish();
                }
            }
        });

        loadQuestion();
    }

    private void loadQuestion() {
        Question q = questionList.get(currentIndex);
        int total = questionList.size();

        progressBar.setMax(total);
        progressBar.setProgress(currentIndex + 1);

        tvQuestionNumber.setText("Question " + (currentIndex + 1) + " of " + total);

        AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setDuration(150);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                tvQuestionText.setText(q.getQuestionText());
                btnOptionA.setText(q.getOptionA());
                btnOptionB.setText(q.getOptionB());
                btnOptionC.setText(q.getOptionC());
                btnOptionD.setText(q.getOptionD());

                resetOptionStyles();
                AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
                fadeIn.setDuration(200);
                optionsContainer.startAnimation(fadeIn);
                tvQuestionText.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        tvQuestionText.startAnimation(fadeOut);
        optionsContainer.startAnimation(fadeOut);
    }

    private void highlightAnswer(Button clickedBtn, int selected, int correct) {
        Button correctBtn = getButtonForOption(correct);
        correctBtn.setBackgroundResource(R.drawable.bg_option_correct);
        correctBtn.setTextColor(Color.WHITE);

        if (selected != correct) {
            clickedBtn.setBackgroundResource(R.drawable.bg_option_wrong);
            clickedBtn.setTextColor(Color.WHITE);
        }
    }

    private void resetOptionStyles() {
        Button[] buttons = {btnOptionA, btnOptionB, btnOptionC, btnOptionD};
        for (Button btn : buttons) {
            btn.setBackgroundResource(R.drawable.bg_option_normal);
            btn.setTextColor(getResources().getColor(R.color.text_primary));
            btn.setEnabled(true);
        }
    }

    private void disableAllOptions() {
        Button[] buttons = {btnOptionA, btnOptionB, btnOptionC, btnOptionD};
        for (Button btn : buttons) {
            btn.setEnabled(false);
        }
    }

    private int getOptionNumber(Button btn) {
        if (btn == btnOptionA) return 1;
        if (btn == btnOptionB) return 2;
        if (btn == btnOptionC) return 3;
        if (btn == btnOptionD) return 4;
        return 0;
    }

    private Button getButtonForOption(int option) {
        switch (option) {
            case 1: return btnOptionA;
            case 2: return btnOptionB;
            case 3: return btnOptionC;
            case 4: return btnOptionD;
            default: return btnOptionA;
        }
    }

    private List<Question> createQuestionList() {
        List<Question> list = new ArrayList<>();
        list.add(new Question("What is the chemical symbol for Gold?", "Au", "Ag", "Fe", "Cu", 1));
        list.add(new Question("Which planet is known as the Red Planet?", "Venus", "Mars", "Jupiter", "Saturn", 2));
        list.add(new Question("What is the powerhouse of the cell?", "Nucleus", "Ribosome", "Mitochondria", "Golgi Apparatus", 3));
        list.add(new Question("How many bones are in the adult human body?", "186", "206", "226", "256", 2));
        list.add(new Question("What gas do plants absorb from the atmosphere?", "Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen", 3));
        list.add(new Question("What is the speed of light approximately?", "300,000 km/s", "150,000 km/s", "500,000 km/s", "100,000 km/s", 1));
        list.add(new Question("Which element has the atomic number 1?", "Helium", "Lithium", "Oxygen", "Hydrogen", 4));
        list.add(new Question("What is the largest ocean on Earth?", "Atlantic", "Indian", "Arctic", "Pacific", 4));
        list.add(new Question("Who developed the theory of relativity?", "Isaac Newton", "Albert Einstein", "Niels Bohr", "Stephen Hawking", 2));
        list.add(new Question("What is the hardest natural substance on Earth?", "Quartz", "Topaz", "Diamond", "Corundum", 3));
        list.add(new Question("What is the pH value of pure water?", "5", "7", "9", "10", 2));
        list.add(new Question("Which organ is responsible for pumping blood?", "Lungs", "Liver", "Kidneys", "Heart", 4));
        return list;
    }
}