package com.jim.pocketaccounter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.jim.pocketaccounter.photocalc.DepthPageTransformer;
import com.jim.pocketaccounter.photocalc.PhotoAdapter;
import com.jim.pocketaccounter.photocalc.PhotoDetails;
import com.jim.pocketaccounter.photocalc.TicketItemViewFragment;
import com.jim.pocketaccounter.photocalc.ViewPagerFixed;

import java.util.ArrayList;

import static com.jim.pocketaccounter.photocalc.PhotoAdapter.BEGIN_DELETE_TICKKETS_PATH;
import static com.jim.pocketaccounter.photocalc.PhotoAdapter.BEGIN_DELETE_TICKKETS_PATH_CACHE;
import static com.jim.pocketaccounter.photocalc.PhotoAdapter.BEGIN_TICKKETS_PATH;
import static com.jim.pocketaccounter.photocalc.PhotoAdapter.BEGIN_TICKKETS_UID;
import static com.jim.pocketaccounter.photocalc.PhotoAdapter.COUNT_DELETES;
import static com.jim.pocketaccounter.photocalc.PhotoAdapter.COUNT_TICKKETS;
import static com.jim.pocketaccounter.photocalc.PhotoAdapter.COUNT_TICKKETS_CACHE;
import static com.jim.pocketaccounter.photocalc.PhotoAdapter.CURRENT_PHOTO_ID;
import static com.jim.pocketaccounter.photocalc.PhotoAdapter.FROM_ADAPTER_WITHOUT_DELETE;

public class PhotoViewActivity extends AppCompatActivity {
    private ViewPagerFixed ticketChangeViewPager;
    private PagerAdapter mPagerAdapter;
    ArrayList<PhotoDetails> myListPhotos;
    ArrayList<PhotoDetails> myDeletedListPhotos;
    int currentTicket=0;
    boolean fromAdapter=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fragment_ticket_view);
        myListPhotos=new ArrayList<>();
        myDeletedListPhotos=new ArrayList<>();
        if(getIntent().getExtras()!=null){
            fromAdapter=(boolean) getIntent().getExtras().get(FROM_ADAPTER_WITHOUT_DELETE);
            int t= (int) getIntent().getExtras().get(COUNT_TICKKETS);
            currentTicket =  (int) getIntent().getExtras().get(CURRENT_PHOTO_ID);
            for (int i = 0; i <t ; i++) {
                PhotoDetails temp=new PhotoDetails((String) getIntent().getExtras().get(BEGIN_TICKKETS_PATH+i),(String)getIntent().getExtras().get(COUNT_TICKKETS_CACHE+i),(String)getIntent().getExtras().get(BEGIN_TICKKETS_UID+i));
                myListPhotos.add(temp);
            }
            Log.d("test", "listSze: "+myListPhotos.size());
        }
        else
        {
            finish();
        }
        ticketChangeViewPager = (ViewPagerFixed) findViewById(R.id.viewpagerik);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),myListPhotos);
        ticketChangeViewPager.setAdapter(mPagerAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ticketChangeViewPager.setPageTransformer(true,new DepthPageTransformer());
        }
        ticketChangeViewPager.setCurrentItem(currentTicket);
        if(fromAdapter){
            findViewById(R.id.delete_item).setVisibility(View.GONE);
        }
        findViewById(R.id.to_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fromAdapter){
                    Intent resultIntent=null;
                    if(myDeletedListPhotos.size()!=0){
                        resultIntent = new Intent();
                        resultIntent.putExtra(COUNT_DELETES,myDeletedListPhotos.size());
                        for (int i = 0; i < myDeletedListPhotos.size() ; i++) {
                            resultIntent.putExtra(BEGIN_DELETE_TICKKETS_PATH+i,myDeletedListPhotos.get(i).getPhotopath());
                            resultIntent.putExtra(BEGIN_DELETE_TICKKETS_PATH_CACHE+i,myDeletedListPhotos.get(i).getPhotopathCache());
                        }
                    }
                    setResult(Activity.RESULT_OK, resultIntent);

                }
                finish();
            }
        });
        findViewById(R.id.imageView6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri image_uri = Uri.parse("file://"+myListPhotos.get(ticketChangeViewPager.getCurrentItem()).getPhotopath());
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, image_uri);
                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_ticket)));
            }
        });
        if(!fromAdapter)
        findViewById(R.id.delete_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PhotoViewActivity.this);
                builder.setMessage("Really you want delete what ticket?")
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Log.d("wwwwww", "onClick: "+ticketChangeViewPager.getCurrentItem());
                                myDeletedListPhotos.add(myListPhotos.get(ticketChangeViewPager.getCurrentItem()));
                                myListPhotos.remove(ticketChangeViewPager.getCurrentItem());
                                if(myListPhotos.size()==0){
                                    Intent resultIntent=null;
                                    if(myDeletedListPhotos.size()!=0){
                                        resultIntent = new Intent();
                                        resultIntent.putExtra(COUNT_DELETES,myDeletedListPhotos.size());
                                        for (int i = 0; i < myDeletedListPhotos.size() ; i++) {
                                            resultIntent.putExtra(BEGIN_DELETE_TICKKETS_PATH+i,myDeletedListPhotos.get(i).getPhotopath());
                                            resultIntent.putExtra(BEGIN_DELETE_TICKKETS_PATH_CACHE+i,myDeletedListPhotos.get(i).getPhotopathCache());
                                        }
                                    }
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    finish();
                                }
                                mPagerAdapter.notifyDataSetChanged();

                            }
                        }) .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.create().show();



            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<PhotoDetails> mylist;
        public ScreenSlidePagerAdapter(FragmentManager fm,ArrayList<PhotoDetails> mylist) {
            super(fm);
            this.mylist=mylist;
        }

        @Override
        public Fragment getItem(int position) {

            TicketItemViewFragment ticketItemViewFragment = new TicketItemViewFragment();
           ticketItemViewFragment.shareUrl(mylist.get(position).getPhotopath());
            return ticketItemViewFragment;
        }

        @Override
        public int getCount() {
            return mylist.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }
    @Override
    public void onBackPressed(){
        PocketAccounter.openActivity=false;
        if(!fromAdapter){
            Log.d("resulttt", "onBackPressed: "+Boolean.toString(fromAdapter)+" - "+myDeletedListPhotos.size());
            Intent resultIntent=null;
            if(myDeletedListPhotos.size()!=0){
                resultIntent = new Intent();
                resultIntent.putExtra(COUNT_DELETES,myDeletedListPhotos.size());
                for (int i = 0; i < myDeletedListPhotos.size() ; i++) {
                    resultIntent.putExtra(BEGIN_DELETE_TICKKETS_PATH+i,myDeletedListPhotos.get(i).getPhotopath());
                    resultIntent.putExtra(BEGIN_DELETE_TICKKETS_PATH_CACHE+i,myDeletedListPhotos.get(i).getPhotopathCache());
                }
            }
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
        else {
            Log.d("resulttt", "onBackPressed: "+Boolean.toString(fromAdapter));
            super.onBackPressed();
        }

    }
}
