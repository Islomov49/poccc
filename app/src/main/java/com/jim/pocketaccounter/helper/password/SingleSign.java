package com.jim.pocketaccounter.helper.password;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jim.pocketaccounter.R;

public class SingleSign extends RelativeLayout {
    private ImageView ivCircle;
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
        ivCircle = new ImageView(context);
        ivCircle.setImageResource(R.drawable.pass_circle);
        ivCircle.setLayoutParams(lp);
        tvNumber = new TextView(context);
        tvNumber.setLayoutParams(lp);
        ivCircle.setVisibility(GONE);
        tvNumber.setVisibility(GONE);
        tvNumber.setTextSize(getResources().getDimension(R.dimen.ten_dp));
        addView(ivCircle);
        addView(tvNumber);
    }
    public void setSign(int sign) {
        tvNumber.setText(Integer.toString(sign));
        tvNumber.setVisibility(VISIBLE);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!circled) {
                    tvNumber.setVisibility(GONE);
                    tvNumber.setText("");
                    ivCircle.setVisibility(VISIBLE);
                    circled = true;
                }
            }
        }, 1500);
    }

    public void makeCircleFaster() {
        handler.removeCallbacksAndMessages(null);
        tvNumber.setVisibility(GONE);
        tvNumber.setText("");
        ivCircle.setVisibility(VISIBLE);
        circled = true;
    }

    public void clearSign() {
        tvNumber.setText("");
        tvNumber.setVisibility(GONE);
        ivCircle.setVisibility(GONE);
        circled = false;
    }
}
