package com.jim.pocketaccounter.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.SettingsActivity;

import java.util.zip.Inflater;

import static android.graphics.Color.RED;

/**
 * Created by DEV on 13.08.2016.
 */

public class PasswordPreference extends DialogPreference  {
    private EditText myPassword1,myPassword2;
    private TextView myFourNumbers,myRepiatPassword;
    private boolean comfirm=false;


    public PasswordPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent (false);
        setDialogLayoutResource(R.layout.password_layout_edit);

//        setTitle(getContext().getString(R.string.enter_password));
    }
    boolean isok=false;
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        comfirm=false;
        if(isok){
            SharedPreferences.Editor editor = getEditor();
            editor.putBoolean("secure",true);
            try {
                editor.apply();
            }catch (Exception o ){
                editor.commit();
            }
            ((CheckBoxPreference)findPreferenceInHierarchy("secure")).setChecked(true);
        }

    }

    @Override
    public void onBindDialogView(View view){
        super.onBindDialogView(view);

        myPassword1=(EditText)view.findViewById(R.id.firstPassword);
        myPassword2=(EditText)view.findViewById(R.id.secondPassword);
        myFourNumbers=(TextView) view.findViewById(R.id.passwordTextShould);
        myRepiatPassword=(TextView) view.findViewById(R.id.passwordRepiat);
        myPassword2.setVisibility(View.GONE);
        myRepiatPassword.setVisibility(View.GONE);
        view.findViewById(R.id.okbuttt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comfirm){
                    myRepiatPassword.setText(getContext().getString(R.string.repiat_yout_password));
                    myRepiatPassword.setTextColor(ContextCompat.getColor(getContext(),R.color.black_for_secondary_text));

                    if(myPassword1.getText().toString().length()!=4){
                        myPassword1.setText("");
                        myFourNumbers.setTextColor(RED);
                        return;
                    }
                    else
                        myFourNumbers.setTextColor(ContextCompat.getColor(getContext(),R.color.black_for_secondary_text));

                    if (myPassword2.getText().toString().length()!=4){
                        myPassword2.setText("");
                        myRepiatPassword.setTextColor(RED);
                        return;

                    }
                    else
                        myRepiatPassword.setTextColor(ContextCompat.getColor(getContext(),R.color.black_for_secondary_text));

                    if (myPassword1.getText().toString().matches(myPassword2.getText().toString())){
                        SharedPreferences.Editor editor = getEditor();
                        editor.putString("password",myPassword2.getText().toString()  );
                        editor.putBoolean("firstclick",false  );
                        editor.commit();
                        isok=true;
                        getDialog().dismiss();
                    }
                    else  {
                        myPassword2.setText("");
                        myRepiatPassword.setText(R.string.please_repait_correct);
                        myRepiatPassword.setTextColor(RED);
                        return;

                    }
                }
                else {

                    if(myPassword1.getText().toString().length()!=4){
                        myPassword1.setText("");
                        myFourNumbers.setText(R.string.was_four_numbers);
                        myFourNumbers.setTextColor(RED);
                        return;
                    }
                    else
                        myFourNumbers.setTextColor(ContextCompat.getColor(getContext(),R.color.black_for_secondary_text));

                    if (myPassword1.getText().toString().matches(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("password",""))){
                      comfirm=true;
                        getDialog().setTitle(R.string.new_password);
                        myPassword1.setText("");
                        myPassword2.setText("");
                        myPassword2.setVisibility(View.VISIBLE);
                        myRepiatPassword.setVisibility(View.VISIBLE);
                        myFourNumbers.setText(R.string.password_should_be_4_numbers);
                        myFourNumbers.setTextColor(ContextCompat.getColor(getContext(),R.color.black_for_secondary_text));


                    }
                    else  {
                        myPassword1.setText("");
                        myFourNumbers.setText(R.string.try_one_more);
                        myFourNumbers.setTextColor(RED);
                        return;

                    }
                }
            }
        });
    }


    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setTitle(R.string.current_password);
        builder.setPositiveButton(null, null);
        builder.setNegativeButton(null, null);
        super.onPrepareDialogBuilder(builder);
    }

    public void show()
    {
        showDialog(null);
    }


}
