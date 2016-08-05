package com.jim.pocketaccounter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.jim.pocketaccounter.report.FilterDialog;
import com.jim.pocketaccounter.report.FilterSelectable;

import java.util.ArrayList;
import java.util.Calendar;


public class ReportByCategory extends Fragment {
    private ImageView ivToolbarMostRight;
    private FilterDialog filterDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.report_by_category, container, false);
        rootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(PocketAccounter.keyboardVisible){
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);}
            }
        },100);

        ViewPager vpReportByCategory = (ViewPager) rootView.findViewById(R.id.vpReportByCategoryPager);
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tlReportByCategoryTab);
        final ReportByCategoryIncomesFragment incomesFragment = new ReportByCategoryIncomesFragment();
        final ReportByCategoryExpansesFragment expansesFragment = new ReportByCategoryExpansesFragment();
        PocketAccounter.toolbar.findViewById(R.id.ivToolbarExcel).setVisibility(View.GONE);
        PocketAccounter.toolbar.findViewById(R.id.spToolbar).setVisibility(View.GONE);
        ivToolbarMostRight = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight);
        ivToolbarMostRight.setImageResource(R.drawable.ic_filter);
        ivToolbarMostRight.setVisibility(View.VISIBLE);
        filterDialog = new FilterDialog(getContext());
        filterDialog.setOnDateSelectedListener(new FilterSelectable() {
            @Override
            public void onDateSelected(Calendar begin, Calendar end) {
                incomesFragment.invalidate(begin, end);
            }
        });
        ivToolbarMostRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.show();
            }
        });
        ArrayList<Fragment> list = new ArrayList<>();
        list.add(incomesFragment);
        list.add(expansesFragment);
        PocketAccounter.toolbar.setTitle(R.string.report_by_categories);
        PocketAccounter.toolbar.setSubtitle("");
        vpReportByCategory.setAdapter(new MyViewPagerAdapter(((PocketAccounter) getContext()).getSupportFragmentManager(), list));
        tabLayout.setupWithViewPager(vpReportByCategory);
        vpReportByCategory.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    filterDialog.setOnDateSelectedListener(new FilterSelectable() {
                        @Override
                        public void onDateSelected(Calendar begin, Calendar end) {
                            incomesFragment.invalidate(begin, end);
                        }
                    });
                }
                else {
                    filterDialog.setOnDateSelectedListener(new FilterSelectable() {
                        @Override
                        public void onDateSelected(Calendar begin, Calendar end) {
                            expansesFragment.invalidate(begin, end);
                        }
                    });
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return rootView;
    }
    private class MyViewPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<Fragment> fragments;
        public MyViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
        public int getCount() {
            return 2;
        }
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return getResources().getString(R.string.income);
            return getResources().getString(R.string.expanse);
        }
    }
}