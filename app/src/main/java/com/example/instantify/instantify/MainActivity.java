package com.example.instantify.instantify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.instantify.instantify.LoginFragment.onShowQuestionListener;
import com.example.instantify.instantify.QuestionFragment.onShowConfirmListener;

public class MainActivity extends AppCompatActivity implements
        onShowQuestionListener,
        onShowConfirmListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {

            // Create and add first fragment (Login page) to an activity
            LoginFragment loginF = new LoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, loginF)
                    .commit();
        }
    }

    @Override
    public void eventShowQuestion(String elementId) {
        /** This is an implementation of  LoginFragment callback interface.
         *  When the activity receives a callback through the interface, it can share
         *  the information with other fragments in the layout as necessary.
         */
        // Create bundle...
        Bundle args = new Bundle();
        // Create new fragment and transaction
        QuestionFragment questionF = new QuestionFragment();

        // Get Lecture ID from user...
        args.putString("id", elementId);

        //...and pass parameters to a second fragment - question page.
        questionF.setArguments(args);
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, questionF)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void eventShowConfirmation(String elementId) {
        /** This is an implementation of  QuestionFragment callback interface.
         */
        if (elementId.contentEquals("-255")) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Sorry! You've answered on this question already! Please go back and try another Lecture ID",
                    Toast.LENGTH_LONG);
            toast.show();
        } else {
            Bundle args = new Bundle();
            // Create new fragment and transaction
            ConfirmationFragment confirmF = new ConfirmationFragment();
            // Supply LECTURE ID input as an argument.
            args.putString("id", elementId);

            confirmF.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, confirmF)
                    .addToBackStack(null)
                    .commit();
        }
    }

}
