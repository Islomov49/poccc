package com.jim.pocketaccounter.helper.password;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jim.pocketaccounter.R;

public class PasswordWindow extends LinearLayout {
    private String password = "";
    private int passwordSize = 4;
    private OnPasswordRightEntered onPasswordRightEntered = null;
    private String currentPassword = "";
    private SingleSign ssFirst, ssSecond, ssThird, ssFourth;
    private ImageView ivPassword;
    public PasswordWindow(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.password_layout ,this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        currentPassword = preferences.getString("password", "1234");
        ssFirst = (SingleSign) findViewById(R.id.ssFirst);
        ssSecond = (SingleSign) findViewById(R.id.ssSecond);
        ssThird = (SingleSign) findViewById(R.id.ssThird);
        ssFourth = (SingleSign) findViewById(R.id.ssFourth);
        ivPassword = (ImageView) findViewById(R.id.ivPassword);
    }

    public PasswordWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.password_layout ,this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        currentPassword = preferences.getString("password", "1234");
        ssFirst = (SingleSign) findViewById(R.id.ssFirst);
        ssSecond = (SingleSign) findViewById(R.id.ssSecond);
        ssThird = (SingleSign) findViewById(R.id.ssThird);
        ssFourth = (SingleSign) findViewById(R.id.ssFourth);
        ivPassword = (ImageView) findViewById(R.id.ivPassword);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public PasswordWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.password_layout ,this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        currentPassword = preferences.getString("password", "1234");
        ssFirst = (SingleSign) findViewById(R.id.ssFirst);
        ssSecond = (SingleSign) findViewById(R.id.ssSecond);
        ssThird = (SingleSign) findViewById(R.id.ssThird);
        ssFourth = (SingleSign) findViewById(R.id.ssFourth);
        ivPassword = (ImageView) findViewById(R.id.ivPassword);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PasswordWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.password_layout ,this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        currentPassword = preferences.getString("password", "1234");
        ssFirst = (SingleSign) findViewById(R.id.ssFirst);
        ssSecond = (SingleSign) findViewById(R.id.ssSecond);
        ssThird = (SingleSign) findViewById(R.id.ssThird);
        ssFourth = (SingleSign) findViewById(R.id.ssFourth);
        ivPassword = (ImageView) findViewById(R.id.ivPassword);
    }

    public void setOnPasswordRightEnteredListener(OnPasswordRightEntered onPasswordRightEnteredListener) {
        this.onPasswordRightEntered = onPasswordRightEnteredListener;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlPassZero:
                if (password.length() == passwordSize) {
                    if (password.matches(currentPassword)) {
                        ivPassword.setImageResource(R.drawable.right_password);
                        ivPassword.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onPasswordRightEntered.onPasswordRight();
                                ivPassword.setImageDrawable(null);
                            }
                        }, 50);
                    }
                    else {
                        ivPassword.setImageDrawable(null);
                        ivPassword.setImageResource(R.drawable.wrong_password);
                    }
                }
                else {
                    switch (password.length()) {
                        case 0:
                            ssFirst.setSign(0);
                            break;
                        case 1:
                            ssFirst.makeCircleFaster();
                            ssSecond.setSign(0);
                            break;
                        case 2:
                            ssSecond.makeCircleFaster();
                            ssThird.setSign(0);
                            break;
                        case 3:
                            ssThird.makeCircleFaster();
                            ssFourth.setSign(0);
                            break;
                    }
                    password += "0";
                }
                break;
            case R.id.rlPassOne:
                if (password.length() == passwordSize) {
                    if (password.matches(currentPassword)) {
                        ivPassword.setImageResource(R.drawable.right_password);
                        ivPassword.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onPasswordRightEntered.onPasswordRight();
                                ivPassword.setImageDrawable(null);
                            }
                        }, 50);
                    }
                    else {
                        ivPassword.setImageDrawable(null);
                        ivPassword.setImageResource(R.drawable.wrong_password);
                    }
                }
                else {
                    switch (password.length()) {
                        case 0:
                            ssFirst.setSign(1);
                            break;
                        case 1:
                            ssFirst.makeCircleFaster();
                            ssSecond.setSign(1);
                            break;
                        case 2:
                            ssSecond.makeCircleFaster();
                            ssThird.setSign(1);
                            break;
                        case 3:
                            ssThird.makeCircleFaster();
                            ssFourth.setSign(1);
                            break;
                    }
                    password += "1";
                }
            case R.id.rlPassTwo:
                if (password.length() == passwordSize) {
                    if (password.matches(currentPassword)) {}
                    else {}
                }
                else
                    password += "2";
                break;
            case R.id.rlPassThree:
                if (password.length() == passwordSize) {
                    if (password.matches(currentPassword)) {}
                    else {}
                }
                else
                    password += "3";
                break;
            case R.id.rlPassFour:
                if (password.length() == passwordSize) {
                    if (password.matches(currentPassword)) {}
                    else {}
                }
                else
                    password += "4";
                break;
            case R.id.rlPassFive:
                if (password.length() == passwordSize) {
                    if (password.matches(currentPassword)) {}
                    else {}
                }
                else {
                    password += "5";

                }
                break;
            case R.id.rlPassSix:
                if (password.length() == passwordSize) {
                    if (password.matches(currentPassword)) {}
                    else {}
                }
                else
                    password += "6";
                break;
            case R.id.rlPassSeven:
                if (password.length() == passwordSize) {
                    if (password.matches(currentPassword)) {}
                    else {}
                }
                else
                    password += "7";
                break;
            case R.id.rlPassEight:
                if (password.length() == passwordSize) {
                    if (password.matches(currentPassword)) {}
                    else {}
                }
                else
                    password += "8";
                break;
            case R.id.rlPassNine:
                if (password.length() == passwordSize) {
                    if (password.matches(currentPassword)) {}
                    else {}
                }
                else
                    password += "9";
                break;
            case R.id.rlPassBack:
                if (password.length() != 0) {
                    password = password.substring(0, password.length() - 1);
                    switch (password.length()) {
                        case 1:
                            ssFirst.clearSign();
                            break;
                        case 2:
                            ssSecond.clearSign();
                            break;
                        case 3:
                            ssThird.clearSign();
                            break;
                        case 4:
                            ssFourth.clearSign();
                            break;
                    }
                }
                break;
        }
    }


}
