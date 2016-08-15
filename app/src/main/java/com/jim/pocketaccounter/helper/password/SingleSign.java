package com.jim.pocketaccounter.helper.password;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jim.pocketaccounter.R;
import com.transitionseverywhere.TransitionManager;

public class SingleSign extends RelativeLayout {
    private TextView tvNumber;
    private boolean circled = false;
    private Handler handler;
    public SingleSign(Context context) {
        super(context);
        init(context);
    }

    public SingleSign(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SingleSign(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SingleSign(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    private void init(Context context) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);

        tvNumber = new TextView(context);
        tvNumber.setLayoutParams(lp);

        tvNumber.setTextSize((int) (getResources().getDimension(R.dimen.thirty_dp) / getResources().getDisplayMetrics().density));
        tvNumber.setTextColor(ContextCompat.getColor(getContext(), R.color.black_for_glavniy_text));
        addView(tvNumber);
    }
    public void setSign(int sign) {
        tvNumber.setText(Integer.toString(sign));
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!circled) {
                    tvNumber.setText("*");
//                    tvNumber.setVisibility(GONE);
//                    tvNumber.setText("");
//                    ivCircle.setVisibility(VISIBLE);
//                    circled = true;
                }
            }
        }, 500);
    }

    public void makeCircleFaster() {
        handler.removeCallbacksAndMessages(null);
        tvNumber.setText("*");
        circled = true;
    }

    public void clearSign() {
        TransitionManager.beginDelayedTransition(this);
        tvNumber.setText("");
        circled = false;
    }
}
