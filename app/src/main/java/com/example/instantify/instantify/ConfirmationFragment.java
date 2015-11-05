package com.example.instantify.instantify;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class ConfirmationFragment extends Fragment {

    Activity a;
    static String id;
    boolean check = false;
    pullFromStackListener mPullFromStack;
    Firebase activeRef;
    ValueEventListener listener;

    public ConfirmationFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            a = (Activity) context;
            mPullFromStack = (pullFromStackListener) a;
        } catch (ClassCastException e) {
            throw new ClassCastException(a.toString() + " must implement LoginFragment");
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirmation_fragment, container, false);

        id = getArguments().getString("id");
        // Show some text info and Lecture ID for future use
        TextView lectureId = (TextView) view.findViewById(R.id.textView1);
        lectureId.setText("LECTURE ID: " + id);

        // Get a reference to Logout button and create onClock listener
        Button logout = (Button) view.findViewById(R.id.logoutB);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish activity and close an application.
                a.finish();
            }
        });

        Firebase idRef = new Firebase("https://instantify.firebaseio.com/" + id);
        activeRef = idRef.child("active_question");
        //Listen for new question and pulls QuestionFragment back from stack
        activeRef.addValueEventListener(listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(check){
                    Log.d("TEST", "TRYING TO PULL FROM STACK");
                    mPullFromStack.pullFromStack();

                } else {
                    check = true;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //leave empty
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        activeRef.removeEventListener(listener);
        super.onPause();
    }

    public interface pullFromStackListener {
        void pullFromStack();
    }
}
