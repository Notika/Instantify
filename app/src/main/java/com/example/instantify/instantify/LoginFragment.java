package com.example.instantify.instantify;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by Nataly on 2015-10-17.
 */
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
        Firebase.setAndroidContext(a);
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
                Firebase ref = new Firebase("https://instantify.firebaseio.com/"+lectureId.getText().toString());
                ref.addListenerForSingleValueEvent(new  ValueEventListener(){

                    //Lecture ID validation
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()!= null) {
                            // Send event with Lection ID
                            questionViewEventListener.eventShowQuestion(lectureId.getText().toString());
                        }
                        else {
                            //If ID does not exist in database, respnd with a toaster message.
                            Toast.makeText(a, "Incorrect Lecture ID",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
                // Send event with Lection ID
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        questionViewEventListener = null;
    }
}