package com.example.instantify.instantify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.instantify.instantify.LoginFragment.onShowQuestionListener;
import com.example.instantify.instantify.QuestionFragment.onShowConfirmListener;

public class MainActivity extends AppCompatActivity implements onShowQuestionListener,
                                                                onShowConfirmListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            LoginFragment loginF = new LoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, loginF)
                    .commit();
        }
    }

    @Override
    public void eventShowQuestion(String elementId) {

        Bundle args = new Bundle();
        QuestionFragment questionF = new QuestionFragment();

        args.putString("id", elementId);

        questionF.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, questionF)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void eventShowConfirmation(String elementId) {

        Bundle args = new Bundle();
        ConfirmationFragment confirmF = new ConfirmationFragment();

        args.putString("id", elementId);

        confirmF.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, confirmF)
                .commit();
    }

}
