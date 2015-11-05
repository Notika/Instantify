package com.example.instantify.instantify;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

public class QuestionFragment extends Fragment {
    //TODO: FIX FRAGMENT STACK ORDER.
    Activity a;
    Firebase questionRef = null;
    static String id;
    EditText interestingThing;
    TextView lectureQuestion;
    boolean alreadyAnswered = false;
    ChildEventListener listener;

    public QuestionFragment() {
    }

    public interface onShowConfirmListener {
        void eventShowConfirmation(String elementId);
    }

    onShowConfirmListener confirmListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            a = (Activity) context;
            confirmListener = (onShowConfirmListener) a;
        } catch (ClassCastException e) {
            throw new ClassCastException(a.toString() + " must implement onShowConfirmListener");
        }
    }

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.question_fragment, container, false);

        interestingThing = (EditText) view.findViewById(R.id.editText2);
        lectureQuestion = (TextView) view.findViewById(R.id.textView3);

        // Set filter for restriction input text chars to 0-9, a-z, A-Z and spacebar only.
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        //interestingThing.setFilters(new InputFilter[]{filter});
        interestingThing.addTextChangedListener(new TextValidator(interestingThing) {
            @Override
            public void validate(TextView textView, String text) {
                /* Validation code here */
                if (text.length() > 2) {
                    Resources res = a.getResources();
                    String[] cleanWords = res.getStringArray(R.array.wordExceptions); //call the index array

                    for (int i = 0; i < cleanWords.length; i++) {
                        if (text.toLowerCase().contains(cleanWords[i])) {
                            textView.setText("");
                            break;
                        }
                    }
                }
            }
        });

        Button backB = (Button) view.findViewById(R.id.button2);
        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pop the last fragment transition from the manager's fragment
                // back stack and return to previous page.
                getFragmentManager().popBackStackImmediate();
            }
        });

        Button submitB = (Button) view.findViewById(R.id.button3);
        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alreadyAnswered) {
                    Toast toast = Toast.makeText(a.getApplicationContext(),
                            "Sorry! Already answered!",
                            Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    //temp. test check
                    if (!interestingThing.getText().toString().isEmpty()) {
                        submitAnswer();
                        interestingThing.setText("");
                    }
                }
                InputMethodManager imm = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(interestingThing.getWindowToken(), 0);
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Get data from bundle
        id = MainActivity.lectureId;
        // // Get a reference to our Lecture IDs
        questionRef = new Firebase("https://instantify.firebaseio.com/" + id);

        // Attach an listener to read the data at our IDs reference
        Firebase activeRef = questionRef.child("active_question");
        final Firebase answerRef = questionRef.child("answers");

        // Value event listener for active question
        //This event listener runs once and then everytime there is update
        activeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lectureQuestion.setText(dataSnapshot.getValue().toString());

                // Checking if device id already exist among answers.
                answerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(MainActivity.deviceId.toString())) {
                            alreadyAnswered = true;
                        } else {
                            alreadyAnswered = false;
                        }
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        //leave empty
                    }
                });
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("DB", firebaseError.getMessage());
            }
        });
    }

    private void submitAnswer() {
        Firebase myFirebaseRef = new Firebase("https://instantify.firebaseio.com");
        Firebase answertRef = myFirebaseRef.child(id).child("answers").child(MainActivity.deviceId);

        Log.d("TEST", id);
        Log.d("TEST", interestingThing.getText().toString());

        answertRef.setValue(interestingThing.getText().toString(), new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d("DB", firebaseError.getMessage());
                } else {
                    confirmListener.eventShowConfirmation(id);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}