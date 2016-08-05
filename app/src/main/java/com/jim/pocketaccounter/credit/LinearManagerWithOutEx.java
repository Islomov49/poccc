package com.jim.pocketaccounter.credit;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by DEV on 26.06.2016.
 */

    public class LinearManagerWithOutEx extends LinearLayoutManager {
    public LinearManagerWithOutEx(Context context) {
        super(context);
    }

    public LinearManagerWithOutEx(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LinearManagerWithOutEx(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //... constructor
        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("probe", "BUNISI YOMON");
            }
        }
    }