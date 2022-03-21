package com.armdri.myracle;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.armdri.myracle.R;

public class Rectangle extends FrameLayout {
    View view;
    CheckBox checkBox;

    public Rectangle(Context context, AttributeSet attributeSet, int color) {
        super(context, attributeSet);
        init(context, color);
    }

    public Rectangle(Context context, int color){
        super(context);
        init(context, color);
    }

    private void init(Context context, int color){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(color == -1)
            view = inflater.inflate(R.layout.empty0, this, true);
        if(color == 0)
            view = inflater.inflate(R.layout.rect0, this, true);
        if(color == 1)
            view = inflater.inflate(R.layout.rect1, this, true);
        if(color == 2)
            view = inflater.inflate(R.layout.rect2, this, true);
        if(color == 3)
            view = inflater.inflate(R.layout.rect3, this, true);
        if(color == 4)
            view = inflater.inflate(R.layout.rect4, this, true);
    }
}
