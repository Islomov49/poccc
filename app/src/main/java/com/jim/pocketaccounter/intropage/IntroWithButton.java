package com.jim.pocketaccounter.intropage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.helper.FloatingActionButton;

import static android.content.Context.MODE_PRIVATE;

public class IntroWithButton extends Fragment {
    TextView textLets;
    ImageView togoBEGIN;
    SharedPreferences sPref;
    SharedPreferences.Editor ed;

    public IntroWithButton() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V=inflater.inflate(R.layout.fragment_intro_with_button, container, false);
        sPref=getActivity().getSharedPreferences("infoFirst",MODE_PRIVATE);
        ed=sPref.edit();

        textLets=(TextView) V.findViewById(R.id.textView8) ;
        togoBEGIN=(ImageView) V.findViewById(R.id.fbDebtBorrowFragment) ;
        Typeface fontBlack = Typeface.createFromAsset(getActivity().getAssets(), "ralewayBlack.ttf");
        textLets.setTypeface(fontBlack);
        togoBEGIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent togoB=new Intent(getActivity(), PocketAccounter.class);
                    startActivity(togoB);
                    ed.putBoolean("FIRST_KEY",false);
                    ed.commit();
                }
                finally {
                    getActivity().finish();
                }
            }
        });
        return  V;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
