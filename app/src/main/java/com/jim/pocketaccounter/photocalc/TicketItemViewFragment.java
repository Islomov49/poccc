package com.jim.pocketaccounter.photocalc;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jim.pocketaccounter.R;

import uk.co.senab.photoview.PhotoViewAttacher;


public class TicketItemViewFragment extends Fragment {
    ImageView ticketItemView;
    PhotoViewAttacher mAttacher;
    String uriTick;
    public void shareUrl(String urlTick){
        uriTick=urlTick;
    }
    public TicketItemViewFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ticket_item_view, container, false);
        ticketItemView=(ImageView) view.findViewById(R.id.ticketitemview);
        ticketItemView.setImageURI(Uri.parse(uriTick));
        mAttacher = new PhotoViewAttacher(ticketItemView);
//        mAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return view;
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
