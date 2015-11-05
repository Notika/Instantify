package com.example.instantify.instantify;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

public class LoginFragment extends Fragment {
    Activity a;
    EditText lectureId;

    public LoginFragment() {
    }

    // Container Activity must implement this interface
    public interface onShowQuestionListener {
        void eventShowQuestion(String elementId);
    }

    onShowQuestionListener questionViewEventListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            /** On success, the questionViewEventListener member holds a reference
             *  to activity's implementation of onShowQuestionListener,
             *  so that LoginFragment can share events with the activity by calling methods
             *  defined by the onShowQuestionListener interface.
             */
            a = (Activity) context;
            questionViewEventListener = (onShowQuestionListener) a;
        } catch (ClassCastException e) {
            // If the activity has not implemented the interface - the fragment throws a ClassCastException.
            throw new ClassCastException(a.toString() + " must implement onShowQuestionListener");
        }
    }

    /**
     * This method will only be called once when the retained Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        lectureId = (EditText) view.findViewById(R.id.editText);
        Button login = (Button) view.findViewById(R.id.button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = lectureId.getText().toString();
                // Check input value, only not null allowed
                if (!input.equals("")) {
                    // Send event with Lection ID
                    getLectureQuestion(input);
                } else {
                    Toast toast = Toast.makeText(a.getApplicationContext(),
                            "Sorry! Empty value is not allowed",
                            Toast.LENGTH_LONG);
                    toast.show();
                }

                InputMethodManager imm = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(lectureId.getWindowToken(), 0);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //questionViewEventListener = null;
    }

    private void getLectureQuestion(String questionID) {
        /** The Firebase library must be initialized once with an Android context.
         *  This must happen before any Firebase app reference is created or used. */
        Firebase.setAndroidContext(a.getApplicationContext());

        Firebase questionRef = new Firebase("https://instantify.firebaseio.com/" + questionID);
        Query queryRef = questionRef.orderByKey();

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    if (snapshot.hasChild("active_question")) {
                        questionViewEventListener.eventShowQuestion(lectureId.getText().toString());
                    } else {
                        // No such lection ID
                        Toast toast = Toast.makeText(a.getApplicationContext(),
                                "Please enter a lecture ID.",
                                Toast.LENGTH_LONG);
                        toast.show();
                    }
                } catch (NullPointerException e) {

                    // No such lection ID
                    Toast toast = Toast.makeText(a.getApplicationContext(),
                            "Sorry! The ID you have entered is not valid! Please try another Lecture ID",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }
}