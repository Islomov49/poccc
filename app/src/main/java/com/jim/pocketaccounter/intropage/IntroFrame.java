package com.jim.pocketaccounter.intropage;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jim.pocketaccounter.R;

import org.w3c.dom.Text;


public class IntroFrame extends Fragment {
    DataIntro dataIntro;
    TextView titleText;
    TextView contentText;
    ImageView contentImage;
    public IntroFrame() {
        // Required empty public constructor
    }
    public void shareData(DataIntro dataIntro){
        this.dataIntro=dataIntro;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V=inflater.inflate(R.layout.fragment_intro_frame, container, false);
        titleText=(TextView) V.findViewById(R.id.textView8);
        contentText=(TextView) V.findViewById(R.id.conntentText);
        contentImage=(ImageView) V.findViewById(R.id.imageView);

        Typeface fontBlack = Typeface.createFromAsset(getActivity().getAssets(), "ralewayBlack.ttf");
        titleText.setTypeface(fontBlack);
        Typeface fontmed = Typeface.createFromAsset(getActivity().getAssets(), "ralewayMedium.ttf");
        contentText.setTypeface(fontmed);
        titleText.setText(dataIntro.getIntoTitle());
        contentText.setText(dataIntro.getContentText());
        contentImage.setImageResource(dataIntro.getImageRes());
        return V;}



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


}
