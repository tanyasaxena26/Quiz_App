package com.tanya.quiz;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity1 extends AppCompatActivity implements View.OnClickListener {
    TextView totalQuestionsTextView;
    TextView QuestionTextView;
    TextView timerTextView;
    Button ansA,ansB,ansC,ansD;
    Button submitBtn;
    int score=0;
    int totalQuestion=QA.question.length;
    int currentQuestionIndex=0;
    String selectedAnswer="";
    CountDownTimer timer;
    long timeRemainingInMillis;
    final long COUNTDOWN_INTERVAL = 1000;
    final long QUESTION_TIME_LIMIT = 30000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        totalQuestionsTextView=findViewById(R.id.total_question);
        QuestionTextView=findViewById(R.id.question);
        timerTextView=findViewById(R.id.timer);
        ansA=findViewById(R.id.ansA);
        ansB=findViewById(R.id.ansB);
        ansC=findViewById(R.id.ansC);
        ansD=findViewById(R.id.ansD);
        submitBtn=findViewById(R.id.submit);
        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        totalQuestionsTextView.setText("Total questions:"+totalQuestion);
        loadNewQuestion();
    }

    @Override
    public void onClick(View view) {
        ansA.setBackgroundColor(Color.WHITE);
        ansB.setBackgroundColor(Color.WHITE);
        ansC.setBackgroundColor(Color.WHITE);
        ansD.setBackgroundColor(Color.WHITE);
        Button clickedButton = (Button) view;

        if (clickedButton.getId() == R.id.submit) {
            if (!selectedAnswer.isEmpty()) {
                disableOptions();
                if (selectedAnswer.equals(QA.correctAnswers[currentQuestionIndex])) {
                    score++;
                }
                else{
                    displayCorrectAnswer();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentQuestionIndex++;
                        loadNewQuestion();
                    }
                }, 1000);
            } else {
                Toast.makeText(MainActivity1.this, "Please select an answer", Toast.LENGTH_SHORT).show();
            }
        } else {
            selectedAnswer = clickedButton.getText().toString();
            highlightSelectedOption(clickedButton);
            disableOptions();
        }
    }
    void highlightSelectedOption(Button selectedButton)
    {
        selectedButton.setBackgroundColor(
                selectedAnswer.equals(QA.correctAnswers[currentQuestionIndex]) ? Color.GREEN : Color.RED
        );
    }
    void disableOptions()
    {
        ansA.setEnabled(false);
        ansB.setEnabled(false);
        ansC.setEnabled(false);
        ansD.setEnabled(false);
    }
    void enableOptions()
    {
        ansA.setEnabled(true);
        ansB.setEnabled(true);
        ansC.setEnabled(true);
        ansD.setEnabled(true);
    }
    void loadNewQuestion()
    {
        if(currentQuestionIndex==totalQuestion)
        {
            finishQuiz();
            return;
        }
        QuestionTextView.setText(QA.question[currentQuestionIndex]);
        ansA.setText(QA.choices[currentQuestionIndex][0]);
        ansB.setText(QA.choices[currentQuestionIndex][1]);
        ansC.setText(QA.choices[currentQuestionIndex][2]);
        ansD.setText(QA.choices[currentQuestionIndex][3]);
        ansA.setBackgroundColor(Color.WHITE);
        ansB.setBackgroundColor(Color.WHITE);
        ansC.setBackgroundColor(Color.WHITE);
        ansD.setBackgroundColor(Color.WHITE);
        enableOptions();
        resetTimer();
        startTimer();
    }
    void resetTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timeRemainingInMillis = QUESTION_TIME_LIMIT;
    }
    void updateTimerUI() {
        long seconds = timeRemainingInMillis / 1000;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
        timerTextView.setText(timeLeftFormatted);
    }

    void startTimer() {
        timer = new CountDownTimer(timeRemainingInMillis, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemainingInMillis = millisUntilFinished;
                updateTimerUI();
            }

            @Override
            public void onFinish() {
                if (currentQuestionIndex < totalQuestion - 1) {
                    displayCorrectAnswer();
                    new Handler().postDelayed(() -> {
                        if (currentQuestionIndex < totalQuestion - 1) {
                            currentQuestionIndex++;
                            loadNewQuestion();
                        } else {
                            finishQuiz();
                        }
                    }, 2000);
                } else {
                    finishQuiz();
                }
            }
        }.start();
    }
    void displayCorrectAnswer()
    {
        int correctAnswerIndex = -1;
        for (int i = 0; i < QA.choices[currentQuestionIndex].length; i++) {
            if (QA.choices[currentQuestionIndex][i].equals(QA.correctAnswers[currentQuestionIndex])) {
                correctAnswerIndex = i;
                break;
            }
        }
        switch (correctAnswerIndex) {
            case 0:
                ansA.setBackgroundColor(Color.GREEN);
                break;
            case 1:
                ansB.setBackgroundColor(Color.GREEN);
                break;
            case 2:
                ansC.setBackgroundColor(Color.GREEN);
                break;
            case 3:
                ansD.setBackgroundColor(Color.GREEN);
                break;
        }
    }
    void finishQuiz()
    {
        String passStatus="";
        if(score>totalQuestion*0.5)
        {
            passStatus="You Have Passed !!\uD83D\uDE0A";
        }
        else {
            passStatus="You Have Failed !!\uD83D\uDE41";
        }
        new AlertDialog.Builder(this).setTitle(passStatus)
                .setMessage("Score is "+score+" out of "+totalQuestion)
                .setPositiveButton("Restart",((dialogInterface, i) -> restartQuiz()))
                .setNegativeButton("Exit", (dialogInterface, i) -> finish())
                .setCancelable(false)
                .show();
    }
    void restartQuiz()
    {
        score=0;
        currentQuestionIndex=0;
        loadNewQuestion();
        enableOptions();
    }
}