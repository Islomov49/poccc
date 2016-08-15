package com.jim.pocketaccounter.helper.password;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jim.pocketaccounter.R;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.TransitionManager;

public class PasswordWindow extends LinearLayout implements View.OnClickListener{
    private String password = "";
    private int passwordSize = 4;
    Animation anim;
    private OnPasswordRightEntered onPasswordRightEntered = null;
    private String currentPassword = "";
    private SingleSign ssFirst, ssSecond, ssThird, ssFourth;
    private TextView tvPassword;
    private SharedPreferences preferences ;
    Vibrator v;
    public PasswordWindow(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.password_layout ,this);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        anim= AnimationUtils.loadAnimation(getContext(), R.anim.wobble);
        ssFirst = (SingleSign) findViewById(R.id.ssFirst);
        ssSecond = (SingleSign) findViewById(R.id.ssSecond);
        ssThird = (SingleSign) findViewById(R.id.ssThird);
        ssFourth = (SingleSign) findViewById(R.id.ssFourth);
        tvPassword = (TextView) findViewById(R.id.ivPassword);
        findViewById(R.id.rlPassZero).setOnClickListener(this);
        findViewById(R.id.rlPassOne).setOnClickListener(this);
        findViewById(R.id.rlPassTwo).setOnClickListener(this);
        findViewById(R.id.rlPassThree).setOnClickListener(this);
        findViewById(R.id.rlPassFour).setOnClickListener(this);
        findViewById(R.id.rlPassFive).setOnClickListener(this);
        findViewById(R.id.rlPassSix).setOnClickListener(this);
        findViewById(R.id.rlPassSeven).setOnClickListener(this);
        findViewById(R.id.rlPassEight).setOnClickListener(this);
        findViewById(R.id.rlPassNine).setOnClickListener(this);
        findViewById(R.id.rlPassBack).setOnClickListener(this);
        findViewById(R.id.exitAp).setOnClickListener(this);

        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        
    }

    public PasswordWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.password_layout ,this);
        anim= AnimationUtils.loadAnimation(getContext(), R.anim.wobble);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        ssFirst = (SingleSign) findViewById(R.id.ssFirst);
        ssSecond = (SingleSign) findViewById(R.id.ssSecond);
        ssThird = (SingleSign) findViewById(R.id.ssThird);
        ssFourth = (SingleSign) findViewById(R.id.ssFourth);
        tvPassword = (TextView) findViewById(R.id.ivPassword);
        findViewById(R.id.rlPassZero).setOnClickListener(this);
        findViewById(R.id.rlPassOne).setOnClickListener(this);
        findViewById(R.id.rlPassTwo).setOnClickListener(this);
        findViewById(R.id.rlPassThree).setOnClickListener(this);
        findViewById(R.id.rlPassFour).setOnClickListener(this);
        findViewById(R.id.rlPassFive).setOnClickListener(this);
        findViewById(R.id.rlPassSix).setOnClickListener(this);
        findViewById(R.id.rlPassSeven).setOnClickListener(this);
        findViewById(R.id.rlPassEight).setOnClickListener(this);
        findViewById(R.id.rlPassNine).setOnClickListener(this);
        findViewById(R.id.rlPassBack).setOnClickListener(this);
        findViewById(R.id.exitAp).setOnClickListener(this);

        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public PasswordWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.password_layout ,this);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        anim= AnimationUtils.loadAnimation(getContext(), R.anim.wobble);
        ssFirst = (SingleSign) findViewById(R.id.ssFirst);
        ssSecond = (SingleSign) findViewById(R.id.ssSecond);
        ssThird = (SingleSign) findViewById(R.id.ssThird);
        ssFourth = (SingleSign) findViewById(R.id.ssFourth);
        tvPassword = (TextView) findViewById(R.id.ivPassword);
        findViewById(R.id.rlPassZero).setOnClickListener(this);
        findViewById(R.id.rlPassOne).setOnClickListener(this);
        findViewById(R.id.rlPassTwo).setOnClickListener(this);
        findViewById(R.id.rlPassThree).setOnClickListener(this);
        findViewById(R.id.rlPassFour).setOnClickListener(this);
        findViewById(R.id.rlPassFive).setOnClickListener(this);
        findViewById(R.id.rlPassSix).setOnClickListener(this);
        findViewById(R.id.rlPassSeven).setOnClickListener(this);
        findViewById(R.id.rlPassEight).setOnClickListener(this);
        findViewById(R.id.rlPassNine).setOnClickListener(this);
        findViewById(R.id.rlPassBack).setOnClickListener(this);
        findViewById(R.id.exitAp).setOnClickListener(this);

        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PasswordWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.password_layout ,this);

        anim= AnimationUtils.loadAnimation(getContext(), R.anim.wobble);
        ssFirst = (SingleSign) findViewById(R.id.ssFirst);
        ssSecond = (SingleSign) findViewById(R.id.ssSecond);
        ssThird = (SingleSign) findViewById(R.id.ssThird);
        ssFourth = (SingleSign) findViewById(R.id.ssFourth);
        tvPassword = (TextView) findViewById(R.id.ivPassword);
        findViewById(R.id.rlPassZero).setOnClickListener(this);
        findViewById(R.id.rlPassOne).setOnClickListener(this);
        findViewById(R.id.rlPassTwo).setOnClickListener(this);
        findViewById(R.id.rlPassThree).setOnClickListener(this);
        findViewById(R.id.rlPassFour).setOnClickListener(this);
        findViewById(R.id.rlPassFive).setOnClickListener(this);
        findViewById(R.id.rlPassSix).setOnClickListener(this);
        findViewById(R.id.rlPassSeven).setOnClickListener(this);
        findViewById(R.id.rlPassEight).setOnClickListener(this);
        findViewById(R.id.rlPassNine).setOnClickListener(this);
        findViewById(R.id.rlPassBack).setOnClickListener(this);
        findViewById(R.id.exitAp).setOnClickListener(this);

        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void setOnPasswordRightEnteredListener(OnPasswordRightEntered onPasswordRightEnteredListener) {
        this.onPasswordRightEntered = onPasswordRightEnteredListener;
    }

    @Override
    public void onClick(View view) {
        currentPassword = preferences.getString("password", "1234");
        ChangeBounds trans=new ChangeBounds();

        TransitionManager.beginDelayedTransition(this,trans);

        switch (view.getId()) {
            case R.id.rlPassZero:
                tvPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_secondary_text));
                if (password.length() == passwordSize-1) {
                    ssThird.makeCircleFaster();
                    ssFourth.setVisibility(VISIBLE);
                    ssFourth.setSign(0);
                    password += "0";
                    if (password.matches(currentPassword)) {
                        onPasswordRightEntered.onPasswordRight();
                        ssFirst.setVisibility(GONE);
                        ssSecond.setVisibility(GONE);
                        ssThird.setVisibility(GONE);
                        ssFourth.setVisibility(GONE);
                        password = "";
                    }
                    else {
                         tvPassword.startAnimation(anim);
                        v.vibrate(40);
                        ssFirst.clearSign();
                        ssFirst.setVisibility(GONE);
                        ssSecond.clearSign();
                        ssSecond.setVisibility(GONE);
                        ssThird.clearSign();
                        ssThird.setVisibility(GONE);
                        ssFourth.clearSign();
                        ssFourth.setVisibility(GONE);
                        password = "";
                    }
                }
                else {
                    if (password.length() == 4) return;
                    switch (password.length()) {
                        case 0:
                            ssFirst.setVisibility(VISIBLE);
                            ssFirst.setSign(0);
                            break;
                        case 1:
                            ssFirst.makeCircleFaster();
                            ssSecond.setVisibility(VISIBLE);
                            ssSecond.setSign(0);
                            break;
                        case 2:
                            ssSecond.makeCircleFaster();
                            ssThird.setVisibility(VISIBLE);
                            ssThird.setSign(0);
                            break;
                    }
                    password += "0";
                }
                break;
            case R.id.rlPassOne:
                tvPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_secondary_text));
                if (password.length() == passwordSize-1) {
                    ssThird.makeCircleFaster();
                    ssFourth.setVisibility(VISIBLE);
                    ssFourth.setSign(1);
                    password += "1";
                    if (password.matches(currentPassword)) {
                        onPasswordRightEntered.onPasswordRight();
                        ssFirst.setVisibility(GONE);
                        ssSecond.setVisibility(GONE);
                        ssThird.setVisibility(GONE);
                        ssFourth.setVisibility(GONE);
                        password = "";
                    }
                    else {
                         tvPassword.startAnimation(anim);
                        v.vibrate(100);
                        ssFirst.clearSign();
                        ssFirst.setVisibility(GONE);
                        ssSecond.clearSign();
                        ssSecond.setVisibility(GONE);
                        ssThird.clearSign();
                        ssThird.setVisibility(GONE);
                        ssFourth.clearSign();
                        ssFourth.setVisibility(GONE);
                        password = "";
                    }
                }
                else {
                    if (password.length() == 4) return;
                    switch (password.length()) {
                        case 0:
                            ssFirst.setVisibility(VISIBLE);
                            ssFirst.setSign(1);
                            break;
                        case 1:
                            ssFirst.makeCircleFaster();
                            ssSecond.setVisibility(VISIBLE);
                            ssSecond.setSign(1);
                            break;
                        case 2:
                            ssSecond.makeCircleFaster();
                            ssThird.setVisibility(VISIBLE);
                            ssThird.setSign(1);
                            break;
                    }
                    password += "1";
                }
                break;
            case R.id.rlPassTwo:
                tvPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_secondary_text));
                if (password.length() == passwordSize-1) {
                    ssThird.makeCircleFaster();
                    ssFourth.setVisibility(VISIBLE);
                    ssFourth.setSign(2);
                    password += "2";
                    if (password.matches(currentPassword)) {
                        onPasswordRightEntered.onPasswordRight();
                        ssFirst.setVisibility(GONE);
                        ssSecond.setVisibility(GONE);
                        ssThird.setVisibility(GONE);
                        ssFourth.setVisibility(GONE);
                        password = "";
                    }
                    else {
                         tvPassword.startAnimation(anim);                         v.vibrate(100);
                        ssFirst.clearSign();
                        ssFirst.setVisibility(GONE);
                        ssSecond.clearSign();
                        ssSecond.setVisibility(GONE);
                        ssThird.clearSign();
                        ssThird.setVisibility(GONE);
                        ssFourth.clearSign();
                        ssFourth.setVisibility(GONE);
                        password = "";
                    }
                }
                else {
                    if (password.length() == 4) return;
                    switch (password.length()) {
                        case 0:
                            ssFirst.setVisibility(VISIBLE);
                            ssFirst.setSign(2);
                            break;
                        case 1:
                            ssFirst.makeCircleFaster();
                            ssSecond.setVisibility(VISIBLE);
                            ssSecond.setSign(2);
                            break;
                        case 2:
                            ssSecond.makeCircleFaster();
                            ssThird.setVisibility(VISIBLE);
                            ssThird.setSign(2);
                            break;
                    }
                    password += "2";
                }
                break;
            case R.id.rlPassThree:
                tvPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_secondary_text));
                if (password.length() == passwordSize-1) {
                    ssThird.makeCircleFaster();
                    ssFourth.setVisibility(VISIBLE);
                    ssFourth.setSign(3);
                    password += "3";
                    if (password.matches(currentPassword)) {
                        onPasswordRightEntered.onPasswordRight();
                        ssFirst.setVisibility(GONE);
                        ssSecond.setVisibility(GONE);
                        ssThird.setVisibility(GONE);
                        ssFourth.setVisibility(GONE);
                        password = "";
                    }
                    else {
                         tvPassword.startAnimation(anim);                         v.vibrate(100);
                        ssFirst.clearSign();
                        ssFirst.setVisibility(GONE);
                        ssSecond.clearSign();
                        ssSecond.setVisibility(GONE);
                        ssThird.clearSign();
                        ssThird.setVisibility(GONE);
                        ssFourth.clearSign();
                        ssFourth.setVisibility(GONE);
                        password = "";
                    }
                }
                else {
                    if (password.length() == 4) return;
                    switch (password.length()) {
                        case 0:
                            ssFirst.setVisibility(VISIBLE);
                            ssFirst.setSign(3);
                            break;
                        case 1:
                            ssFirst.makeCircleFaster();
                            ssSecond.setVisibility(VISIBLE);
                            ssSecond.setSign(3);
                            break;
                        case 2:
                            ssSecond.makeCircleFaster();
                            ssThird.setVisibility(VISIBLE);
                            ssThird.setSign(3);
                            break;
                    }
                    password += "3";
                }
                break;
            case R.id.rlPassFour:
                tvPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_secondary_text));
                if (password.length() == passwordSize-1) {
                    ssThird.makeCircleFaster();
                    ssFourth.setVisibility(VISIBLE);
                    ssFourth.setSign(4);
                    password += "4";
                    if (password.matches(currentPassword)) {
                        onPasswordRightEntered.onPasswordRight();
                        ssFirst.setVisibility(GONE);
                        ssSecond.setVisibility(GONE);
                        ssThird.setVisibility(GONE);
                        ssFourth.setVisibility(GONE);
                        password = "";
                    }
                    else {
                         tvPassword.startAnimation(anim);                         v.vibrate(100);
                        ssFirst.clearSign();
                        ssFirst.setVisibility(GONE);
                        ssSecond.clearSign();
                        ssSecond.setVisibility(GONE);
                        ssThird.clearSign();
                        ssThird.setVisibility(GONE);
                        ssFourth.clearSign();
                        ssFourth.setVisibility(GONE);
                        password = "";
                    }
                }
                else {
                    if (password.length() == 4) return;
                    switch (password.length()) {
                        case 0:
                            ssFirst.setVisibility(VISIBLE);
                            ssFirst.setSign(4);
                            break;
                        case 1:
                            ssFirst.makeCircleFaster();
                            ssSecond.setVisibility(VISIBLE);
                            ssSecond.setSign(4);
                            break;
                        case 2:
                            ssSecond.makeCircleFaster();
                            ssThird.setVisibility(VISIBLE);
                            ssThird.setSign(4);
                            break;
                    }
                    password += "4";
                }
                break;
            case R.id.rlPassFive:
                tvPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_secondary_text));
                if (password.length() == passwordSize-1) {
                    ssThird.makeCircleFaster();
                    ssFourth.setVisibility(VISIBLE);
                    ssFourth.setSign(5);
                    password += "5";
                    if (password.matches(currentPassword)) {
                        onPasswordRightEntered.onPasswordRight();
                        ssFirst.setVisibility(GONE);
                        ssSecond.setVisibility(GONE);
                        ssThird.setVisibility(GONE);
                        ssFourth.setVisibility(GONE);
                        password = "";
                    }
                    else {
                         tvPassword.startAnimation(anim);                         v.vibrate(100);
                        ssFirst.clearSign();
                        ssFirst.setVisibility(GONE);
                        ssSecond.clearSign();
                        ssSecond.setVisibility(GONE);
                        ssThird.clearSign();
                        ssThird.setVisibility(GONE);
                        ssFourth.clearSign();
                        ssFourth.setVisibility(GONE);
                        password = "";
                    }
                }
                else {
                    if (password.length() == 4) return;
                    switch (password.length()) {
                        case 0:
                            ssFirst.setVisibility(VISIBLE);
                            ssFirst.setSign(5);
                            break;
                        case 1:
                            ssFirst.makeCircleFaster();
                            ssSecond.setVisibility(VISIBLE);
                            ssSecond.setSign(5);
                            break;
                        case 2:
                            ssSecond.makeCircleFaster();
                            ssThird.setVisibility(VISIBLE);
                            ssThird.setSign(5);
                            break;
                    }
                    password += "5";
                }
                break;
            case R.id.rlPassSix:
                tvPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_secondary_text));
                if (password.length() == passwordSize-1) {
                    ssThird.makeCircleFaster();
                    ssFourth.setVisibility(VISIBLE);
                    ssFourth.setSign(6);
                    password += "6";
                    if (password.matches(currentPassword)) {
                        onPasswordRightEntered.onPasswordRight();
                        ssFirst.setVisibility(GONE);
                        ssSecond.setVisibility(GONE);
                        ssThird.setVisibility(GONE);
                        ssFourth.setVisibility(GONE);
                    }
                    else {
                         tvPassword.startAnimation(anim);                         v.vibrate(100);
                        ssFirst.clearSign();
                        ssFirst.setVisibility(GONE);
                        ssSecond.clearSign();
                        ssSecond.setVisibility(GONE);
                        ssThird.clearSign();
                        ssThird.setVisibility(GONE);
                        ssFourth.clearSign();
                        ssFourth.setVisibility(GONE);
                        password = "";
                    }
                }
                else {
                    if (password.length() == 4) return;
                    switch (password.length()) {
                        case 0:
                            ssFirst.setVisibility(VISIBLE);
                            ssFirst.setSign(6);
                            break;
                        case 1:
                            ssFirst.makeCircleFaster();
                            ssSecond.setVisibility(VISIBLE);
                            ssSecond.setSign(6);
                            break;
                        case 2:
                            ssSecond.makeCircleFaster();
                            ssThird.setVisibility(VISIBLE);
                            ssThird.setSign(6);
                            break;
                    }
                    password += "6";
                }
                break;
            case R.id.rlPassSeven:
                tvPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_secondary_text));
                if (password.length() == passwordSize-1) {
                    ssThird.makeCircleFaster();
                    ssFourth.setVisibility(VISIBLE);
                    ssFourth.setSign(7);
                    password += "7";
                    if (password.matches(currentPassword)) {
                        onPasswordRightEntered.onPasswordRight();
                        ssFirst.setVisibility(GONE);
                        ssSecond.setVisibility(GONE);
                        ssThird.setVisibility(GONE);
                        ssFourth.setVisibility(GONE);
                    }
                    else {
                         tvPassword.startAnimation(anim);                         v.vibrate(100);
                        ssFirst.clearSign();
                        ssFirst.setVisibility(GONE);
                        ssSecond.clearSign();
                        ssSecond.setVisibility(GONE);
                        ssThird.clearSign();
                        ssThird.setVisibility(GONE);
                        ssFourth.clearSign();
                        ssFourth.setVisibility(GONE);
                        password = "";
                    }
                }
                else {
                    if (password.length() == 4) return;
                    switch (password.length()) {
                        case 0:
                            ssFirst.setVisibility(VISIBLE);
                            ssFirst.setSign(7);
                            break;
                        case 1:
                            ssFirst.makeCircleFaster();
                            ssSecond.setVisibility(VISIBLE);
                            ssSecond.setSign(7);
                            break;
                        case 2:
                            ssSecond.makeCircleFaster();
                            ssThird.setVisibility(VISIBLE);
                            ssThird.setSign(7);
                            break;
                    }
                    password += "7";
                }
                break;
            case R.id.rlPassEight:
                tvPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_secondary_text));
                if (password.length() == passwordSize-1) {
                    ssThird.makeCircleFaster();
                    ssFourth.setVisibility(VISIBLE);
                    ssFourth.setSign(8);
                    password += "8";
                    if (password.matches(currentPassword)) {
                        onPasswordRightEntered.onPasswordRight();
                        ssFirst.setVisibility(GONE);
                        ssSecond.setVisibility(GONE);
                        ssThird.setVisibility(GONE);
                        ssFourth.setVisibility(GONE);
                    }
                    else {
                         tvPassword.startAnimation(anim);                         v.vibrate(100);
                        ssFirst.clearSign();
                        ssFirst.setVisibility(GONE);
                        ssSecond.clearSign();
                        ssSecond.setVisibility(GONE);
                        ssThird.clearSign();
                        ssThird.setVisibility(GONE);
                        ssFourth.clearSign();
                        ssFourth.setVisibility(GONE);
                        password = "";
                    }
                }
                else {
                    if (password.length() == 4) return;
                    switch (password.length()) {
                        case 0:
                            ssFirst.setVisibility(VISIBLE);
                            ssFirst.setSign(8);
                            break;
                        case 1:
                            ssFirst.makeCircleFaster();
                            ssSecond.setVisibility(VISIBLE);
                            ssSecond.setSign(8);
                            break;
                        case 2:
                            ssSecond.makeCircleFaster();
                            ssThird.setVisibility(VISIBLE);
                            ssThird.setSign(8);
                            break;
                    }
                    password += "8";
                }
                break;
            case R.id.rlPassNine:
                tvPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_secondary_text));
                if (password.length() == passwordSize-1) {
                    ssThird.makeCircleFaster();
                    ssFourth.setVisibility(VISIBLE);
                    ssFourth.setSign(9);
                    password += "9";
                    if (password.matches(currentPassword)) {
                        onPasswordRightEntered.onPasswordRight();
                        ssFirst.setVisibility(GONE);
                        ssSecond.setVisibility(GONE);
                        ssThird.setVisibility(GONE);
                        ssFourth.setVisibility(GONE);
                        password = "";
                    }
                    else {
                         tvPassword.startAnimation(anim);                         v.vibrate(100);
                        ssFirst.clearSign();
                        ssFirst.setVisibility(GONE);
                        ssSecond.clearSign();
                        ssSecond.setVisibility(GONE);
                        ssThird.clearSign();
                        ssThird.setVisibility(GONE);
                        ssFourth.clearSign();
                        ssFourth.setVisibility(GONE);
                        password = "";
                    }
                }
                else {
                    if (password.length() == 4) return;
                    switch (password.length()) {
                        case 0:
                            ssFirst.setVisibility(VISIBLE);
                            ssFirst.setSign(9);
                            break;
                        case 1:
                            ssFirst.makeCircleFaster();
                            ssSecond.setVisibility(VISIBLE);
                            ssSecond.setSign(9);
                            break;
                        case 2:
                            ssSecond.makeCircleFaster();
                            ssThird.setVisibility(VISIBLE);
                            ssThird.setSign(9);
                            break;
                    }
                    password += "9";
                }
                break;
            case R.id.rlPassBack:
                tvPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_secondary_text));
                if (password.length() != 0) {
                    switch (password.length()) {
                        case 1:
                            ssFirst.clearSign();
                            ssFirst.setVisibility(GONE);
                            break;
                        case 2:
                            ssSecond.clearSign();
                            ssSecond.setVisibility(GONE);
                            break;
                        case 3:
                            ssThird.clearSign();
                            ssThird.setVisibility(GONE);
                            break;
                        case 4:
                            ssFourth.clearSign();
                            ssFourth.setVisibility(GONE);
                            tvPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_secondary_text));
                            break;
                    }
                    password = password.substring(0, password.length()-1);
                }
                break;
            case R.id.exitAp:
                onPasswordRightEntered.onExit();
                break;

        }
    }


}
