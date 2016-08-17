package com.jim.pocketaccounter.photocalc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jim.pocketaccounter.PhotoViewActivity;
import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.RecordEditFragment;
import com.jim.pocketaccounter.credit.AdapterCridet;
import com.jim.pocketaccounter.widget.CalcActivity;


import java.io.File;
import java.util.ArrayList;

import static android.support.v4.view.PagerAdapter.POSITION_NONE;

/**
 * Created by DEV on 07.08.2016.
 */

public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static final  String  COUNT_TICKKETS="conutTICKET";
    public static final  String  COUNT_TICKKETS_CACHE="conutTICKETCahce";
    public static final  String  BEGIN_TICKKETS_PATH="key";
    public static final  String  BEGIN_TICKKETS_UID="uid";
    public static final  String  CURRENT_PHOTO_ID="currentphoto";

    public static final  String  COUNT_DELETES="conutdelets";
    public static final  String  BEGIN_DELETE_TICKKETS_PATH="deletsPath";
    public static final  String  BEGIN_DELETE_TICKKETS_PATH_CACHE="deletsPathCACHE";

    public static final  String  FROM_ADAPTER_WITHOUT_DELETE="fromadapwithoutdel";

    final static int VIEW_NULL=0;
    final static int VIEW_NOT_NULL=1;
    boolean fromAdapter=false;
    RecordEditFragment.OpenIntentFromAdapter openActiv;


    public static final  int  REQUEST_DELETE_PHOTOS=101;
    ArrayList<PhotoDetails> myTicketsList;
    Context context;
    public PhotoAdapter(ArrayList<PhotoDetails> myTicketsList, Context context,RecordEditFragment.OpenIntentFromAdapter opent){
        this.myTicketsList=myTicketsList;
        this.context=context;
        openActiv=opent;

    }
    public PhotoAdapter(ArrayList<PhotoDetails> myTicketsList, Context context,RecordEditFragment.OpenIntentFromAdapter opent,boolean keyFromAdapter){
        this.myTicketsList=myTicketsList;
        this.context=context;
        openActiv=opent;
        fromAdapter=keyFromAdapter;
    }
        @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType==VIEW_NOT_NULL) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ticket_view, parent, false);
            vh=new ViewHolder(v);
        }
            else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.null_lay, parent, false);
            vh=new Fornull(v);
        }
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        File forExistCache=new File(myTicketsList.get(position).getPhotopathCache());
        File forExist=new File(myTicketsList.get(position).getPhotopath());
        boolean keyik = (forExistCache.exists()&&forExist.exists());

        return keyik ? VIEW_NOT_NULL : VIEW_NULL;
    }
    public static class Fornull extends RecyclerView.ViewHolder {

        public Fornull(View v) {
            super(v);

        }}
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holdere, final int position) {
        if(holdere instanceof Fornull){   return;  }
         ViewHolder holder=(ViewHolder) holdere;
        PhotoDetails ph=myTicketsList.get(position);
        holder.ticketView.setImageURI(Uri.parse(ph.getPhotopathCache()));
        holder.ticketView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoview=new Intent(context, PhotoViewActivity.class);
                photoview.putExtra(COUNT_TICKKETS,myTicketsList.size());
                photoview.putExtra(CURRENT_PHOTO_ID,position);
                photoview.putExtra(FROM_ADAPTER_WITHOUT_DELETE,fromAdapter);
                int t=0;
                for (PhotoDetails temp:myTicketsList) {

                    photoview.putExtra(BEGIN_TICKKETS_PATH+t,temp.getPhotopath());
                    photoview.putExtra(COUNT_TICKKETS_CACHE+t,temp.getPhotopathCache());
                    photoview.putExtra(BEGIN_TICKKETS_UID+t,temp.getRecordID());
                    t++;
                }
                openActiv.startActivityFromFragmentForResult(photoview);

            }
        });
    }

    @Override
    public int getItemCount() {
        return myTicketsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ticketView;

        public ViewHolder(View itemView) {
            super(itemView);
            ticketView=(ImageView) itemView.findViewById(R.id.ticket_id);
        }
    }


}
