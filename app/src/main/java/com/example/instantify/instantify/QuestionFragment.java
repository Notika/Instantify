package com.example.instantify.instantify;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
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

/**
 * Created by Nataly on 2015-10-17.
 */
public class QuestionFragment extends Fragment {

    Activity a;
    String id;
    EditText interestingThing;
    TextView lectureQuestion;
    int nmbKeys = 0;
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
            throw new ClassCastException(a.toString() + " must implement QuestionFragment");
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

        getPhoneId();

        // Get data from bundle
        id = getArguments().getString("id");

        Firebase.setAndroidContext(a.getApplicationContext());

        Firebase questionRef = new Firebase("https://instantify.firebaseio.com/Lecture_ID_" + id + "/011");
        Query queryRef = questionRef.orderByKey();

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                System.out.println("Number" + snapshot.getChildrenCount() + " The " + snapshot.getKey() + " lecture tables is " + snapshot.getValue());

                nmbKeys++;

                String txt = snapshot.getValue().toString();
                if (snapshot.getKey().contentEquals("Question")) {
                    lectureQuestion.setText(txt);
                }

                if (snapshot.getValue().toString().contains(deviceId)) {
                    alreadyAnswered = true;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

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

        Button submitB = (Button) view.findViewById(R.id.button3);
        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alreadyAnswered){
                    confirmListener.eventShowConfirmation("-255");
                } else {
                    submitAnswer();
                }
            }
        });

        return view;
    }

    private void submitAnswer() {
        Firebase myFirebaseRef = new Firebase("https://instantify.firebaseio.com/");

        Firebase questRef = myFirebaseRef.child("Lecture_ID_" + id).child("011").child("Answer" + String.valueOf(nmbKeys));
        questRef.setValue(interestingThing.getText().toString() + " deviceId:" + deviceId, new Firebase.CompletionListener() {
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

        nmbKeys = 0;
    }

    private void getPhoneId() {
        final TelephonyManager tm = (TelephonyManager) a.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(a.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String[] separated = deviceUuid.toString().split("-");
        deviceId = separated[4];
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        confirmListener = null;
    }

}