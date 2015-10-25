package com.example.instantify.instantify;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

public class QuestionFragment extends Fragment {

    Activity a;
    String id;
    EditText interestingThing;
    TextView lectureQuestion;

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

        // Get data from bundle
        id = getArguments().getString("id");
        // // Get a reference to our Lecture IDs
        Firebase questionRef = new Firebase("https://instantify.firebaseio.com/ID_" + id);
        Query queryRef = questionRef.orderByKey();

        // Attach an listener to read the data at our IDs reference
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                System.out.println("Key: " + snapshot.getKey() + " , value: " + snapshot.getValue());

                if (snapshot.getKey().contentEquals("active_question")) {
                    lectureQuestion.setText(snapshot.getValue().toString());
                }

                if (snapshot.getKey().contentEquals("answers")) {
                    if (snapshot.getValue().toString().contains(MainActivity.deviceId)) {
                        Toast toast = Toast.makeText(a.getApplicationContext(),
                                "Sorry! You've answered on this question already! Please try another Lecture ID",
                                Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            }

            // Get the data on a record that has changed
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println("Key: " + dataSnapshot.getKey() + " , value: " + dataSnapshot.getValue());
                lectureQuestion.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                lectureQuestion.setText("No questions today!");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }

        });

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
        interestingThing.setFilters(new InputFilter[]{filter});
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
                submitAnswer();
            }
        });

        return view;
    }

    private void submitAnswer() {
        Firebase myFirebaseRef = new Firebase("https://instantify.firebaseio.com");

        Firebase answertRef = myFirebaseRef.child("/ID_" + id).child("answers").child(MainActivity.deviceId);
        answertRef.setValue(interestingThing.getText().toString(), new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    System.out.println("Data could not be saved. " + firebaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                    confirmListener.eventShowConfirmation(id);
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        confirmListener = null;
    }
}