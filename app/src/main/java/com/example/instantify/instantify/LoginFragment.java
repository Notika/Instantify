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
                // Send event with Lection ID
                questionViewEventListener.eventShowQuestion(lectureId.getText().toString());
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