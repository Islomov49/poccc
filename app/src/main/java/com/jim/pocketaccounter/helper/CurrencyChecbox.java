package com.jim.pocketaccounter.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jim.pocketaccounter.R;

public class CurrencyChecbox extends RelativeLayout implements View.OnClickListener{
    private boolean checked = false;
    private ImageView ivCheckSign;
    private OnCheckListener listener = new OnCheckListener() {
        @Override
        public void onCheck(boolean isChecked) {
            return;
        }
    };
    public CurrencyChecbox(Context context) {
        super(context);
        init();
    }
    public CurrencyChecbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }
    public CurrencyChecbox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CurrencyChecbox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.currency_checkbox, this);
        ivCheckSign = (ImageView) findViewById(R.id.ivCheckSign);
        if (checked)
            ivCheckSign.setVisibility(VISIBLE);
        else
            ivCheckSign.setVisibility(GONE);
        setOnClickListener(this);
    }
    public void setOnCheckListener(OnCheckListener listener) {
        this.listener = listener;
    }
    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
        if (checked)
            ivCheckSign.setVisibility(VISIBLE);
        else
            ivCheckSign.setVisibility(GONE);
    }
    @Override
    public void onClick(View v) {
        checked = !checked;
        if (checked)
            ivCheckSign.setVisibility(VISIBLE);
        else
            ivCheckSign.setVisibility(GONE);
        listener.onCheck(checked);
    }

    public interface OnCheckListener {
        public void onCheck(boolean isChecked);
    }
}
