package com.jim.pocketaccounter;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.jim.pocketaccounter.credit.AdapterCridetArchive;
import com.jim.pocketaccounter.helper.FloatingActionButton;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.util.ArrayList;


public class CreditTabLay extends Fragment  implements View.OnClickListener, ViewPager.OnPageChangeListener{
    ForFab A1;
    FloatingActionButton fb;
    SvyazkaFragmentov svyaz;
    private ArrayList<Fragment> list;
    public static int pos = 0;
    private ViewPager viewPager;

    public CreditTabLay() {
        Log.d("gogogo", "CreditTabLay: ");
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View V=inflater.inflate(R.layout.fragment_credit_tab_lay, container, false);
        V.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(PocketAccounter.keyboardVisible){
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(V.getWindowToken(), 0);}
            }
        },100);
        TabLayout tabLayout = (TabLayout) V.findViewById(R.id.sliding_tabs);
        fb=(FloatingActionButton) V.findViewById(R.id.fbDebtBorrowFragment);
        fb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                A1.pressedFab();
            }
        });
        ((ImageView)PocketAccounter.toolbar.findViewById(R.id.ivToolbarExcel)).setVisibility(View.GONE);
        viewPager = (ViewPager) V.findViewById(R.id.viewpager);
        list = new ArrayList<>();
        CreditFragment creditFragment = new CreditFragment();
        creditFragment.setCreditTabLay(this);
        CreditArchiveFragment creditArchiveFragment = new CreditArchiveFragment();
        list.add(creditFragment);
        list.add(creditArchiveFragment);
        final PagerAdapter adapter = new PagerAdapter
                (getActivity().getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(pos
        );
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);

        return V;}
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                fb.setAlpha(1-positionOffset);
            }
            else {
                if(positionOffset>=0.1f&&fb.getVisibility()==View.VISIBLE)
                    fb.setVisibility(View.GONE);
                else if(positionOffset<=0.1f&&fb.getVisibility()==View.GONE)
                    fb.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        if(position==1){
            fb.setVisibility(View.GONE);
        }
        else{
            fb.setVisibility(View.VISIBLE);
        }
        pos = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View v) {
    }

    private boolean show = false;
    public void onScrolledList(boolean k) {
        if (k) {
            if (!show)
                fb.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fab_down));
            show = true;
        } else {
            if (show)
                fb.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fab_up));
            show = false;
        }
    }

    AdapterCridetArchive.GoCredFragForNotify svyazForNotifyFromArchAdap;
    public class PagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<Fragment> list;
        public PagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    CreditFragment credA= (CreditFragment) list.get(position);
                    A1=credA.getEvent();
                    credA.setSvyaz(new SvyazkaFragmentov() {
                        @Override
                        public void itemInsertedToArchive() {
                            svyaz.itemInsertedToArchive();
                        }
                    });
                    svyazForNotifyFromArchAdap=credA.getInterfaceNotify();
                    return credA;
                case 1:
                    CreditArchiveFragment arch= (CreditArchiveFragment) list.get(position);
                    svyaz=arch.getSvyaz();
                    arch.setSvyazToAdapter(svyazForNotifyFromArchAdap);
                    return arch;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getResources().getString(R.string.active);
            }
            return getResources().getString(R.string.archive);
        }
    }

    public interface ForFab{
        public void pressedFab();
    }
    public interface SvyazkaFragmentov{
        public void itemInsertedToArchive();
    }

}
