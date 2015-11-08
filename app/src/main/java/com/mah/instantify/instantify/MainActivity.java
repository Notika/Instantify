package com.mah.instantify.instantify;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;

import com.mah.instantify.instantify.LoginFragment.onShowQuestionListener;
import com.mah.instantify.instantify.QuestionFragment.onShowConfirmListener;
import com.mah.instantify.instantify.ConfirmationFragment.pullFromStackListener;

import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements
        onShowQuestionListener,
        onShowConfirmListener,
        pullFromStackListener {

    static String deviceId = "";
    static String lectureId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO: MOVE AND LOAD FROM XML FILE
        int colors[] = {0xff4cb897, 0xff2C868A, 0xff9F2525, 0xff9F2D75, 0xff3B5B9E, 0xff272623, 0xffEF862B};
        int idx = new Random().nextInt(colors.length);

        setActivityBackgroundColor(colors[idx]);

        if (savedInstanceState == null) {

            // Create and add first fragment (Login page) to an activity
            LoginFragment loginF = new LoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, loginF)
                    .commit();
        }
        // Get unique phone ID
        deviceId = getUniquePhoneId();
    }

    public String getUniquePhoneId() {
        final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;

        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        // Phone ID is 64 symbols long. Take only a last part of it.
        // TODO: maybe use all of 64 symbols to minimize collision
        String[] separated = deviceUuid.toString().split("-");
        return separated[4];
    }

    public void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }

    @Override
    public void eventShowQuestion(String elementId) {
        /** This is an implementation of  LoginFragment callback interface.
         *  When the activity receives a callback through the interface, it can share
         *  the information with other fragments in the layout as necessary.
         */
        // Create bundle...
        Bundle args = new Bundle();
        // Create new fragment and transaction
        QuestionFragment questionF = new QuestionFragment();

        // Get Lecture ID from user...
        args.putString("id", elementId);
        lectureId = elementId;

        //...and pass parameters to a second fragment - question page.
        questionF.setArguments(args);
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, questionF)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void eventShowConfirmation(String elementId) {
        /** This is an implementation of  QuestionFragment callback interface.
         */
        Bundle args = new Bundle();
        // Create new fragment and transaction
        ConfirmationFragment confirmF = new ConfirmationFragment();
        // Supply LECTURE ID input as an argument.
        args.putString("id", elementId);

        confirmF.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, confirmF)
                .addToBackStack(null)
                .commit();
    }

    public void pullFromStack() {
        getSupportFragmentManager().popBackStackImmediate();
    }
}
