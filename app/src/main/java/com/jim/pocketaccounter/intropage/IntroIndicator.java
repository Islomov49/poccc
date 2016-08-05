package com.jim.pocketaccounter.intropage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;

import java.util.ArrayList;
import java.util.List;

public class IntroIndicator extends AppCompatActivity {
    ViewPager pager;
    PagerAdapter pagerAdapter;
    List<Fragment> fragments;
    TextView forskip;
    SharedPreferences sPref;
    SharedPreferences.Editor ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro_indicator);
        sPref=getSharedPreferences("infoFirst",MODE_PRIVATE);
        ed=sPref.edit();

        forskip = (TextView) findViewById(R.id.forskip);
        pager = (ViewPager) findViewById(R.id.pager);
        initFrags();
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        forskip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               try {
                   Intent goMain=new Intent(IntroIndicator.this, PocketAccounter.class);
                   startActivity(goMain);
                   ed.putBoolean("FIRST_KEY",false);
                   ed.commit();
               }
               finally {
                   finish();
               }
            }
        });

        PageIndicator mIndicator= (PageCircleIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);

        pager.setPageTransformer(true, new RotateDownTransformer());
    }
    private void initFrags(){
        fragments=new ArrayList<>();

        //Sozdaniya i otpravka data v fragmentam
        IntroFrame appInfo=new IntroFrame();
        appInfo.shareData(new DataIntro(getString(R.string.appInfoTitel),getString(R.string.introAppInfo),R.drawable.big_icon));
        fragments.add(appInfo);

        IntroFrame udobstvaInfo=new IntroFrame();
        udobstvaInfo.shareData(new DataIntro(getString(R.string.quickInfp),getString(R.string.quickInfo),R.drawable.design_info));
        fragments.add(udobstvaInfo);

        IntroFrame dizaynInfo=new IntroFrame();
        dizaynInfo.shareData(new DataIntro(getString(R.string.designInfo),getString(R.string.clear_desgn),R.drawable.photoim));
        fragments.add(dizaynInfo);

        IntroFrame syncInfo=new IntroFrame();
        syncInfo.shareData(new DataIntro(getString(R.string.safeInfo),getString(R.string.secureInfo),R.drawable.sync_info));
        fragments.add(syncInfo);

        IntroFrame flixebleReportInfo=new IntroFrame();
        flixebleReportInfo.shareData(new DataIntro(getString(R.string.flexInfo),getString(R.string.flexinfo),R.drawable.static_innfo));
        fragments.add(flixebleReportInfo);

        fragments.add(new IntroWithButton());

    }
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

}
