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
import android.widget.TextView;

/**
 * Created by Nataly on 2015-10-17.
 */

public class ConfirmationFragment extends Fragment {

    Activity a;

    public ConfirmationFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            a = (Activity) context;

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

        TextView lectureId = (TextView) view.findViewById(R.id.textView1);
        lectureId.setText("LECTURE ID: " + getArguments().getString("id"));
        Button logout = (Button) view.findViewById(R.id.logoutB);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                a.finish();
            }
        });
        return view;
    }
}
