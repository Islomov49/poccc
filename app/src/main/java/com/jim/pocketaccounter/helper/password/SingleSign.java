package com.jim.pocketaccounter.helper.password;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jim.pocketaccounter.R;

public class SingleSign extends RelativeLayout {
    private ImageView ivCircle;
    private TextView tvNumber;
    private boolean circleMode = false;
    public SingleSign(Context context) {
        super(context);
    }

    public SingleSign(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleSign(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SingleSign(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
    }
    public void setSign(int sign) {
        tvNumber.setText(Integer.toString(sign));
        tvNumber.setVisibility(VISIBLE);
        tvNumber.postDelayed(new Runnable() {
            @Override
            public void run() {
                circleMode = true;
                tvNumber.setVisibility(GONE);
                ivCircle.setVisibility(VISIBLE);
            }
        }, 200);
    }

    public void makeCircleFaster() {
        if (!circleMode) {
            tvNumber.setVisibility(GONE);
            ivCircle.setVisibility(VISIBLE);
        }
    }

    public void clearSign() {
        tvNumber.setText("");
        tvNumber.setVisibility(GONE);
        ivCircle.setVisibility(GONE);
        circleMode = false;
    }
}
