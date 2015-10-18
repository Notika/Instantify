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

    public interface onShowQuestionListener {
        void eventShowQuestion(String elementId);
    }

    onShowQuestionListener questionViewEventListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            a = (Activity) context;
            questionViewEventListener = (onShowQuestionListener) a;
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
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        lectureId = (EditText) view.findViewById(R.id.editText);
        Button login = (Button) view.findViewById(R.id.button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                questionViewEventListener.eventShowQuestion(lectureId.getText().toString());
            }
        });
        return view;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        questionViewEventListener = null;
        }
}
