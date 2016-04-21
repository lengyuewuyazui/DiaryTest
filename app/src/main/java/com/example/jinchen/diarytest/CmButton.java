package com.example.jinchen.diarytest;

/**
 * Created by JinChen on 2016/3/27.
 */
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
public class CmButton extends ImageButton implements OnTouchListener, OnFocusChangeListener {
    public CmButton(Context context) {
        super(context);
        this.setOnTouchListener(this);
        this.setOnFocusChangeListener(this);
    }
    public CmButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.imageButtonStyle);
        this.setOnTouchListener(this);
        this.setOnFocusChangeListener(this);
    }
    public CmButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusable(true);
        this.setOnTouchListener(this);
        this.setOnFocusChangeListener(this);
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // 灰色效果
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        if (hasFocus) {
            ((ImageButton) v).getDrawable().setColorFilter(new ColorMatrixColorFilter(cm));
        } else {
            ((ImageButton) v).getDrawable().clearColorFilter();
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 灰色效果
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ((ImageButton) v).getDrawable().setColorFilter(new ColorMatrixColorFilter(cm));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            ((ImageButton) v).getDrawable().clearColorFilter();
        }
        return false;
    }
}
