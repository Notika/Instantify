package com.example.instantify.instantify;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.UUID;


public class QuestionFragment extends Fragment {

    Activity a;
    String id;
    EditText interestingThing;
    TextView lectureQuestion;
    String deviceId = "";
    boolean alreadyAnswered = false;

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

        // Get unique phone ID
        getPhoneId();

        // Get data from bundle
        id = getArguments().getString("id");

        /** The Firebase library must be initialized once with an Android context.
         *  This must happen before any Firebase app reference is created or used. */
        Firebase.setAndroidContext(a.getApplicationContext());

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
                    if (snapshot.getValue().toString().contains(deviceId)) {
                        alreadyAnswered = true;
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
        interestingThing.setFilters(new InputFilter[] { filter });


        Button submitB = (Button) view.findViewById(R.id.button3);
        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alreadyAnswered) {
                    confirmListener.eventShowConfirmation("-255");
                } else {
                    submitAnswer();
                }
            }
        });

        return view;
    }

    private void submitAnswer() {
        Firebase myFirebaseRef = new Firebase("https://instantify.firebaseio.com");

        Firebase answertRef = myFirebaseRef.child("/ID_" + id).child("answers").child(deviceId);
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

    private void getPhoneId() {

        final TelephonyManager tm = (TelephonyManager) a.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;

        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(a.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        // Phone ID is 64 symbols long. Take only a last part of it.
        String[] separated = deviceUuid.toString().split("-");
        deviceId = separated[4];
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        confirmListener = null;
    }
}