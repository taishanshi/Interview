package com.stone.interview.my_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class SelfView extends View implements View.OnTouchListener {
    public SelfView(Context context) {
        super(context);
    }

    public SelfView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SelfView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SelfView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setOnTouchListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
