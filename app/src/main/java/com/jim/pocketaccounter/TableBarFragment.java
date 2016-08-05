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

public class TableBarFragment extends Fragment {
    private ReportByIncomeExpanseTableFragment tableFragment;
    private ReportByIncomeExpanseBarFragment barFragment;
    private ImageView ivToolbarMostRight, ivToolbarExcel;
    private FilterDialog filterDialog;

    @Override
    public void onStart() {
        super.onStart();
        ivToolbarExcel.setVisibility(View.VISIBLE);
        ivToolbarMostRight.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.table_bar_fragment_layout, container, false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(PocketAccounter.keyboardVisible){
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);}
            }
        },100);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.vpTableBarFragment);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tlTableBarFragment);
        ArrayList<Fragment> list = new ArrayList<>();
        filterDialog = new FilterDialog(getContext());
        ivToolbarMostRight = (ImageView)PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight);
        ivToolbarMostRight.setImageResource(R.drawable.ic_filter);
        ivToolbarMostRight.setVisibility(View.VISIBLE);
        PocketAccounter.toolbar.findViewById(R.id.spToolbar).setVisibility(View.GONE);
        ivToolbarMostRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.show();
            }
        });
        PocketAccounter.toolbar.setTitle(R.string.report_by_income_expanse);
        PocketAccounter.toolbar.setSubtitle("");
        tableFragment = new ReportByIncomeExpanseTableFragment();
        ivToolbarExcel = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarExcel);
        ivToolbarExcel.setImageDrawable(null);
        ivToolbarExcel.setImageResource(R.drawable.ic_excel);
        ivToolbarExcel.setVisibility(View.VISIBLE);
        barFragment = new ReportByIncomeExpanseBarFragment();
        list.add(tableFragment);
        list.add(barFragment);
        viewPager.setAdapter(new TableBarAdapter(((PocketAccounter)getContext()).getSupportFragmentManager(), list));
        tabLayout.setupWithViewPager(viewPager);
        filterDialog.setOnDateSelectedListener(new FilterSelectable() {
            @Override
            public void onDateSelected(Calendar begin, Calendar end) {
                tableFragment.invalidate(begin, end);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(final int position) {
                filterDialog.setOnDateSelectedListener(new FilterSelectable() {
                    @Override
                    public void onDateSelected(Calendar begin, Calendar end) {
                        if (position == 0) {
                            tableFragment.invalidate(begin, end);
                            ivToolbarExcel.setVisibility(View.VISIBLE);
                        }
                        else if (position == 1){
                            barFragment.invalidate(begin, end);
                            ivToolbarExcel.setVisibility(View.GONE);
                        }
                    }
                });
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        return view;
    }
    private class TableBarAdapter extends FragmentStatePagerAdapter {
        private ArrayList<Fragment> list;
        public TableBarAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }
        public Fragment getItem(int position) {
            return list.get(position);
        }
        public int getCount() {return 2;}

        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getResources().getString(R.string.table);
            }
            return getString(R.string.bars);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
